package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Triplet implements Set {
    private Tile tile;
    private Integer[] indexes;

    public Triplet(Tile tile, Integer firstIndex, Integer secondIndex, Integer thirdIndex) {
        this.tile = tile;
        this.indexes = new Integer[]{firstIndex, secondIndex, thirdIndex};
    }

    @Override
    public String toString() {
        return "(" + getTile() + ", " + getTile() + ", " + getTile() + ")";
    }
}
