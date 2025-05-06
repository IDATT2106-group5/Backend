package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Severity;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.IncidentRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ScenarioRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.List;
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
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  /**
   * Constructor for IncidentService.
   *
   * @param incidentRepository  The repository for incident-related operations.
   * @param scenarioRepository  The repository for scenario-related operations.
   * @param notificationService the notification service
   */
  public IncidentService(IncidentRepository incidentRepository,
                         ScenarioRepository scenarioRepository,
                         NotificationService notificationService, UserRepository userRepository) {
    this.incidentRepository = incidentRepository;
    this.scenarioRepository = scenarioRepository;
    this.notificationService = notificationService;
    this.userRepository = userRepository;
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
    notificationService.notifyIncident("[EMERGENCY ALERT]: " + scenario.getName() +
        " is in progress near you. Specific instructions can be found in the app.",
        incident);
    logger.info("Incident created successfully: {}", incident.getName());
  }

  /**
   * Update incident.
   *
   * @param id      the id
   * @param request the request
   */
  public void updateIncident(Long id, IncidentRequestDto request) {
    Incident incident = incidentRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("Incident not found with ID: {}", id);
          return new IllegalArgumentException("Incident not found with ID: " + id);
        });

    if (request.getScenarioId() == null) {
      logger.error("Scenario ID is required when updating an incident.");
      throw new IllegalArgumentException("Scenario ID is required when updating an incident.");
    }

    Scenario scenario = scenarioRepository.findById(request.getScenarioId())
        .orElseThrow(() -> {
          logger.error("Scenario not found with ID: {}", request.getScenarioId());
          return new IllegalArgumentException(
              "Scenario not found with ID: " + request.getScenarioId());
        });

    incident.setName(request.getName());
    incident.setDescription(request.getDescription());
    incident.setLatitude(request.getLatitude());
    incident.setLongitude(request.getLongitude());
    incident.setImpactRadius(request.getImpactRadius());
    incident.setSeverity(Severity.valueOf(request.getSeverity().toUpperCase()));
    incident.setStartedAt(request.getStartedAt());
    incident.setEndedAt(request.getEndedAt());
    incident.setScenario(scenario);

    incidentRepository.save(incident);
    logger.info("Incident with ID {} updated successfully", id);
  }

  /**
   * Delete incident.
   *
   * @param id the id
   */
  public void deleteIncident(Long id) {
    if (!incidentRepository.existsById(id)) {
      logger.error("Incident not found with ID: {}", id);
      throw new IllegalArgumentException("Incident not found with ID: " + id);
    }

    incidentRepository.deleteById(id);
    logger.info("Incident with ID {} deleted successfully", id);
  }

  /**
   * Gets all incidents.
   *
   * @return the all incidents
   */
  public List<IncidentResponseDto> getAllIncidents() {
    logger.info("Fetching all incidents");
    return incidentRepository.findAll()
        .stream()
        .map(IncidentResponseDto::fromEntity)
        .toList();
  }
}
