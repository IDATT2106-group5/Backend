package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.MapIconResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.MapIconType;
import edu.ntnu.idatt2106.krisefikser.service.MapIconService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the MapIconController class.
 */
class MapIconControllerTest {

  @Mock
  private MapIconService mapIconService;

  @InjectMocks
  private MapIconController mapIconController;

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
    void shouldCreateMapIconSuccessfully() {
      MapIconRequestDto request = new MapIconRequestDto();
      ResponseEntity<Map<String, String>> response = mapIconController.createMapIcon(request);
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertEquals("Map icon created successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      MapIconRequestDto request = new MapIconRequestDto();
      doThrow(new IllegalArgumentException("Invalid data")).when(mapIconService)
          .createMapIcon(request);
      ResponseEntity<Map<String, String>> response = mapIconController.createMapIcon(request);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid data", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenExceptionThrown() {
      MapIconRequestDto request = new MapIconRequestDto();
      doThrow(new RuntimeException("Unexpected error")).when(mapIconService).createMapIcon(request);
      ResponseEntity<Map<String, String>> response = mapIconController.createMapIcon(request);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the updateMapIcon method.
   */
  @Nested
  class UpdateMapIconTests {

    @Test
    void shouldUpdateMapIconSuccessfully() {
      MapIconRequestDto request = new MapIconRequestDto();
      ResponseEntity<Map<String, String>> response = mapIconController.updateMapIcon(1L, request);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Map icon updated successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequestOnUpdate_whenIllegalArgumentExceptionThrown() {
      MapIconRequestDto request = new MapIconRequestDto();
      doThrow(new IllegalArgumentException("Not found")).when(mapIconService)
          .updateMapIcon(1L, request);
      ResponseEntity<Map<String, String>> response = mapIconController.updateMapIcon(1L, request);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Not found", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerErrorOnUpdate_whenExceptionThrown() {
      MapIconRequestDto request = new MapIconRequestDto();
      doThrow(new RuntimeException("Unexpected error")).when(mapIconService)
          .updateMapIcon(1L, request);
      ResponseEntity<Map<String, String>> response = mapIconController.updateMapIcon(1L, request);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the deleteMapIcon method.
   */
  @Nested
  class DeleteMapIconTests {

    @Test
    void shouldDeleteMapIconSuccessfully() {
      ResponseEntity<Map<String, String>> response = mapIconController.deleteMapIcon(1L);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Map icon deleted successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequestOnDelete_whenIllegalArgumentExceptionThrown() {
      doThrow(new IllegalArgumentException("Not found")).when(mapIconService).deleteMapIcon(1L);
      ResponseEntity<Map<String, String>> response = mapIconController.deleteMapIcon(1L);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Not found", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerErrorOnDelete_whenExceptionThrown() {
      doThrow(new RuntimeException("Unexpected error")).when(mapIconService).deleteMapIcon(1L);
      ResponseEntity<Map<String, String>> response = mapIconController.deleteMapIcon(1L);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the getMapIcons method.
   */
  @Nested
  class GetMapIconsTests {

    @Test
    void shouldReturnMapIconsSuccessfully() {
      MapIconResponseDto responseDto = new MapIconResponseDto();
      responseDto.setType(MapIconType.MEETINGPLACE);
      when(mapIconService.getMapIcons(10.0, 10.0, 5.0, null)).thenReturn(List.of(responseDto));
      ResponseEntity<List<MapIconResponseDto>> response = mapIconController.getMapIcons(10.0, 10.0,
          5.0, null);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnInternalServerErrorOnGet_whenExceptionThrown() {
      when(mapIconService.getMapIcons(10.0, 10.0, 5.0, null))
          .thenThrow(new RuntimeException("Unexpected error"));
      ResponseEntity<List<MapIconResponseDto>> response = mapIconController.getMapIcons(10.0, 10.0,
          5.0, null);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

  @Nested
  class FindClosestMapIconTests {

    @Test
    void shouldReturnClosestMapIcon_whenFound() {
      // Arrange
      MapIconResponseDto icon = new MapIconResponseDto();
      icon.setId(1L);
      icon.setType(MapIconType.SHELTER);
      icon.setLatitude(63.42);
      icon.setLongitude(10.39);

      when(mapIconService.findClosestMapIcon(63.42, 10.39, null)).thenReturn(icon);

      // Act
      ResponseEntity<?> response = mapIconController.findClosestMapIcon(63.42, 10.39, null);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(MapIconType.SHELTER, ((MapIconResponseDto) response.getBody()).getType());
    }

    @Test
    void shouldReturnNotFound_whenNoIconFound() {
      // Arrange
      when(mapIconService.findClosestMapIcon(63.42, 10.39, null)).thenReturn(null);

      // Act
      ResponseEntity<?> response = mapIconController.findClosestMapIcon(63.42, 10.39, null);

      // Assert
      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnInternalServerError_whenExceptionThrown() {
      // Arrange
      when(mapIconService.findClosestMapIcon(63.42, 10.39, MapIconType.HOSPITAL))
          .thenThrow(new RuntimeException("Database error"));

      // Act
      ResponseEntity<?> response = mapIconController.findClosestMapIcon(63.42, 10.39,
          MapIconType.HOSPITAL);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Internal server error",
          ((Map<String, String>) response.getBody()).get("error"));
    }
  }
}
