import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StepVerifierTest {

    @Test
    public void testFluxJust(){
        StepVerifier.create(Flux.just("food", "bar")).expectNext("food", "bar").verifyComplete();
    }

    @Test
    public void testFluxAssert(){
        StepVerifier.create(Flux.just("food", "bar"))
        .assertNext(val -> Assertions.assertEquals(val, "food"))
        .assertNext(val -> Assertions.assertEquals(val, "bar"))
        .verifyComplete();
    }

    @Test
    public void fluxElementsError(){
        StepVerifier.create(
                Flux.create(emitter ->{
                        int i = 2;
                        while(i>0){
                            emitter.next(i);
                            i--;
                        }
                        emitter.error(new IllegalAccessException());
        })
        ).expectNext(2, 1)
                .expectError(IllegalAccessException.class)
        .verify();
    }


    @Test
    public void fluxVirtualTime(){
        StepVerifier.withVirtualTime(() ->Flux.interval(Duration.ofMinutes(1)).take(1))
                .expectSubscription()
                //
                .thenAwait(Duration.ofMinutes(1))
                .expectNextCount(1)
                //.expectNoEvent(Duration.ofMinutes(4))
                .expectComplete()
                .verify();
    }

    @Test
    public void monoTime(){
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofHours(3)))
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(2))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void backpressure1(){
//        StepVerifier
//                .withVirtualTime(() -> Flux.fromIterable(Arrays.asList(1, 2, 3)).delayElements(Duration.ofMillis(1)))
//                .expectSubscription()
//                .thenAwait(Duration.ofMillis(5))
//                .thenRequest(2)
//                .expectNext(1, 2)
//                .thenRequest(3)
//                .expectNext(3)
//                .thenAwait(Duration.ofMillis(10))
//                .expectNextCount(2)
//                .expectComplete()
//        .verify();

        StepVerifier.create(Flux.fromIterable(Arrays.asList(1, 2, 3)))
                .thenRequest(2)
                .expectNext(1, 2)
                .thenRequest(1)
                .expectNext(3)
                .expectComplete()
                .verify();
    }
}
