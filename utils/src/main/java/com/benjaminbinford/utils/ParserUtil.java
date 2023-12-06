package com.benjaminbinford.utils;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.data.Lazy;
import org.typemeta.funcj.functions.Functions.F;
import org.typemeta.funcj.functions.Functions.F0;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;
import org.typemeta.funcj.parser.Result;
import org.typemeta.funcj.parser.SymSet;

import static org.typemeta.funcj.parser.Text.ws;

import java.util.Map;

import org.typemeta.funcj.parser.Text;

import com.benjaminbinford.utils.LineTrackingInput.Position;

import static org.typemeta.funcj.parser.Text.chr;
import static org.typemeta.funcj.parser.Text.intr;
import static org.typemeta.funcj.parser.Text.string;
import static org.typemeta.funcj.parser.Text.ulng;

public interface ParserUtil {
    public static <T> Parser<Chr, T> tok(Parser<Chr, T> p) {
        return p.andL(ws.skipMany());
    }

    public static Parser<Chr, String> tok(String s) {
        return string(s).andL(ws.skipMany());
    }

    public static Parser<Chr, String> tok(String s0, String... s1) {
        return Parser.choice(IList.of(s0, s1).map(Text::string)).andL(ws.skipMany());
    }

    public static Parser<Chr, Integer> intTok() {
        return intr.andL(ws.skipMany());
    }

    public static Parser<Chr, Long> longTok() {
        return ulng.andL(ws.skipMany());
    }

    public static Parser<Chr, Chr> tok(char c) {
        return chr(c).andL(ws.skipMany());
    }

    public static <T> Parser<Chr, T> grammar(Parser<Chr, T> p) {
        return ws.skipMany().andR(p);
    }

    public static <K, V> F<IList<Map<K, V>>, Map<K, V>> mergeMaps(F0<Map<K, V>> initial) {
        return maps -> maps.stream().reduce(initial.apply(), (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        });
    }

    public static LineTrackingInput lineTrackingInput(String input) {
        return new LineTrackingInput(Input.of(input));
    }

    public static Parser<Chr, Position> position() {
        return new Parser<Chr, Position>() {

            @Override
            public Lazy<Boolean> acceptsEmpty() {
                return () -> true;
            }

            @Override
            public Lazy<SymSet<Chr>> firstSet() {
                return SymSet::empty;
            }

            @Override
            public Result<Chr, Position> apply(Input<Chr> in, SymSet<Chr> follow) {
                return Result.success((Position) in.position(), in);
            }

        };

    }
}
