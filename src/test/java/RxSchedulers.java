import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class RxSchedulers {

    @Test
    public void whichThread() throws InterruptedException {
        // Subscribed thread
        Mono<String> mono = Mono.just("hello");

        Thread t = new Thread(() ->
                                      mono
                                              .map(msg -> msg + " thread")
                                              .subscribe(consumer ->
                                                                 System.out.println(
                                                                         Thread.currentThread().getName() + ":"
                                                                         + consumer)));

        t.start();
        t.join();
    }

    @Test
    public void schedulersImmediate() throws InterruptedException {
        // Subscribed thread, same as prev test
        Mono<String> mono = Mono.just("hello");

        Thread t = new Thread(() ->
                                      mono
                                              .map(msg -> msg + " thread")
                                              .subscribeOn(Schedulers.immediate())
                                              .subscribe(consumer ->
                                                                 System.out.println(
                                                                         Thread.currentThread().getName() + ":"
                                                                         + consumer)));

        t.start();
        t.join();
    }

    @Test
    public void schedulersElastic() throws InterruptedException {
        //
        Mono<String> mono = Mono.just("hello");

        Thread t = new Thread(() ->
                                      mono
                                              .map(msg -> msg + " thread")
                                              .subscribeOn(Schedulers.elastic())
                                              .subscribe(consumer ->
                                                                 System.out.println(
                                                                         Thread.currentThread().getName() + ":"
                                                                         + consumer)));

        t.start();
        t.join();
    }

    @Test
    public void schedulersSingle() throws InterruptedException {
        Mono<String> mono = Mono.just("hello")
                .map(msg -> msg + " thread")
                ;

        Thread t1 = new Thread(() ->
                                      mono
                                              .subscribeOn(Schedulers.single())
                                              .subscribe(consumer ->
                                                                 System.out.println(
                                                                         Thread.currentThread().getName() + ":"
                                                                         + consumer)));

        Thread t2 = new Thread(() ->
                                       mono
                                               .subscribeOn(Schedulers.single())
                                               .subscribe(consumer ->
                                                                  System.out.println(
                                                                          Thread.currentThread().getName() + ":"
                                                                          + consumer)));

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @Test
    public void schedulersNewSingle() throws InterruptedException {
        Mono<String> mono = Mono.just("hello")
                .map(msg -> msg + " thread")
                ;

        Thread t1 = new Thread(() ->
                                       mono
                                               .subscribeOn(Schedulers.newSingle("asingle"))
                                               .subscribe(consumer ->
                                                                  System.out.println(
                                                                          Thread.currentThread().getName() + ":"
                                                                          + consumer)));

        Thread t2 = new Thread(() ->
                                       mono
                                               .subscribeOn(Schedulers.newSingle("asingle"))
                                               .subscribe(consumer ->
                                                                  System.out.println(
                                                                          Thread.currentThread().getName() + ":"
                                                                          + consumer)));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }
}
