package com.benjaminbinford.day7;

public enum CardWithJoker {
    A, K, Q, T, N9, N8, N7, N6, N5, N4, N3, N2, J;

    public static CardWithJoker of(char c) {
        switch (c) {
            case 'A':
                return A;
            case 'K':
                return K;
            case 'Q':
                return Q;
            case 'J':
                return J;
            case 'T':
                return T;
            case '9':
                return N9;
            case '8':
                return N8;
            case '7':
                return N7;
            case '6':
                return N6;
            case '5':
                return N5;
            case '4':
                return N4;
            case '3':
                return N3;
            case '2':
                return N2;
            default:
                throw new IllegalArgumentException("Invalid card: " + c);
        }
    }
}
