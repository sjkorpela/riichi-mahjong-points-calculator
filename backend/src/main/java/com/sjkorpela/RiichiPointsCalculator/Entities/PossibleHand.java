package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public class PossibleHand {
    private List<Set> sets;
    private List<ResponseYaku> yaku;
    private Integer fu;
    private boolean openHand;

    public PossibleHand(CheckingHand hand) {
        this.sets = new ArrayList<>();
        this.sets.addAll(hand.getSequences());
        this.sets.addAll(hand.getTriplets());
        this.sets.add(hand.getPair());
        this.fu = null;
        this.openHand = hand.isOpenHand();

        this.yaku = new ArrayList<>();
    }

    public List<Sequence> getSequences() {
        List<Sequence> sequences = new ArrayList<>();
        for (Set set : sets) {
            if (set instanceof Sequence) {
                sequences.add((Sequence) set);
            }
        }
        return sequences;
    }

    public List<Triplet> getTriplets() {
        List<Triplet> triplets = new ArrayList<>();
        for (Set set : sets) {
            if (set instanceof Triplet) {
                triplets.add((Triplet) set);
            }
        }
        return triplets;
    }

    public Pair getPair() {
        for (Set set : sets) {
            if (set instanceof Pair) {
                return (Pair)set;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        for (Sequence sequence : getSequences()) {
            string.append("(" + sequence.getTiles()[0] + ", " + sequence.getTiles()[1] + ", " + sequence.getTiles()[2] + ")");
        }
        for (Triplet triplet : getTriplets()) {
            string.append("(" + triplet.getTile() + ", " + triplet.getTile() + ", " + triplet.getTile() + ")");
        }
        string.append("(" + getPair().getTile() + ", " + getPair().getTile() + ")");
        string.append(", Fu: " + fu);

        return string.toString();
    }
}
