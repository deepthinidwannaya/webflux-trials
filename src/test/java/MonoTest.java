import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
public class MonoTest {

    @Test
    void justMono(){
        Mono.just("A")
        .log()
        .subscribe();
    }

    @Test
    void justMonoWithConsumer(){
        Mono.just("A")
                .log()
                .subscribe(x -> System.out.println(x));
    }

    @Test
    void justMonoWithDoOn(){
        Mono.just("A")
                .log()
                .doOnSubscribe(subs -> System.out.println("Subscribed: "+ subs))
                .doOnRequest(req -> System.out.println("Request: "+ req))
                .doOnSuccess(complete -> System.out.println("Complete: "+ complete))
                .subscribe(System.out::println);
    }

    @Test
    void emptyMono(){
        Mono.empty()
                .log()
                .subscribe(System.out::println);
    }

    @Test
    void emptyCompleteConsumerMono(){
        Mono.empty()
                .log()
                .subscribe(System.out::println,
                           null,
                           ()-> System.out.println("Done"));
    }

    @Test
    void errorRTEMono(){
        Mono.error(new RuntimeException())
                .log()
                .subscribe();
    }

    @Test
    void errorMonoConsumer(){
        Mono.error(new RuntimeException())
                .log()
                .subscribe(System.out::println,
                           e-> System.out.println("Error: "+ e));
    }

    @Test
    void monoDoTwoThings(){
        //Mono.create();
    }

    @Test
    void monoSubReturn(){
        AtomicInteger value = new AtomicInteger();
        Mono.just(1).subscribe(
                num -> {
                    value.set(num);

                }
        );
        System.out.println(value);
    }

    @Test
    void monoNever(){
        Mono.never().log().subscribe(System.out::println);
    }
}
