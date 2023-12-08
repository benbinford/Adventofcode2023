package com.benjaminbinford.day8;

import static org.typemeta.funcj.parser.Text.alphaNum;
import static org.typemeta.funcj.parser.Text.string;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;
import org.typemeta.funcj.tuples.Tuple2;
import org.typemeta.funcj.tuples.Tuple3;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    private Instructions instructions;
    private Map<String, Node> nodes;

    public App(String input) {
        var sections = input.split("\n\n");
        instructions = parseInstructions(sections[0]);
        parseTree(sections[1]);
    }

    Parser<Chr, String> nodeName = alphaNum.many1().map(l -> IList.listToString(l.map(Chr::charValue)));

    Parser<Chr, Tuple3<String, String, String>> nodeParser = nodeName.andL(string(" = (")).and(nodeName)
            .andL(string(", "))
            .and(nodeName).andL(string(")")).map(Tuple3::of);

    private void parseTree(String input) {

        nodes = new HashMap<>();

        for (var line : input.split("\n")) {
            var parts = nodeParser.parse(Input.of(line)).getOrThrow();
            var label = parts._1;
            var left = parts._2;
            var right = parts._3;

            var node = nodes.computeIfAbsent(label, Node::new);
            node.setLeft(nodes.computeIfAbsent(left, Node::new));
            node.setRight(nodes.computeIfAbsent(right, Node::new));

        }

    }

    private Instructions parseInstructions(String input) {
        return new Instructions(input.chars().mapToObj(c -> Instruction.valueOf(String.valueOf((char) c))).toList());
    }

    public Tuple2<Node, Integer> run(Node initial, Predicate<Node> success) {

        var node = initial;
        var i = 0;
        instructions.reset();

        do {
            var nextInstruction = instructions.next();
            if (nextInstruction == Instruction.L) {
                node = node.getLeft();
            } else if (nextInstruction == Instruction.R) {
                node = node.getRight();
            }
            i++;
        } while (!success.test(node));

        return Tuple2.of(node, i);
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day8/input.txt");

        App app = new App(input);
        IO.answer(app.runPart1());
        IO.answer(app.runSimultaneous());
    }

    int runPart1() {
        return run(nodes.get("AAA"), n -> n.getLabel().equals("ZZZ"))._2;
    }

    public long runSimultaneous() {
        List<Node> initial = nodes.values().stream().filter(n -> n.getLabel().endsWith("A"))
                .collect(Collectors.toList());

        List<Tuple2<Node, Integer>> zNodes = initial.stream().map(n -> run(n, m -> m.getLabel().endsWith("Z")))
                .collect(Collectors.toList());

        List<Tuple2<Integer, Integer>> cycles = zNodes.stream()
                .map(tn -> Tuple2.of(tn._2, run(tn._1, m -> m.equals(tn._1))._2))
                .collect(Collectors.toList());

        return cycles.stream().map(t -> (long) t._2)
                .reduce(1l, this::lcm);

    }

    private long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private long gcd(long a, long b) {
        if (b == 0l) {
            return a;
        }
        return gcd(b, a % b);
    }

}
