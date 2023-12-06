package com.benjaminbinford.day6;

public record Race(long time, long distance) {

    public long countWins() {

        // upgrade: binary search [0..time+1)
        // find the edges of the winning region
        // return time where winning end - time where winning starts

        long firstWin = -1l;
        long firstLossAfterWin = -1l;
        for (var i = 0l; i < time + 1; i++) {
            var speed = i;
            var remainingTime = time - i;
            var distance = remainingTime * speed;
            if (firstWin < 0 && distance > distance()) {
                firstWin = i;
            } else if (firstWin >= 0 && distance <= distance()) {
                firstLossAfterWin = i;
                break;
            }
        }

        return firstLossAfterWin - firstWin;
    }
}
