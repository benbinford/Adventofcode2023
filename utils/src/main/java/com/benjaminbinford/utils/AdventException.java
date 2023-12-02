package com.benjaminbinford.utils;

import java.io.IOException;

public class AdventException extends RuntimeException {

    public AdventException(IOException e) {
        super(e);
    }

}
