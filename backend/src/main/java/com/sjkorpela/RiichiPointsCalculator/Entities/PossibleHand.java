package com.sjkorpela.RiichiPointsCalculator.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PossibleHand {
    private List<Set> sets;
    private List<ResponseYaku> responseYaku;
    private Integer fu;
    private boolean openHand;
    private int winningIndex;

    public PossibleHand(CheckingHand hand) {
        this.sets = new ArrayList<>();
        this.sets.addAll(hand.getSequences());
        this.sets.addAll(hand.getTriplets());
        this.sets.add(hand.getPair());
        this.fu = null;
        this.openHand = hand.isOpenHand();
        this.winningIndex = hand.getWinningIndex();

        this.responseYaku = new ArrayList<>();
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
            string.append(sequence.toString());
        }
        for (Triplet triplet : getTriplets()) {
            string.append(triplet.toString());
        }
        string.append(getPair().toString());
        string.append(", Fu: ").append(fu);
        string.append(", WinIndex: ").append(winningIndex);

        return string.toString();
    }
}
