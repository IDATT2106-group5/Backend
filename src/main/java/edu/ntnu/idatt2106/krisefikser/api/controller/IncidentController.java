package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.IncidentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling incident-related requests. This includes creating, updating, deleting,
 * and retrieving incidents.
 */

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class IncidentController {

  private static final Logger logger = LoggerFactory.getLogger(IncidentController.class);
  private final IncidentService incidentService;

  public IncidentController(IncidentService incidentService) {
    this.incidentService = incidentService;
  }

  /**
   * Creates a new incident. This endpoint is only accessible to users with the ADMIN role.
   *
   * @param request The request containing the incident details.
   * @return A response entity indicating the result of the operation.
   */

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> createIncident(
      @Valid
      @RequestBody IncidentRequestDto request) {
    try {
      incidentService.createIncident(request);
      logger.info("Incident created successfully: {}", request.getName());
      return ResponseEntity.status(201).body(Map.of("message", "Incident created successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during incident creation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during incident creation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Updates an existing incident. This endpoint is only accessible to users with the ADMIN role.
   *
   * @param id      The ID of the incident to update.
   * @param request The request containing the updated incident details.
   * @return A response entity indicating the result of the operation.
   */

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> updateIncident(
      @Valid
      @PathVariable Long id,
      @RequestBody IncidentRequestDto request) {
    try {
      incidentService.updateIncident(id, request);
      logger.info("Incident with ID {} updated successfully: {}", id, request.getName());
      return ResponseEntity.ok(Map.of("message", "Incident updated successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during incident update: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during incident update: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Deletes an existing incident. This endpoint is only accessible to users with the ADMIN role.
   *
   * @param id The ID of the incident to delete.
   * @return A response entity indicating the result of the operation.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> deleteIncident(@PathVariable Long id) {
    try {
      incidentService.deleteIncident(id);
      logger.info("Incident with ID {} deleted successfully", id);
      return ResponseEntity.ok(Map.of("message", "Incident deleted successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during incident deletion: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during incident deletion: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Retrieves all incidents. This endpoint is accessible to all users.
   *
   * @return A response entity containing a list of incidents.
   */
  @GetMapping
  public ResponseEntity<List<IncidentResponseDto>> getAllIncidents() {
    try {
      List<IncidentResponseDto> incidents = incidentService.getAllIncidents();
      logger.info("Retrieved {} incidents", incidents.size());
      return ResponseEntity.ok(incidents);
    } catch (Exception e) {
      logger.error("Error retrieving incidents: {}", e.getMessage(), e);
      return ResponseEntity.status(500).build();
    }
  }
}
