package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
      List<MapIcon> icons = new ArrayList<>();

      MapIcon near = new MapIcon();
      near.setLatitude(63.42);
      near.setLongitude(10.39);
      near.setDescription("nearby");
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
}
