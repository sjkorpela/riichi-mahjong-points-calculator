package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.ResponseYaku;
import com.sjkorpela.RiichiPointsCalculator.Enums.Yaku;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

/**
 * Service that handles reading {@link com.sjkorpela.RiichiPointsCalculator.Enums.Yaku} details from yaku.json.
 *
 * @author Santeri Korpela
 */
public class JsonService {

    /**
     * Reads given {@link com.sjkorpela.RiichiPointsCalculator.Enums.Yaku}'s details from yaku.json.
     *
     * @param yaku {@link com.sjkorpela.RiichiPointsCalculator.Enums.Yaku} to be fetched
     * @param open if it should read the open or closed han
     * @return returns {@link com.sjkorpela.RiichiPointsCalculator.Entities.ResponseYaku} with {@link com.sjkorpela.RiichiPointsCalculator.Enums.Yaku} details
     */
    public static ResponseYaku getYakuDetails(Yaku yaku, boolean open) {
        if (yaku == null) { throw new IllegalArgumentException("Yaku can't be null."); }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("src/main/resources/yaku.json"));
        JsonNode entry = jsonNode.get(yaku.toString());

        return new ResponseYaku(
            yaku,
            entry.get("englishName").asString("Error"),
            entry.get("japaneseName").asString("Error"),
            entry.get("description").asString("Error"),
            entry.get(open ? "openHan" : "closedHan").asInt(0)
        );
    }
}
