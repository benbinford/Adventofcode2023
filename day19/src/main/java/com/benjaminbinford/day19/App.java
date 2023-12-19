package com.benjaminbinford.day19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.benjaminbinford.utils.AdventException;
import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    static final long MAX_COMBOS = 256000000000000l;

    record Range(int min, int max) {
        public static Range of(int min, int max) {
            return new Range(min, max);
        }

        public int size() {
            return max - min + 1;
        }

    }

    record PartRange(Map<String, Range> ranges) {

    }

    List<Map<String, Integer>> parts = new ArrayList<>();
    Map<String, Workflow> workflows = new HashMap<>();

    enum Op {

        GT {
            @Override
            public boolean apply(int a, int b) {
                return a > b;
            }

            @Override
            public Range[] split(Range myRange, int value) {

                var range1 = myRange;
                var rangeRest = myRange;

                // 0 1 2 3 4 5 6 7 8 9 10
                // o < >
                if (value < myRange.min) { // (5) 5..=10
                    rangeRest = null;
                } else if (value >= myRange.min && value < myRange.max) {

                    range1 = Range.of(value + 1, myRange.max);
                    rangeRest = Range.of(myRange.min, value);

                } else if (value >= myRange.max) {
                    range1 = null;
                } else {
                    assert (false);
                }

                return new Range[] { range1, rangeRest };
            }
        },
        LT {
            @Override
            public boolean apply(int a, int b) {
                return a < b;
            }

            @Override
            protected Range[] split(Range myRange, int value) {
                var range1 = myRange;
                var rangeRest = myRange;

                // 0 1 2 3 4 5 6 7 8 9 10
                // o < >
                if (value > myRange.max) {
                    rangeRest = null;
                } else if (value > myRange.min && value <= myRange.max) {

                    range1 = Range.of(myRange.min, value - 1);
                    rangeRest = Range.of(value, myRange.max);

                } else if (value <= myRange.min) {
                    range1 = null;
                } else {
                    assert (false);
                }

                return new Range[] { range1, rangeRest };
            }

        };

        public abstract boolean apply(int a, int b);

        protected abstract Range[] split(Range r, int value);

        public final PartRange[] split(PartRange ruleRange, String prop, int value) {

            final var myRange = ruleRange.ranges.get(prop);

            final var myRanges = split(myRange, value);

            PartRange range1 = null;
            PartRange rangeRest = null;

            if (myRanges[0] != null) {
                range1 = new PartRange(new HashMap<>(ruleRange.ranges));
                range1.ranges.put(prop, myRanges[0]);
            }

            if (myRanges[1] != null) {
                rangeRest = new PartRange(new HashMap<>(ruleRange.ranges));
                rangeRest.ranges.put(prop, myRanges[1]);
            }

            return new PartRange[] { range1, rangeRest };
        }

        @Override
        public String toString() {
            switch (this) {

                case GT:
                    return ">";
                case LT:
                    return "<";
                default:
                    throw new IllegalArgumentException("Invalid operator");
            }
        }

        public static Op of(String op) {
            switch (op) {

                case ">":
                    return GT;
                case "<":
                    return LT;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + op);
            }
        }

    }

    public interface Rule {
        public Optional<String> apply(Map<String, Integer> part);

        public String dest();

        public static Rule of(String rule) {
            final var parts = rule.split(":");
            if (parts.length == 1) {
                return new ConstantRule(parts[0]);
            } else if (parts.length == 2) {
                return DynamicRule.of(parts[0], parts[1]);
            } else {
                throw new IllegalArgumentException("Invalid rule: " + rule);
            }
        }

        public PartRange[] split(PartRange ruleRange);

        public default long combinations(PartRange ruleRange, Map<String, Workflow> workflows) {
            if (dest().equals("R")) {
                return 0l;
            } else if (dest().equals("A")) {
                return ruleRange.ranges.values().stream().mapToLong(Range::size).reduce(1l, (a, b) -> a * b);
            } else {
                return workflows.get(dest()).combinations(ruleRange, workflows);
            }
        }

    }

    record ConstantRule(String dest) implements Rule {
        public Optional<String> apply(Map<String, Integer> part) {
            return Optional.of(dest);
        }

        public String toString() {
            return dest;
        }

        @Override
        public PartRange[] split(PartRange ruleRange) {
            return new PartRange[] { ruleRange, null };
        }

    }

    record DynamicRule(String prop, Op op, int value, String dest) implements Rule {

        public static DynamicRule of(String rep, String dest) {
            return new DynamicRule(rep.substring(0, 1), Op.of(rep.substring(1, 2)), Integer.parseInt(rep.substring(2)),
                    dest);
        }

        public Optional<String> apply(Map<String, Integer> part) {
            return op.apply(part.get(prop), value) ? Optional.of(dest) : Optional.empty();
        }

        public String toString() {
            return String.format("%s%s%d:%s", prop, op, value, dest);
        }

        @Override
        public PartRange[] split(PartRange ruleRange) {
            return op.split(ruleRange, prop, value);
        }

    }

    class Workflow {
        Optional<String> constant;
        List<Rule> rules;

        public Workflow(List<Rule> rules) {
            this.rules = new ArrayList<>(rules);
            this.constant = Optional.empty();
            foldConstants(Collections.emptyMap());
        }

        public boolean foldConstants(Map<String, Workflow> cws) {

            boolean changed = false;

            for (var i = 0; i < rules.size(); i++) {
                final var rule = rules.get(i);
                if (cws.containsKey(rule.dest())) {
                    final var cw = cws.get(rule.dest());
                    if (rule instanceof ConstantRule) {
                        rules.set(i, new ConstantRule(cw.getConstant().orElseThrow()));
                    } else if (rule instanceof

                    DynamicRule(String prop, Op op, int value, String dest)) {
                        rules.set(i, new DynamicRule(prop, op, value, cw.getConstant().orElseThrow()));
                    }
                    changed = true;
                }
            }

            String dest = rules.get(0).dest();
            if (rules.stream().allMatch(r -> r.dest().equals(dest))) {
                constant = Optional.of(dest);
                changed = true;
            }
            return changed;
        }

        public Optional<String> getConstant() {
            return constant;
        }

        public List<Rule> getSubrules() {
            return rules;
        }

        public String apply(Map<String, Integer> part) {
            if (constant.isPresent())
                return constant.get();

            return rules.stream().map(r -> r.apply(part)).filter(Optional::isPresent).map(Optional::get).findFirst()
                    .orElseThrow();
        }

        @Override
        public String toString() {
            return String.format("{%s}", rules.stream().map(Rule::toString).reduce((a, b) -> a + "," + b).orElse(""));
        }

        public long combinations(PartRange initialRange, Map<String, Workflow> workflows) {

            long sum = 0;
            var ruleRange = initialRange;
            for (var rule : rules) {
                if (ruleRange == null) {
                    break;
                }
                var newRanges = rule.split(ruleRange);
                ruleRange = newRanges[0];
                if (ruleRange != null) {
                    sum += rule.combinations(ruleRange, workflows);
                }
                ruleRange = newRanges[1];
            }
            if (sum > MAX_COMBOS) {
                throw new AdventException("Too many combinations");
            }
            return sum;
        }

    }

    public App(String input) {
        final var lines = input.split("\n\n");
        final var wfs = lines[0].split("\n");
        final var messages = lines[1].split("\n");

        for (var wf : wfs) {
            final var defParts = wf.split("[{}]");
            final var id = defParts[0];
            final var rules = Arrays.stream(defParts[1].split(",")).map(Rule::of).toList();
            workflows.put(id, new Workflow(rules));
        }

        for (var message : messages) {
            final var part = new HashMap<String, Integer>();
            final var props = message.split("[{},]");
            for (var prop : props) {
                if (prop.isBlank())
                    continue;
                part.put(prop.substring(0, 1), Integer.parseInt(prop.substring(2)));
            }

            parts.add(part);
        }

    }

    public int partTotal(Map<String, Integer> part) {
        String dest = "in";
        int i = 0;
        while (true) {
            final var wf = workflows.get(dest);
            dest = wf.apply(part);
            if (dest.equals("A")) {
                return part.values().stream().mapToInt(Integer::intValue).sum();
            } else if (dest.equals("R")) {
                return 0;
            } else if (i > 1_000_000) {
                throw new AdventException("Infinite loop detected");
            }

            i++;
            dest = wf.apply(part);
        }
    }

    public List<Map<String, Integer>> getParts() {
        return parts;
    }

    public Map<String, Workflow> getWorkflows() {
        return workflows;
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day19/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);
        app.optimizeConstants();
        IO.answer(app.partTotals());
        IO.answer(app.partCombinations());
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    public long partTotals() {

        return parts.stream().mapToLong(this::partTotal).sum();

    }

    public void optimizeConstants() {
        Map<String, Workflow> cws = new HashMap<>();

        workflows.entrySet().stream().filter(e -> e.getValue().getConstant().isPresent())
                .forEach(e -> cws.put(e.getKey(), e.getValue()));

        cws.keySet().forEach(workflows::remove);

        boolean changed = false;

        do {
            changed = false;
            var it = workflows.entrySet().iterator();
            while (it.hasNext()) {
                final var wfe = it.next();
                final var wf = wfe.getValue();
                final var wfid = wfe.getKey();

                if (wf.foldConstants(cws)) {
                    changed = true;
                    if (wf.constant.isPresent()) {
                        it.remove();
                        cws.put(wfid, wf);
                    }
                }

            }
        } while (changed);
    }

    public long partCombinations() {

        var initialRange = new PartRange(Map.of("x", Range.of(1, 4000), "m", Range.of(1, 4000), "a", Range.of(1, 4000),
                "s", Range.of(1, 4000)));

        return workflows.get("in").combinations(initialRange, workflows);

    }

}
