package com.sjkorpela.RiichiPointsCalculator.Enums;

import java.util.Arrays;
import java.util.List;

public enum Yaku {
    // 1 Han
    Riichi,
    Ippatsu,
    Tsumo,
    Pinfu,
    AllSimples,
    PureDoubleSequence,
    PrevalentWind,
    SeatWind,
    GreenDragon,
    RedDragon,
    WhiteDragon,
    AfterKan,
    LastTile,
    Dora,
    AkaDora,


    // 2 Han
    DoubleRiichi,
    TripleTriplets,
    ThreeQuads,
    AllTriplets,
    ThreeConcealedTriplets,
    LittleThreeDragons,
    AllTerminalsAndHonors,
    SevenPairs,
    HalfOutsideHand,
    PureStraight,
    MixedTripleSequence,


    // 4 Han
    TwicePureDoubleSequence,
    FullyOutsideHand,
    HalfFlush,


    // 6 Han
    FullFlush,


    // Yakuman
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


    // Double Yakuman
    SingleWaitFourConcealedTriplets,
    ThirteenWaitThirteenOrphans,
    TrueNineGates,
    FourBigWinds
    ;

    public static List<Yaku> getYakuman() {
        return Arrays.asList(
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
    }


}
