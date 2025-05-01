package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.IncidentRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ScenarioRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the IncidentService class.
 */
class IncidentServiceTest {

  @Mock
  private IncidentRepository incidentRepository;

  @Mock
  private ScenarioRepository scenarioRepository;

  @InjectMocks
  private IncidentService incidentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Test cases for the createIncident method.
   */
  @Nested
  class CreateIncidentTests {

    @Test
    void createIncident_shouldSucceed_whenScenarioExists() {
      Long scenarioId = 1L;
      Scenario scenario = new Scenario();
      scenario.setId(scenarioId);

      IncidentRequestDto request = new IncidentRequestDto();
      request.setName("Flood");
      request.setDescription("Severe flooding");
      request.setLatitude(60.0);
      request.setLongitude(10.0);
      request.setImpactRadius(5.0);
      request.setSeverity("yellow");
      request.setStartedAt(LocalDateTime.now());
      request.setEndedAt(LocalDateTime.now().plusHours(2));
      request.setScenarioId(scenarioId);

      when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
      when(incidentRepository.save(any(Incident.class))).thenReturn(new Incident());

      assertDoesNotThrow(() -> incidentService.createIncident(request));

      verify(scenarioRepository).findById(scenarioId);
      verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void createIncident_shouldFail_whenScenarioIdMissing() {
      IncidentRequestDto request = new IncidentRequestDto();
      request.setName("Earthquake");

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> incidentService.createIncident(request));

      assertEquals("Scenario ID is required for creating an incident.", exception.getMessage());
      verifyNoInteractions(scenarioRepository, incidentRepository);
    }

    @Test
    void createIncident_shouldFail_whenScenarioNotFound() {
      Long scenarioId = 42L;
      IncidentRequestDto request = new IncidentRequestDto();
      request.setName("Explosion");
      request.setScenarioId(scenarioId);

      when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> incidentService.createIncident(request));

      assertEquals("Scenario not found with ID: " + scenarioId, exception.getMessage());
      verify(scenarioRepository).findById(scenarioId);
      verifyNoMoreInteractions(incidentRepository);
    }
  }
}
