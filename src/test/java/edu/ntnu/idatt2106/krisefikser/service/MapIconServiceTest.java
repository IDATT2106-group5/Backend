package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.MapIcon;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.MapIconType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.MapIconRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the MapIconService class.
 */
class MapIconServiceTest {

  @Mock
  private MapIconRepository mapIconRepository;

  @InjectMocks
  private MapIconService mapIconService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Test cases for the createMapIcon method.
   */
  @Nested
  class CreateMapIconTests {

    @Test
    void createMapIcon_shouldSucceed_withCoordinates() {
      MapIconRequestDto request = new MapIconRequestDto();
      request.setType(MapIconType.MEETINGPLACE);
      request.setLatitude(63.42);
      request.setLongitude(10.39);

      when(mapIconRepository.save(any(MapIcon.class))).thenReturn(new MapIcon());

      assertDoesNotThrow(() -> mapIconService.createMapIcon(request));
      verify(mapIconRepository).save(any(MapIcon.class));
    }

    @Test
    void createMapIcon_shouldSucceed_withAddress() {
      MapIconRequestDto request = new MapIconRequestDto();
      request.setType(MapIconType.FOODSTATION);
      request.setAddress("Some Address, City, Country");

      when(mapIconRepository.save(any(MapIcon.class))).thenReturn(new MapIcon());

      assertDoesNotThrow(() -> mapIconService.createMapIcon(request));
      verify(mapIconRepository).save(any(MapIcon.class));
    }

    @Test
    void createMapIcon_shouldFail_whenNoLocationProvided() {
      MapIconRequestDto request = new MapIconRequestDto();
      request.setType(MapIconType.SHELTER);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> mapIconService.createMapIcon(request));

      assertEquals("Either coordinates or address must be provided.", exception.getMessage());
      verifyNoInteractions(mapIconRepository);
    }
  }

  /**
   * Test cases for the updateMapIcon method.
   */
  @Nested
  class UpdateMapIconTests {

    @Test
    void updateMapIcon_shouldSucceed_whenMapIconExists() {
      Long id = 1L;
      MapIcon existing = new MapIcon();
      existing.setId(id);
      when(mapIconRepository.findById(id)).thenReturn(Optional.of(existing));

      MapIconRequestDto request = new MapIconRequestDto();
      request.setType(MapIconType.SHELTER);

      assertDoesNotThrow(() -> mapIconService.updateMapIcon(id, request));
      verify(mapIconRepository).save(any(MapIcon.class));
    }

    @Test
    void updateMapIcon_shouldFail_whenMapIconNotFound() {
      Long id = 999L;
      when(mapIconRepository.findById(id)).thenReturn(Optional.empty());

      MapIconRequestDto request = new MapIconRequestDto();
      request.setType(MapIconType.SHELTER);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> mapIconService.updateMapIcon(id, request));

      assertEquals("Map icon not found", exception.getMessage());
      verify(mapIconRepository).findById(id);
      verifyNoMoreInteractions(mapIconRepository);
    }
  }

  /**
   * Test cases for the deleteMapIcon method.
   */
  @Nested
  class DeleteMapIconTests {

    @Test
    void deleteMapIcon_shouldSucceed_whenExists() {
      Long id = 1L;
      when(mapIconRepository.existsById(id)).thenReturn(true);

      assertDoesNotThrow(() -> mapIconService.deleteMapIcon(id));
      verify(mapIconRepository).deleteById(id);
    }

    @Test
    void deleteMapIcon_shouldFail_whenNotFound() {
      Long id = 99L;
      when(mapIconRepository.existsById(id)).thenReturn(false);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> mapIconService.deleteMapIcon(id));

      assertEquals("Map icon not found", exception.getMessage());
      verify(mapIconRepository).existsById(id);
      verifyNoMoreInteractions(mapIconRepository);
    }
  }

  /**
   * Test cases for the getMapIcons method.
   */
  @Nested
  class GetMapIconsTests {

    @Test
    void getMapIcons_shouldReturnOnlyNearbyMatches() {

      MapIcon near = new MapIcon();
      near.setLatitude(63.42);
      near.setLongitude(10.39);
      near.setDescription("nearby");
      List<MapIcon> icons = new ArrayList<>();
      icons.add(near);

      MapIcon far = new MapIcon();
      far.setLatitude(50.0);
      far.setLongitude(8.0);
      far.setDescription("far away");
      icons.add(far);

      when(mapIconRepository.findAll()).thenReturn(icons);

      List<MapIconResponseDto> result = mapIconService.getMapIcons(63.42, 10.39, 10, "nearby");

      assertEquals(1, result.size());
    }
  }

  @Nested
  class FindClosestMapIconTests {

    @Test
    void findClosestMapIcon_shouldReturnClosest_whenMultipleIconsExist() {
      // Create a closer icon
      MapIcon closerIcon = new MapIcon();
      closerIcon.setId(1L);
      closerIcon.setType(MapIconType.SHELTER);
      closerIcon.setLatitude(63.41); // Very close to search point
      closerIcon.setLongitude(10.38);

      // Create a farther icon
      MapIcon fartherIcon = new MapIcon();
      fartherIcon.setId(2L);
      fartherIcon.setType(MapIconType.SHELTER);
      fartherIcon.setLatitude(63.43); // Further from search point
      fartherIcon.setLongitude(10.41);

      List<MapIcon> icons = new ArrayList<>();
      icons.add(fartherIcon);
      icons.add(closerIcon);

      when(mapIconRepository.findAll()).thenReturn(icons);

      // Act
      MapIconResponseDto result = mapIconService.findClosestMapIcon(63.42, 10.39, null);

      // Assert
      assertNotNull(result);
      assertEquals(1L, result.getId());
    }

    @Test
    void findClosestMapIcon_shouldFilterByType_whenTypeProvided() {
      // Arrange
      List<MapIcon> icons = new ArrayList<>();

      MapIcon foodStation = new MapIcon();
      foodStation.setId(1L);
      foodStation.setType(MapIconType.FOODSTATION);
      foodStation.setLatitude(63.42);
      foodStation.setLongitude(10.39);

      when(mapIconRepository.findByType(MapIconType.FOODSTATION)).thenReturn(List.of(foodStation));

      // Act
      MapIconResponseDto result = mapIconService.findClosestMapIcon(63.42, 10.39,
          MapIconType.FOODSTATION);

      // Assert
      assertNotNull(result);
      assertEquals(MapIconType.FOODSTATION, result.getType());
    }

    @Test
    void findClosestMapIcon_shouldReturnNull_whenNoIconsFound() {
      // Arrange
      when(mapIconRepository.findAll()).thenReturn(new ArrayList<>());

      // Act
      MapIconResponseDto result = mapIconService.findClosestMapIcon(63.42, 10.39, null);

      // Assert
      assertNull(result);
    }

    @Test
    void calculateDistance_shouldReturnCorrectDistance() {
      // Arrange
      double lat1 = 63.42;
      double lon1 = 10.39;
      double lat2 = 63.43;
      double lon2 = 10.41;

      // Act
      double distance = mapIconService.calculateDistance(lat1, lon1, lat2, lon2);

      // Assert
      // Actual calculated distance is about 1.49 km
      assertTrue(distance > 1.4 && distance < 1.6,
          "Distance should be approximately 1.4-1.6 km, but was " + distance);
    }
  }

  /**
   * Test cases for the getMapIcons method with query parameter.
   */
  @Nested
  class MatchesQueryTests {

    @Test
    void getMapIcons_shouldReturnAll_whenQueryIsNull() {
      // Arrange
      MapIcon icon = createIcon("Hospital", "123 Main St", "555-1234");
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      // Mock the public calculateDistance method instead
      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, null);

      // Assert
      assertEquals(1, result.size());
      assertEquals(icon.getId(), result.get(0).getId());
      verify(mapIconRepository).findAll();
    }

    private MapIcon createIcon(String description, String address, String contactInfo) {
      MapIcon icon = new MapIcon();
      icon.setId(1L);
      icon.setType(MapIconType.HOSPITAL);
      icon.setDescription(description);
      icon.setAddress(address);
      icon.setContactInfo(contactInfo);
      icon.setLatitude(0.0);
      icon.setLongitude(0.0);
      return icon;
    }

    @Test
    void getMapIcons_shouldReturnAll_whenQueryIsEmpty() {
      // Same approach as above but with empty string
      MapIcon icon = createIcon("Hospital", "123 Main St", "555-1234");
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "");

      assertEquals(1, result.size());
      assertEquals(icon.getId(), result.get(0).getId());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldMatchDescription() {
      // Arrange
      MapIcon icon = createIcon("Emergency Hospital", null, null);
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "emergency");

      // Assert
      assertEquals(1, result.size());
      assertEquals(icon.getId(), result.get(0).getId());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldMatchAddress() {
      // Arrange
      MapIcon icon = createIcon(null, "123 Main Street", null);
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "main");

      // Assert
      assertEquals(1, result.size());
      assertEquals(icon.getId(), result.get(0).getId());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldMatchContactInfo() {
      // Arrange
      MapIcon icon = createIcon(null, null, "email@example.com");
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "email");

      // Assert
      assertEquals(1, result.size());
      assertEquals(icon.getId(), result.get(0).getId());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldNotMatch_whenNoneOfTheFieldsMatch() {
      // Arrange
      MapIcon icon = createIcon("Hospital", "123 Main St", "555-1234");
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "pharmacy");

      // Assert
      assertEquals(0, result.size());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldHandleAllNullFields() {
      // Arrange
      MapIcon icon = createIcon(null, null, null);
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "hospital");

      // Assert
      assertEquals(0, result.size());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldHandleCaseSensitivity() {
      // Arrange
      MapIcon icon = createIcon("Emergency HOSPITAL", null, null);
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);
      org.mockito.Mockito.doReturn(5.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble(),
          org.mockito.ArgumentMatchers.anyDouble());

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, "hospital");

      // Assert
      assertEquals(1, result.size());
      assertEquals(icon.getId(), result.get(0).getId());
      verify(mapIconRepository).findAll();
    }

    @Test
    void getMapIcons_shouldFilterByRadius() {
      // Arrange
      MapIcon icon = createIcon("Hospital", "123 Main St", "555-1234");
      List<MapIcon> icons = List.of(icon);
      when(mapIconRepository.findAll()).thenReturn(icons);

      // Create a spy that properly intercepts the isWithinRadius method call
      MapIconService spyService = org.mockito.Mockito.spy(mapIconService);

      // Use PowerMockito to mock the private method
      // Since we can't do that directly, we mock calculateDistance with a large value
      // The key is to make sure we're mocking all possible calls to calculateDistance
      org.mockito.Mockito.doReturn(20.0).when(spyService).calculateDistance(
          org.mockito.ArgumentMatchers.eq(0.0),
          org.mockito.ArgumentMatchers.eq(0.0),
          org.mockito.ArgumentMatchers.eq(0.0),
          org.mockito.ArgumentMatchers.eq(0.0));

      // Act
      List<MapIconResponseDto> result = spyService.getMapIcons(0.0, 0.0, 10.0, null);

      // Assert
      assertEquals(0, result.size());
      verify(mapIconRepository).findAll();
    }
  }
}
