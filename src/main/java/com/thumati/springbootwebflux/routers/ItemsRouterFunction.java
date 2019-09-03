package com.thumati.springbootwebflux.routers;

import com.thumati.springbootwebflux.handlers.ItemsHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.thumati.springbootwebflux.util.ItemConstants.ITEMCAPPED_FUNCTIONAL_STREAM_END_POINT_V1;
import static com.thumati.springbootwebflux.util.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouterFunction {

    @Bean
    public RouterFunction<ServerResponse> itemsRouter(ItemsHandlerFunction handlerFunction){
        return RouterFunctions.route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON_UTF8)), handlerFunction::getAllItems)
                                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON_UTF8)), handlerFunction::getItemById)
                                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON_UTF8)), handlerFunction::createItem)
                                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON_UTF8)), handlerFunction::deleteById)
                                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON_UTF8)), handlerFunction::updateItemById);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRouter(ItemsHandlerFunction handlerFunction){
        return RouterFunctions.route(GET("/functional/runtimeException").and(accept(MediaType.APPLICATION_JSON_UTF8)), handlerFunction::handleError);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamRoute(ItemsHandlerFunction handlerFunction){
        return RouterFunctions.route(GET(ITEMCAPPED_FUNCTIONAL_STREAM_END_POINT_V1).and(accept(MediaType.APPLICATION_STREAM_JSON)), handlerFunction::getItemCappedStream);
    }
}
