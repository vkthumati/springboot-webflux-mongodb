package com.thumati.springbootwebflux.repositories;

import com.thumati.springbootwebflux.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepositories extends ReactiveMongoRepository<Item, String> {

    Mono<Item> findByDescription(String description);
}
