package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ScenarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the ScenarioService class.
 */
public class ScenarioServiceTest {

  @Mock
  private ScenarioRepository scenarioRepository;

  @InjectMocks
  private ScenarioService scenarioService;

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
    void createScenario_shouldSucceed_whenValidInput() {
      // Arrange
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("Power Outage");
      request.setDescription("No electricity");
      request.setToDo("Use flashlight");
      request.setPackingList("Batteries, candles");

      Scenario scenario = request.toEntity();

      when(scenarioRepository.save(any(Scenario.class))).thenReturn(scenario);

      // Act & Assert
      assertDoesNotThrow(() -> scenarioService.createScenario(request));
      verify(scenarioRepository).save(any(Scenario.class));
    }

    @Test
    void createScenario_shouldFail_whenNameIsNull() {
      // Arrange
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName(null);

      // Act
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> scenarioService.createScenario(request));

      // Assert
      assertEquals("Scenario name is required", exception.getMessage());
      verifyNoInteractions(scenarioRepository);
    }

    @Test
    void createScenario_shouldFail_whenNameIsBlank() {
      // Arrange
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("   ");

      // Act
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> scenarioService.createScenario(request));

      // Assert
      assertEquals("Scenario name is required", exception.getMessage());
      verifyNoInteractions(scenarioRepository);
    }
  }

  /**
   * Test cases for the updateScenario method.
   */
  @Nested
  class UpdateScenarioTests {

    @Test
    void updateScenario_shouldSucceed_whenValidInput() {
      // Arrange
      Long scenarioId = 1L;
      Scenario existing = new Scenario();
      existing.setId(scenarioId);
      existing.setName("Old Name");
      existing.setDescription("Old desc");

      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("Updated Name");
      request.setDescription("Updated Description");
      request.setToDo("Updated ToDo");
      request.setPackingList("Updated PackingList");

      when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(existing));

      // Act & Assert
      assertDoesNotThrow(() -> scenarioService.updateScenario(scenarioId, request));
      verify(scenarioRepository).findById(scenarioId);
      verify(scenarioRepository).save(any(Scenario.class));
    }

    @Test
    void updateScenario_shouldFail_whenNameIsNull() {
      // Arrange
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName(null);

      // Act
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> scenarioService.updateScenario(1L, request));

      // Assert
      assertEquals("Scenario name is required", exception.getMessage());
      verifyNoInteractions(scenarioRepository);
    }

    @Test
    void updateScenario_shouldFail_whenNameIsBlank() {
      // Arrange
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("   ");

      // Act
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> scenarioService.updateScenario(1L, request));

      // Assert
      assertEquals("Scenario name is required", exception.getMessage());
      verifyNoInteractions(scenarioRepository);
    }

    @Test
    void updateScenario_shouldFail_whenScenarioNotFound() {
      // Arrange
      Long scenarioId = 42L;
      ScenarioRequestDto request = new ScenarioRequestDto();
      request.setName("Updated Name");

      when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

      // Act
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> scenarioService.updateScenario(scenarioId, request));

      // Assert
      assertEquals("Scenario not found with ID: " + scenarioId, exception.getMessage());
      verify(scenarioRepository).findById(scenarioId);
      verifyNoMoreInteractions(scenarioRepository);
    }
  }

  /**
   * Test cases for the getAllScenarios method.
   */
  @Nested
  class GetAllScenariosTests {

    @Test
    void shouldReturnListOfScenarioResponseDtos() {
      // Arrange
      Scenario scenario1 = new Scenario(1L, "Power Outage", "No power", "Stay warm", "Batteries");
      Scenario scenario2 = new Scenario(2L, "Flood", "Water rising", "Evacuate", "Life jacket");

      when(scenarioRepository.findAll()).thenReturn(List.of(scenario1, scenario2));

      // Act
      List<ScenarioResponseDto> result = scenarioService.getAllScenarios();

      // Assert
      assertEquals(2, result.size());
      assertEquals("Power Outage", result.get(0).getName());
      assertEquals("Flood", result.get(1).getName());
      verify(scenarioRepository).findAll();
    }
  }

}
