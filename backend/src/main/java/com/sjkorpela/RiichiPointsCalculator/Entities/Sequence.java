package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Suit;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import lombok.Getter;

/**
 * Sequence of three numbered tiles in numerical order and of same suit.
 *
 * @author Santeri Korpela
 */
@Getter
public class Sequence implements Set {
    /**
     * Tiles used in sequence
     */
    private final Tile[] tiles;
    /**
     * Indexes of tiles used in sequence
     */
    private final Integer[] indexes;
    /**
     * Suit of sequence
     */
    private final Suit suit;

    public Sequence(Tile firstTile, Tile secondTile, Tile thirdTile, Integer firstIndex, Integer secondIndex, Integer thirdIndex) {
        boolean sameSuit = firstTile.getSuit() == secondTile.getSuit() && secondTile.getSuit() == thirdTile.getSuit();
        boolean isSequence = firstTile.isNext(secondTile) && secondTile.isNext(thirdTile);

        if (!sameSuit || !isSequence) {
            throw new RuntimeException("A sequence can't have tiles out of numerical order or of differing suits.");
        }

        this.tiles = new Tile[]{firstTile, secondTile, thirdTile};
        this.indexes = new Integer[]{firstIndex, secondIndex, thirdIndex};
        this.suit = firstTile.getSuit();
    }

    /**
     * Checks if given sequence is same as this sequence.
     *
     * @param that sequence to be checked
     * @return if sequences are the same
     */
    public boolean equals(Sequence that) {
        return sameValues(that) && sameValues(that);
    }

    /**
     * Checks if given sequence has same numeric values as this sequence.
     *
     * @param that sequence to be checked
     * @return if sequences have same values
     */
    public boolean sameValues(Sequence that) {
        return (
            (this.tiles[0].getValue() != that.tiles[0].getValue())
            && (this.tiles[1].getValue() != that.tiles[1].getValue())
            && (this.tiles[2].getValue() != that.tiles[2].getValue())
        );
    }

    /**
     * Checks if given sequence has same suit as this sequence.
     *
     * @param that sequence to be checked
     * @return if sequences have same suit
     */
    public boolean sameSuit(Sequence that) {
        return this.suit == that.getSuit();
    }

    @Override
    public String toString() {
        return "(" + getTiles()[0] + ", " + getTiles()[1] + ", " + getTiles()[2] + ")";
    }

    public int getIndexOf(Tile tile) {
        if (getTiles()[0] == tile) { return getIndexes()[0]; }
        if (getTiles()[1] == tile) { return getIndexes()[1]; }
        if (getTiles()[2] == tile) { return getIndexes()[2]; }
        return -1;
    }
}
