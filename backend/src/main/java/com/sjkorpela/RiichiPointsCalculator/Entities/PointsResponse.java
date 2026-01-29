package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import com.sjkorpela.RiichiPointsCalculator.Services.JsonService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PointsResponse {

    private final List<ResponseYaku> yaku;
    private final boolean openHand;
    private final boolean yakuman;

    public PointsResponse(PointsRequest request) {
        this.yaku = new ArrayList<>();
        this.openHand = request.getOpenHand();
        this.yakuman = request.getYakumanAchieved();

        List<Yaku> allYakuman = Yaku.getYakuman();

        for (Yaku yaku : request.getYaku()) {
//            System.out.println("Yaku: " + yaku);
            if (!this.yakuman || allYakuman.contains(yaku)) {
                this.yaku.add(JsonService.getYakuDetails(yaku, request.getOpenHand()));
            }
        }

        for (ResponseYaku yaku : request.getResponseYaku()) {
//            System.out.println("ResponseYaku: " + yaku);
            if (!this.yakuman || allYakuman.contains(yaku.getYaku())) {
                this.yaku.add(yaku);
            }
        }
    }
}
