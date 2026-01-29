package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.*;
import com.sjkorpela.RiichiPointsCalculator.Enums.Suit;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Type;
import com.sjkorpela.RiichiPointsCalculator.Enums.Wind;

import java.util.*;

public class HandService {

    public static void getPossibleHands(PointsRequest request) {
        CheckingHand initialHand = new CheckingHand(request.getFullHandAsList(), request.getOpenHand());

        List<CheckingHand> readyHands = new ArrayList<CheckingHand>();
        checkForNextSet(initialHand, readyHands);

        for (CheckingHand hand : readyHands) {
            request.getPossibleHands().add(new PossibleHand(hand));
        }
    }

    private static void checkForNextSet(CheckingHand hand, List<CheckingHand> readyHands) {

        Integer currentIndex = hand.getFirstUnspentIndex();
        List<Tile> tiles = hand.getTiles();

        if (currentIndex == null || currentIndex == tiles.size() - 1) {
            hand.setReady(true);
            readyHands.add(hand);
            return;
        }

        Tile currentTile = hand.getTiles().get(currentIndex);
        boolean nextIsUnspent = !hand.getSpentTiles().contains(currentIndex + 1);
        boolean nextIsMatch = nextIsUnspent && currentTile.equals(tiles.get(currentIndex + 1));

        if (nextIsMatch && hand.getPair() == null) {
            CheckingHand nextCheck = new CheckingHand(hand);
            nextCheck.setPair(currentTile, currentIndex, currentIndex + 1);
            checkForNextSet(nextCheck, readyHands);
        }

        boolean atLeastTwoTilesLeft = currentIndex + 2 < tiles.size();
        boolean nextTwoAreUnspent = nextIsUnspent && atLeastTwoTilesLeft && !hand.getSpentTiles().contains(currentIndex + 2);
        boolean nextTwoAreMatch = nextIsMatch && nextTwoAreUnspent && currentTile.equals(tiles.get(currentIndex + 2));

        if (nextTwoAreMatch) {
            CheckingHand nextCheck = new CheckingHand(hand);
            nextCheck.addTriplet(currentTile, currentIndex, currentIndex + 1, currentIndex + 2);
            checkForNextSet(nextCheck, readyHands);
        }

        Integer secondIndex = null;
        Integer thirdIndex = null;

        for (int checkIndex = currentIndex + 1; checkIndex < tiles.size(); checkIndex++) {
            Tile checkTile = tiles.get(checkIndex);

            if (secondIndex == null && currentTile.isNext(checkTile) && !hand.getSpentTiles().contains(checkIndex)) {
                secondIndex = checkIndex;
            }

            if (secondIndex != null && tiles.get(secondIndex).isNext(checkTile) && !hand.getSpentTiles().contains(checkIndex)) {
                thirdIndex = checkIndex;
                break;
            }
        }

        if (thirdIndex != null) {
            CheckingHand nextCheck = new CheckingHand(hand);
            nextCheck.addSequence(currentTile, tiles.get(secondIndex), tiles.get(thirdIndex), currentIndex, secondIndex, thirdIndex);
            checkForNextSet(nextCheck, readyHands);
        }
    }

    public static void countFu(PointsRequest request) {

        for (PossibleHand hand : request.getPossibleHands()) {


            // All hands start with 20 Fu
            int fu = 20;

            List<Triplet> triplets = hand.getTriplets();
            for (Triplet triplet : triplets) {
                // Basically, a triplet gives +2 Fu, double if it's a terminal/honor, and half if it's open
                // And a kan gives +4 Fu, with the same rules

                Tile tile = triplet.getTile();
                int tempFu = 2;

                if (tile.getType() != Type.Simple) {
                    tempFu *= 2;
                }

                // nts: if set is open, half the fu !!!!!
                // nts: implement kans :')

                fu += tempFu;

                // Also check for Pair Wait
                // Pair Wait (+2 Fu) is a lone tile waiting for its pair, ex.: 9 waiting for 9
                if (triplet.getTile() == request.getWinningTile()) {
                    fu += 2;
                }
            }

            List<Sequence> sequences = hand.getSequences();
            for (Sequence sequence : sequences) {
                Tile[] tiles = sequence.getTiles();

                // Closed Wait (+2 Fu) is a sequence waiting for its middle tile, ex: 68 waiting for 7
                if (tiles[1] == request.getWinningTile()) {
                    fu += 2;
                }

                // Edge Wait (+2 Fu) is a terminal sequence waiting for the third tile from the edge, ex: 12 waiting for 3
                boolean lowEdgeWait = tiles[0].getValue() == 1 && tiles[2] == request.getWinningTile();
                boolean highEdgeWait = tiles[2].getValue() == 9 && tiles[0] == request.getWinningTile();
                if (lowEdgeWait || highEdgeWait) {
                    fu += 2;
                }
            }

            // These waits provide no Fu, so they don't need to be checked
            // Open Wait (+0 Fu) is a non-terminal sequence waiting for tiles at its ends, ex.: 34 waiting for 2 or 5
            // Dual Pair Wait (+0 Fu) is two pairs waiting to make one into a triplet, ex,: 2288 waiting for 2 or 8

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
            fu += request.getTsumo() ? 2 : 10;

            // Fu is always rounded up to the nearest 10, except for 7P

            hand.setFu(fu);
        }
    }

    public static PossibleHand getPossibleHand(List<PossibleHand> hands) {
        int mostHan = 0;
        PossibleHand bestHand = null;

        for (PossibleHand hand : hands) {
            int tempHan = 0;
            for (ResponseYaku yaku : hand .getYaku()) {
                tempHan += yaku.getHan();
            }
            if (bestHand == null || tempHan > mostHan) {
                mostHan = tempHan;
                bestHand = hand;
            } else if (tempHan == mostHan) {
                bestHand = hand.getFu() > bestHand.getFu() ? hand : bestHand;
            }
        }

        if (bestHand == null) {
            throw new IllegalArgumentException("Something went wrong determining the best hand structure. Contact dev.");
        }
        return bestHand;
    }
}
