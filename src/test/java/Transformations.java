import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Transformations {

    @Test
    public void toUpperFluxSync(){
        Flux.fromIterable(Arrays.asList("abc", "def", "ghi"))
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    @Test
    public void toUpperFluxAsync(){
        Flux.fromIterable(Arrays.asList("a b c", "d e f", "g h i"))
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(val ->
                        Flux.fromArray(val.toUpperCase().split(" ")))
                .subscribe(val -> System.out.println(Thread.currentThread().getName()+" "+val));
    }

    @Test
    public void mergeFluxWithDelay(){
        // Order changes
        Flux.fromIterable(Arrays.asList("abc", "def", "dgi")).delaySequence(Duration.ofMillis(1))
        .mergeWith(Flux.fromIterable(Arrays.asList("123", "E23", "sfdg")))
        .subscribe(System.out::println);
    }

    @Test
    public void concatFluxWithDelay(){
        // Preserves order
        Flux.fromIterable(Arrays.asList("abc", "def", "dgi")).delaySequence(Duration.ofMillis(1))
                .concatWith(Flux.fromIterable(Arrays.asList("123", "E23", "sfdg")))
                .subscribe(System.out::println);
    }


}
