package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

/**
 * The type Edit member dto.
 */
public class EditMemberDto {
  private Long memberId;
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

  public Long getMemberId() {
    return memberId;
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
