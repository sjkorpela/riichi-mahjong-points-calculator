package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.ResponseYaku;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class JsonServiceTests {

    private final ResponseYaku whiteDragon = new ResponseYaku(
            Yaku.WhiteDragon,
            "White Dragon",
            "Yakuhai",
            "A triplet of White Dragon tiles.",
            1,
            new ArrayList<>()
    );

    private final ResponseYaku closedPureStraight = new ResponseYaku(
            Yaku.PureStraight,
            "Pure Straight",
            "Ittsuu",
            "Three sequences of values 1-2-3, 4-5-6, and 7-8-9 all of the same suit.",
            2,
            new ArrayList<>()
    );

    private final ResponseYaku openPureStraight = new ResponseYaku(
            Yaku.PureStraight,
            "Pure Straight",
            "Ittsuu",
            "Three sequences of values 1-2-3, 4-5-6, and 7-8-9 all of the same suit.",
            1,
            new ArrayList<>()
    );

    @Test
    public void shouldGetYakuDetails() {
        assertEquals(whiteDragon, JsonService.getYakuDetails(Yaku.WhiteDragon, true));
    }

    @Test
    public void shouldGetOpenHandHan() {
        assertSame(1, JsonService.getYakuDetails(Yaku.PureStraight, true).getHan());
    }

    @Test
    public void shouldGetClosedHandHan() {
        assertSame(2, JsonService.getYakuDetails(Yaku.PureStraight, false).getHan());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForNullParam() {
        assertThrows(IllegalArgumentException.class, () -> JsonService.getYakuDetails(null, true));
    }
}
