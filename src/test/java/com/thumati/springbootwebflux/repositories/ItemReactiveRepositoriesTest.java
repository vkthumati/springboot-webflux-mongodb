package com.thumati.springbootwebflux.repositories;

import com.thumati.springbootwebflux.model.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoriesTest {

    @Autowired
    ItemReactiveRepositories repositories;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
                                        new Item(null, "LG TV", 300.0),
                                        new Item(null, "Apple Watch", 450.0),
                                        new Item(null, "Beats Headphones", 120.0),
                                        new Item("abc123", "Bosh Speakers", 660.0));

    @Before
    public void setUp(){
        repositories.deleteAll()
                    .thenMany(Flux.fromIterable(itemList))
                    .flatMap(repositories::save)
                    .doOnNext( item -> System.out.println("Inserted item is : "+item))
                    .blockLast();
    }

    @Test
    public void getAllItems(){
        Flux<Item> itemFlux = repositories.findAll();

        StepVerifier.create(itemFlux)
                    .expectSubscription()
                    .expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    public void findById(){
        Mono<Item> itemMono = repositories.findById("abc123");

        StepVerifier.create(itemMono)
                    .expectSubscription()
                    .expectNextMatches( item -> item.getId().equalsIgnoreCase("abc123"))
                    .verifyComplete();
    }

    @Test
    public void findByDescription(){
        Mono<Item> itemMono = repositories.findByDescription("Samsung TV").log("findItemByDescription: ");

        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextMatches( item -> item.getPrice()==400.0)
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        Mono<Item> savedItem = repositories.save(new Item(null, "Google Home Mini", 690.0));

        StepVerifier.create(savedItem.log("saveItem : "))
                    .expectSubscription()
                    .expectNextMatches(item -> item.getId()!=null && item.getDescription().equalsIgnoreCase("Google Home Mini"))
                    .verifyComplete();

    }

    @Test
    public void updateItem(){
        double price = 786.0;
        Mono<Item> updatedItem = repositories.findByDescription("Apple Watch")
        .map( item -> {
            item.setPrice(price);
            return item;
        })
        .flatMap( repositories::save);

        StepVerifier.create(updatedItem.log("updateItem : "))
                    .expectSubscription()
                    .expectNextMatches(item -> item.getPrice()==786.0)
                    .verifyComplete();

    }

    @Test
    public void deleteItemById(){
        Mono<Void> mono = repositories.findById("abc123")
                    .map(Item::getId)
                    .flatMap(repositories::deleteById);

        StepVerifier.create(mono.log("deleteById : "))
                    .expectSubscription()
                    .expectNext()
                    .verifyComplete();

        StepVerifier.create(repositories.findAll())
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }

    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = repositories.findByDescription("LG TV") //Mono<Item>
                                            .flatMap( repositories::delete);

        StepVerifier.create(deletedItem.log("deleteItem : "))
                    .expectSubscription()
                    .verifyComplete();

        StepVerifier.create(repositories.findAll().log("The new item list : "))
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }


}

