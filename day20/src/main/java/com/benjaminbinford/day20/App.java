package com.benjaminbinford.day20;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    enum Pulse {
        LOW, HIGH
    }

    record Message(String sender, String destination, Pulse pulse) {
    }

    sealed interface Module permits Broadcaster, FlipFlop, Conjunction {
        public String getId();

        void pulse(Pulse p, String sender);

        void addInput(String id);

        void addOutput(String output);

        static Module parse(String rep, Deque<Message> queue) {
            var parts = rep.split(" -> ");
            var id = parts[0];
            Module m;
            switch (id.charAt(0)) {
                case '%':
                    m = new FlipFlop(id.substring(1), queue);
                    break;
                case '&':
                    m = new Conjunction(id.substring(1), queue);
                    break;
                default:
                    m = new Broadcaster(id, queue);
                    break;
            }

            var outputs = parts[1].split("\\s*,\\s*");
            for (var output : outputs) {
                m.addOutput(output);
            }
            return m;
        }

        List<String> getOutputs();
    }

    abstract static class BaseModule {
        String id;
        List<String> outputs;

        Deque<Message> queue;

        protected BaseModule(String id, Deque<Message> queue) {
            this.id = id;
            this.outputs = new ArrayList<>();
            this.queue = queue;
        }

        public String getId() {
            return id;
        }

        public void addOutput(String output) {
            outputs.add(output);
        }

        public abstract String getType();

        @Override
        public String toString() {
            return String.format("%s -> %s%s -> %s", getInputs().stream().collect(Collectors.joining(",")), getType(),
                    id,
                    outputs.stream().collect(Collectors.joining(",")));
        }

        public void addInput(String id) {

        }

        public Collection<String> getInputs() {
            return Collections.emptyList();
        }

        public List<String> getOutputs() {
            return outputs;
        }
    }

    static final class Broadcaster extends BaseModule implements Module {
        public Broadcaster(String id, Deque<Message> queue) {
            super(id, queue);
        }

        List<String> inputs = new ArrayList<>();

        @Override
        public void addInput(String id) {
            inputs.add(id);
        }

        @Override
        public Collection<String> getInputs() {
            return inputs;
        }

        @Override
        public void pulse(Pulse p, String sender) {
            for (var output : outputs) {
                queue.add(new Message(id, output, p));
            }
        }

        @Override
        public String getType() {
            return "";
        }
    }

    static final class FlipFlop extends BaseModule implements Module {

        boolean on = false;
        List<String> inputs = new ArrayList<>();

        @Override
        public void addInput(String id) {
            inputs.add(id);
        }

        @Override
        public Collection<String> getInputs() {
            return inputs;
        }

        public FlipFlop(String id, Deque<Message> queue) {
            super(id, queue);
        }

        @Override
        public void pulse(Pulse p, String sender) {
            if (p == Pulse.LOW) {
                on = !on;
                var newPulse = on ? Pulse.HIGH : Pulse.LOW;
                for (var output : outputs) {
                    queue.add(new Message(id, output, newPulse));
                }
            }

        }

        @Override
        public String getType() {
            return "%";
        }
    }

    void pushButton() {
        sendMessage(new Message("button", "broadcaster", Pulse.LOW));
        while (!queue.isEmpty()) {
            sendMessage(queue.poll());
        }
    }

    long pushButton1000Times() {
        for (var i = 0; i < 1000; i++) {
            pushButton();
        }

        return highCount * lowCount;
    }

    private boolean sendMessage(Message m) {
        Module module = modules.get(m.destination);
        if (m.pulse == Pulse.LOW) {
            lowCount++;
        } else {
            highCount++;
        }

        if (module != null) {
            module.pulse(m.pulse, m.sender);
        }

        return stopMessage != null && stopMessage.equals(m);
    }

    static final class Conjunction extends BaseModule implements Module {

        Map<String, Boolean> inputs = new HashMap<>();

        public Conjunction(String id, Deque<Message> queue) {
            super(id, queue);
        }

        @Override
        public void pulse(Pulse p, String sender) {
            inputs.put(sender, p == Pulse.HIGH);
            Pulse newPulse;
            if (inputs.values().stream().allMatch(b -> b)) {
                newPulse = Pulse.LOW;
            } else {
                newPulse = Pulse.HIGH;
            }
            for (var output : outputs) {
                queue.add(new Message(id, output, newPulse));
            }
        }

        @Override
        public String getType() {
            return "&";
        }

        @Override
        public void addInput(String id) {
            inputs.put(id, false);
        }

        @Override
        public Collection<String> getInputs() {
            return inputs.keySet();
        }
    }

    Deque<Message> queue;
    Map<String, Module> modules = new HashMap<>();
    long lowCount = 0l;
    long highCount = 0l;

    Message stopMessage;

    public App(String input) {
        queue = new ArrayDeque<>();
        input.lines().map(s -> Module.parse(s, queue)).forEach(m -> modules.put(m.getId(), m));
        modules.values().forEach(m -> m.getOutputs().forEach(d -> {
            var t = modules.get(d);
            if (t != null) {
                t.addInput(m.getId());
            }
        }));
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day20/input.txt");

        long startTime = System.nanoTime();
        // final var app = new App(input);

        // IO.answer(app.pushButton1000Times());

        var app2 = new App(input);

        // System.out.println("button -> broadcaster");
        // for (var m : app2.modules.values()) {
        // for (var o : m.getOutputs()) {
        // System.out.println(String.format("%s -> %s", m.getId(), o));
        // }
        // }
        // IO.answer(app2.pushButtonUntilRxLow());

        IO.answer(new App(input).pushButtonUntilMessage("hk", new Message("pm", "dt", Pulse.HIGH)));
        IO.answer(new App(input).pushButtonUntilMessage("jr", new Message("dl", "dt", Pulse.HIGH)));
        IO.answer(new App(input).pushButtonUntilMessage("qz", new Message("vk", "dt", Pulse.HIGH)));
        IO.answer(new App(input).pushButtonUntilMessage("tx", new Message("ks", "dt", Pulse.HIGH)));

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    private long pushButtonUntilMessage(String start, Message message) {
        stopMessage = message;
        long c = 0;
        var found = false;
        while (true) {
            c++;
            sendMessage(new Message("broadcaster", start, Pulse.LOW));
            while (!queue.isEmpty()) {
                found |= sendMessage(queue.poll());
            }
            if (found) {
                break;
            }
        }

        return c;
    }
}
