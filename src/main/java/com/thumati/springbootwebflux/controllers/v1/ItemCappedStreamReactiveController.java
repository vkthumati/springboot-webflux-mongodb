package com.thumati.springbootwebflux.controllers.v1;

import com.thumati.springbootwebflux.model.ItemCapped;
import com.thumati.springbootwebflux.repositories.ItemCappedReactiveRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.thumati.springbootwebflux.util.ItemConstants.ITEMCAPPED_STREAM_END_POINT_V1;

@RestController
@Slf4j
public class ItemCappedStreamReactiveController {

    @Autowired
    private ItemCappedReactiveRepositories repositories;

    @GetMapping(value = ITEMCAPPED_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsCappedStream(){
        return repositories.findItemsBy();
    }
}
