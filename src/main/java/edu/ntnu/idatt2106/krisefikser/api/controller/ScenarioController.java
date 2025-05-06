package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.ScenarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling scenario related requests.
 */
@RestController
@RequestMapping("/api/scenarios")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ScenarioController {

  private static final Logger logger = LoggerFactory.getLogger(ScenarioController.class);
  private final ScenarioService scenarioService;

  /**
   * Constructor for ScenarioController.
   *
   * @param scenarioService the service for handling scenario related requests
   */
  public ScenarioController(ScenarioService scenarioService) {
    this.scenarioService = scenarioService;
  }

  /**
   * Create a new scenario.
   *
   * @param request the scenario request DTO
   * @return a response entity with the result of the operation
   */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> createScenario(
      @Valid
      @RequestBody ScenarioRequestDto request) {
    try {
      scenarioService.createScenario(request);
      logger.info("Scenario created successfully: {}", request.getName());
      return ResponseEntity.status(201).body(Map.of("message", "Scenario created successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during scenario creation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during scenario creation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Update an existing scenario by ID.
   *
   * @param id      the ID of the scenario to update
   * @param request the updated scenario data
   * @return a response entity with the result of the operation
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> updateScenario(
      @PathVariable Long id,
      @Valid @RequestBody ScenarioRequestDto request) {
    try {
      scenarioService.updateScenario(id, request);
      logger.info("Scenario with ID {} updated successfully", id);
      return ResponseEntity.ok(Map.of("message", "Scenario updated successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during scenario update: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during scenario update: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Get all scenarios.
   *
   * @return a response entity with the list of scenarios
   */
  @GetMapping
  public ResponseEntity<List<ScenarioResponseDto>> getAllScenarios() {
    try {
      List<ScenarioResponseDto> scenarios = scenarioService.getAllScenarios();
      logger.info("Fetched {} scenarios", scenarios.size());
      return ResponseEntity.ok(scenarios);
    } catch (Exception e) {
      logger.error("Failed to fetch scenarios: {}", e.getMessage(), e);
      return ResponseEntity.status(500).build();
    }
  }
}
