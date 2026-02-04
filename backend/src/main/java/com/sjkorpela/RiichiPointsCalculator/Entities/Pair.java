package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Pair implements Set {
    private Tile tile;
    private Integer[] indexes;

    public Pair(Tile tile, Integer firstIndex, Integer secondIndex) {
        this.tile = tile;
        this.indexes = new Integer[]{firstIndex, secondIndex};
    }

    @Override
    public String toString() {
        return "(" + getTile() + ", " + getTile() + ")";
    }
}
