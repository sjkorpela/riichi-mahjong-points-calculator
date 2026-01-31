package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

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
    private int fu;
    private boolean openHand;

    public CheckingHand(List<Tile> tiles, boolean openHand) {
        if (tiles == null || tiles.size() != 14) { throw new IllegalArgumentException("New CheckingHand's tiles must be not null and 14 tiles."); }
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
        this.fu = old.fu;
    }

    public Integer getFirstUnspentIndex() {
        for (int i = 0; i < tiles.size(); i++) {
            if (!spentTiles.contains(i)) {
                return i;
            }
        }
        return null;
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
}
