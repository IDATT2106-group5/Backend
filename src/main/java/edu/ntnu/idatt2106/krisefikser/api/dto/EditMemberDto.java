package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * The type Edit member dto.
 */
public class EditMemberDto {
  private String fullName;
  private String newFullName;
  private Long householdId;

  /**
   * Gets household id.
   *
   * @return the household id
   */
  public Long getHouseholdId() {
    return householdId;
  }

  /**
   * Gets full name.
   *
   * @return the full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Gets new full name.
   *
   * @return the new full name
   */
  public String getNewFullName() {
    return newFullName;
  }
}
