package com.thumati.springbootwebflux.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                                        .log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] names = {"adam", "anna", "jack", "jenny"};

        Flux<String> namesFlux = Flux.fromArray(names)
                                        .log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream(){

        Flux<String> namesFlux = Flux.fromStream(names.stream())
                                        .log();
        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty(){
        Mono<String> mono = Mono.justOrEmpty(null); //Mono.empty();

        StepVerifier.create(mono.log())
                    .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){
        Mono<String> mono = Mono.fromSupplier( () -> "Hello")
                                .log();

        StepVerifier.create(mono)
                    .expectNext("Hello")
                    .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> numbersFlux = Flux.range(1, 5)
                                    .log();
        StepVerifier.create(numbersFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
