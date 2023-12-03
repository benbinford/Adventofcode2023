package com.benjaminbinford.utils;

import java.util.Objects;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.parser.Input;

public class LineTrackingStringInput implements Input<Chr> {

    public static record Position(int line, int column, int offset) {
        public static Position mzero() {
            return new Position(0, 0, 0);
        }

        public Position next(Chr chr) {
            if (chr.charValue() == '\n') {
                return new Position(line + 1, 0, offset + 1);
            } else {
                return new Position(line, column + 1, offset + 1);
            }
        }

        public Position backupOne() {
            if (column == 0) {
                if (line == 0) {
                    throw new IllegalStateException("Cannot backup past start of input");
                }
                return new Position(line - 1, 0, offset - 1);
            } else {
                return new Position(line, column - 1, offset - 1);
            }
        }

    }

    private final char[] data;
    private Position position;
    private final LineTrackingStringInput other;

    LineTrackingStringInput(char[] data) {
        this.data = data;
        this.position = Position.mzero();
        this.other = new LineTrackingStringInput(this, data);
    }

    LineTrackingStringInput(LineTrackingStringInput other, char[] data) {
        this.data = data;
        this.position = Position.mzero();
        this.other = other;
    }

    private LineTrackingStringInput setPosition(Position position) {
        this.position = position;
        return this;
    }

    @Override
    public String toString() {
        final String dataStr = isEof() ? "EOF" : String.valueOf(data[position.offset]);
        return "LineTrackingStringInput{" + position + ",data=\"" + dataStr + "\"";
    }

    @Override
    public boolean isEof() {
        return position.offset() >= data.length;
    }

    @Override
    public Chr get() {
        return Chr.valueOf(data[position.offset]);
    }

    @Override
    public Input<Chr> next() {
        return other.setPosition(position.next(get()));
    }

    @Override
    public Object position() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LineTrackingStringInput that = (LineTrackingStringInput) o;
        return position.equals(that.position) &&
                data == that.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, position);
    }
}
