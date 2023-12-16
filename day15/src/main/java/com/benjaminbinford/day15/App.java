package com.benjaminbinford.day15;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    private static record Operation(String label, int focalLength) {
        public String toString() {
            return String.format("%s=%d", label, focalLength);
        }
    }

    List<String> initSequence;

    List<ArrayList<Operation>> sequence;

    public App(String input) {
        initSequence = List.of(input.split(","));
        sequence = Stream.generate(ArrayList<Operation>::new).limit(256).toList();

        initSequence.forEach(this::applyOperation);
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day15/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(app.sumHashes());
        IO.answer(app.sumFocalLengths());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }

    public long sumFocalLengths() {
        long result = 0l;
        for (var i = 0; i < sequence.size(); i++) {
            result += sumBucket(sequence.get(i)) * (i + 1);
        }
        return result;
    }

    private static long sumBucket(ArrayList<Operation> bucket) {
        return IntStream.range(0, bucket.size()).mapToLong(i -> bucket.get(i).focalLength() * (i + 1)).sum();
    }

    public static int hash(String str) {
        int currentValue = 0;
        for (int i = 0; i < str.length(); i++) {
            currentValue += str.charAt(i);
            currentValue *= 17;
            currentValue %= 256;
        }
        return currentValue;
    }

    public long sumHashes() {
        return initSequence.parallelStream().mapToLong(App::hash).sum();
    }

    private void applyOperation(String operation) {
        if (operation.endsWith("-")) {
            applyRemove(operation.substring(0, operation.length() - 1));
        } else {
            var components = operation.split("=");
            applyAdd(components[0], Integer.parseInt(components[1]));

        }
    }

    private void applyAdd(String label, int focalLength) {
        var operation = new Operation(label, focalLength);
        var it = sequence.get(hash(label)).listIterator();
        while (it.hasNext()) {
            if (it.next().label.equals(label)) {
                it.remove();
                it.add(operation);
                return;
            }
        }
        it.add(operation);
    }

    private void applyRemove(String label) {
        var it = sequence.get(hash(label)).iterator();
        while (it.hasNext()) {
            if (it.next().label.equals(label)) {
                it.remove();
                return;
            }
        }
    }

    @Override
    public String toString() {
        return sequence.stream().map(Object::toString).collect(Collectors.joining("\n\n"));
    }
}
