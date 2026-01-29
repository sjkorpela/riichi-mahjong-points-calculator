package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.*;
import com.sjkorpela.RiichiPointsCalculator.Entities.Set;
import com.sjkorpela.RiichiPointsCalculator.Enums.*;

import java.util.*;
import java.util.stream.Stream;

public class YakuService {
    /**
     *
     *
     * @param request object that the hand is checked from
     */
    public static void checkForFlagsYaku(PointsRequest request) {
        if (request.getFlags().getOrDefault("blessedHand", false)) {
//            request.getYaku().add(Yaku.BlessedHand);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.BlessedHand,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
            return;
        }

        if (request.getFlags().getOrDefault("afterKan", false)) {
//            request.getYaku().add(Yaku.AfterKan);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.AfterKan,
                    Collections.singletonList(request.getWinningTile()),
                    request.getOpenHand()
            ));
        }

        if (request.getFlags().getOrDefault("lastTile", false)) {
//            request.getYaku().add(Yaku.LastTile);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.LastTile,
                    Collections.singletonList(request.getWinningTile()),
                    request.getOpenHand()
            ));
        }
    }

    /**
     * Thirteen Orphans is a Yakuman that requires the hand to have of every terminal and honor tile, and one duplicate.
     * <p>
     * It also has a rarer Double Yakuman version; Thirteen-Wait Thirteen Orphans. It requires the play hand of 13 tiles
     * to be the 13 orphans, meaning that any orphan draw or discard completes the missing duplicate tile. Which would
     * mean that there's 13 possible tiles to win with, thus Thirteen-Wait Thirteen Orphans.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForThirteenOrphans(PointsRequest request) {

        // Get all orphans aka all terminal and honor tiles
        List<Tile> orphans = Stream.concat(
                Tile.getAllTilesByType(Type.Terminal).stream(),
                Tile.getAllTilesByType(Type.Honor).stream()
        ).toList();

        // Check which orphan is paired and which one is missing
        Tile pair = null;
        Tile missing = null;

        for (Tile orphan : orphans) {
            int amount = request.getHand().getOrDefault(orphan, 0);

            // If there's more than 2 of any orphan, 13o is not possible
            if (amount > 2) { return; }

            // If multiple orphans are paired or missing, 13o is not possible
            if (missing != null && amount == 0) { return; }
            else if (pair != null && amount == 2) { return; }
            else if (missing == null && amount == 0) { missing = orphan; }
            else if (pair == null && amount == 2) { pair = orphan; }
        }

        // If only one is null, something went wrong
        if (pair == null ^ missing == null) { throw new IllegalArgumentException("Hand isn't valid."); }

        // If the winning tile isn't the missing tile, the hand can't be valid
        if (missing != null && request.getWinningTile() != missing) { throw new IllegalArgumentException("Hand isn't valid."); }


        // If no orphans were paired or missing, and winning tile is an orphan, hand is 13w13o
        if (pair == null && orphans.contains(request.getWinningTile())) {
//            request.getYaku().add(Yaku.ThirteenWaitThirteenOrphans);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.ThirteenWaitThirteenOrphans,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        } else {
//            request.getYaku().add(Yaku.ThirteenOrphans);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.ThirteenOrphans,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        }
    }

    /**
     * Half/Full Flush is a Yaku that requires all numbered tiles in the hand to be of the same suit.
     * <p>
     * It's only a Half Flush if the hand also has honors, and a Full Flush if it only has numbered tiles.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForFlushYaku(PointsRequest request) {
        List<Tile> hand = request.getFullHandAsList();

        boolean hasHonors = false;
        Suit suit = null;
        List<Tile> usedTiles = new ArrayList<Tile>();

        for (Tile tile : hand) {
            Type tileType = tile.getType();
            if (!hasHonors && tileType == Type.Honor) {
                hasHonors = true;
            } else if (suit == null && tileType != Type.Honor) {
                suit = tile.getSuit();
                usedTiles.add(tile);
            } else if (suit != null && tileType != Type.Honor && tile.getSuit() != suit) {
                return;
            } else if (tileType != Type.Honor) {
                usedTiles.add(tile);
            }
        }

        // If no numbered tiles, no flush...
        if (usedTiles.isEmpty()) { return; }

        // Save for later, nts: check if is ever used...
        request.setFlushSuit(suit);

        // If hand has honors it's a half flush, otherwise it's a full flush
        if (hasHonors) {
//            request.getYaku().add(Yaku.HalfFlush);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.HalfFlush,
                    usedTiles,
                    request.getOpenHand()
            ));
        } else {
//            request.getYaku().add(Yaku.FullFlush);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.FullFlush,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
        }
    }

    /**
     * Seven Pairs is a Yaku that requires:
     * - The hand to be made of seven pairs
     * <p>
     * Seven Pairs is a unique hand structure, but is still compatible with:
     * - All Honors (Yakuman)
     * - All Terminals and Honors
     * - All Simples
     * - Half/Full Flush
     * And of those some are also compatible with each other.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForSevenPairs(PointsRequest request) {

        for (Map.Entry<Tile, Integer> entry : request.getFullHandAsMap().entrySet()) {
            if (entry.getValue() != 2) { return; }
        }

        request.getResponseYaku().add(new ResponseYaku(
                Yaku.SevenPairs,
                request.getFullHandAsList(),
                request.getOpenHand()
        ));
    }

    public static void checkForNineGates(PointsRequest request) {
        List<Tile> gates = Tile.getAllTilesBySuit(request.getFlushSuit());

        Tile missing = null;

        // this got a bit messy, maybe because am tired, nts: read through this sometime

        for (Tile gate : gates) {
            int amount = request.getHand().getOrDefault(gate, 0);

//            System.out.println(gate);

//            System.out.println("Terminal short: " + (gate.getType() == Type.Terminal && amount <= 2 && request.getWinningTile() != gate));
            // 9g requires three of both terminals but one can be the winning tile
            if (gate.getType() == Type.Terminal && amount <= 2 && request.getWinningTile() != gate) { return; }

//            System.out.println("Missing but not winning: " + (missing != null && request.getWinningTile().getValue() != missing.getValue()));
            // if a tile is missing but not the winning tile, hand can't be 9n
            if (missing != null && request.getWinningTile().getValue() != missing.getValue() ) { return; }

            // check if missing tile is 5 and current tile is r5 and is
            // and if so mark 5 as not missing
//            System.out.println("Red gate replaces missing: " + (gate.getRed() && missing != null && missing.getValue() == gate.getValue() && amount > 0));
            if (gate.getRed() && missing != null && missing.getValue() == gate.getValue() && amount > 0) {
                missing = null;
            }

//            System.out.println("Double missing: " + (missing != null && amount == 0));
            if (missing != null && amount == 0 && missing.getValue() != gate.getValue()) { return; }

//            System.out.println("Found missing: " + (missing == null && amount == 0 && !gate.getRed()));
            if (missing == null && amount == 0 && !gate.getRed()) { missing = gate; }
        }

        // loop fail states:
            // hand has less than 3 of a terminal, but the winning tile isn't the missing terminal
            // hand has a missing simple, but the winning tile isn't that missing simple
            // hand has a missing simple, and discovers another missing simple
        // so, hand must have:
            // 3 of both terminals, or 2 of one terminal with the third as the winning tile
            // one missing tile that is the winning tile
            // at most one missing tile of 1-9
        // so everything should be covered

        // if no tiles are missing, hand is t9n, otherwise only 9n
        if (missing == null) {
//            request.getYaku().add(Yaku.TrueNineGates);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.TrueNineGates,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        } else {
//            request.getYaku().add(Yaku.NineGates);
            request.setYakumanAchieved(true);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.NineGates,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
        }
    }

    /**
     * All Green is a Yakuman that requires:
     * - The hand to be made entirely of tiles that only have green on them.
     *      - These tiles are Sou 2-4, Sou 6, Sou 8, and the Green Dragon.
     * <p>
     * Some rulesets have different additional requirements, such as:
     * - The hand must have the Green Dragon.
     * - The hand must be four triplets and a pair.
     * But they aren't accounted for here.
     *
     * @param request PointsRequest object that the hand is checked from
     */
    public static void checkForAllGreen(PointsRequest request) {
        ArrayList<Tile> greens = new ArrayList<>(List.of(new Tile[]{Tile.s2, Tile.s3, Tile.s4, Tile.s6, Tile.s8, Tile.dg}));
        List<Tile> hand = request.getFullHandAsList();

        for (Tile tile : hand) {
            if (!greens.contains(tile)) { return; }
        }

        // It's possible that the hand isn't a valid 3-3-3-3-2 or 7p? nts: implement seq/tri/pair check

//        request.getYaku().add(Yaku.AllGreen);
        request.getResponseYaku().add(new ResponseYaku(
                Yaku.AllGreen,
                request.getFullHandAsList(),
                request.getOpenHand()
        ));
        request.setYakumanAchieved(true);
    }

    /**
     * All Triplets is a Yaku that requires:
     * - The hand to be made of 4 triplets and a pair.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForAllTriplets(PointsRequest request) {

        Tile pair = null;

        for (Map.Entry<Tile, Integer> entry : request.getFullHandAsMap().entrySet()) {
            if (entry.getValue() < 2 || entry.getValue() > 3) {
                return;
            }

            if (entry.getValue() == 2 && pair == null) {
                pair = entry.getKey();
            } else if (entry.getValue() == 2) {
                return;
            }
        }

//        request.getYaku().add(Yaku.AllTriplets);
        request.getResponseYaku().add(new ResponseYaku(
                Yaku.AllTriplets,
                request.getFullHandAsList(),
                request.getOpenHand()
        ));
    }

    /**
     * All Simples is a Yaku that requires:
     * - The hand to be made of all simples, aka no honors or terminals
     * <p>
     * All Simples is incompatible with all Yaku/Yakuman that require honors or terminals:
     * - Thirteen Orphans
     * - All Honors
     * - All Terminals and Honors
     * - All Terminals
     * - Big/Little Three Dragons
     * - Four Big/Little Winds
     * - half/FUlly Outside Hand
     * - Any Yakuhai (ex. Green Dragon or Seat Wind)
     * So checking for it early cuts down on Yaku checks.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForAllSimples(PointsRequest request) {
        for (Tile tile : request.getFullHandAsList()) {
            if (tile.getType() != Type.Simple) { return; }
        }

//        request.getYaku().add(Yaku.AllSimples);
        request.getResponseYaku().add(new ResponseYaku(
                Yaku.AllSimples,
                request.getFullHandAsList(),
                request.getOpenHand()
        ));
    }

    /**
     * All these Yaku are compatible with most other Yaku and require a closed hand:
     * - Tsumo requires that the winning tile is self-drawn.
     * - Riichi requires that Riichi is called when in Tenpai.
     * - Double Riichi requires Riichi to be called on the player's first turn.
     * - Ippatsu is complex, but basically requires that the player that called Riichi draws/calls their winning tile
     *   before they can discard again, or before another player calls Chi/Pon.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForRiichiAndTsumo(PointsRequest request) {
        if (request.getOpenHand()) { return; }

        if (request.getTsumo()) {
//            request.getYaku().add(Yaku.Tsumo);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.Tsumo,
                    Collections.singletonList(request.getWinningTile()),
                    request.getOpenHand()
            ));
        }

        boolean riichi = request.getFlags().getOrDefault("riichi", false);
        boolean doubleRiichi = request.getFlags().getOrDefault("doubleRiichi", false);
        boolean ippatsu = request.getFlags().getOrDefault("ippatsu", false);

        if (riichi && !doubleRiichi) {
//            request.getYaku().add(Yaku.Riichi);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.Riichi,
                    new ArrayList<Tile>(),
                    request.getOpenHand()
            ));
        } else if (riichi && doubleRiichi) {
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.DoubleRiichi,
                    new ArrayList<Tile>(),
                    request.getOpenHand()
            ));
        }

        if (riichi && ippatsu) {
//            request.getYaku().add(Yaku.Ippatsu);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.Ippatsu,
                    new ArrayList<Tile>(),
                    request.getOpenHand()
            ));
        }
    }

    public static void checkForPinfu(PointsRequest request) {
        // nts: implement...
    }

    /**
     * All Terminals and Honors, All Terminals, and All Honors are as their names imply.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForAllTerminalsAndOrHonors(PointsRequest request) {
        List<Tile> hand = request.getFullHandAsList();

        boolean hasTerminals = false;
        boolean hasHonors = false;

        for (Tile tile : hand) {
            switch (tile.getType()) {
                case Terminal:
                    hasTerminals = true;
                    break;
                case Honor:
                    hasHonors = true;
                    break;
                default:
                    return;
            }
        }

        if (hasTerminals && hasHonors) {
//            request.getYaku().add(Yaku.AllTerminalsAndHonors);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.AllTerminalsAndHonors,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
        } else if (hasTerminals) {
//            request.getYaku().add(Yaku.AllTerminals);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.AllTerminals,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        } else if (hasHonors) {
//            request.getYaku().add(Yaku.AllHonors);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.AllHonors,
                    request.getFullHandAsList(),
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        }
    }

    /**
     * Big Three Dragons requires a triplet of each dragon tile: Green, Red, and White.
     * Little Three Dragons is a downgrade of Big Three Dragons where one of the dragon triplets is a pair.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForBigOrLittleThreeDragons(PointsRequest request) {
        List<Tile> dragons = Tile.getAllTilesBySuit(Suit.Dragon);
        HashMap<Tile, Integer> hand = request.getFullHandAsMap();

        // One wind pair is allowed
        boolean dragonPair = false;

        for (Tile dragon : dragons) {
            int dragonAmount = hand.getOrDefault(dragon, 0);

            if (dragonAmount < 2) {
                return;
            } else if (dragonAmount == 2 && !dragonPair) {
                dragonPair = true;
            } else if (dragonAmount == 2) {
                return;
            } else if (dragonAmount > 3) {
                // Theoretically not possible but check anyway
                return;
            }
        }

        if (!dragonPair) {
//            request.getYaku().add(Yaku.BigThreeDragons);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.BigThreeDragons,
                    new ArrayList<>(), // nts: implement
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        } else {
//            request.getYaku().add(Yaku.LittleThreeDragons);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.LittleThreeDragons,
                    new ArrayList<>(), // nts: implement
                    request.getOpenHand()
            ));
        }
    }

    public static void checkForFourBigOrLittleWinds(PointsRequest request) {
        List<Tile> winds = Tile.getAllTilesBySuit(Suit.Wind);
        HashMap<Tile, Integer> hand = request.getFullHandAsMap();

        // One wind pair is allowed
        boolean windPair = false;

        for (Tile wind : winds) {
            int windAmount = hand.getOrDefault(wind, 0);

            if (windAmount < 2) {
                return;
            } else if (windAmount == 2 && !windPair) {
                windPair = true;
            } else if (windAmount == 2) {
                return;
            } else if (windAmount > 3) {
                // Theoretically not possible but check anyway
                return;
            }
        }

        if (!windPair) {
//            request.getYaku().add(Yaku.FourBigWinds);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.FourBigWinds,
                    new ArrayList<>(), // nts: implement
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        } else {
//            request.getYaku().add(Yaku.FourLittleWinds);
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.FourLittleWinds,
                    new ArrayList<>(), // nts: implement
                    request.getOpenHand()
            ));
            request.setYakumanAchieved(true);
        }
    }

    /**
     * Yakuhai is actually a shared name for 6 different Yaku:
     * - Green Dragon Yakuhai: A triplet of Green Dragons.
     * - Red Dragon Yakuhai: A triplet of Red Dragons.
     * - White Dragon Yakuhai: A triplet of White Dragons.
     * - Prevalent Wind Yakuhai: A triplet of the round wind. Most commonly East.
     * - Seat Wind Yakuhai: A triplet of the player's seat wind.
     * <p>
     * If the round wind and seat wind are the same, both Yakuhai are awarded.
     *
     * @param request object that the hand is checked from
     */
    public static void checkForYakuhai(PointsRequest request) {
        List<Tile> honors = Tile.getAllTilesByType(Type.Honor);
        Wind roundWind = request.getRoundWind();
        Wind seatWind = request.getSeatWind();
        HashMap<Tile, Integer> hand = request.getFullHandAsMap();

        for (Tile honor : honors) {
            int honorAmount = hand.getOrDefault(honor, 0);
            Suit honorSuit = honor.getSuit();

            if (honorSuit == Suit.Dragon && honorAmount == 3) {
                switch (honor) {
                    case dg:
//                        request.getYaku().add(Yaku.GreenDragon);
                        request.getResponseYaku().add(new ResponseYaku(
                                Yaku.GreenDragon,
                                Arrays.asList(honor, honor, honor),
                                request.getOpenHand()
                        ));
                        break;
                    case dr:
//                        request.getYaku().add(Yaku.RedDragon);
                        request.getResponseYaku().add(new ResponseYaku(
                                Yaku.RedDragon,
                                Arrays.asList(honor, honor, honor),
                                request.getOpenHand()
                        ));
                        break;
                    case dw:
//                        request.getYaku().add(Yaku.WhiteDragon);
                        request.getResponseYaku().add(new ResponseYaku(
                                Yaku.WhiteDragon,
                                Arrays.asList(honor, honor, honor),
                                request.getOpenHand()
                        ));
                        break;
                }
            } else if (honorSuit == Suit.Wind && honorAmount == 3) {
                if (honor.isWind(roundWind)) {
//                    request.getYaku().add(Yaku.PrevalentWind);
                    request.getResponseYaku().add(new ResponseYaku(
                            Yaku.PrevalentWind,
                            Arrays.asList(honor, honor, honor),
                            request.getOpenHand()
                    ));
                }
                if (honor.isWind(seatWind)) {
//                    request.getYaku().add(Yaku.SeatWind);
                    request.getResponseYaku().add(new ResponseYaku(
                            Yaku.SeatWind,
                            Arrays.asList(honor, honor, honor),
                            request.getOpenHand()
                    ));
                }
            }
        }
    }

    /**
     * Fully Outside Hand is a Yaku that requires:
     * - All sets and pairs to include a terminal
     * <p>
     * If the hand has sets or pairs of honors, it's a Half Outside Hand.
     *
     * @param hand hand that the Yaku is checked from
     */
    public static void checkForOutsideHand(PossibleHand hand) {

        boolean hasHonors = false;

        for (Set set : hand.getSets()) {
            if (set instanceof Sequence) {
                Tile[] tiles = ((Sequence) set).getTiles();
                if (tiles[0].getValue() != 1 || tiles[2].getValue() != 9) {
                    return;
                }
            } else if (set instanceof Triplet) {
                Type tileType = ((Triplet) set).getTile().getType();
                if (tileType == Type.Honor) {
                    hasHonors = true;
                } else if (tileType == Type.Simple) {
                    return;
                }
            } else if (set instanceof Pair) {
                Type tileType = ((Pair) set).getTile().getType();
                if (tileType == Type.Honor) {
                    hasHonors = true;
                } else if (tileType == Type.Simple) {
                    return;
                }
            }
        }

        if (hasHonors) {
//            hand.getYaku().add(Yaku.HalfOutsideHand);
            hand.getYaku().add(new ResponseYaku(
                    Yaku.HalfOutsideHand,
                    new ArrayList<>(),
                    hand.isOpenHand()
            ));
        } else {
//            hand.getYaku().add(Yaku.FullyOutsideHand);
            hand.getYaku().add(new ResponseYaku(
                    Yaku.FullyOutsideHand,
                    new ArrayList<>(),
                    hand.isOpenHand()
            ));
        }
    }

    /**
     * Pure Straight is a Yaku that requires:
     * - A sequence of 123.
     * - A sequence of 456.
     * - A sequence of 789.
     * - And for those three sequences to be of the same suit.
     *
     * @param hand hand that the Yaku is checked from
     */
    public static void checkForPureStraight(PossibleHand hand) {
        for (Suit suit : Suit.values()) {
            boolean oneTwoThree = false;
            boolean fourFiveSix = false;
            boolean sevenEightNine = false;

            for (Sequence set : hand.getSequences()) {
                if (set.getSuit() == suit) {
                    Tile[] tiles = set.getTiles();
                    if (tiles[0].getValue() == 1) {
                        oneTwoThree = true;
                    } else if (tiles[1].getValue() == 5) {
                        fourFiveSix = true;
                    } else if (tiles[2].getValue() == 9) {
                        sevenEightNine = true;
                    }
                }
            }

            if (oneTwoThree && fourFiveSix && sevenEightNine) {
//                hand.getYaku().add(Yaku.PureStraight);
                hand.getYaku().add(new ResponseYaku(
                        Yaku.PureStraight,
                        new ArrayList<>(),
                        hand.isOpenHand()
                ));
                return;
            }
        }
    }

    public static void checkForPureDoubleSequences(PossibleHand hand) {

        int pureDoubleSequenceAmount = 0;

        for (Set set : hand.getSets()) {
            if (set instanceof Sequence) {
                for (Set checkSet : hand.getSets()) {
                    if (checkSet != set && set.equals(checkSet)) {
                        pureDoubleSequenceAmount++;
                    }
                }
            }
        }

        if (pureDoubleSequenceAmount == 1) {
//            hand.getYaku().add(Yaku.PureDoubleSequence);
            hand.getYaku().add(new ResponseYaku(
                    Yaku.PureDoubleSequence,
                    new ArrayList<>(),
                    hand.isOpenHand()
            ));
        } else if (pureDoubleSequenceAmount == 2) {
//            hand.getYaku().add(Yaku.TwicePureDoubleSequence);
            hand.getYaku().add(new ResponseYaku(
                    Yaku.TwicePureDoubleSequence,
                    new ArrayList<>(),
                    hand.isOpenHand()
            ));
        }
    }

    public static void checkForMixedTriples(PossibleHand hand) {
        List<Triplet> triplets = hand.getTriplets();
        List<Sequence> sequences = hand.getSequences();

        boolean tripleTriplets = false;

        if (triplets.size() >= 3) {
            HashMap<Integer, Integer> count = new HashMap<>();

            for (Triplet triplet : triplets) {
                Tile tile = triplet.getTile();
                if (tile.getType() != Type.Honor) {
                    count.merge(tile.getValue(), 1, Integer::sum);
                }
            }

            for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
                int amount = entry.getValue();

                if (amount >= 3) {
//                    hand.getYaku().add(Yaku.TripleTriplets);
                    hand.getYaku().add(new ResponseYaku(
                            Yaku.TripleTriplets,
                            new ArrayList<>(),
                            hand.isOpenHand()
                    ));
                    tripleTriplets = true;
                }
            }
        }

        if (!tripleTriplets && sequences.size() >= 3) {
            HashMap<Integer, Integer> count = new HashMap<>();

            for (Sequence sequence : sequences) {
                Tile tile = sequence.getTiles()[0];
                count.merge(tile.getValue(), 1, Integer::sum);
            }

            for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
                int amount = entry.getValue();

                if (amount >= 3) {
//                    hand.getYaku().add(Yaku.MixedTripleSequence);
                    hand.getYaku().add(new ResponseYaku(
                            Yaku.MixedTripleSequence,
                            new ArrayList<>(),
                            hand.isOpenHand()
                    ));
                }
            }
        }
    }

    public static void checkForConcealedTriplets(PointsRequest request) {
        boolean closedTsumo = !request.getOpenHand() && request.getTsumo();

        // nts: double check this

        for (PossibleHand hand : request.getPossibleHands()) {
            List<Triplet> triplets = hand.getTriplets();

            int concealedTriplets = 0;

            if (!closedTsumo) {
                for (Triplet triplet : triplets) {
                    if (triplet.getTile() != request.getWinningTile()) {
                        concealedTriplets++;
                    }
                }
            } else {
                concealedTriplets = triplets.size();
            }
            // DOES NOT ACCOUNT FOR SINGLE WAIT FOUR CONCEALED TRIPLETS
            if (concealedTriplets == 4) {
//                hand.getYaku().add(Yaku.FourConcealedTriplets);
                hand.getYaku().add(new ResponseYaku(
                        Yaku.FourConcealedTriplets,
                        new ArrayList<>(),
                        hand.isOpenHand()
                ));
            } else if (concealedTriplets == 3) {
//                hand.getYaku().add(Yaku.ThreeConcealedTriplets);
                hand.getYaku().add(new ResponseYaku(
                        Yaku.ThreeConcealedTriplets,
                        new ArrayList<>(),
                        hand.isOpenHand()
                ));
            }
        }
    }

    public static void checkForDora(PointsRequest request) {
        List<Tile> doraTiles = List.of(request.getDora());
        List<Tile> hand = request.getFullHandAsList();

        int doraCount = 0;
        List<Tile> doras = new ArrayList<>();
        int akaDoraCount = 0;
        List<Tile> akaDoras = new ArrayList<>();

        for (Tile tile : hand) {
            if (tile.getRed()) {
                akaDoraCount++;
                akaDoras.add(tile);
            }
            for (Tile dora : doraTiles) {
                if (dora.isDoraIndicatorOf(tile)) {
                    doraCount++;
                    doras.add(tile);
                }
            }
        }

        if (doraCount > 0) {
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.Dora,
                    doras,
                    request.getOpenHand(),
                    doraCount
            ));
        }

        if (akaDoraCount > 0) {
            request.getResponseYaku().add(new ResponseYaku(
                    Yaku.AkaDora,
                    akaDoras,
                    request.getOpenHand(),
                    akaDoraCount
            ));
        }

    }
}
