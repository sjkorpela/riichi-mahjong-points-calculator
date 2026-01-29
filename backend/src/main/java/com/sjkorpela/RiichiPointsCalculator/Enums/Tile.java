package com.sjkorpela.RiichiPointsCalculator.Enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

/**
 * Enum values of all Riichi Mahjong Tiles.
 * <p>
 * The tiles have relevant attributes as getters:
 * <ul>
 * <li> Tile value with getValue() as int, 1-9 for numbered tiles, 0-3 for wind tiles, and 0-2 for dragon tiles</li>
 * <li>Tile suit with getSuit() as {@link com.sjkorpela.RiichiPointsCalculator.Enums.Suit}</li>
 * <li>Tile type with getType() as {@link com.sjkorpela.RiichiPointsCalculator.Enums.Type}</li>
 * <li>If the tile is a Red Dora with getRed() as boolean</li>
 * <li>A more readable name with getReadableName(), ex. s1 -> Sou 1, or we -> Wind East</li>
 * </ul>
 * <p>
 * @author Santeri Korpela
 */
@Getter
public enum Tile {
    s1("Sou 1"),
    s2("Sou 2"),
    s3("Sou 3"),
    s4("Sou 4"),
    s5("Sou 5"),
    s5r("Red Sou 5"),
    s6("Sou 6"),
    s7("Sou 7"),
    s8("Sou 8"),
    s9("Sou 9"),
    m1("Man 1"),
    m2("Man 2"),
    m3("Man 3"),
    m4("Man 4"),
    m5("Man 5"),
    m5r("Red Man 5"),
    m6("Man 6"),
    m7("Man 7"),
    m8("Man 8"),
    m9("Man 9"),
    p1("Pin 1"),
    p2("Pin 2"),
    p3("Pin 3"),
    p4("Pin 4"),
    p5("Pin 5"),
    p5r("Red Pin 5"),
    p6("Pin 6"),
    p7("Pin 7"),
    p8("Pin 8"),
    p9("Pin 9"),
    we("East Wind"),
    ws("South Wind"),
    ww("West Wind"),
    wn("North Wind"),
    dg("Green Dragon"),
    dr("Red Dragon"),
    dw("White Dragon"),
    any("Debug Tile"),
    ;

    private final int value;
    private final Suit suit;
    private final Type type;
    private final Boolean red;
    private final String readableName;

    Tile(String readableName) {
        this.readableName = readableName;
        this.red = this.toString().length() == 3 && this.toString().charAt(2) == 'r';

        switch (this.toString().charAt(0)) {
            case 's':
                this.suit = Suit.Sou;
                this.value = Character.getNumericValue(this.toString().charAt(1));
                this.type = (this.value == 1 || this.value == 9) ? Type.Terminal : Type.Simple;
                break;
            case 'm':
                this.suit = Suit.Man;
                this.value = Character.getNumericValue(this.toString().charAt(1));
                this.type = (this.value == 1 || this.value == 9) ? Type.Terminal : Type.Simple;
                break;
            case 'p':
                this.suit = Suit.Pin;
                this.value = Character.getNumericValue(this.toString().charAt(1));
                this.type = (this.value == 1 || this.value == 9) ? Type.Terminal : Type.Simple;
                break;
            case 'w':
                this.suit = Suit.Wind;
                this.value = Wind.valueOf(this.toString()).ordinal();
                this.type = Type.Honor;
                break;
            case 'd':
                this.suit = Suit.Dragon;
                this.value = Dragon.valueOf(this.toString()).ordinal();
                this.type = Type.Honor;
                break;
            default:
                this.suit = null;
                this.value = 0;
                this.type = null;
                break;
        }
    }

    /**
     * Get all tiles of given {@link com.sjkorpela.RiichiPointsCalculator.Enums.Suit}.
     * <p>
     * Ex. get all wind tiles with `Tile.getAllTilesBySuit(Suit.Wind);`
     *
     * @param target target suit
     * @return list of all tiles of suit
     */
    public static List<Tile> getAllTilesBySuit(Suit target) {
        return Arrays.stream(Tile.values()).filter(tile -> tile.getSuit() == target).toList();
    }

    /**
     * Get all tiles of given {@link com.sjkorpela.RiichiPointsCalculator.Enums.Type}.
     * <p>
     * Ex. get all terminal tiles with `Tile.getAllTilesBySuit(Type.Terminal);`
     *
     * @param target target type
     * @return list of all tiles of type
     */
    public static List<Tile> getAllTilesByType(Type target) {
        return Arrays.stream(Tile.values()).filter(tile -> tile.getType() == target).toList();
    }

    /**
     * Checks if given tile is same as this tile.
     * <p>
     * Needed to be overridden to account for red fives.
     *
     * @param that tile to check
     * @return if tiles are the same
     */
    public boolean equals(Tile that) {
        return this.getSuit() == that.getSuit() && this.getValue() == that.getValue();
    }

    /**
     * Checks if given {@link com.sjkorpela.RiichiPointsCalculator.Enums.Wind} is same as current tile.
     * <p>
     * Ex. ´Tile.we.equalsWind(Wind.we)´ returns true, ´Tile.s1.equalsWind(Wind.wn)´ returns false
     *
     * @param wind wind to be checked
     * @return if tile is target wind
     */
    public boolean equalsWind(Wind wind) {
        return this.value == wind.ordinal();
    }

    /**
     * Returns this tile converted to equivalent {@link com.sjkorpela.RiichiPointsCalculator.Enums.Wind} or null.
     * <p>
     * Ex. ´Tile.we.toWind()´ returns ´Wind.we´, ´Tile.s1.toWind()´ returns ´null´
     *
     * @return tile converted to wind or null
     */
    public Wind toWind() {
        if (this.suit != Suit.Wind) { return null; }
        return Wind.valueOf(this.toString());
    }

    /**
     * Checks if given tile is the next one in a sequence.
     * <p>
     * Ex. ´Tile.s1.isNext(Tile.s2)´ returns true, ´Tile.s9.isNext(Tile.s1)´ returns false
     *
     * @param that tile to check
     * @return if the tile is next
     */
    public boolean isNext(Tile that) {
        return this.getSuit() == that.getSuit() && this.getValue() + 1 == that.getValue();
    }

    /**
     * Checks if given tile is the Dora indicated by this one.
     *
     * @param that tile to check
     * @return if the tile is indicated
     */
    public boolean isDoraIndicatorOf(Tile that) {

        switch (this.getSuit()) {
            case Wind:
                switch (Wind.valueOf(this.toString())) {
                    case we -> { return that.equals(Tile.ws); }
                    case ws -> { return that.equals(Tile.ww); }
                    case ww -> { return that.equals(Tile.wn); }
                    case wn -> { return that.equals(Tile.we); }
                }
            case Dragon:
                switch (Dragon.valueOf(this.toString())) {
                    case dg -> { return that.equals(Tile.dr); }
                    case dr -> { return that.equals(Tile.dw); }
                    case dw -> { return that.equals(Tile.dg); }
                }
            default:
                boolean isNext = this.isNext(that);
                boolean loopsAround = this.getValue() == 9 && that.getValue() == 1 && this.getSuit() == that.getSuit();
                return isNext || loopsAround;
        }
    }
}
