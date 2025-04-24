package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The type Map icon.
 */
@Entity
@Table(name = "map_icon")
public class MapIcon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String type;

  @Column
  private String address;

  @Column
  private Double latitude;

  @Column
  private Double longitude;

}
