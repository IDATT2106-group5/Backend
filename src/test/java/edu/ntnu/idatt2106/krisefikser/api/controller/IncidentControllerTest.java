package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentResponseDto;
import edu.ntnu.idatt2106.krisefikser.service.IncidentService;
import java.time.LocalDateTime;
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
 * Unit tests for the IncidentController class.
 */
public class IncidentControllerTest {

  @Mock
  private IncidentService incidentService;

  @InjectMocks
  private IncidentController incidentController;

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
    void shouldCreateIncidentSuccessfully() {
      IncidentRequestDto request = new IncidentRequestDto();
      request.setName("Test Incident");
      request.setDescription("Test");
      request.setLatitude(63.42);
      request.setLongitude(10.39);
      request.setImpactRadius(1.0);
      request.setSeverity("yellow");
      request.setStartedAt(LocalDateTime.now());
      request.setScenarioId(1L);

      ResponseEntity<Map<String, String>> response = incidentController.createIncident(request);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertEquals("Incident created successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      IncidentRequestDto request = new IncidentRequestDto();
      doThrow(new IllegalArgumentException("Invalid data")).when(incidentService)
          .createIncident(request);

      ResponseEntity<Map<String, String>> response = incidentController.createIncident(request);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid data", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      IncidentRequestDto request = new IncidentRequestDto();
      doThrow(new RuntimeException("Unexpected error")).when(incidentService)
          .createIncident(request);

      ResponseEntity<Map<String, String>> response = incidentController.createIncident(request);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the updateIncident method.
   */
  @Nested
  class UpdateIncidentTests {

    @Test
    void shouldUpdateIncidentSuccessfully() {
      IncidentRequestDto request = new IncidentRequestDto();
      request.setName("Updated Incident");
      request.setDescription("Updated");
      request.setLatitude(63.42);
      request.setLongitude(10.39);
      request.setImpactRadius(2.0);
      request.setSeverity("yellow");
      request.setStartedAt(LocalDateTime.now());
      request.setScenarioId(1L);

      ResponseEntity<Map<String, String>> response = incidentController.updateIncident(1L, request);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Incident updated successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      IncidentRequestDto request = new IncidentRequestDto();
      doThrow(new IllegalArgumentException("Invalid update")).when(incidentService)
          .updateIncident(1L, request);

      ResponseEntity<Map<String, String>> response = incidentController.updateIncident(1L, request);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid update", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      IncidentRequestDto request = new IncidentRequestDto();
      doThrow(new RuntimeException("Unexpected failure")).when(incidentService)
          .updateIncident(1L, request);

      ResponseEntity<Map<String, String>> response = incidentController.updateIncident(1L, request);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  @Nested
  class DeleteIncidentTests {

    @Test
    void shouldDeleteIncidentSuccessfully() {
      Long id = 1L;

      ResponseEntity<Map<String, String>> response = incidentController.deleteIncident(id);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Incident deleted successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      Long id = 42L;
      doThrow(new IllegalArgumentException("Incident not found")).when(incidentService)
          .deleteIncident(id);

      ResponseEntity<Map<String, String>> response = incidentController.deleteIncident(id);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Incident not found", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      Long id = 99L;
      doThrow(new RuntimeException("Unexpected failure")).when(incidentService).deleteIncident(id);

      ResponseEntity<Map<String, String>> response = incidentController.deleteIncident(id);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Test cases for the getAllIncidents method.
   */
  @Nested
  class GetAllIncidentsTests {

    @Test
    void shouldReturnListOfIncidentsSuccessfully() {
      IncidentResponseDto incident1 = new IncidentResponseDto();
      incident1.setId(1L);
      incident1.setName("Flom");

      IncidentResponseDto incident2 = new IncidentResponseDto();
      incident2.setId(2L);
      incident2.setName("Brann");

      when(incidentService.getAllIncidents()).thenReturn(List.of(incident1, incident2));

      ResponseEntity<List<IncidentResponseDto>> response = incidentController.getAllIncidents();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(2, response.getBody().size());
      assertEquals("Flom", response.getBody().get(0).getName());
      assertEquals("Brann", response.getBody().get(1).getName());
    }

    @Test
    void shouldReturnInternalServerError_whenExceptionThrown() {
      when(incidentService.getAllIncidents()).thenThrow(new RuntimeException("Unexpected failure"));

      ResponseEntity<List<IncidentResponseDto>> response = incidentController.getAllIncidents();

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
  }

}

