package com.thumati.springbootwebflux.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoSwitchIfEmptyTest {

    @Test
    public void monoSwitchIfEmpty(){
        //Mono<Integer> monoInteger = Mono.justOrEmpty(Optional.<Integer>empty());
        //Mono.empty();

        Mono<Integer> integerMono = Mono.<Integer>empty().switchIfEmpty(
                                                                Mono.defer( () -> {
                                                                    System.out.println("Mono defer :::::::::::::::");
                                                                    return Mono.just(new Integer(100));
                                                                })
                                                            );

        //System.out.println("Value : "+integerMono.toString());
        //System.out.println("Value : "+integerMono.subscribe( integer -> System.out.println("Integer : "+integer.intValue())));
       StepVerifier.create(integerMono)
                    .expectSubscription()
                    .expectNextCount(1)
                    .verifyComplete();

    }
}
