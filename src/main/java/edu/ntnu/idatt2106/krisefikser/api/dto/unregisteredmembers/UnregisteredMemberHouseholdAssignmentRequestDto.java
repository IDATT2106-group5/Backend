package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

/**
 * Data Transfer Object for the request to assign an unregistered member to a household. Contains
 * the full name of the unregistered member and the ID of the household to which the member is being
 * assigned.
 */
public class UnregisteredMemberHouseholdAssignmentRequestDto {

  private String fullName;

  /**
   * Gets full name.
   *
   * @return the full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Sets full name.
   *
   * @param fullName the full name
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}
