package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MapIcon;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MapIconRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class MapIconService {

  private final MapIconRepository mapIconRepository;

  public MapIconService(MapIconRepository mapIconRepository) {
    this.mapIconRepository = mapIconRepository;
  }

  /**
   * Creates a new map icon.
   *
   * @param request the request data
   */
  @Transactional
  public void createMapIcon(MapIconRequestDto request) {
    if ((request.getLatitude() == null || request.getLongitude() == null) && (
        request.getAddress() == null || request.getAddress().isBlank())) {
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

    mapIconRepository.save(mapIcon);
  }

  /**
   * Updates an existing map icon.
   *
   * @param id      the ID of the map icon
   * @param request the updated data
   */
  @Transactional
  public void updateMapIcon(Long id, MapIconRequestDto request) {
    MapIcon mapIcon = mapIconRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Map icon not found"));

    mapIcon.setType(request.getType());
    mapIcon.setAddress(request.getAddress());
    mapIcon.setLatitude(request.getLatitude());
    mapIcon.setLongitude(request.getLongitude());
    mapIcon.setDescription(request.getDescription());
    mapIcon.setOpeningHours(request.getOpeningHours());
    mapIcon.setContactInfo(request.getContactInfo());

    mapIconRepository.save(mapIcon);
  }

  /**
   * Deletes a map icon.
   *
   * @param id the ID of the map icon
   */
  @Transactional
  public void deleteMapIcon(Long id) {
    if (!mapIconRepository.existsById(id)) {
      throw new IllegalArgumentException("Map icon not found");
    }
    mapIconRepository.deleteById(id);
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
    List<MapIcon> allIcons = mapIconRepository.findAll();

    Stream<MapIcon> filtered = allIcons.stream()
        .filter(icon -> icon.getLatitude() != null && icon.getLongitude() != null)
        .filter(icon -> isWithinRadius(latitude, longitude, icon.getLatitude(), icon.getLongitude(),
            radiusKm));

    if (query != null && !query.isBlank()) {
      filtered = filtered.filter(icon -> matchesQuery(icon, query));
    }

    return filtered
        .map(MapIconResponseDto::fromEntity)
        .collect(Collectors.toList());
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
    final int EARTH_RADIUS_KM = 6371;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return (EARTH_RADIUS_KM * c) <= radiusKm;
  }

  /**
   * Checks if a map icon matches the search query.
   *
   * @param icon  the map icon
   * @param query the search query
   * @return true if matches, false otherwise
   */
  private boolean matchesQuery(MapIcon icon, String query) {
    if (query == null || query.isBlank()) {
      return true;
    }
    String lowerQuery = query.toLowerCase();

    return
        (icon.getDescription() != null && icon.getDescription().toLowerCase().contains(lowerQuery))
            || (icon.getAddress() != null && icon.getAddress().toLowerCase().contains(lowerQuery))
            || (icon.getContactInfo() != null && icon.getContactInfo().toLowerCase()
            .contains(lowerQuery));
  }
}
