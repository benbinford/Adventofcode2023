package com.benjaminbinford.utils;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.parser.Input;

public class LineTrackingInput implements Input<Chr> {

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

        public boolean touchingBox(Position upperLeft, Position lowerRight) {
            return line >= upperLeft.line - 1 && line <= lowerRight.line + 1 && column >= upperLeft.column - 1
                    && column <= lowerRight.column + 1;
        }
    }

    private Input<Chr> backingInput;
    private Position position;

    LineTrackingInput(Input<Chr> backingInput) {
        this.backingInput = backingInput;
        this.position = Position.mzero();
    }

    LineTrackingInput(Input<Chr> backingInput, Position position) {
        this.backingInput = backingInput;
        this.position = position;
    }

    @Override
    public String toString() {
        return "LineTrackingStringInput{" + position + ",input=\"" + backingInput + "\"" + "}";
    }

    @Override
    public boolean isEof() {
        return backingInput.isEof();
    }

    @Override
    public Chr get() {
        return backingInput.get();
    }

    @Override
    public Input<Chr> next() {
        Input<Chr> nextInput = backingInput.next();

        return new LineTrackingInput(nextInput, position.next(get()));
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
        LineTrackingInput that = (LineTrackingInput) o;
        return backingInput.equals(that.backingInput);
    }

    @Override
    public int hashCode() {
        return backingInput.hashCode();
    }
}
