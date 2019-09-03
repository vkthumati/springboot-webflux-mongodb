package com.thumati.springbootwebflux.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxVirtualTimeTest {

    @Test
    public void withoutVirtualTimeTest(){
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                                    .take(3);
        StepVerifier.create(longFlux.log())
                    .expectSubscription()
                    .expectNext(0l,1l,2l)
                    .verifyComplete();
    }

    @Test
    public void withVirtualTimeTest(){
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                                    .take(3)
                                    .log();

        StepVerifier.withVirtualTime( () -> longFlux)
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(3))
                    .expectNext(0l,1l,2l)
                    .verifyComplete();
    }

    @Test
    public void combineUsingConcat_withDelay(){
        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> concat = Flux.concat(flux1, flux2);

        StepVerifier.withVirtualTime( () -> concat.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
}
