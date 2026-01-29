package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.PointsRequest;
import com.sjkorpela.RiichiPointsCalculator.Entities.PossibleHand;
import com.sjkorpela.RiichiPointsCalculator.Entities.Triplet;
import com.sjkorpela.RiichiPointsCalculator.Enums.*;

import java.util.List;

public class PointsService {

    // Seven Pairs always gets 25 Fu
    private static final int sevenPairsFu = 25;

    public static void getYaku(PointsRequest request) {

        // Yaku that are based on factors outside the winning hand itself, ex.:
        // - Calling Ron on the last discard of the hand, aka Under the River
        // There's a possible Yakuman, so this should be checked early to limit the search to Yakuman only
        YakuService.checkForFlagsYaku(request); // possible Yakuman

        // Seven Pairs, All Simples, and Half/Full Flush aren't Yakuman, but they should still be checked even if a
        // Yakuman is achieved. Because they're much cheaper checks and so can still be used to filter out Yakuman with
        // more expensive checking processes.

        // Due to its unique hand structure, Seven Pairs is compatible with only 5 Yaku/Yakuman:
        // - All Simples
        // - All Honors (Yakuman)
        // - All Terminals and Honors
        // - Half Flush
        // - Full Flush
        // So, checking for 7P early cuts out like 32 Yaku.
        YakuService.checkForSevenPairs(request);
        boolean sevenPairs = request.getYaku().contains(Yaku.SevenPairs);
        if (sevenPairs) { request.setFu(sevenPairsFu); }

        // CHECK FOR PURE DOUBLE SEQUENCE BECAUSE IT'S BETTER THAN 7P!!!!!

        // All Simples also cuts out like 16 possible Yaku/Yakuman. Basically any that require honors or terminals.
        YakuService.checkForAllSimples(request);
        boolean allSimples = request.getYaku().contains(Yaku.AllSimples);

        // Either kind of Flush blocks:
        // - Triple Triplets
        // - Mixed Triple Sequence
        // And a Full Flush blocks:
        // - Half Outside Hand
        // - Any Yaku that require honors
        // Flushes are also a prerequisite for All Green and (True) Nine Gates
        YakuService.checkForFlushYaku(request);
        boolean fullFlush = request.getYaku().contains(Yaku.FullFlush);
        boolean halfFlush = request.getYaku().contains(Yaku.HalfFlush);
        boolean eitherFlush = fullFlush || halfFlush;

        if (!request.getYakumanAchieved() ||!request.getOpenHand()) {
            YakuService.checkForRiichiAndTsumo(request);
            YakuService.checkForPinfu(request); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        boolean tsumo = request.getYaku().contains(Yaku.Tsumo);

        // All Triplets cuts out all sequence dependant Yaku/Yakuman
        if (!sevenPairs) { YakuService.checkForAllTriplets(request); }
        boolean allTriplets = request.getYaku().contains(Yaku.AllTriplets);

        if (!allSimples) { YakuService.checkForThirteenOrphans(request); } // possible Yakuman
        boolean thirteenOrphans = request.getYaku().contains(Yaku.ThirteenOrphans) || request.getYaku().contains(Yaku.ThirteenWaitThirteenOrphans);
        boolean hasSequences = !sevenPairs && !allTriplets && !thirteenOrphans;

        if (!sevenPairs && !thirteenOrphans) {
            HandService.getPossibleHands(request);
            if (request.getPossibleHands().isEmpty()) {
                throw new IllegalArgumentException("Hand isn't valid! Not Seven Pairs, Thirteen Orphans, or a valid collection of four sets and one pair.");
            }
        }

        if (fullFlush && hasSequences) { YakuService.checkForNineGates(request); } // possible Yakuman
        if (!eitherFlush && !sevenPairs) { YakuService.checkForAllGreen(request); } // possible Yakuman


        if (!allSimples && !thirteenOrphans) {
            YakuService.checkForAllTerminalsAndOrHonors(request);
            boolean allTerminals = request.getYaku().contains(Yaku.AllTerminals);

            if (!allTerminals && !sevenPairs) {
                YakuService.checkForBigOrLittleThreeDragons(request);
                YakuService.checkForFourBigOrLittleWinds(request);
            }

            if (!request.getYakumanAchieved()) {
                YakuService.checkForYakuhai(request);

                for (PossibleHand hand : request.getPossibleHands()) {
                    YakuService.checkForOutsideHand(hand);
                    YakuService.checkForPureStraight(hand);
                }
            }
        }

        if (!request.getOpenHand()) {
            for (PossibleHand hand : request.getPossibleHands()) {
                YakuService.checkForPureDoubleSequences(hand);
            }
        }

        if (!eitherFlush) {
            for (PossibleHand hand : request.getPossibleHands()) {
                YakuService.checkForMixedTriples(hand);
            }
        }

        YakuService.checkForConcealedTriplets(request);

        YakuService.checkForDora(request);
    }

    public static void countFu(PointsRequest request) {

        for (PossibleHand hand : request.getPossibleHands()) {


            // All hands start with 20 Fu
            int fu = 20;

            // Basically, a triplet gives +2 Fu, double if it's a terminal/honor, and half if it's open
            // And a kan gives +4 Fu, with the same rules
            // Sequences give no Fu
            List<Triplet> triplets = hand.getTriplets();
            for (Triplet triplet : triplets) {
                Tile tile = triplet.getTile();
                int tempFu = 2;

                if (tile.getType() != Type.Simple) {
                    tempFu *= 2;
                }

                // nts: if set is open, half the fu !!!!!

                fu += tempFu;
            }

            // Open Wait (+0 Fu) is a non-terminal sequence waiting for tiles at its ends, ex.: 34 waiting for 2 or 5
            // Dual Pair Wait (+0 Fu) is two pairs waiting to make one into a triplet, ex,: 2288 waiting for 2 or 8
            // Closed Wait (+2 Fu) is a sequence waiting for its middle tile, ex: 68 waiting for 7
            // Edge Wait (+2 Fu) is a terminal sequence waiting for the third tile from the edge, ex: 12 waiting for 3
            // Pair Wait (+2 Fu) is a lone tile waiting for its pair, ex.: 9 waiting for 9

            // If the hands pair is Yakuhai, +2 Fu
            Tile pairTile = hand.getPair().getTile();
            if (pairTile.getSuit() == Suit.Dragon) { fu += 2; }
            if (pairTile.getSuit() == Suit.Wind) {
                Wind wind = pairTile.toWind();
                if (wind == request.getRoundWind()) { fu += 2; }
                if (wind == request.getSeatWind()) {fu += 2; }
            }

            // Closed hand Ron is +10 Fu, but does not interrupt Pinfu
            // Tsumo is +2 Fu, but does not interrupt Pinfu

            // Fu is always rounded up to the nearest 10, except for 7P
        }
    }

    public static void requestToResponse(PointsRequest request) {

    }
}
