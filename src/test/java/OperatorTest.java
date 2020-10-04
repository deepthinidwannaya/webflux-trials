import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OperatorTest {

    @Test
    public void map(){
        Flux.range(1, 10)
                .map(i -> i * 10)
                .subscribe(System.out::println);
    }

    @Test
    public void flatMap(){
        Flux.range(1, 10)
                .flatMap(i -> Flux.range(i * 10, 2))
                .subscribe(System.out::println);
    }

    @Test
    public void flatMapMany(){
        Mono.just(10)
                .flatMapMany(integer -> Flux.range(integer, 2))
                .subscribe(System.out::println);
    }

    @Test
    public void concat() throws InterruptedException {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> fiveToTen = Flux.range(6,5)
                .delayElements(Duration.ofMillis(400));

        // oneToFive.concatWith(fiveToTen);

        Flux.concat(oneToFive, fiveToTen)
                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    public void merge() throws InterruptedException {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> fiveToTen = Flux.range(6,5)
                .delayElements(Duration.ofMillis(400));

        // oneToFive.concatWith(fiveToTen);

        Flux.merge(oneToFive, fiveToTen)
                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    public void zip() throws InterruptedException {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> fiveToTen = Flux.range(6,5)
                .delayElements(Duration.ofMillis(400));

        // oneToFive.concatWith(fiveToTen);

        Flux.zip(oneToFive, fiveToTen,
                 (i1, i2) -> i1 +", "+ i2)
                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

}
