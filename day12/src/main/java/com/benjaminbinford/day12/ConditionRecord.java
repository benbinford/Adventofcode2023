package com.benjaminbinford.day12;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typemeta.funcj.tuples.Tuple2;

public record ConditionRecord(
        ArrayList<Tuple2<Condition, Integer>> conditions, ArrayList<Integer> runLengths, String rep) {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionRecord.class);

    public static ConditionRecord explode(String line) {
        var components = line.split(" ");
        var cond = components[0];
        var runLengths = components[1];
        cond = cond + "?" + cond + "?" + cond + "?" + cond + "?" + cond;
        runLengths = runLengths + "," + runLengths + "," + runLengths + "," + runLengths + "," + runLengths;
        return ofParts(cond, runLengths);
    }

    public static ConditionRecord of(String line) {
        var components = line.split(" ");
        return ofParts(components[0], components[1]);
    }

    public static ConditionRecord ofParts(String conditionsRep, String runLengthsRep) {

        var conditions = buildConditions(conditionsRep);

        var runLengths = getRunLengths(runLengthsRep);

        return new ConditionRecord(conditions, runLengths, conditionsRep + " " + runLengthsRep);

    }

    private static ArrayList<Integer> getRunLengths(String s) {
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt)
                .<ArrayList<Integer>>collect(ArrayList::new,
                        ArrayList::add, ArrayList::addAll);
    }

    private static record ConditionBuilder(int currentRun, HeadList<Tuple2<Condition, Integer>> conditions,
            HeadList<Integer> targets, String rep) {

    }

    private static ArrayList<Tuple2<Condition, Integer>> buildConditions(String s) {
        var conditions = new ArrayList<Condition>(s.chars().mapToObj(Condition::of).toList());

        // Run length encoding

        var compressedConditions = new ArrayList<Tuple2<Condition, Integer>>();

        var currentCondition = conditions.get(0);
        var currentRunLength = 1;

        for (int i = 1; i < conditions.size(); i++) {
            if (conditions.get(i) == currentCondition) {
                currentRunLength++;
            } else {
                compressedConditions.add(new Tuple2<>(currentCondition, currentRunLength));
                currentCondition = conditions.get(i);
                currentRunLength = 1;
            }
        }

        compressedConditions.add(new Tuple2<>(currentCondition, currentRunLength));

        return compressedConditions;
    }

    public sealed interface HeadList<T> permits EmptyList, NonEmptyList {
        public static <T> HeadList<T> of(List<T> list) {
            if (list.isEmpty()) {
                return new EmptyList<>();
            }
            return new NonEmptyList<>(list.get(0), list.subList(1, list.size()));
        }

        public static <T> HeadList<T> of(T t, List<T> list) {
            return new NonEmptyList<>(t, list);
        }

        public Stream<T> stream();

        public boolean isEmpty();

        public HeadList<T> next();

        public HeadList<T> replaceHead(T t);
    }

    public record EmptyList<T>() implements HeadList<T> {
        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }

        public boolean isEmpty() {
            return true;
        }

        public HeadList<T> next() {
            return this;
        }

        public HeadList<T> replaceHead(T t) {
            return this;
        }
    }

    public record NonEmptyList<T>(T head, List<T> tail) implements HeadList<T> {
        @Override
        public Stream<T> stream() {
            return Stream.concat(Stream.of(head), tail.stream());
        }

        public boolean isEmpty() {
            return false;
        }

        public HeadList<T> next() {
            return HeadList.of(tail);
        }

        public HeadList<T> replaceHead(T t) {
            return HeadList.of(t, tail);
        }
    }

    public static long findExplodedArrangements(String line) {
        var components = line.split(" ");

        String crSa = components[0] + "?";
        var conditionsA = buildConditions(crSa);

        var runLengths = getRunLengths(components[1]);
        var crA = new ConditionRecord(conditionsA, runLengths, crSa + " " + components[1]).findArrangements();

        var actualResult = explode(line).findArrangements();

        if (components[0].charAt(0) == '#') {
            crA = ConditionRecord.of(line).findArrangements();
            var result = crA * crA * crA * crA * crA;

            // LOGGER.info("{} {} {}", line, line, crA);
            // LOGGER.info("{} {} (b invalid)", line, result);
            if (result != actualResult) {
                LOGGER.info("{} {} {}", line, result, actualResult);
            }
            return actualResult;
        } else if (components[0].charAt(components[0].length() - 1) == '#') {
            var result = crA * crA * crA * crA * crA;

            // LOGGER.info("{} {} {}", line, crSa, crA);
            // LOGGER.info("{} {} (b invalid)", line, result);
            if (result != actualResult) {
                LOGGER.info("{} {} {}", line, result, actualResult);
            }
            return actualResult;
        } else {

            String crSb = "?" + components[0];
            var conditionsB = buildConditions(crSb);

            var crB = new ConditionRecord(conditionsB, runLengths, crSa + " " + components[1]).findArrangements();

            var upper = Math.max(crA, crB);
            var lower = Math.min(crA, crB);

            var result = lower * upper * upper * upper * upper;

            // LOGGER.info("{} {} {}", line, crSa, crA);
            // LOGGER.info("{} {} {}", line, crSb, crB);
            // LOGGER.info("{} {} ", line, result);
            if (result != actualResult) {
                LOGGER.info("{} {} {}", line, result, actualResult);
            }
            return actualResult;
        }
    }

    public long findArrangements() {
        var builders = new ArrayDeque<ConditionBuilder>();
        long arrangementCount = 0l;
        builders.push(new ConditionBuilder(0, HeadList.of(conditions),
                HeadList.of(runLengths), ""));

        while (!builders.isEmpty()) {
            var builder = builders.pop();

            if (builder instanceof

            ConditionBuilder(var currentRun, var conditions, var targets, var rep)) {

                var noMoreDamagedConditions = emptyOrUnknown(conditions.stream());

                if (noMoreDamagedConditions && targets.isEmpty()) {
                    // LOGGER.info("{} {}", ConditionRecord.this.rep, rep);
                    arrangementCount++;
                } else if (noMoreDamagedConditions
                        && targets instanceof

                        NonEmptyList(var currentTarget, var remainingTargets)
                        && currentRun == currentTarget
                        && remainingTargets.isEmpty()) {

                    // LOGGER.info("{} {}", ConditionRecord.this.rep, rep);
                    arrangementCount++;

                }

                else if (conditions instanceof

                NonEmptyList(var c, var remainingConditions)
                        && targets instanceof

                        NonEmptyList(var targetRun, var remainingTargets)
                        && currentRun <= targetRun) {

                    switch (c.get1()) {
                        case OPERATIONAL:

                            HeadList<Integer> nextTargets;
                            if (currentRun == targetRun) {
                                nextTargets = targets.next();
                                currentRun = 0;
                            } else {
                                nextTargets = targets;
                            }

                            if (currentRun == 0) {
                                builders.push(new ConditionBuilder(0, conditions.next(),
                                        nextTargets, rep + c.get1().toString().repeat(c.get2())));
                            }
                            break;
                        case DAMAGED:
                            currentRun += c.get2();

                            builders.push(new ConditionBuilder(currentRun, conditions.next(),
                                    targets, rep + c.get1().toString().repeat(c.get2())));

                            break;
                        case UNKNOWN:
                            if (c.get2() == 0) {
                                builders.push(new ConditionBuilder(currentRun, conditions.next(),
                                        targets, rep));
                                break;
                            }

                            var remainingUnknown = currentRun + c.get2() - targetRun;
                            // to finish off the sequence completely within the unknowns,
                            // we need the targetRun + a remainingUnknown to be operational
                            // otherwise we let all the unknowns be damaged and the next loop
                            // will handle the case of the next condition be operational or damaged

                            if (currentRun == 0) {
                                builders.push(
                                        new ConditionBuilder(0,
                                                conditions.replaceHead(Tuple2.of(Condition.UNKNOWN, c.get2() - 1)),
                                                targets, rep + "."));
                            }

                            if (currentRun == targetRun) {
                                // consume the current target and add a "." to close out the run

                                builders.push(
                                        new ConditionBuilder(0,
                                                conditions.replaceHead(Tuple2.of(Condition.UNKNOWN, c.get2() - 1)),
                                                targets.next(), rep + "."));
                            } else {
                                if (remainingUnknown <= 0) {
                                    builders.push(new ConditionBuilder(remainingUnknown + targetRun, conditions.next(),
                                            targets, rep + "#".repeat(c.get2())));

                                } else {
                                    // consume the current target

                                    builders.push(
                                            new ConditionBuilder(0,
                                                    conditions.replaceHead(
                                                            Tuple2.of(Condition.UNKNOWN, remainingUnknown - 1)),
                                                    targets.next(),
                                                    rep + "#".repeat(c.get2() - remainingUnknown) + "."));

                                }
                            }
                            break;
                    }

                }
            }

        }

        return arrangementCount;

    }

    private boolean emptyOrUnknown(Stream<Tuple2<Condition, Integer>> conditions) {
        return conditions.allMatch(t -> t.get1() == Condition.UNKNOWN || t.get1() == Condition.OPERATIONAL);

    }

}
