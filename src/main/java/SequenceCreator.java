import java.util.List;
import java.util.function.Consumer;

import reactor.core.publisher.Flux;

public class SequenceCreator {
    public Consumer<List<Integer>> consumer;

    public Flux<Integer> createNumberSequence() {
        return Flux.create(sink ->
                                   SequenceCreator.this.consumer = items -> items.forEach(sink::next));
    }

    public static void main(String[] args) {
        SequenceCreator sequenceCreator = new SequenceCreator();
        //List<Integer> sequence1 = sequenceCreator.generateFibonacciWithTuples().take(3).collectList().block();
        //List<Integer> sequence2 = sequenceCreator.generateFibonacciWithTuples().take(4).collectList().block();

        // other statements described below
    }

}
