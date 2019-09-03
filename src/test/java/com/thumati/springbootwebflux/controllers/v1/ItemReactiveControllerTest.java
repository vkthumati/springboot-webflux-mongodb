package com.thumati.springbootwebflux.controllers.v1;

import com.thumati.springbootwebflux.model.Item;
import com.thumati.springbootwebflux.repositories.ItemReactiveRepositories;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.thumati.springbootwebflux.util.ItemConstants.ITEM_END_POINT_V1;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemReactiveControllerTest {

    @Autowired
    private ItemReactiveRepositories repositories;

    @Autowired
    private WebTestClient webTestClient;

    private List<Item> data(){
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 300.0),
                new Item(null, "Apple Watch", 450.0),
                new Item(null, "Beats Headphones", 120.0),
                new Item("abc123", "Bosh Speakers", 660.0));
    }

    @Before
    public void setUp(){
        repositories.deleteAll()
                    .thenMany(Flux.fromIterable(data()))
                    .flatMap(repositories::save)
                    .doOnNext(item -> System.out.println("Inserted item : "+item))
                    .blockLast();
    }

    @Test
    public void getAllItems(){
        webTestClient.get()
                    .uri(ITEM_END_POINT_V1)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                    .expectBodyList(Item.class)
                    .hasSize(5);
    }

    @Test
    public void getAllItems_approach2(){
        webTestClient.get()
                .uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith( response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach( item -> assertTrue(item.getId()!=null));
                });
    }

    @Test
    public void getAllItems_approach3(){
        Flux<Item> itemFlux = webTestClient.get()
                    .uri(ITEM_END_POINT_V1)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                    .returnResult(Item.class)
                    .getResponseBody();

        StepVerifier.create(itemFlux.log("getAllItems_approach3"))
                    .expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    public void findById(){
        webTestClient.get()
                    .uri(ITEM_END_POINT_V1.concat("/{id}"), "abc123")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                    .expectBody()
                    .jsonPath("$.price", 660.0);
    }

    @Test
    public void findById_notFound(){
        webTestClient.get()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), "abc")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItem(){
        Item item = new Item(null, "Iphone X", 999.0);
        webTestClient.post()
                    .uri(ITEM_END_POINT_V1)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(Mono.just(item), Item.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.description").isEqualTo("Iphone X")
                    .jsonPath("$.price").isEqualTo(999.0);
    }

    @Test
    public void deleteById(){
        webTestClient.delete()
                    .uri(ITEM_END_POINT_V1.concat("/{id}"), "abc123")
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Void.class);

    }

    @Test
    public void updateItem(){
        Item item = new Item(null, "Beat Headphones", 616.0);
        webTestClient.put()
                    .uri(ITEM_END_POINT_V1.concat("/{id}"), "abc123")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .body(Mono.just(item), Item.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.price", 616.0);
    }

    @Test
    public void updateItem_notFound(){
        Item item = new Item(null, "Beat Headphones", 616.0);
        webTestClient.put()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), "abc")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void runtimeException(){
        webTestClient.get()
                    .uri(ITEM_END_POINT_V1.concat("/runtimeException"))
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody(String.class)
                    .isEqualTo("RuntimeException occurred.");
    }
}
