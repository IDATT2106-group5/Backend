package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.HouseholdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Household", description = "Endpoints for managing a household")
@RestController
@RequestMapping("/api/household")
public class HouseholdController {
  HouseholdService householdService;
  private static final Logger LOGGER = LoggerFactory.getLogger(HouseholdController.class);

  @Operation(summary = "Creates a household", description = "Creates a household with the given name and address for a given user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Household created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid household data"),
      @ApiResponse(responseCode = "403", description = "Unauthorized request"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping("/create")
  public ResponseEntity<String> createHousehold(
      @RequestBody CreateHouseholdRequestDto request) {
    try {
      householdService.createHousehold(request);
      LOGGER.info("Household created successfully: {}", request.getName());
      return ResponseEntity.ok("Household created successfully");
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Validation error during household creation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Unexpected error during household creation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body("Internal server error");
    }
  }

  /**
   * Constructor for HouseholdController.
   *
   * @param householdService the household service
   */

}
