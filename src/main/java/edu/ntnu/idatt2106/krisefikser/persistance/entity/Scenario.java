package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The type Scenario.
 */

@Entity
@Table(name = "scenario")
public class Scenario {

  @Id
  private Long id;
  private String name;
  private String description;
  private String toDo;
  private String packingList;

  public Scenario() {
  }

  /**
   * Instantiates a new Scenario with all fields.
   *
   * @param id          The ID of the scenario
   * @param name        The name of the scenario
   * @param description The description of the scenario
   * @param toDo        The to-do list for the scenario
   * @param packingList The packing list for the scenario
   */
  public Scenario(Long id, String name, String description, String toDo, String packingList) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.toDo = toDo;
    this.packingList = packingList;
  }

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

  public String getPackingList() {
    return packingList;
  }

  public void setPackingList(String packingList) {
    this.packingList = packingList;
  }

  public String getToDo() {
    return toDo;
  }

  public void setToDo(String toDo) {
    this.toDo = toDo;
  }
}
