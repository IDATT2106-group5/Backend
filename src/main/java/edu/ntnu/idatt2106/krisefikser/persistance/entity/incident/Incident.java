package edu.ntnu.idatt2106.krisefikser.persistance.entity.incident;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * The type Incident.
 */
@Entity
@Table(name = "incident")
public class Incident {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;
  @Column
  private String description;
  @Column
  private Double latitude;

  @Column
  private Double longitude;

  @Column
  private Double impactRadius;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Severity severity;
  @Column
  private LocalDateTime startedAt;
  @Column
  private LocalDateTime endedAt;

  @ManyToOne
  @JoinColumn(name = "scenario_id", referencedColumnName = "id")
  private Scenario scenario;

  // Getters and Setters
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

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getImpactRadius() {
    return impactRadius;
  }

  public void setImpactRadius(Double impactRadius) {
    this.impactRadius = impactRadius;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
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

  public Scenario getScenario() {
    return scenario;
  }

  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }
}
