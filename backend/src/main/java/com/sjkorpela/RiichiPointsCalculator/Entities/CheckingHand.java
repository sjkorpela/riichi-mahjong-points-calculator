package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CheckingHand {
    private List<Tile> tiles;
    private List<Integer> spentTiles;
    private List<Triplet> triplets;
    private List<Sequence> sequences;
    private Pair pair = null;
    private Integer fu = null;
    private boolean openHand;
    private boolean ready = false;

    public CheckingHand(List<Tile> tiles, boolean openHand) {
        this.tiles = tiles;
        this.spentTiles = new ArrayList<Integer>();
        this.triplets = new ArrayList<Triplet>();
        this.sequences = new ArrayList<Sequence>();
        this.openHand = openHand;
    }

    public CheckingHand(CheckingHand old) {
        this.tiles = new ArrayList<Tile>(old.tiles);
        this.spentTiles = new ArrayList<Integer>(old.spentTiles) {
            @Override
            public void add(int index, Integer element) {
                if (this.contains(index)) {
                    throw new RuntimeException("Index " + index + " is already spent!");
                }
                super.add(index, element);
            }
        };
        this.triplets = new ArrayList<Triplet>(old.triplets);
        this.sequences = new ArrayList<Sequence>(old.sequences);
        this.pair = old.pair;
        this.openHand = old.openHand;
        this.ready = old.ready;
    }

    public Integer getFirstUnspentIndex() {
        for (int i = 0; i < tiles.size(); i++) {
            if (!spentTiles.contains(i)) {
                return i;
            }
        }
        return null;
    }

    public List<Tile> getRemainingTiles() {
        List<Tile> left = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            if (!spentTiles.contains(i)) {
                left.add(tiles.get(i));
            }
        }
        return left;
    }

    public void setPair(Tile tile, Integer firstIndex, Integer secondIndex) {
        spentTiles.add(firstIndex);
        spentTiles.add(secondIndex);
        pair = new Pair(tile, firstIndex, secondIndex);
    }

    public void addTriplet(Tile tile, Integer firstIndex, Integer secondIndex, Integer thirdIndex) {
        spentTiles.add(firstIndex);
        spentTiles.add(secondIndex);
        spentTiles.add(thirdIndex);
        triplets.add(new Triplet(tile, firstIndex, secondIndex, thirdIndex));
    }

    public void addSequence(Tile firstTile, Tile secondTile, Tile thirdTile, Integer firstIndex, Integer secondIndex, Integer thirdIndex) {
        spentTiles.add(firstIndex);
        spentTiles.add(secondIndex);
        spentTiles.add(thirdIndex);
        sequences.add(new Sequence(firstTile, secondTile, thirdTile, firstIndex, secondIndex, thirdIndex));
    }

    @Override
    public String toString() {
        String seqStr = "";
        String triStr = "";
        String paiStr = "";
        for (Sequence s : sequences) {
            seqStr += "[" + s.getTiles()[0] + " " + s.getTiles()[1] + " " + s.getTiles()[2] + "]";
        }
        for (Triplet t : triplets) {
            triStr += "[" + t.getTile() + " " + t.getTile() + " " +t.getTile() + "]";
        }
        paiStr = pair == null ? "" : "[" + pair.getTile() + " " + pair.getTile() + "]";
        return "{ " + seqStr + triStr + paiStr + ", " + spentTiles.size() + "/" + tiles.size() + " }";
    }
}
