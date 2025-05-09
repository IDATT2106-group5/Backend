package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.ScenarioService;
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
 * Unit tests for the ScenarioController class.
 */
class ScenarioControllerTest {

  @Mock
  private ScenarioService scenarioService;

  @InjectMocks
  private ScenarioController scenarioController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Test cases for the createScenario method.
   */
  @Nested
  class CreateScenarioTests {

    @Test
    void shouldCreateScenarioSuccessfully() {
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("Power Outage");
      request.setDescription("Loss of electricity");
      request.setToDo("Find candles");
      request.setPackingList("Flashlight, batteries");

      ResponseEntity<Map<String, String>> response = scenarioController.createScenario(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertEquals("Scenario created successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      ScenarioRequestDto request = new ScenarioRequestDto();
      doThrow(new IllegalArgumentException("Invalid data")).when(scenarioService)
          .createScenario(request);

      ResponseEntity<Map<String, String>> response = scenarioController.createScenario(request);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid data", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      ScenarioRequestDto request = new ScenarioRequestDto();
      doThrow(new RuntimeException("Unexpected error")).when(scenarioService)
          .createScenario(request);

      ResponseEntity<Map<String, String>> response = scenarioController.createScenario(request);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the updateScenario method.
   */
  @Nested
  class UpdateScenarioTests {

    @Test
    void shouldUpdateScenarioSuccessfully() {
      // Arrange
      Long scenarioId = 1L;
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("Updated Scenario");
      request.setDescription("Updated desc");
      request.setToDo("Updated to-do");
      request.setPackingList("Updated packing list");

      // Act
      ResponseEntity<Map<String, String>> response = scenarioController.updateScenario(scenarioId,
          request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Scenario updated successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      // Arrange
      Long scenarioId = 1L;
      ScenarioRequestDto request = new ScenarioRequestDto();
      doThrow(new IllegalArgumentException("Invalid update")).when(scenarioService)
          .updateScenario(scenarioId, request);

      // Act
      ResponseEntity<Map<String, String>> response = scenarioController.updateScenario(scenarioId,
          request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid update", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      // Arrange
      Long scenarioId = 1L;
      ScenarioRequestDto request = new ScenarioRequestDto();
      doThrow(new RuntimeException("Unexpected error")).when(scenarioService)
          .updateScenario(scenarioId, request);

      // Act
      ResponseEntity<Map<String, String>> response = scenarioController.updateScenario(scenarioId,
          request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the getAllScenarios method.
   */
  @Nested
  class GetAllScenariosTests {

    @Test
    void shouldReturnListOfScenariosSuccessfully() {
      // Arrange
      ScenarioResponseDto scenario1 = new ScenarioResponseDto();
      scenario1.setId(1L);
      scenario1.setName("Power Outage");

      ScenarioResponseDto scenario2 = new ScenarioResponseDto();
      scenario2.setId(2L);
      scenario2.setName("Flood");

      when(scenarioService.getAllScenarios()).thenReturn(List.of(scenario1, scenario2));

      // Act
      ResponseEntity<List<ScenarioResponseDto>> response = scenarioController.getAllScenarios();

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(2, response.getBody().size());
      assertEquals("Power Outage", response.getBody().get(0).getName());
      assertEquals("Flood", response.getBody().get(1).getName());
    }

    @Test
    void shouldReturnInternalServerError_whenExceptionThrown() {
      // Arrange
      when(scenarioService.getAllScenarios()).thenThrow(new RuntimeException("DB error"));

      // Act
      ResponseEntity<List<ScenarioResponseDto>> response = scenarioController.getAllScenarios();

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

}
