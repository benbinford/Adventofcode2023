package com.benjaminbinford.day20;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.benjaminbinford.day20.App.Broadcaster;
import com.benjaminbinford.day20.App.Conjunction;
import com.benjaminbinford.day20.App.Message;
import com.benjaminbinford.day20.App.FlipFlop;
import com.benjaminbinford.day20.App.Pulse;
import com.benjaminbinford.utils.IO;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void testParsing() {
        final var input = IO.getResource("com/benjaminbinford/day20/testinput.txt");

        final var app = new App(input);

        assertEquals(5, app.modules.size());

        final var broadcaster = app.modules.get("broadcaster");
        assertTrue(broadcaster instanceof Broadcaster);
        assertEquals(3, broadcaster.getOutputs().size());

        final var a = app.modules.get("a");
        assertTrue(a instanceof FlipFlop);
        assertEquals(1, a.getOutputs().size());
        assertEquals("b", a.getOutputs().get(0));
        final var b = app.modules.get("b");
        assertTrue(b instanceof FlipFlop);
        assertEquals(1, b.getOutputs().size());
        assertEquals("c", b.getOutputs().get(0));
        final var c = app.modules.get("c");
        assertTrue(c instanceof FlipFlop);
        assertEquals(1, c.getOutputs().size());
        assertEquals("inv", c.getOutputs().get(0));

        final var inv = app.modules.get("inv");
        assertTrue(inv instanceof Conjunction);
        assertEquals(1, inv.getOutputs().size());
        assertEquals(1, ((Conjunction) inv).inputs.size());
        assertEquals("a", inv.getOutputs().get(0));

    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testQueuing() {
        final var input = IO.getResource("com/benjaminbinford/day20/testinput.txt");

        final var app = new App(input);

        app.modules.get("broadcaster").pulse(Pulse.HIGH, "button");

        assertEquals(3, app.queue.size());

        assertEquals(new ArrayDeque<>(List.of(new Message("broadcaster", "a", Pulse.HIGH),
                new Message("broadcaster", "b", Pulse.HIGH),
                new Message("broadcaster", "c", Pulse.HIGH))).toString(), app.queue.toString());

    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testButtonPush() {
        final var input = IO.getResource("com/benjaminbinford/day20/testinput.txt");

        final var app = new App(input);

        app.pushButton();

        assertEquals(0, app.queue.size());

        assertEquals(8, app.lowCount);
        assertEquals(4, app.highCount);

        assertFalse(((FlipFlop) app.modules.get("a")).on);
        assertFalse(((FlipFlop) app.modules.get("b")).on);
        assertFalse(((FlipFlop) app.modules.get("c")).on);
    }

    @Test
    void testButtonPush_1000() {
        final var input = IO.getResource("com/benjaminbinford/day20/testinput.txt");

        final var app = new App(input);

        assertEquals(32000000, app.pushButton1000Times());

        assertEquals(0, app.queue.size());

        assertEquals(8000, app.lowCount);
        assertEquals(4000, app.highCount);

    }

    @Test
    void testButtonPush_2_1000() {
        final var input = """
                broadcaster -> a
                %a -> inv, con
                &inv -> b
                %b -> con
                &con -> output
                """;

        final var app = new App(input);

        assertEquals(11687500, app.pushButton1000Times());

        assertEquals(0, app.queue.size());

        assertEquals(4250, app.lowCount);
        assertEquals(2750, app.highCount);

    }

    @Test
    void testButtonPush2() {
        final var input = """
                broadcaster -> a
                %a -> inv, con
                &inv -> b
                %b -> con
                &con -> output
                """;

        final var app = new App(input);

        app.pushButton();

        assertEquals(4, app.lowCount);
        assertEquals(4, app.highCount);

        app.pushButton();

        assertEquals(8, app.lowCount);
        assertEquals(6, app.highCount);

        app.pushButton();

        assertEquals(13, app.lowCount);
        assertEquals(9, app.highCount);

        app.pushButton();

        assertEquals(17, app.lowCount);
        assertEquals(11, app.highCount);

        app.pushButton();

        assertEquals(21, app.lowCount);
        assertEquals(15, app.highCount);
    }
}
