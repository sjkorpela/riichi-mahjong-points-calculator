package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.PointsRequest;
import com.sjkorpela.RiichiPointsCalculator.Entities.PossibleHand;
import com.sjkorpela.RiichiPointsCalculator.Entities.ResponseYaku;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Wind;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YakuServiceTests {

    private static PointsRequest getEmptyRequest() {
        PointsRequest request = new PointsRequest();

        request.setHand(new HashMap<Tile, Integer>());
        request.setWinningTile(Tile.any);
        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.we);
        request.setDora(new Tile[]{});
        request.setTsumo(true);
        request.setOpenHand(false);
        request.setFlags(new HashMap<String, Boolean>());
        request.setYaku(new ArrayList<Yaku>());
        request.setResponseYaku(new ArrayList<ResponseYaku>());
        request.setYakumanAchieved(false);
        request.setFlushSuit(null);
        request.setPossibleHands(new ArrayList<PossibleHand>());
        request.setFu(null);

        return request;
    }

    @Test
    public void shouldCheckForOnlyBlessedHand() {
        PointsRequest testRequest = getEmptyRequest();

        testRequest.getFlags().put("blessedHand", true);
        testRequest.getFlags().put("afterKan", true);
        testRequest.getFlags().put("lastTile", true);

        YakuService.checkForFlagsYaku(testRequest);

        List<ResponseYaku> yaku = testRequest.getResponseYaku();
        assertEquals(1, yaku.size());
        assertEquals(Yaku.BlessedHand, yaku.getFirst().getYaku());
        assertTrue(testRequest.getYakumanAchieved());
    }

    @Test
    public void shouldCheckForOnlyAfterKan() {
        PointsRequest testRequest = getEmptyRequest();

        testRequest.getFlags().put("afterKan", true);

        YakuService.checkForFlagsYaku(testRequest);

        List<ResponseYaku> yaku = testRequest.getResponseYaku();
        assertEquals(1, yaku.size());
        assertEquals(Yaku.AfterKan, yaku.getFirst().getYaku());
    }

    @Test
    public void shouldCheckForOnlyLastTile() {
        PointsRequest testRequest = getEmptyRequest();

        testRequest.getFlags().put("lastTile", true);

        YakuService.checkForFlagsYaku(testRequest);

        List<ResponseYaku> yaku = testRequest.getResponseYaku();
        assertEquals(1, yaku.size());
        assertEquals(Yaku.LastTile, yaku.getFirst().getYaku());
    }
}
