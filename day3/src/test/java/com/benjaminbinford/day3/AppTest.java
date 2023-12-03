package com.benjaminbinford.day3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.benjaminbinford.day3.Schematic.EnginePart;
import com.benjaminbinford.day3.Schematic.PartNumber;
import com.benjaminbinford.utils.LineTrackingStringInput.Position;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrue() {
        var input = "467..114..\n" + // 0
                "...*......\n" + // 11
                "..35..633.\n" + // 22
                "......#...\n" + // 33
                "617*......\n" + // 44
                ".....+.58.\n" + // 55
                "..592.....\n" + // 66
                "......755.\n" + // 77
                "...$.*....\n" + // 88
                ".664.598.."; // 99
        ;
        var schematic = Schematic.parse(input);

        assertEquals(
                new Schematic(
                        List.of(
                                new EnginePart(new Position(1, 3, 14), true),
                                new EnginePart(new Position(3, 6, 39), false),
                                new EnginePart(new Position(4, 3, 47), true),
                                new EnginePart(new Position(5, 5, 60), false),
                                new EnginePart(new Position(8, 3, 91), false),
                                new EnginePart(new Position(8, 5, 93), true)),
                        List.of(
                                new PartNumber(new Position(0, 0, 0), new Position(0, 2, 2), 467),
                                new PartNumber(new Position(0, 5, 5), new Position(0, 7, 7), 114),
                                new PartNumber(new Position(2, 2, 24), new Position(2, 3, 25), 35),
                                new PartNumber(new Position(2, 6, 28), new Position(2, 8, 30), 633),
                                new PartNumber(new Position(4, 0, 44), new Position(4, 2, 46), 617),
                                new PartNumber(new Position(5, 7, 62), new Position(5, 8, 63), 58),
                                new PartNumber(new Position(6, 2, 68), new Position(6, 4, 70), 592),
                                new PartNumber(new Position(7, 6, 83), new Position(7, 8, 85), 755),
                                new PartNumber(new Position(9, 1, 100), new Position(9, 3, 102), 664),
                                new PartNumber(new Position(9, 5, 104), new Position(9, 7, 106), 598))),
                schematic);

        assertEquals(List.of(
                467, 35, 633,
                617,
                592,
                755,
                664,
                598),
                schematic.findAttachedPartNumbers().map(c -> c.partNumber()).collect(Collectors.toList()));

        assertEquals(4361, schematic.sumAttachedPartNumbers());
        var gearRatios = schematic.findGearRatios().iterator();
        assertEquals(16345, gearRatios.next());
        assertEquals(451490, gearRatios.next());
        assertFalse(gearRatios.hasNext());

    }
}
