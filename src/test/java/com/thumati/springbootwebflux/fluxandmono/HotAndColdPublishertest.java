package com.thumati.springbootwebflux.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HotAndColdPublishertest {

    @Test
    public void coldPublisherTest() throws InterruptedException{
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                                        .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe( s -> System.out.println("Subscriber 1 : "+s)); //emits the values from beginning

        TimeUnit.SECONDS.sleep(4);

        stringFlux.subscribe( s -> System.out.println("Subscriber 2 : "+s)); //emits the values from beginning

        TimeUnit.SECONDS.sleep(7);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException{
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe( s -> System.out.println("Subscriber 1 : "+s)); //emits the values from beginning

        TimeUnit.SECONDS.sleep(4);

        connectableFlux.subscribe( s -> System.out.println("Subscriber 2 : "+s)); //doesn't emit the values from beginning

        TimeUnit.SECONDS.sleep(6);
    }
}
