import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OtherOperators {

    @Test
    public void zip(){
        Flux<Integer> f1 = Flux.fromIterable(Arrays.asList(1, 2, 3));
        Flux<Integer> f2 = Flux.fromIterable(Arrays.asList(11, 21, 31));
        Flux<Integer> f3 = Flux.fromIterable(Arrays.asList(11, 12, 13));

        Flux.zip(f1, f2, f3)
                .subscribe(System.out::println);


    }

    @Test
    public void fastestMono(){
        Mono<Integer> m1 = Mono.just(1).delayElement(Duration.ofMillis(3));
        Mono<Integer> m2 = Mono.just(100).delayElement(Duration.ofMillis(2));
        Mono<Integer> m3 = Mono.just(134).delayElement(Duration.ofMillis(5));

        Mono.first(m1, m2, m3)
                .subscribe(System.out::println);
    }

    @Test
    public void thenOperator(){
        Flux.fromIterable(Arrays.asList(1,2,3))
                .then()
                .and(Flux.fromIterable(Arrays.asList("a", "d")))
                .subscribe(System.out::println);
    }

    @Test
    public void errorTry() {
        Flux.error(new IllegalStateException())
                .then()
                .doOnError(e-> e.printStackTrace())
                .subscribe(System.out::println);
    }

}
