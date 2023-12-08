package com.benjaminbinford.day8;

import java.util.Iterator;
import java.util.List;

public class Instructions implements Iterator<Instruction> {

    private List<Instruction> instructionList;
    private Iterator<Instruction> iterator;

    public Instructions(List<Instruction> list) {
        instructionList = list;
        reset();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Instruction next() {
        if (!iterator.hasNext()) {
            reset();
        }
        return iterator.next();
    }

    public void reset() {
        iterator = instructionList.iterator();
    }

}
