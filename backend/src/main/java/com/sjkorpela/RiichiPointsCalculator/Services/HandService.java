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

        List<CheckingHand> readyHands = new ArrayList<>();

        findAllPossibleHands(initialHand, readyHands);
        List<CheckingHand> separatedReadyHands = separatePossibleFuOverlaps(readyHands, request.getWinningTile());

        for (CheckingHand hand : separatedReadyHands) {
            request.getPossibleHands().add(new PossibleHand(hand));
        }
    }

    private static void findAllPossibleHands(CheckingHand hand, List<CheckingHand> readyHands) {

        Integer currentIndex = hand.getFirstUnspentIndex();
        List<Tile> tiles = hand.getTiles();
        if (currentIndex == null || currentIndex == tiles.size() - 1) {
            readyHands.add(hand);
            return;
        }
        Tile currentTile = hand.getTiles().get(currentIndex);

        boolean nextIsUnspent = !hand.getSpentTiles().contains(currentIndex + 1);
        boolean nextIsMatch = nextIsUnspent && currentTile.equals(tiles.get(currentIndex + 1));

        if (nextIsMatch && hand.getPair() == null) {
            CheckingHand nextCheck = new CheckingHand(hand);
            nextCheck.setPair(currentTile, currentIndex, currentIndex + 1);
            findAllPossibleHands(nextCheck, readyHands);
        }

        boolean atLeastTwoTilesLeft = currentIndex + 2 < tiles.size();
        boolean nextTwoAreUnspent = nextIsUnspent && atLeastTwoTilesLeft && !hand.getSpentTiles().contains(currentIndex + 2);
        boolean nextTwoAreMatch = nextIsMatch && nextTwoAreUnspent && currentTile.equals(tiles.get(currentIndex + 2));

        if (nextTwoAreMatch) {
            CheckingHand nextCheck = new CheckingHand(hand);
            nextCheck.addTriplet(currentTile, currentIndex, currentIndex + 1, currentIndex + 2);
            findAllPossibleHands(nextCheck, readyHands);
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
            findAllPossibleHands(nextCheck, readyHands);
        }
    }

    public static List<CheckingHand> separatePossibleFuOverlaps(List<CheckingHand> hands, Tile winningTile) {
        List<CheckingHand> newList = new ArrayList<>();

        for (CheckingHand hand : hands) {
            List<CheckingHand> possibleHands = new ArrayList<>();

            for (Triplet triplet : hand.getTriplets()) {
                if (triplet.getTile() == winningTile) {
                    CheckingHand newHand = new CheckingHand(hand);
                    newHand.setWinningIndex(triplet.getIndexes()[0]);
                    possibleHands.add(newHand);
                }
            }
            for (Sequence sequence : hand.getSequences()) {
                if (sequence.getIndexOf(winningTile) != -1) {
                    CheckingHand newHand = new CheckingHand(hand);
                    newHand.setWinningIndex(sequence.getIndexOf(winningTile));
                    possibleHands.add(newHand);
                }
            }
            if (hand.getPair().getTile() == winningTile) {
                CheckingHand newHand = new CheckingHand(hand);
                newHand.setWinningIndex(hand.getPair().getIndexes()[0]);
                possibleHands.add(newHand);
            }

            newList.addAll(possibleHands);
        }

        return newList;
    }

    public static void countFu(PointsRequest request) {

        for (PossibleHand hand : request.getPossibleHands()) {
            System.out.println("Counting Fu for: " + hand);


            // All hands start with 20 Fu
            int fu = 20;

            List<Triplet> triplets = hand.getTriplets();
            for (Triplet triplet : triplets) {
                // Basically, a triplet gives +2 Fu, double if it's a terminal/honor, and double if it's closed
                // And a kan gives +4 Fu, with the same rules

                Tile tile = triplet.getTile();
                int tempFu = 2;

                if (tile.getType() != Type.Simple) {
                    tempFu *= 2;
                }

                if (!hand.isOpenHand()) {
                    tempFu *= 2;
                }

                // nts: implement kans :')
                // also nts: implement being open/closed for individual sets

                System.out.println(triplet + " is total: +" + tempFu);
                fu += tempFu;
            }

            List<Sequence> sequences = hand.getSequences();
            for (Sequence sequence : sequences) {
                Tile[] tiles = sequence.getTiles();
                Integer[] indexes = sequence.getIndexes();

                // Closed Wait (+2 Fu) is a sequence waiting for its middle tile, ex: 68 waiting for 7
                if (indexes[1] == hand.getWinningIndex()) {
                    System.out.println(sequence + " is a Closed Wait: +2");
                    fu += 2;
                }

                // Edge Wait (+2 Fu) is a terminal sequence waiting for the third tile from the edge, ex: 12 waiting for 3
                boolean lowEdgeWait = tiles[0].getValue() == 1 && indexes[2] == hand.getWinningIndex();
                boolean highEdgeWait = tiles[2].getValue() == 9 && indexes[0] == hand.getWinningIndex();
                if (lowEdgeWait || highEdgeWait) {
                    System.out.println(sequence + " is an Edge Wait: +2");
                    fu += 2;
                }
            }

            // These waits provide no Fu, so they don't need to be checked
            // Open Wait (+0 Fu) is a non-terminal sequence waiting for tiles at its ends, ex.: 34 waiting for 2 or 5
            // Dual Pair Wait (+0 Fu) is two pairs waiting to make one into a triplet, ex,: 2288 waiting for 2 or 8

            // If the hands pair is Yakuhai, +2 Fu
            Tile pairTile = hand.getPair().getTile();
            if (pairTile.getSuit() == Suit.Dragon) {
                System.out.println(hand.getPair() + " is a dragon: +2");
                fu += 2;
            }
            if (pairTile.getSuit() == Suit.Wind) {
                Wind wind = pairTile.toWind();
                if (wind == request.getRoundWind()) {
                    System.out.println(hand.getPair() + " is round wind: +2");
                    fu += 2;
                }
                if (wind == request.getSeatWind()) {
                    System.out.println(hand.getPair() + " is seat wind: +2");
                    fu += 2;
                }
            }

            // Pair Wait (+2 Fu) is a lone tile waiting for its pair, ex.: 9 waiting for 9
            if (pairTile == request.getWinningTile()) {
                System.out.println(hand.getPair() + " is a pair wait: +2");
                fu += 2;
            }

            // Tsumo is +2 Fu, but does not interrupt Pinfu
            // Closed hand Ron is +10 Fu, but does not interrupt Pinfu
            if (request.getTsumo()) {
                System.out.println("Tsumo: +2");
                fu += 2;
            } else if (!request.getOpenHand()) {
                System.out.println("Closed Ron: +10");
                fu += 10;
            }

            // Fu is always rounded up to the nearest 10, except for 7P
            // It's rounded later to help with not counting Ron/Tsumo Fu for Pinfu
            System.out.println("Fu: " + fu);
            hand.setFu(fu);
        }
    }

    public static int roundFu(int fu) {
        // If Fu is 25, it's because of Seven Pairs, and should be kept as 25
        if (fu == 25) { return fu; }
        return (int) (Math.ceil((double)fu / 10) * 10);
    }

    public static PossibleHand getBestPossiblehand(List<PossibleHand> hands) {
        if (hands.size() == 1) { return hands.getFirst(); }

        int mostHan = 0;
        PossibleHand bestHand = null;

        for (PossibleHand hand : hands) {
            int tempHan = 0;
            for (ResponseYaku yaku : hand.getResponseYaku()) {
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
