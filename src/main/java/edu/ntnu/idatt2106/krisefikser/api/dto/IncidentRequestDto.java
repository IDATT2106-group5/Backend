package edu.ntnu.idatt2106.krisefikser.api.dto;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * The Incident request dto.
 */
public class IncidentRequestDto {

  @NotBlank
  private String name;
  private String description;
  @NotNull
  private double latitude;
  @NotNull
  private double longitude;
  private double impactRadius;
  private String severity;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  @NotNull
  private Long scenarioId;

  public Incident toEntity(Scenario scenario) {
    Incident incident = new Incident();
    incident.setName(this.name);
    incident.setDescription(this.description);
    incident.setLatitude(this.latitude);
    incident.setLongitude(this.longitude);
    incident.setImpactRadius(this.impactRadius);
    incident.setSeverity(Severity.valueOf(this.severity.toUpperCase()));
    incident.setStartedAt(this.startedAt);
    incident.setEndedAt(this.endedAt);
    incident.setScenario(scenario);
    return incident;
  }


  // Getters and setters
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

