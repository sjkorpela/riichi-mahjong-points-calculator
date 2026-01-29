package com.sjkorpela.RiichiPointsCalculator.Entities;

/**
 * Joining interface for {@link com.sjkorpela.RiichiPointsCalculator.Entities.Sequence}s, {@link com.sjkorpela.RiichiPointsCalculator.Entities.Triplet}s, and {@link com.sjkorpela.RiichiPointsCalculator.Entities.Pair}s.
 *
 * @author Santeri Korpela
 */
public interface Set {
    /**
     * Indexes of tiles used in set
     */
    Integer[] indexes = new Integer[0];
}
