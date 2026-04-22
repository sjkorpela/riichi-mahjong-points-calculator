package com.sjkorpela.RiichiPointsCalculator.Enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TileTests {

    @Test
    public void shouldGetTileAttributesFromName() {
        assertEquals(1, Tile.s1.getValue());
        assertEquals(5, Tile.m5r.getValue());
        assertEquals(6, Tile.p6.getValue());
        assertEquals(0, Tile.we.getValue());
        assertEquals(3, Tile.wn.getValue());
        assertEquals(0, Tile.dg.getValue());
        assertEquals(1, Tile.dr.getValue());

        assertEquals(Suit.Sou, Tile.s1.getSuit());
        assertEquals(Suit.Man, Tile.m2.getSuit());
        assertEquals(Suit.Pin, Tile.p3.getSuit());
        assertEquals(Suit.Wind, Tile.ww.getSuit());
        assertEquals(Suit.Dragon, Tile.dw.getSuit());

        assertEquals(Type.Terminal, Tile.s1.getType());
        assertEquals(Type.Simple, Tile.m5r.getType());
        assertEquals(Type.Terminal, Tile.p9.getType());
        assertEquals(Type.Honor, Tile.ws.getType());
        assertEquals(Type.Honor, Tile.dg.getType());

        assertSame(true, Tile.s5r.getRed());
        assertSame(true, Tile.m5r.getRed());
        assertSame(true, Tile.p5r.getRed());
        assertSame(false, Tile.s5.getRed());
        assertSame(false, Tile.we.getRed());
        assertSame(false, Tile.dw.getRed());
    }

    @Test
    public void shouldConvertTileToWindOrReturnNull() {
        assertEquals(Wind.we, Tile.we.toWind());
        assertEquals(Wind.ws, Tile.ws.toWind());
        assertEquals(Wind.ww, Tile.ww.toWind());
        assertEquals(Wind.wn, Tile.wn.toWind());

        assertNull(Tile.p1.toWind());
        assertNull(Tile.m5.toWind());
        assertNull(Tile.s8.toWind());
        assertNull(Tile.dg.toWind());
    }

    @Test
    public void shouldCheckIfTilesAreEqual() {
        assertSame(true, Tile.s1.equals(Tile.s1));
        assertSame(true, Tile.m5.equals(Tile.m5));
        assertSame(true, Tile.m5.equals(Tile.m5r));
        assertSame(true, Tile.p9.equals(Tile.p9));
        assertSame(true, Tile.we.equals(Tile.we));
        assertSame(true, Tile.dw.equals(Tile.dw));

        assertSame(false, Tile.s1.equals(Tile.m5));
        assertSame(false, Tile.p9.equals(Tile.we));
        assertSame(false, Tile.m5.equals(Tile.p5r));
        assertSame(false, Tile.s5r.equals(Tile.m5r));
        assertSame(false, Tile.we.equals(Tile.ww));
        assertSame(false, Tile.wn.equals(Tile.dg));
        assertSame(false, Tile.dw.equals(Tile.dr));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForNullParam() {
        assertThrows(IllegalArgumentException.class, () -> Tile.s1.equals(null));
        assertThrows(IllegalArgumentException.class, () -> Tile.getAllTilesBySuit(null));
        assertThrows(IllegalArgumentException.class, () -> Tile.getAllTilesByType(null));
        assertThrows(IllegalArgumentException.class, () -> Tile.s1.equalsWind(null));
        assertThrows(IllegalArgumentException.class, () -> Tile.s1.isNext(null));
        assertThrows(IllegalArgumentException.class, () -> Tile.s1.isDoraIndicatorOf(null));
    }

    @Test
    public void shouldReturnAllSouTiles() {
        List<Tile> allSouTiles = Arrays.asList(
                Tile.s1,
                Tile.s2,
                Tile.s3,
                Tile.s4,
                Tile.s5,
                Tile.s5r,
                Tile.s6,
                Tile.s7,
                Tile.s8,
                Tile.s9
        );
        assertEquals(allSouTiles, Tile.getAllTilesBySuit(Suit.Sou));
    }

    @Test
    public void shouldReturnAllWindTiles() {
        List<Tile> allWindTiles = Arrays.asList(
                Tile.we,
                Tile.ws,
                Tile.ww,
                Tile.wn
        );
        assertEquals(allWindTiles, Tile.getAllTilesBySuit(Suit.Wind));
    }

    @Test
    public void shouldReturnAllTerminalTiles() {
        List<Tile> allHonors = Arrays.asList(
                Tile.s1,
                Tile.s9,
                Tile.m1,
                Tile.m9,
                Tile.p1,
                Tile.p9
        );
        assertEquals(allHonors, Tile.getAllTilesByType(Type.Terminal));
    }

    @Test
    public void shouldReturnAllHonorTiles() {
        List<Tile> allHonors = Arrays.asList(
                Tile.we,
                Tile.ws,
                Tile.ww,
                Tile.wn,
                Tile.dg,
                Tile.dr,
                Tile.dw
        );
        assertEquals(allHonors, Tile.getAllTilesByType(Type.Honor));
    }

    @Test
    public void shouldCheckIfTileEqualsWind() {
        assertSame(true, Tile.we.equalsWind(Wind.we));
        assertSame(true, Tile.ws.equalsWind(Wind.ws));
        assertSame(true, Tile.ww.equalsWind(Wind.ww));
        assertSame(true, Tile.wn.equalsWind(Wind.wn));

        assertSame(false, Tile.we.equalsWind(Wind.ws));
        assertSame(false, Tile.we.equalsWind(Wind.ww));
        assertSame(false, Tile.we.equalsWind(Wind.wn));
        assertSame(false, Tile.ws.equalsWind(Wind.we));
    }

    @Test
    public void shouldCheckIfTileIsNext() {
        assertSame(true, Tile.s1.isNext(Tile.s2));
        assertSame(true, Tile.m5.isNext(Tile.m6));
        assertSame(true, Tile.p5r.isNext(Tile.p6));
        assertSame(true, Tile.we.isNext(Tile.ws));
        assertSame(true, Tile.dg.isNext(Tile.dr));

        assertSame(false, Tile.s1.isNext(Tile.m2));
        assertSame(false, Tile.s9.isNext(Tile.s1));
        assertSame(false, Tile.m9.isNext(Tile.we));
        assertSame(false, Tile.wn.isNext(Tile.dg));
        assertSame(false, Tile.wn.isNext(Tile.we));
        assertSame(false, Tile.dw.isNext(Tile.dg));
    }

    @Test
    public void shouldCheckIfTileIsIndicatedDora() {
        assertSame(true, Tile.s1.isDoraIndicatorOf(Tile.s2));
        assertSame(true, Tile.m5r.isDoraIndicatorOf(Tile.m6));
        assertSame(true, Tile.s9.isDoraIndicatorOf(Tile.s1));
        assertSame(true, Tile.we.isDoraIndicatorOf(Tile.ws));
        assertSame(true, Tile.wn.isDoraIndicatorOf(Tile.we));
        assertSame(true, Tile.dw.isDoraIndicatorOf(Tile.dg));

        assertSame(false, Tile.s1.isDoraIndicatorOf(Tile.s3));
        assertSame(false, Tile.m5r.isDoraIndicatorOf(Tile.p6));
        assertSame(false, Tile.s9.isDoraIndicatorOf(Tile.m5r));
        assertSame(false, Tile.s9.isDoraIndicatorOf(Tile.m1));
        assertSame(false, Tile.ws.isDoraIndicatorOf(Tile.we));
        assertSame(false, Tile.ww.isDoraIndicatorOf(Tile.m5));
        assertSame(false, Tile.dr.isDoraIndicatorOf(Tile.dr));
        assertSame(false, Tile.dg.isDoraIndicatorOf(Tile.s1));
    }
}
