package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import com.sjkorpela.RiichiPointsCalculator.Services.JsonService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class ResponseYaku {
    private Yaku yaku;
    private String englishName;
    private String japaneseName;
    private String description;
    private int han = 0;
    private List<Tile> tiles;

    public ResponseYaku(Yaku yaku, List<Tile> tiles, boolean open) {
        this.yaku = yaku;
        this.tiles = tiles;

        ResponseYaku details = JsonService.getYakuDetails(yaku, open);

        this.englishName = details.englishName;
        this.japaneseName = details.japaneseName;
        this.description = details.description;
        this.han = details.han;
    }

    public ResponseYaku(Yaku yaku, List<Tile> tiles, boolean open, int han) {
        this.yaku = yaku;
        this.tiles = tiles;
        this.han = han;

        ResponseYaku details = JsonService.getYakuDetails(yaku, open);

        this.englishName = details.englishName;
        this.japaneseName = details.japaneseName;
        this.description = details.description;
    }

    public ResponseYaku(Yaku yaku, String englishName, String japaneseName, String description, Integer han) {
        this.yaku = yaku;
        this.englishName = englishName;
        this.japaneseName = japaneseName;
        this.description = description;
        this.han = han;
        this.tiles = new ArrayList<>();
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof ResponseYaku)) { return false; }
        System.out.println(this.tiles.equals(((ResponseYaku)that).getTiles()));
        return this.yaku == ((ResponseYaku)that).getYaku()
                && this.englishName.equals(((ResponseYaku)that).getEnglishName())
                && this.japaneseName.equals(((ResponseYaku)that).getJapaneseName())
                && this.description.equals(((ResponseYaku)that).getDescription())
                && this.han == ((ResponseYaku)that).getHan()
                && this.tiles.equals(((ResponseYaku)that).getTiles());
    }
}
