package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Suit;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Wind;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/* Template Hand
{
    "hand": {
        "s1": 1,
        "s2": 1,
        "s3": 1,
        "m4": 1,
        "m5r": 1,
        "m6": 1,
        "p7": 1,
        "p8": 1,
        "p9": 1,
        "we": 3,
        "dw": 1
    },
    "winningTile": "dw",
    "roundWind": "we",
    "seatWind": "we",
    "dora": ["m3", "s9"],
    "openHand": false,
    "tsumo": false,
    "flags" : {
        "riichi": true
    }
}
 */

@Getter
@Setter
public class PointsRequest {
    private HashMap<Tile, Integer> hand;
    private Tile winningTile;
    private Wind roundWind;
    private Wind seatWind;
    private Tile[] dora;
    private Boolean tsumo;
    private Boolean openHand;
    private HashMap<String, Boolean> flags;

    private List<Yaku> yaku;
    private List<ResponseYaku> responseYaku;
    private Boolean yakumanAchieved;
    private Suit flushSuit;
    private List<PossibleHand> possibleHands;
    private Integer fu;

    public void initializeOtherFields() {
        fu = 0;
        yaku = new ArrayList<Yaku>();
        responseYaku = new ArrayList<ResponseYaku>();
        yakumanAchieved = false;
        flushSuit = null;
        possibleHands = new ArrayList<>();
    }

    public List<Tile> getFullHandAsList() {
        List<Tile> list = new ArrayList<>();

        for (Map.Entry<Tile, Integer> entry : hand.entrySet()) {
            Tile tile = entry.getKey();
            Integer count = entry.getValue();
            for (int i = 0; i < count; i++) {
                list.add(tile);
            }
        }

        list.add(winningTile);

        Collections.sort(list);

        return list;
    }

    public HashMap<Tile, Integer> getFullHandAsMap() {
        HashMap<Tile, Integer> temp =  new HashMap<Tile, Integer>(hand);
        temp.merge(winningTile, 1, Integer::sum);
        return temp;
    }
}
