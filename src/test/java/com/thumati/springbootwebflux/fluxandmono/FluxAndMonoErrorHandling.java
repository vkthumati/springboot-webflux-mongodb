package com.thumati.springbootwebflux.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorHandling {

    @Test
    public void fluxErrorThrow(){
        Flux<String> flux = Flux.just("A", "B", "C")
                                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                                .concatWith(Flux.just("D"))
                                .log();

        StepVerifier.create(flux)
                    .expectSubscription()
                    .expectNext("A","B","C")
                    .expectErrorMessage("Error Occurred")
                    .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorResume(){
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume( e -> {
                    System.out.println("Exception is : "+e);
                    return Flux.just("default", "default1");
                })
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorReturn(){
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default")
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap(){
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new MyCustomException("My error message"))
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A","B","C")
                .expectError(MyCustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_WithRetry(){
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Error Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap( e -> new MyCustomException("My error message"))
                .retry(2)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(MyCustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetryBackOff(){
        Flux<String> flux = Flux.just("A", "B", "C")
                                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                                .concatWith(Flux.just("D"))
                                .onErrorMap( e -> new MyCustomException(e.getMessage()))
                                .retryBackoff(2, Duration.ofSeconds(5))
                                .log();

        StepVerifier.create(flux)
                    .expectSubscription()
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectNext("A", "B", "C")
                    .expectError(IllegalStateException.class)
                    .verify();

    }

}
