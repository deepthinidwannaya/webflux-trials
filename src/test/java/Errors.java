import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Errors {

    @Test
    public void monoOnErrorReturn(){
        Mono.create(sink-> {
            sink.error(new IllegalStateException());
        })
        .onErrorReturn(100)
        .subscribe(System.out::println);
    }

    @Test
    public void fluxOnError() {
        Flux.create(sink -> {
            sink.next(1);
            sink.next(2);
            sink.error(new IllegalStateException());
            sink.next(4);
        })
                .onErrorReturn(20)
                .subscribe(System.out::println);
    }

    @Test
    public void fluxOnErrorResume() {
        Flux.create(sink -> {
            sink.next(1);
            sink.next(2);
            sink.error(new IllegalStateException());
            sink.next(4);
        })
                .onErrorResume(fallback ->{
                    return Flux.just(100);
                })
                .subscribe(System.out::println);
    }





}
