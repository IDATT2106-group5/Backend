package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The Scenario entity.
 */
@Entity
@Table(name = "scenario")
public class Scenario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  @Column(name = "to_do", columnDefinition = "TEXT")
  private String toDo;
  @Column(name = "packing_list", columnDefinition = "TEXT")
  private String packingList;
  @Column(name = "icon_name")
  private String iconName;

  /**
   * Default constructor.
   */
  public Scenario() {
  }

  /**
   * Constructor with parameters.
   *
   * @param id          The ID of the scenario
   * @param name        The name of the scenario
   * @param description The description of the scenario
   * @param toDo        The information about what to do
   * @param packingList The packing list for the scenario
   * @param iconName    The icon name for the scenario
   */
  public Scenario(Long id, String name, String description, String toDo, String packingList,
      String iconName) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.toDo = toDo;
    this.packingList = packingList;
    this.iconName = iconName;
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

  public String getIconName() {
    return iconName;
  }

  public void setIconName(String iconName) {
    this.iconName = iconName;
  }
}
