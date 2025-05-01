package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.IncidentRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ScenarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for handling incident-related operations.
 */
@Service
public class IncidentService {

  private static final Logger logger = LoggerFactory.getLogger(IncidentService.class);
  private final IncidentRepository incidentRepository;
  private final ScenarioRepository scenarioRepository;

  /**
   * Constructor for IncidentService.
   *
   * @param incidentRepository The repository for incident-related operations.
   * @param scenarioRepository The repository for scenario-related operations.
   */
  public IncidentService(IncidentRepository incidentRepository,
      ScenarioRepository scenarioRepository) {
    this.incidentRepository = incidentRepository;
    this.scenarioRepository = scenarioRepository;
  }

  /**
   * Creates a new incident.
   *
   * @param request the incident request containing details for the new incident
   */
  public void createIncident(IncidentRequestDto request) {
    if (request.getScenarioId() == null) {
      logger.error("Scenario ID is required for creating an incident.");
      throw new IllegalArgumentException("Scenario ID is required for creating an incident.");
    }

    Long scenarioId = request.getScenarioId();

    Scenario scenario = scenarioRepository.findById(scenarioId)
        .orElseThrow(() -> {
          logger.error("Scenario not found with ID: {}", scenarioId);
          return new IllegalArgumentException("Scenario not found with ID: " + scenarioId);
        });

    Incident incident = request.toEntity(scenario);
    incidentRepository.save(incident);
  }
}
