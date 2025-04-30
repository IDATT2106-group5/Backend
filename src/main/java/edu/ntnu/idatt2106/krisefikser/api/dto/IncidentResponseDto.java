package edu.ntnu.idatt2106.krisefikser.api.dto;

import java.time.LocalDateTime;

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

