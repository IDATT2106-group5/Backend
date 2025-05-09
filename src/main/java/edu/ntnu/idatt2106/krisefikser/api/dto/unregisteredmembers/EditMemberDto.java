package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

/**
 * The type Edit member dto.
 */
public class EditMemberDto {
  private Long memberId;
  private String newFullName;


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

  public void setMemberId(long l) {
  }

  public void setNewFullName(String janeDoe) {
  }
}
