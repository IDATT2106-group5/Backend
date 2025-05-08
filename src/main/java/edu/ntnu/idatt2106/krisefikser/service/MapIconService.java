package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MapIcon;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.MapIconType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MapIconRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This is a service class for managing map icons.
 */

@Service
public class MapIconService {

  private static final Logger logger = LoggerFactory.getLogger(MapIconService.class);
  private final MapIconRepository mapIconRepository;

  public MapIconService(MapIconRepository mapIconRepository) {
    this.mapIconRepository = mapIconRepository;
    logger.info("MapIconService initialized");
  }

  /**
   * Creates a new map icon.
   *
   * @param request the request data
   */
  @Transactional
  public void createMapIcon(MapIconRequestDto request) {
    logger.info("Creating new map icon of type: {}", request.getType());
    logger.debug("Map icon request details: address={}, coordinates=({},{})",
        request.getAddress(), request.getLatitude(), request.getLongitude());

    if ((request.getLatitude() == null || request.getLongitude() == null) && (
        request.getAddress() == null || request.getAddress().isBlank())) {
      logger.warn("Invalid map icon request: missing both coordinates and address");
      throw new IllegalArgumentException("Either coordinates or address must be provided.");
    }

    MapIcon mapIcon = new MapIcon();
    mapIcon.setType(request.getType());
    mapIcon.setAddress(request.getAddress());
    mapIcon.setLatitude(request.getLatitude());
    mapIcon.setLongitude(request.getLongitude());
    mapIcon.setDescription(request.getDescription());
    mapIcon.setOpeningHours(request.getOpeningHours());
    mapIcon.setContactInfo(request.getContactInfo());

    logger.debug("Saving map icon to database");
    mapIconRepository.save(mapIcon);
    logger.info("Map icon created successfully with ID: {}", mapIcon.getId());
  }

  /**
   * Updates an existing map icon.
   *
   * @param id      the ID of the map icon
   * @param request the updated data
   */
  @Transactional
  public void updateMapIcon(Long id, MapIconRequestDto request) {
    logger.info("Updating map icon with ID: {}", id);
    logger.debug("Update request details: type={}, address={}, coordinates=({},{})",
        request.getType(), request.getAddress(), request.getLatitude(), request.getLongitude());

    MapIcon mapIcon = mapIconRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Map icon not found with ID: {}", id);
          return new IllegalArgumentException("Map icon not found");
        });

    logger.debug("Found existing map icon: type={}, address={}",
        mapIcon.getType(), mapIcon.getAddress());

    mapIcon.setType(request.getType());
    mapIcon.setAddress(request.getAddress());
    mapIcon.setLatitude(request.getLatitude());
    mapIcon.setLongitude(request.getLongitude());
    mapIcon.setDescription(request.getDescription());
    mapIcon.setOpeningHours(request.getOpeningHours());
    mapIcon.setContactInfo(request.getContactInfo());

    logger.debug("Saving updated map icon");
    mapIconRepository.save(mapIcon);
    logger.info("Map icon with ID {} updated successfully", id);
  }

  /**
   * Deletes a map icon.
   *
   * @param id the ID of the map icon
   */
  @Transactional
  public void deleteMapIcon(Long id) {
    logger.info("Deleting map icon with ID: {}", id);

    if (!mapIconRepository.existsById(id)) {
      logger.warn("Map icon not found with ID: {}", id);
      throw new IllegalArgumentException("Map icon not found");
    }

    logger.debug("Map icon exists, proceeding with deletion");
    mapIconRepository.deleteById(id);
    logger.info("Map icon with ID {} deleted successfully", id);
  }

  /**
   * Retrieves all map icons, filtered by radius and search words.
   *
   * @param latitude  the latitude of the base point
   * @param longitude the longitude of the base point
   * @param radiusKm  the radius in kilometers
   * @return the list of map icons
   */
  @Transactional
  public List<MapIconResponseDto> getMapIcons(double latitude, double longitude, double radiusKm,
      String query) {
    logger.info("Fetching map icons within {}km of coordinates ({}, {}), query: '{}'",
        radiusKm, latitude, longitude, query);

    List<MapIcon> allIcons = mapIconRepository.findAll();
    logger.debug("Retrieved {} map icons from database before filtering", allIcons.size());

    Stream<MapIcon> filtered = allIcons.stream()
        .filter(icon -> icon.getLatitude() != null && icon.getLongitude() != null)
        .filter(icon -> isWithinRadius(latitude, longitude, icon.getLatitude(), icon.getLongitude(),
            radiusKm));

    if (query != null && !query.isBlank()) {
      logger.debug("Applying search query filter: '{}'", query);
      filtered = filtered.filter(icon -> matchesQuery(icon, query));
    }

    List<MapIconResponseDto> result = filtered
        .map(MapIconResponseDto::fromEntity)
        .collect(Collectors.toList());

    logger.info("Returning {} map icons after filtering", result.size());
    logger.debug("Filter reduced results from {} to {} icons", allIcons.size(), result.size());
    return result;
  }

  /**
   * Checks if two geographical coordinates are within a specified radius.
   *
   * @param lat1     the latitude of the first point
   * @param lon1     the longitude of the first point
   * @param lat2     the latitude of the second point
   * @param lon2     the longitude of the second point
   * @param radiusKm the radius in kilometers
   * @return true if within radius, false otherwise
   */
  private boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2,
      double radiusKm) {
    logger.trace("Checking if point ({}, {}) is within {}km of ({}, {})",
        lat2, lon2, radiusKm, lat1, lon1);

    final int earthRadiusKm = 6371;
    double dlat = Math.toRadians(lat2 - lat1);
    double dlon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(dlon / 2) * Math.sin(dlon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = earthRadiusKm * c;

    boolean isWithin = distance <= radiusKm;
    logger.trace("Distance calculated: {}km, within radius: {}", distance, isWithin);
    return isWithin;
  }

  /**
   * Checks if a map icon matches the search query.
   *
   * @param icon  the map icon
   * @param query the search query
   * @return true if matches, false otherwise
   */
  private boolean matchesQuery(MapIcon icon, String query) {
    logger.trace("Checking if icon {} matches query: '{}'", icon.getId(), query);

    if (query == null || query.isBlank()) {
      logger.trace("Empty query, returning match by default");
      return true;
    }
    String lowerQuery = query.toLowerCase();

    boolean matches =
        (icon.getDescription() != null && icon.getDescription().toLowerCase().contains(lowerQuery))
            || (icon.getAddress() != null && icon.getAddress().toLowerCase().contains(lowerQuery))
            || (icon.getContactInfo() != null && icon.getContactInfo().toLowerCase()
            .contains(lowerQuery));

    logger.trace("Icon {} {} query '{}'", icon.getId(), matches ? "matches" : "does not match",
        query);
    return matches;
  }

  /**
   * Finds the closest map icon of a specific type from a given location.
   *
   * @param latitude  the latitude of the user's location
   * @param longitude the longitude of the user's location
   * @param type      the type of map icon to find (null for any type)
   * @return the closest map icon or null if none found
   */
  public MapIconResponseDto findClosestMapIcon(double latitude, double longitude,
      MapIconType type) {
    logger.info("Finding closest map icon to coordinates ({}, {}), type: {}",
        latitude, longitude, type != null ? type : "ANY");

    List<MapIcon> allIcons;

    // If type is provided, filter by type, otherwise get all icons
    if (type != null) {
      logger.debug("Filtering icons by type: {}", type);
      allIcons = mapIconRepository.findByType(type);
    } else {
      logger.debug("Retrieving all map icons regardless of type");
      allIcons = mapIconRepository.findAll();
    }

    if (allIcons.isEmpty()) {
      logger.info("No map icons found matching the criteria");
      return null;
    }

    logger.debug("Retrieved {} icons to search for closest", allIcons.size());

    // Find the closest icon
    MapIcon closest = null;
    double minDistance = Double.MAX_VALUE;

    for (MapIcon icon : allIcons) {
      if (icon.getLatitude() != null && icon.getLongitude() != null) {
        double distance = calculateDistance(
            latitude, longitude,
            icon.getLatitude(), icon.getLongitude()
        );

        logger.trace("Icon ID: {}, distance: {}km", icon.getId(), distance);

        if (distance < minDistance) {
          minDistance = distance;
          closest = icon;
          logger.trace("New closest icon found: ID={}, distance={}km", icon.getId(), distance);
        }
      } else {
        logger.trace("Skipping icon ID: {} - missing coordinates", icon.getId());
      }
    }

    if (closest != null) {
      logger.info("Found closest map icon: ID={}, type={}, distance={}km",
          closest.getId(), closest.getType(), minDistance);
      return MapIconResponseDto.fromEntity(closest);
    } else {
      logger.info("No suitable map icons found with coordinates");
      return null;
    }
  }

  /**
   * Calculates the distance between two geographical points using the Haversine formula.
   *
   * @param lat1 the latitude of the first point
   * @param lon1 the longitude of the first point
   * @param lat2 the latitude of the second point
   * @param lon2 the longitude of the second point
   * @return the distance in kilometers
   */
  public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    logger.trace("Calculating distance between ({}, {}) and ({}, {})", lat1, lon1, lat2, lon2);

    final int earthRadiusKm = 6371;
    double dlat = Math.toRadians(lat2 - lat1);
    double dlon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(dlon / 2) * Math.sin(dlon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = earthRadiusKm * c;

    logger.trace("Distance calculated: {}km", distance);
    return distance;
  }
}