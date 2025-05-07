package edu.ntnu.idatt2106.krisefikser.api.dto;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import jakarta.validation.constraints.NotBlank;

/**
 * The Scenario request DTO.
 */
public class ScenarioRequestDto {

  @NotBlank
  private String name;
  private String description;
  private String toDo;
  private String packingList;
  private String iconName;

  /**
   * Converts this DTO to a Scenario entity.
   *
   * @return the Scenario entity
   */
  public Scenario toEntity() {
    Scenario scenario = new Scenario();
    scenario.setName(this.name);
    scenario.setDescription(this.description);
    scenario.setToDo(this.toDo);
    scenario.setPackingList(this.packingList);
    scenario.setIconName(this.iconName);
    return scenario;
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

  public String getToDo() {
    return toDo;
  }

  public void setToDo(String toDo) {
    this.toDo = toDo;
  }

  public String getPackingList() {
    return packingList;
  }

  public void setPackingList(String packingList) {
    this.packingList = packingList;
  }

  public String getIconName() {
    return iconName;
  }

  public void setIconName(String iconName) {
    this.iconName = iconName;
  }

}
