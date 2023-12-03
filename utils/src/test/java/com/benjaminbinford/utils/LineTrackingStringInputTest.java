package com.benjaminbinford.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.typemeta.funcj.data.Chr;

import com.benjaminbinford.utils.LineTrackingStringInput.Position;

class PositionTest {
    @Test
    void testNextWithNewLineCharacter() {
        Position position = new Position(0, 0, 0);
        Position nextPosition = position.next(Chr.valueOf('\n'));
        assertEquals(1, nextPosition.line());
        assertEquals(0, nextPosition.column());
        assertEquals(1, nextPosition.offset());
    }

    @Test
    void testNextWithNonNewLineCharacter() {
        Position position = new Position(0, 0, 0);
        Position nextPosition = position.next(Chr.valueOf('a'));
        assertEquals(0, nextPosition.line());
        assertEquals(1, nextPosition.column());
        assertEquals(1, nextPosition.offset());
    }

    @Test
    void testNextWithMultipleCharacters() {
        Position position = new Position(0, 0, 0);
        position = position.next(Chr.valueOf('a'));
        position = position.next(Chr.valueOf('b'));
        position = position.next(Chr.valueOf('\n'));
        position = position.next(Chr.valueOf('c'));
        assertEquals(1, position.line());
        assertEquals(1, position.column());
        assertEquals(4, position.offset());
    }
}