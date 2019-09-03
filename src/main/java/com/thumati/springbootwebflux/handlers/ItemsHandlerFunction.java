package com.thumati.springbootwebflux.handlers;

import com.thumati.springbootwebflux.model.Item;
import com.thumati.springbootwebflux.model.ItemCapped;
import com.thumati.springbootwebflux.repositories.ItemCappedReactiveRepositories;
import com.thumati.springbootwebflux.repositories.ItemReactiveRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandlerFunction {

    @Autowired
    private ItemReactiveRepositories repositories;

    @Autowired
    private ItemCappedReactiveRepositories streamRepositories;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){
        return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .body(repositories.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = repositories.findById(id);

        return itemMono.flatMap(item -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                                .body(fromObject(item)))
                                                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);

        return itemMono.flatMap( item -> ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                                            .body(repositories.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> voidMono = repositories.deleteById(id);

        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .body(voidMono, Void.class);
    }

    public Mono<ServerResponse> updateItemById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                        .flatMap( itemFromRequest -> {
                            Mono<Item> savedItem = repositories.findById(id)
                                    .flatMap(itemFromDatabase -> {
                                        itemFromDatabase.setDescription(itemFromRequest.getDescription());
                                        itemFromDatabase.setPrice(itemFromRequest.getPrice());
                                        return repositories.save(itemFromDatabase);
                                    });
                            return savedItem;
                        });

        return updatedItem.flatMap( item ->
                                            ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                                            .body(fromObject(item)))
                            .switchIfEmpty(notFound);


    }

    public Mono<ServerResponse> handleError(ServerRequest request){
        throw new RuntimeException("RuntimeException Occurred.");
    }

    public Mono<ServerResponse> getItemCappedStream(ServerRequest serverRequest){
        return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_STREAM_JSON)
                            .body(streamRepositories.findItemsBy(), ItemCapped.class);
    }
}
