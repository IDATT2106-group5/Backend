package edu.ntnu.idatt2106.krisefikser.api.dto.incident;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.incident.Scenario;

/**
 * The Scenario response DTO.
 */
public class ScenarioResponseDto {

  private Long id;
  private String name;
  private String description;
  private String toDo;
  private String packingList;
  private String iconName;

  /**
   * Converts a Scenario entity to a ScenarioResponseDto.
   *
   * @param scenario the Scenario entity
   * @return the ScenarioResponseDto
   */
  public static ScenarioResponseDto fromEntity(Scenario scenario) {
    ScenarioResponseDto dto = new ScenarioResponseDto();
    dto.setId(scenario.getId());
    dto.setName(scenario.getName());
    dto.setDescription(scenario.getDescription());
    dto.setToDo(scenario.getToDo());
    dto.setPackingList(scenario.getPackingList());
    dto.setIconName(scenario.getIconName());
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
