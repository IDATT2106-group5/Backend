package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;

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

  @Column
  private int severity;
  @Column
  private Timestamp startedAt;
  @Column
  private Timestamp endedAt;

  @JoinColumn
  @OneToOne
  private User createdBy;

}
