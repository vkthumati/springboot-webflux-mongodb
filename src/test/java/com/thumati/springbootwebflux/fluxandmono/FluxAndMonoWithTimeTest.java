package com.thumati.springbootwebflux.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException{
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)) //Starts from 0 -> ......
                                        .log();
        //StepVerifier.create(infiniteFlux);
        infiniteFlux.subscribe( e -> System.out.println("Value is : "+e));

        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void infiniteSequenceTest() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100)) //Flux<Long>
                                    .take(3)
                                    .log();
        StepVerifier.create(finiteFlux)
                    .expectSubscription()
                    .expectNext(0L,1L,2L)
                    .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100)) //Flux<Long>
                                    .map( e -> new Integer(e.intValue())) //Flux<Integer>
                                    .take(3)
                                    .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_withDelay() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100)) //Flux<Long>
                .delayElements(Duration.ofSeconds(1))
                .map( e -> new Integer(e.intValue())) //Flux<Integer>
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}
