package com.thumati.springbootwebflux.controllers.v1;

import com.thumati.springbootwebflux.model.Item;
import com.thumati.springbootwebflux.repositories.ItemReactiveRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.thumati.springbootwebflux.util.ItemConstants.ITEM_END_POINT_V1;

@RestController
@Slf4j
public class ItemReactiveController {

    @Autowired
    private ItemReactiveRepositories repositories;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){
        return repositories.findAll();
    }

    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){
        return repositories.findById(id)
                            .map( item -> new ResponseEntity<>(item, HttpStatus.OK))
                            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return repositories.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteById(@PathVariable String id){
        return repositories.deleteById(id);
    }

    /**
     * id and item to be updated in the req = path variable and request body - completed
     * get the item from database using the id from request path variable- completed
     * update the item from database using the item from request body - completed
     * save the item into database - completed
     * return the saved item - completed
     * @param id
     * @param item
     * @return
     */
    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item itemFromRequest){
         return repositories.findById(id)
                            .flatMap( itemFromDatabase -> {
                                itemFromDatabase.setPrice(itemFromRequest.getPrice());
                                itemFromDatabase.setDescription(itemFromRequest.getDescription());
                                return repositories.save(itemFromDatabase); })
                            .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> runtimeException(){
        return repositories.findAll()
                            .concatWith(Mono.error(new RuntimeException("RuntimeException occurred.")));
    }

    /*@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
        log.error("Exception caught in handleRuntimeException : {}",ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }*/
}
