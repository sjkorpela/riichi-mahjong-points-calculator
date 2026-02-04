package com.sjkorpela.RiichiPointsCalculator.Entities;

import com.sjkorpela.RiichiPointsCalculator.Enums.Wind;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import com.sjkorpela.RiichiPointsCalculator.Services.HandService;
import com.sjkorpela.RiichiPointsCalculator.Services.JsonService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PointsResponse {

    private final List<ResponseYaku> yaku;
    private final boolean openHand;
    private final boolean yakuman;
    private final boolean dealer;
    private final int han;
    private final int fu;
    private final int score;

    public PointsResponse(PointsRequest request) {
        this.yaku = new ArrayList<>();
        this.openHand = request.getOpenHand();
        this.yakuman = request.getYakumanAchieved();

        List<Yaku> allYakuman = Yaku.getYakuman();

        for (Yaku yaku : request.getYaku()) {
            System.out.println(" RY: " + yaku);
            if (!this.yakuman || allYakuman.contains(yaku)) {
                this.yaku.add(JsonService.getYakuDetails(yaku, request.getOpenHand()));
            }
        }

        for (ResponseYaku yaku : request.getResponseYaku()) {
            System.out.println("RRY: " + yaku.getYaku());
            if (!this.yakuman || allYakuman.contains(yaku.getYaku())) {
                this.yaku.add(yaku);
            }
        }

        if (!this.hasYaku(Yaku.SevenPairs) && !this.hasYaku(Yaku.ThirteenOrphans) && !this.hasYaku(Yaku.ThirteenWaitThirteenOrphans)) {
            PossibleHand bestHand = HandService.getBestPossiblehand(request.getPossibleHands());
            this.fu = HandService.roundFu(bestHand.getFu());

            for (ResponseYaku yaku : bestHand.getResponseYaku()) {
                System.out.println("HRY: " + yaku.getYaku());
                if (!this.yakuman || allYakuman.contains(yaku.getYaku())) {
                    this.yaku.add(yaku);
                }
            }
        } else {
            this.fu = request.getFu();
        }


        int tempHan = 0;
        for (ResponseYaku yaku : this.yaku) {
            tempHan += yaku.getHan();
        }
        this.han = tempHan;

        this.dealer = request.getSeatWind() == Wind.we;

        // Score is determined by if dealer or not
        // Dealer is paid 50% more
        if (this.han >= 13) {        // Yakuman
            this.score = this.dealer ? 48000 : 32000;
        } else if (this.han >= 11) { // Sanbaiman
            this.score = this.dealer ? 36000 : 24000;
        } else if (this.han >= 8) {  // Baiman
            this.score = this.dealer ? 24000 : 16000;
        } else if (this.han >= 6) {  // Haneman
            this.score = this.dealer ? 18000 : 12000;
        } else if (this.han >= 5) {  // Mangan
            this.score = this.dealer ? 12000 : 8000;
        } else { // Han < 5
            double basicPoints = (this.fu * Math.pow(2, 2 + this.han));
            basicPoints = basicPoints > 2000 ? 2000 : basicPoints; // Cap at 2000
            this.score = (int) (Math.ceil(basicPoints * (this.dealer ? 6 : 4) / 100) * 100);
        }

        System.out.println(tempHan + " Han & " + this.getFu() + " Fu " + (this.dealer ? "as dealer" : "as non-dealer") + " -> " + this.getScore());
    }

    public boolean hasYaku(Yaku yaku) {
        for (ResponseYaku responseYaku : this.yaku) {
            if (responseYaku.getYaku() == yaku) {
                return true;
            }
        }
        return false;
    }
}
