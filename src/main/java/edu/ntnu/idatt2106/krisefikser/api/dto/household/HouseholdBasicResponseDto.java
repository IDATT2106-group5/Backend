package edu.ntnu.idatt2106.krisefikser.api.dto.household;

/**
 * A simple DTO for Household.
 */
public class HouseholdBasicResponseDto {

  private final Long id;
  private final String name;

  /**
   * Constructor for HouseholdBasicResponseDto.
   *
   * @param id   the id of the household
   * @param name the name of the household
   */
  public HouseholdBasicResponseDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
