package edu.ntnu.idatt2106.krisefikser.service.incident;

import edu.ntnu.idatt2106.krisefikser.api.dto.incident.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.incident.IncidentResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.incident.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.incident.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Severity;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.incident.IncidentRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.incident.ScenarioRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.user.UserRepository;
import edu.ntnu.idatt2106.krisefikser.service.notification.NotificationService;
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
    logger.info("IncidentService initialized");
  }

  /**
   * Creates a new incident.
   *
   * @param request the incident request containing details for the new incident
   */
  public void createIncident(IncidentRequestDto request) {
    logger.info("Creating new incident: {}", request.getName());
    logger.debug(
        "Incident creation request details: scenario={}, "
            + "severity={}, coordinates=({},{}), radius={}",
        request.getScenarioId(), request.getSeverity(), request.getLatitude(),
        request.getLongitude(),
        request.getImpactRadius());

    if (request.getScenarioId() == null) {
      logger.error("Scenario ID is required for creating an incident.");
      throw new IllegalArgumentException("Scenario ID is required for creating an incident.");
    }

    Long scenarioId = request.getScenarioId();
    logger.debug("Looking up scenario with ID: {}", scenarioId);

    Scenario scenario = scenarioRepository.findById(scenarioId)
        .orElseThrow(() -> {
          logger.error("Scenario not found with ID: {}", scenarioId);
          return new IllegalArgumentException("Scenario not found with ID: " + scenarioId);
        });
    logger.debug("Found scenario: {}", scenario.getName());

    Incident incident = request.toEntity(scenario);
    logger.debug("Converting request to incident entity");

    incidentRepository.save(incident);
    logger.debug("Incident saved to database with ID: {}", incident.getId());

    logger.debug("Sending notification for incident: {}", incident.getName());
    notificationService.notifyIncident("[EMERGENCY ALERT]: " + scenario.getName()
            + " is in progress near you. Specific instructions can be found in the app.",
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
    logger.info("Updating incident with ID: {}", id);
    logger.debug("Update request details: name={}, scenario={}, severity={}",
        request.getName(), request.getScenarioId(), request.getSeverity());

    Incident incident = incidentRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("Incident not found with ID: {}", id);
          return new IllegalArgumentException("Incident not found with ID: " + id);
        });
    logger.debug("Found existing incident: {}", incident.getName());

    if (request.getScenarioId() == null) {
      logger.error("Scenario ID is required when updating an incident.");
      throw new IllegalArgumentException("Scenario ID is required when updating an incident.");
    }

    logger.debug("Looking up scenario with ID: {}", request.getScenarioId());
    Scenario scenario = scenarioRepository.findById(request.getScenarioId())
        .orElseThrow(() -> {
          logger.error("Scenario not found with ID: {}", request.getScenarioId());
          return new IllegalArgumentException(
              "Scenario not found with ID: " + request.getScenarioId());
        });
    logger.debug("Found scenario: {}", scenario.getName());

    logger.debug("Updating incident properties");
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
    logger.debug("Incident saved to database after update");
    logger.info("Incident with ID {} updated successfully", id);

    if (incident.getEndedAt() != null) {
      logger.debug("Incident has ended, sending closure notification");
      notificationService.notifyIncident(incident.getName()
              + " har avsluttet. Ta kontakt med dine nermeste.",
          incident);
    } else {
      logger.debug("Incident updated, sending update notification");
      notificationService.notifyIncident(incident.getName()
              + " har utviklet seg. Les mer på nyhetssiden.",
          incident);
    }
    logger.debug("Notifications sent for updated incident");
  }

  /**
   * Delete incident.
   *
   * @param id the id
   */
  public void deleteIncident(Long id) {
    logger.info("Deleting incident with ID: {}", id);

    if (!incidentRepository.existsById(id)) {
      logger.error("Incident not found with ID: {}", id);
      throw new IllegalArgumentException("Incident not found with ID: " + id);
    }
    logger.debug("Verified incident exists with ID: {}", id);

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

    List<Incident> incidents = incidentRepository.findAll();
    logger.debug("Retrieved {} incidents from database", incidents.size());

    List<IncidentResponseDto> dtos = incidents.stream()
        .map(IncidentResponseDto::fromEntity)
        .toList();
    logger.debug("Converted incidents to DTOs");

    logger.info("Returning {} incidents", dtos.size());
    return dtos;
  }
}