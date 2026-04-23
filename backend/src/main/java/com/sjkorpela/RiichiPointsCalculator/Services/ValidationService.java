package com.sjkorpela.RiichiPointsCalculator.Services;

import com.sjkorpela.RiichiPointsCalculator.Entities.PointsRequest;
import com.sjkorpela.RiichiPointsCalculator.Enums.Tile;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ValidationService {

    private static final int fullHandSize = 13; // How many tiles the hand should have
    private static final int maxDoraCount = 10; // How many dora the request should have
    private static final int maxTileAmount = 4; // How many of one tile is allowed to exist
    private static final int redFiveTileAmount = 1; // How many red tile are allowed to exist

    public static void validatePointsRequest(PointsRequest request) {
        Tile[] dora = request.getDora();
        HashMap<Tile, Integer> hand = request.getHand();
        HashMap<Tile, Integer> spentTiles = new HashMap<Tile, Integer>();

        int handSize = 0;
        for (Map.Entry<Tile, Integer> entry : hand.entrySet()) {
            Tile tile = entry.getKey();
            Integer count = entry.getValue();
            spentTiles.merge(tile, count, Integer::sum);
            handSize += count;
        }
        if (handSize != fullHandSize) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Hand must be 13 tiles total. Winning Tile is a separate field."
        );

        if (dora.length > maxDoraCount) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Too many Dora provided. Limit is 10."
        );

        Arrays.stream(dora).forEach((tile) -> {
            spentTiles.merge(tile, 1, Integer::sum);
        });

        spentTiles.put(request.getWinningTile(), 1);

        for (Map.Entry<Tile, Integer> entry : spentTiles.entrySet()) {
            Tile tile = entry.getKey();
            Integer count = entry.getValue();

            if (tile.getRed() && count > redFiveTileAmount) throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    count + " of tile " + tile.getReadableName() + " is too many. Limit is " + redFiveTileAmount + " for red fives."
            );
            else if (tile.getValue() == 5 && count > maxTileAmount - redFiveTileAmount) throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    count + " of tile " + tile.getReadableName() + " is too many. Limit is " + (maxTileAmount - redFiveTileAmount) + " because of red fives."
            );
            else if (count > maxTileAmount) throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    count + " of tile " + tile.getReadableName() + " is too many. Limit is " + maxTileAmount + " for most tiles."
            );
        }
    }
}
