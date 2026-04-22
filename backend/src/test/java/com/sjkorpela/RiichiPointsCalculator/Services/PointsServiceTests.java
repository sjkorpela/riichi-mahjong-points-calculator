package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.PointsRequest;
import com.sjkorpela.RiichiPointsCalculator.Entities.PointsResponse;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import com.sjkorpela.RiichiPointsCalculator.Enums.Wind;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class PointsServiceTests {

    @Test
    public void shouldScoreRiichiTsumoPinfuDoraAkaDora18000() {
        PointsRequest request = new PointsRequest();

        // closed: m345, p234, p56w7, s567, 2we; tsumo
        HashMap<Tile, Integer> hand = new HashMap<>();
        hand.put(Tile.m3, 1);
        hand.put(Tile.m4, 1);
        hand.put(Tile.m5, 1);
        hand.put(Tile.p2, 1);
        hand.put(Tile.p3, 1);
        hand.put(Tile.p4, 1);
        hand.put(Tile.p5r, 1);
        hand.put(Tile.p6, 1);
        request.setWinningTile(Tile.p7);
        hand.put(Tile.s5, 1);
        hand.put(Tile.s6, 1);
        hand.put(Tile.s7, 1);
        hand.put(Tile.ww, 2);
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.we);
        request.setDora(new Tile[]{Tile.p4, Tile.s4});
        request.setTsumo(true);
        request.setOpenHand(false);

        HashMap<String, Boolean> flags = new HashMap<>();
        flags.put("riichi", true);
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(false, response.isOpenHand());
        assertEquals(true, response.isDealer());
        assertEquals(false, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.Riichi));
        assertTrue(response.hasYaku(Yaku.Tsumo));
        assertTrue(response.hasYaku(Yaku.Pinfu));
        assertTrue(response.hasYaku(Yaku.Dora));
        assertTrue(response.hasYaku(Yaku.AkaDora));

        assertEquals(6, response.getHan());
        assertEquals(20, response.getFu());
        assertEquals(18000, response.getScore());

        System.out.println("Test clear!\n\n\n\n\n");
    }

    @Test
    public void shouldScoreHalfFlushDora12000() {
        PointsRequest request = new PointsRequest();

        // closed: sw456, p789, 2dw open: s222, s111; tsumo
        HashMap<Tile, Integer> hand = new HashMap<>();
        request.setWinningTile(Tile.s4);
        hand.put(Tile.s5, 1);
        hand.put(Tile.s6, 1);
        hand.put(Tile.s7, 1);
        hand.put(Tile.s8, 1);
        hand.put(Tile.s9, 1);
        hand.put(Tile.dw, 2);
        hand.put(Tile.s2, 3); // Open set
        hand.put(Tile.s1, 3); // Open set
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.we);
        request.setDora(new Tile[]{Tile.s9});
        request.setTsumo(true);
        request.setOpenHand(true);

        HashMap<String, Boolean> flags = new HashMap<>();
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(true, response.isOpenHand());
        assertEquals(true, response.isDealer());
        assertEquals(false, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.HalfFlush));
        assertTrue(response.hasYaku(Yaku.Dora));

        assertEquals(5, response.getHan());
        assertEquals(30, response.getFu());
        assertEquals(12000, response.getScore());

        System.out.println("Test clear!\n\n\n\n\n");
    }

    @Test
    public void shouldScoreRiichiMixedTripleSequenceDoraAkaDora16000() {
        PointsRequest request = new PointsRequest();

        // closed: 2m5, m789, p789, s45r6, s7w89; ron
        HashMap<Tile, Integer> hand = new HashMap<>();
        hand.put(Tile.m5, 2);
        hand.put(Tile.m7, 1);
        hand.put(Tile.m8, 1);
        hand.put(Tile.m9, 1);
        hand.put(Tile.p7, 1);
        hand.put(Tile.p8, 1);
        hand.put(Tile.p9, 1);
        hand.put(Tile.s4, 1);
        hand.put(Tile.s5r, 1);
        hand.put(Tile.s6, 1);
        hand.put(Tile.s7, 1);
        request.setWinningTile(Tile.s8);
        hand.put(Tile.s9, 1);
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.ws);
        request.setDora(new Tile[]{Tile.m4, Tile.s8, Tile.m9, Tile.m4});
        request.setTsumo(false);
        request.setOpenHand(false);

        HashMap<String, Boolean> flags = new HashMap<>();
        flags.put("riichi", true);
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(false, response.isOpenHand());
        assertEquals(false, response.isDealer());
        assertEquals(false, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.Riichi));
        assertTrue(response.hasYaku(Yaku.MixedTripleSequence));
        assertTrue(response.hasYaku(Yaku.Dora));
        assertTrue(response.hasYaku(Yaku.AkaDora));

        assertEquals(9, response.getHan());
        assertEquals(40, response.getFu());
        assertEquals(16000, response.getScore());

        System.out.println("Test clear!\n\n\n");
    }

    @Test
    public void shouldScoreAllSimples1000() {
        PointsRequest request = new PointsRequest();

        // closed: m23w4, m678, 2p2 open: s678, 4p8; ron
        HashMap<Tile, Integer> hand = new HashMap<>();
        hand.put(Tile.m2, 1);
        hand.put(Tile.m3, 1);
        request.setWinningTile(Tile.m4);
        hand.put(Tile.m6, 1);
        hand.put(Tile.m7, 1);
        hand.put(Tile.m8, 1);
        hand.put(Tile.p2, 2);
        hand.put(Tile.s6, 1);
        hand.put(Tile.s7, 1);
        hand.put(Tile.s8, 1);
        hand.put(Tile.p8, 3); // Kans not implemented
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.ws);
        request.setDora(new Tile[]{Tile.s2, Tile.p3, Tile.p4, Tile.s9});
        request.setTsumo(false);
        request.setOpenHand(true);

        HashMap<String, Boolean> flags = new HashMap<>();
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(true, response.isOpenHand());
        assertEquals(false, response.isDealer());
        assertEquals(false, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.AllSimples));

        assertEquals(1, response.getHan());
        assertEquals(30, response.getFu());
        assertEquals(1000, response.getScore());

        System.out.println("Test clear!\n\n\n");
    }

    @Test
    public void shouldScoreRiichiPinfu2000() {
        PointsRequest request = new PointsRequest();

        // closed: m456, 2p3, s12w3, sw345, s789; ron
        HashMap<Tile, Integer> hand = new HashMap<>();
        hand.put(Tile.m4, 1);
        hand.put(Tile.m5, 1);
        hand.put(Tile.m6, 1);
        hand.put(Tile.p3, 2);
        hand.put(Tile.s1, 1);
        hand.put(Tile.s2, 1);
        hand.put(Tile.s3, 1);
        request.setWinningTile(Tile.s3);
        hand.put(Tile.s4, 1);
        hand.put(Tile.s5, 1);
        hand.put(Tile.s7, 1);
        hand.put(Tile.s8, 1);
        hand.put(Tile.s9, 1);
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.ws);
        request.setDora(new Tile[]{Tile.m8, Tile.m6});
        request.setTsumo(false);
        request.setOpenHand(false);

        HashMap<String, Boolean> flags = new HashMap<>();
        flags.put("riichi", true);
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(false, response.isOpenHand());
        assertEquals(false, response.isDealer());
        assertEquals(false, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.Riichi));
        assertTrue(response.hasYaku(Yaku.Pinfu));

        assertEquals(2, response.getHan());
        assertEquals(30, response.getFu());
        assertEquals(2000, response.getScore());

        System.out.println("Test clear!\n\n\n");
    }

    @Test
    public void shouldScoreThirteenOrphans48000() {
        PointsRequest request = new PointsRequest();

        // closed: mw1, m9, 2p1, p9, s1, s9, we, ws, ww, wn, dw, dg, dr; ron
        HashMap<Tile, Integer> hand = new HashMap<>();
        request.setWinningTile(Tile.m1);
        hand.put(Tile.m9, 1);
        hand.put(Tile.p1, 2);
        hand.put(Tile.p9, 1);
        hand.put(Tile.s1, 1);
        hand.put(Tile.s9, 1);
        hand.put(Tile.we, 1);
        hand.put(Tile.ws, 1);
        hand.put(Tile.ww, 1);
        hand.put(Tile.wn, 1);
        hand.put(Tile.dw, 1);
        hand.put(Tile.dg, 1);
        hand.put(Tile.dr, 1);
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.we);
        request.setDora(new Tile[]{Tile.p2, Tile.m7});
        request.setTsumo(false);
        request.setOpenHand(false);

        HashMap<String, Boolean> flags = new HashMap<>();
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(false, response.isOpenHand());
        assertEquals(true, response.isDealer());
        assertEquals(true, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.ThirteenOrphans));

        assertEquals(13, response.getHan());
        assertEquals(0, response.getFu());
        assertEquals(48000, response.getScore());

        System.out.println("Test clear!\n\n\n");
    }

    @Test
    public void shouldScoreThirteenWaitThirteenOrphans48000() {
        PointsRequest request = new PointsRequest();

        // closed: mw1, m9, 2p1, p9, s1, s9, we, ws, ww, wn, dw, dg, dr; ron
        HashMap<Tile, Integer> hand = new HashMap<>();
        request.setWinningTile(Tile.m1);
        hand.put(Tile.m1, 1);
        hand.put(Tile.m9, 1);
        hand.put(Tile.p1, 1);
        hand.put(Tile.p9, 1);
        hand.put(Tile.s1, 1);
        hand.put(Tile.s9, 1);
        hand.put(Tile.we, 1);
        hand.put(Tile.ws, 1);
        hand.put(Tile.ww, 1);
        hand.put(Tile.wn, 1);
        hand.put(Tile.dw, 1);
        hand.put(Tile.dg, 1);
        hand.put(Tile.dr, 1);
        request.setHand(hand);

        request.setRoundWind(Wind.we);
        request.setSeatWind(Wind.we);
        request.setDora(new Tile[]{Tile.p2, Tile.m7});
        request.setTsumo(false);
        request.setOpenHand(false);

        HashMap<String, Boolean> flags = new HashMap<>();
        request.setFlags(flags);

        request.initializeOtherFields();
        PointsService.calculatePoints(request);
        PointsResponse response = new PointsResponse(request);

        assertEquals(false, response.isOpenHand());
        assertEquals(true, response.isDealer());
        assertEquals(true, response.isYakuman());

        assertTrue(response.hasYaku(Yaku.ThirteenWaitThirteenOrphans));

        assertEquals(26, response.getHan());
        assertEquals(0, response.getFu());
        assertEquals(48000, response.getScore());

        System.out.println("Test clear!\n\n\n");
    }
}
