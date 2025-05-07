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
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Severity;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.IncidentRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ScenarioRepository;
import java.time.LocalDateTime;
import java.util.List;
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

  @Mock
  private NotificationService notificationService;

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

  /**
   * Test cases for the updateIncident method.
   */
  @Nested
  class UpdateIncidentTests {

    @Test
    void updateIncident_shouldSucceed_whenIncidentAndScenarioExist() {
      Long incidentId = 2L;
      Long scenarioId = 1L;

      Incident incident = new Incident();
      incident.setId(incidentId);

      Scenario scenario = new Scenario();
      scenario.setId(scenarioId);

      IncidentRequestDto request = new IncidentRequestDto();
      request.setName("Updated Incident");
      request.setDescription("Updated Description");
      request.setLatitude(63.0);
      request.setLongitude(10.0);
      request.setImpactRadius(3.5);
      request.setSeverity("red");
      request.setStartedAt(LocalDateTime.now());
      request.setEndedAt(LocalDateTime.now().plusHours(1));
      request.setScenarioId(scenarioId);

      when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));
      when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
      when(incidentRepository.save(any(Incident.class))).thenReturn(incident);

      assertDoesNotThrow(() -> incidentService.updateIncident(incidentId, request));
      verify(incidentRepository).findById(incidentId);
      verify(scenarioRepository).findById(scenarioId);
      verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void updateIncident_shouldFail_whenIncidentNotFound() {
      Long incidentId = 99L;
      IncidentRequestDto request = new IncidentRequestDto();
      request.setScenarioId(1L);

      when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> incidentService.updateIncident(incidentId, request));

      assertEquals("Incident not found with ID: " + incidentId, exception.getMessage());
      verify(incidentRepository).findById(incidentId);
      verifyNoMoreInteractions(incidentRepository);
    }

    @Test
    void updateIncident_shouldFail_whenScenarioIdIsMissing() {
      Long incidentId = 2L;
      Incident incident = new Incident();
      incident.setId(incidentId);

      IncidentRequestDto request = new IncidentRequestDto();

      when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> incidentService.updateIncident(incidentId, request));

      assertEquals("Scenario ID is required when updating an incident.", exception.getMessage());
      verify(incidentRepository).findById(incidentId);
      verifyNoMoreInteractions(incidentRepository, scenarioRepository);
    }

    @Test
    void updateIncident_shouldFail_whenScenarioNotFound() {
      Long incidentId = 2L;
      Long scenarioId = 42L;

      Incident incident = new Incident();
      incident.setId(incidentId);

      IncidentRequestDto request = new IncidentRequestDto();
      request.setScenarioId(scenarioId);

      when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));
      when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> incidentService.updateIncident(incidentId, request));

      assertEquals("Scenario not found with ID: " + scenarioId, exception.getMessage());
      verify(incidentRepository).findById(incidentId);
      verify(scenarioRepository).findById(scenarioId);
      verifyNoMoreInteractions(incidentRepository);
    }
  }

  /**
   * Test cases for the deleteIncident method.
   */
  @Nested
  class DeleteIncidentTests {

    @Test
    void deleteIncident_shouldSucceed_whenIncidentExists() {
      Long id = 1L;
      when(incidentRepository.existsById(id)).thenReturn(true);

      assertDoesNotThrow(() -> incidentService.deleteIncident(id));
      verify(incidentRepository).existsById(id);
      verify(incidentRepository).deleteById(id);
    }

    @Test
    void deleteIncident_shouldFail_whenIncidentDoesNotExist() {
      Long id = 99L;
      when(incidentRepository.existsById(id)).thenReturn(false);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> incidentService.deleteIncident(id));

      assertEquals("Incident not found with ID: " + id, exception.getMessage());
      verify(incidentRepository).existsById(id);
      verifyNoMoreInteractions(incidentRepository);
    }
  }

  /**
   * Test cases for the getAllIncidents method.
   */
  @Nested
  class GetAllIncidentsTests {

    @Test
    void getAllIncidents_shouldReturnListOfIncidentResponseDtos() {
      // Arrange
      Scenario scenario = new Scenario();
      scenario.setId(1L);

      Incident incident1 = new Incident();
      incident1.setId(1L);
      incident1.setName("Flom");
      incident1.setDescription("Flom i Oslo");
      incident1.setLatitude(63.42);
      incident1.setLongitude(10.39);
      incident1.setImpactRadius(2.5);
      incident1.setSeverity(Severity.RED);
      incident1.setStartedAt(LocalDateTime.now());
      incident1.setEndedAt(LocalDateTime.now().plusHours(1));
      incident1.setScenario(scenario);

      Incident incident2 = new Incident();
      incident2.setId(2L);
      incident2.setName("Brann");
      incident2.setDescription("Brann i skogen");
      incident2.setLatitude(63.43);
      incident2.setLongitude(10.40);
      incident2.setImpactRadius(1.5);
      incident2.setSeverity(Severity.YELLOW);
      incident2.setStartedAt(LocalDateTime.now());
      incident2.setEndedAt(LocalDateTime.now().plusHours(2));
      incident2.setScenario(scenario);

      when(incidentRepository.findAll()).thenReturn(List.of(incident1, incident2));

      // Act
      List<IncidentResponseDto> result = incidentService.getAllIncidents();

      // Assert
      assertEquals(2, result.size());
      assertEquals("Flom", result.get(0).getName());
      assertEquals("Brann", result.get(1).getName());
      verify(incidentRepository).findAll();
    }
  }
}
