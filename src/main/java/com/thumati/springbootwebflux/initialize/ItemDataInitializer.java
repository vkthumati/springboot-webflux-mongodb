package com.thumati.springbootwebflux.initialize;

import com.thumati.springbootwebflux.model.Item;
import com.thumati.springbootwebflux.model.ItemCapped;
import com.thumati.springbootwebflux.repositories.ItemCappedReactiveRepositories;
import com.thumati.springbootwebflux.repositories.ItemReactiveRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepositories repositories;

    @Autowired
    private ItemCappedReactiveRepositories cappedRepositories;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

    }

    private void initialDataSetUp(){
        repositories.deleteAll()
                    .thenMany(Flux.fromIterable(data()))
                    .flatMap(repositories::save)
                    .thenMany(repositories.findAll())
                    .subscribe( item -> System.out.println("Item inserted from CommandLineRunner : "+item));
    }

    private List<Item> data(){
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 300.0),
                new Item(null, "Apple Watch", 450.0),
                new Item(null, "Beats Headphones", 120.0),
                new Item("abc123", "Bosh Speakers", 660.0));
    }

    public void dataSetUpForCappedCollection(){
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(3))
             .map( i -> new ItemCapped(null, "Random ItemCapped"+i, (100.0+i)));

        cappedRepositories.insert(itemCappedFlux)
                            .subscribe(itemCapped -> log.info("Inserted Item is : "+itemCapped));
    }
}
