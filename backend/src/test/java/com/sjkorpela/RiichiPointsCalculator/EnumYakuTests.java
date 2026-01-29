package com.sjkorpela.RiichiPointsCalculator;


import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import static com.sjkorpela.RiichiPointsCalculator.Enums.Yaku.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

public class EnumYakuTests {

    @Test
    public void shouldProvideAllYakuman() {
        List<Yaku> allYakuman = Arrays.asList(
                BlessedHand,
                BigThreeDragons,
                FourLittleWinds,
                FourConcealedTriplets,
                FourQuads,
                AllHonors,
                AllGreen,
                AllTerminals,
                ThirteenOrphans,
                NineGates,
                SingleWaitFourConcealedTriplets,
                ThirteenWaitThirteenOrphans,
                TrueNineGates,
                FourBigWinds
        );

        assertEquals(allYakuman, Yaku.getYakuman());
    }
}
