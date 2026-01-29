package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import com.sjkorpela.RiichiPointsCalculator.Services.JsonService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ResponseYaku {
    private Yaku yaku;
    private String englishName;
    private String japaneseName;
    private String description;
    private Integer han = 0;
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

    public ResponseYaku(Yaku yaku, List<Tile> tiles, boolean open, Integer han) {
        this.yaku = yaku;
        this.tiles = tiles;
        this.han = han;

        ResponseYaku details = JsonService.getYakuDetails(yaku, open);

        this.englishName = details.englishName;
        this.japaneseName = details.japaneseName;
        this.description = details.description;
    }

    public ResponseYaku(String englishName, String japaneseName, String description, Integer han) {
        this.englishName = englishName;
        this.japaneseName = japaneseName;
        this.description = description;
        this.han = han;
        this.tiles = null;
    }
}
