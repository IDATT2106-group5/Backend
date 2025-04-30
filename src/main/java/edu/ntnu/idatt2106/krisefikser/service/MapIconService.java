package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MapIcon;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MapIconRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
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
   * @return the created map icon
   */
  @Transactional
  public MapIconResponseDto createMapIcon(MapIconRequestDto request) {
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

    mapIcon = mapIconRepository.save(mapIcon);

    return MapIconResponseDto.fromEntity(mapIcon);
  }

  /**
   * Updates an existing map icon.
   *
   * @param id      the ID of the map icon
   * @param request the updated data
   * @return the updated map icon
   */
  @Transactional
  public MapIconResponseDto updateMapIcon(Long id, MapIconRequestDto request) {
    MapIcon mapIcon = mapIconRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Map icon not found"));

    mapIcon.setType(request.getType());
    mapIcon.setAddress(request.getAddress());
    mapIcon.setLatitude(request.getLatitude());
    mapIcon.setLongitude(request.getLongitude());
    mapIcon.setDescription(request.getDescription());
    mapIcon.setOpeningHours(request.getOpeningHours());
    mapIcon.setContactInfo(request.getContactInfo());

    mapIcon = mapIconRepository.save(mapIcon);

    return MapIconResponseDto.fromEntity(mapIcon);
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

    return allIcons.stream()
        .filter(icon -> icon.getLatitude() != null && icon.getLongitude() != null)
        .filter(icon -> isWithinRadius(latitude, longitude, icon.getLatitude(), icon.getLongitude(),
            radiusKm))
        .filter(icon -> matchesQuery(icon, query))
        .map(MapIconResponseDto::fromEntity)
        .collect(Collectors.toList());
  }

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
