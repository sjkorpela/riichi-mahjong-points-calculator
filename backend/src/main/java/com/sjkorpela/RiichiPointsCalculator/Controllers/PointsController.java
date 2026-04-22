package com.sjkorpela.RiichiPointsCalculator.Controllers;

import com.sjkorpela.RiichiPointsCalculator.Entities.PointsRequest;
import com.sjkorpela.RiichiPointsCalculator.Entities.PointsResponse;
import com.sjkorpela.RiichiPointsCalculator.Services.PointsService;
import com.sjkorpela.RiichiPointsCalculator.Services.ValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller that handles points calculation requests.
 *
 * @author Santeri Korpela
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class PointsController {

    /**
     * Handles point calculation requests.
     *
     * @param request {@link com.sjkorpela.RiichiPointsCalculator.Entities.PointsRequest} to handle
     * @return Response with {@link com.sjkorpela.RiichiPointsCalculator.Entities.PointsResponse} or error message.
     */
    @PostMapping("/points")
    public ResponseEntity<?> CalculatePoints(@RequestBody PointsRequest request) {
        try {
            ValidationService.validatePointsRequest(request);
            request.initializeOtherFields();
            PointsService.calculatePoints(request);
            PointsResponse response = new PointsResponse(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
