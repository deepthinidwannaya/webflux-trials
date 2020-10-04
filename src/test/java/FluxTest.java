import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;


public class FluxTest {

    @Test
    void firstFlux(){
        Flux.just("A", "B", "C")
                .log()
                .subscribe();
    }

    @Test
    void fluxFromIterable(){
        Flux.fromIterable(Arrays.asList("A", "B", "C"))
                .log()
                .take(2)
                .subscribe();
    }

    @Test
    void fluxFromInterval() throws Exception{
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .take(2)
                .subscribe();
        Thread.sleep(2000);
    }

    @Test
    void fluxMap() throws Exception{
        Flux.just("A", "B", "C")
                .log()
                .map(i -> i.concat("test"))
                .log()
        .subscribe(System.out::println);
    }


    @Test
    void fluxFlatMap() throws Exception{
        Flux.just("A", "B", "C")
                .log()
                .flatMap(i -> Flux.just(i.concat("test")))
                //.log()
                .subscribe(System.out::println);
    }

    @Test
    void fluxMultiCalls() throws Exception {
        // From DB call
        Flux.just(1, 2, 3, 4, 5)
                // Make API call for each row from DB Call
                .flatMap(integer -> Flux.just(integer*2))
                // Make API call for each from previous result+db call
                .flatMap(integer -> Flux.just(Tuples.of(integer, integer * 100)))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(System.out::println);
    }

    @Test
    void fluxConcurrentFlatMap() throws Exception{
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .subscribeOn(Schedulers.elastic())
                .flatMap(
                        val -> {
                            /*try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            return Flux.just(val*10);
                        },
                        2
                )
                .subscribeOn(Schedulers.elastic())
                .subscribe();
        Thread.sleep(1000000);
    }

    @Test
    void fluxConcurrentElastic() throws Exception{
        Flux.<Double>generate(
                sink -> sink.next(Math.random())
        )
                .take(1000)
                .log()
                .flatMap(
                        val -> {
                            /*try {
                                Thread.sleep((long) Math.floor(Math.random() * 100));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            return Flux.just(val*10);
                        }
                )
                .subscribeOn(Schedulers.elastic())
                .log()
                .subscribe();
        Thread.sleep(1000);
    }

    @Test
    void fluxParallelBoundedElastic() throws Exception{
        Flux.<Double>generate(
                sink -> sink.next(Math.random())
        )
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .log()
                .flatMap(
                        val -> {
                            try {
                                Thread.sleep((long) Math.floor(Math.random() * 100));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return Flux.just(val*10);
                        }
                )
                .log()
                .subscribe();
        Thread.sleep(1000);
    }

    @Test
    public void whatIsItDoing() throws InterruptedException {
        // Customer notification service -> runs the flatmap publisher concurrently while detaching from subscriber's thread (subscribe runs on different thread)
        Flux.fromIterable(Arrays.asList(1,2,3,4,5,6,7,8,9,10))
                //.parallel(5)  // Parallel flux
                //.runOn(Schedulers.parallel())
                .flatMap(val -> Mono.<Integer>create(sink -> {
                    sink.success(val*10);
                }).subscribeOn(Schedulers.parallel()) // Run the individual elements on its own thread
                        .materialize()
                        .map(customerNotificationSignal -> {
                            System.out.println(Thread.currentThread().getName()+" MAP "+ customerNotificationSignal);
                            return customerNotificationSignal;
                        }), 2)
                .log()
                .subscribeOn(Schedulers.parallel()) // Run the publisher on a different thread.
                .doOnComplete(() ->
                        System.out.println(Thread.currentThread().getName()))
        .subscribe(
                val -> System.out.println(Thread.currentThread().getName()+" SUB "+ val)
        );

        Thread.sleep(1000);
    }

    @Test
    public void whatIsItDoingNow() throws InterruptedException {
        // Eventlog service ->
        Flux<Signal<Object>> flux = Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .flatMap(eventSource ->
                                 Mono.create(sink -> sink.success(eventSource*10))
                                         .materialize()
                                         .map(Optional::ofNullable)
                                         .filter(Optional::isPresent)
                                         .map(Optional::get))
                .subscribeOn(Schedulers.elastic())
                .log();

        //Thread.sleep(1000);
        flux.subscribe(
                vals -> {
                    System.out.println(Thread.currentThread().getName()+".."+vals.get());
                }
        );

        Thread.sleep(1000);

    }

    @Test
    void counter() throws InterruptedException {
        Duration period = Duration.of(100L, ChronoUnit.MILLIS);
        Flux.interval(period).take(10L).subscribe(System.out::println);
        Thread.sleep(1000);
    }

    @Test
    void syncIncreasingNums() {
        Flux.generate(
                () -> 1,
                (state, sink) -> {
                    sink.next(state);
                    return state+1;
                }
        )
                .take(5)
                .subscribe(System.out::println)
                ;
    }

    private static List<String> method(){
        System.out.println("method called");
        return Arrays.asList("fii", "fs");
    }

    @Test
    void fluxFromIterableMethod() {
        Flux.fromIterable(FluxTest.method())
                .log()
                .subscribe();
    }

    @Test
    void fluxWrapper() {
        Flux.create(fluxSink -> method().forEach(fluxSink::next))
                .log()
                .subscribe();
    }

}
