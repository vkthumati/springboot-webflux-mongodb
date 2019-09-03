package com.thumati.springbootwebflux.controllers.v1;

import com.thumati.springbootwebflux.model.ItemCapped;
import com.thumati.springbootwebflux.repositories.ItemCappedReactiveRepositories;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.thumati.springbootwebflux.util.ItemConstants.ITEMCAPPED_STREAM_END_POINT_V1;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class ItemCappedStreamReactiveControllerTest {

    @Autowired
    private ItemCappedReactiveRepositories repositories;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webTestClient;

    @Before
    public void setUp(){
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
        dataSetUpForCappedCollection();
    }

    private void dataSetUpForCappedCollection(){
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                                                .map( i -> new ItemCapped(null, "Random ItemCapped"+i, (100.0+i)))
                                                .take(5);

        repositories.insert(itemCappedFlux)
                    .doOnNext( itemCapped -> System.out.println("Inserted Item in setUp : "+itemCapped))
                    .blockLast();
    }

    @Test
    public void getItemsCappedStream(){
        Flux<ItemCapped> itemCappedFlux = webTestClient.get()
                .uri(ITEMCAPPED_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                    .expectNextCount(5)
                    .thenCancel()
                    .verify();
    }
}
