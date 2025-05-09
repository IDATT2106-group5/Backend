package edu.ntnu.idatt2106.krisefikser.api.dto.incident;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.incident.Incident;
import java.time.LocalDateTime;

/**
 * The Incident response dto.
 */
public class IncidentResponseDto {

  private Long id;
  private String name;
  private String description;
  private double latitude;
  private double longitude;
  private double impactRadius;
  private String severity;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Long scenarioId;

  /**
   * Converts an Incident entity to an IncidentResponseDto.
   *
   * @param incident the Incident entity
   * @return the IncidentResponseDto
   */
  public static IncidentResponseDto fromEntity(Incident incident) {
    IncidentResponseDto dto = new IncidentResponseDto();
    dto.setId(incident.getId());
    dto.setName(incident.getName());
    dto.setDescription(incident.getDescription());
    dto.setLatitude(incident.getLatitude());
    dto.setLongitude(incident.getLongitude());
    dto.setImpactRadius(incident.getImpactRadius());
    dto.setSeverity(incident.getSeverity().name());
    dto.setStartedAt(incident.getStartedAt());
    dto.setEndedAt(incident.getEndedAt());
    dto.setScenarioId(incident.getScenario() != null ? incident.getScenario().getId() : null);
    return dto;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getImpactRadius() {
    return impactRadius;
  }

  public void setImpactRadius(double impactRadius) {
    this.impactRadius = impactRadius;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public LocalDateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(LocalDateTime endedAt) {
    this.endedAt = endedAt;
  }

  public Long getScenarioId() {
    return scenarioId;
  }

  public void setScenarioId(Long scenarioId) {
    this.scenarioId = scenarioId;
  }
}

