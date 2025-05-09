package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.MapIconType;
import edu.ntnu.idatt2106.krisefikser.service.MapIconService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling MapIcon related requests.
 */
@Tag(name = "MapIcons", description = "Endpoints for MapIcon related requests")
@RestController
@RequestMapping("/api/map-icons")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MapIconController {

  private static final Logger logger = LoggerFactory.getLogger(MapIconController.class);
  private final MapIconService mapIconService;

  /**
   * Constructor for MapIconController.
   *
   * @param mapIconService the service for managing map icons
   */
  public MapIconController(MapIconService mapIconService) {
    this.mapIconService = mapIconService;
  }

  /**
   * Creates a new map icon.
   *
   * @param request the map icon to create
   * @return a response entity indicating the result of the operation
   */
  @Operation(summary = "Creates a map icon",
      description = "Creates a new map icon. Only accessible by admins")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> createMapIcon(
      @RequestBody MapIconRequestDto request) {
    try {
      mapIconService.createMapIcon(request);
      logger.info("Map icon created successfully: {}", request);
      return ResponseEntity.status(201).body(Map.of("message", "Map icon created successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during map icon creation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during map icon creation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Updates an existing map icon.
   *
   * @param id      the ID of the map icon to update
   * @param request the updated map icon data
   * @return a response entity indicating the result of the operation
   */
  @Operation(summary = "Updates a map icon",
      description = "Updates an existing map icon with a given id. Only accessible by admins")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> updateMapIcon(
      @PathVariable Long id,
      @RequestBody MapIconRequestDto request) {
    try {
      mapIconService.updateMapIcon(id, request);
      logger.info("Map icon updated successfully: {}", request);
      return ResponseEntity.ok(Map.of("message", "Map icon updated successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during map icon update: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during map icon update: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Deletes a map icon.
   *
   * @param id the ID of the map icon to delete
   * @return a response entity indicating the result of the operation
   */
  @Operation(summary = "Deletes a map icon",
      description = "Deletes a map icon with a given id. Only accessible by admins")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> deleteMapIcon(@PathVariable Long id) {
    try {
      mapIconService.deleteMapIcon(id);
      logger.info("Map icon with ID {} deleted successfully", id);
      return ResponseEntity.ok(Map.of("message", "Map icon deleted successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during map icon deletion: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during map icon deletion: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Retrieves map icons within a specified radius, optionally filtered by search query.
   *
   * @param latitude  the latitude of the center of the map
   * @param longitude the longitude of the center of the map
   * @param radiusKm  the radius in kilometers to search within
   * @param query     optional search query to filter map icons
   * @return a list of map icons within the specified radius
   */
  @Operation(summary = "Gets map icons",
      description = "Gets map icons within a specified radius from a given location. "
          + "Optionally filtered by search query. Accessible to all users")
  @GetMapping
  public ResponseEntity<List<MapIconResponseDto>> getMapIcons(
      @RequestParam double latitude,
      @RequestParam double longitude,
      @RequestParam double radiusKm,
      @RequestParam(required = false) String query) {
    try {
      List<MapIconResponseDto> icons = mapIconService.getMapIcons(latitude, longitude, radiusKm,
          query);
      logger.info("Retrieved {} map icons within radius of {} km", icons.size(), radiusKm);
      return ResponseEntity.ok(icons);
    } catch (Exception e) {
      logger.error("Error retrieving map icons: {}", e.getMessage(), e);
      return ResponseEntity.status(500).build();
    }
  }

  /**
   * Finds the closest map icon of a specified type from a given location.
   *
   * @param latitude  the user's current latitude
   * @param longitude the user's current longitude
   * @param type      the type of map icon to find (optional - if not provided, finds closest of any
   *                  type)
   * @return ResponseEntity containing the closest map icon or an appropriate error response
   */
  @Operation(summary = "Finds closest map icon",
      description = "Finds the closest map icon of a specified type from a given location. "
          + "If no type is provided, it finds the closest of any type. Accessible to all users")
  @GetMapping("/closest")
  public ResponseEntity<?> findClosestMapIcon(
      @RequestParam double latitude,
      @RequestParam double longitude,
      @RequestParam(required = false) MapIconType type) {
    try {
      MapIconResponseDto closest = mapIconService.findClosestMapIcon(latitude, longitude, type);
      if (closest == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(closest);
    } catch (Exception e) {
      logger.error("Error finding closest map icon: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}
