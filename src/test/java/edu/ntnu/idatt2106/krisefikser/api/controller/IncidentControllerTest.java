package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.IncidentService;
import java.time.LocalDateTime;
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
class IncidentControllerTest {

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
}

