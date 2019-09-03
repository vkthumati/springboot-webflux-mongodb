package com.thumati.springbootwebflux.handlers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FluxAndMonoHandlerFunction {

    public Mono<ServerResponse> flux(ServerRequest serverRequest){
        return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                                    Flux.just(1,2,3,4,5,6,7,8,9,10)
                                        .log(),
                                        Integer.class
                            );
    }

    public Mono<ServerResponse> mono(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(122)
                                .log(),
                        Integer.class
                );
    }
}
