package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;

public class MembershipRequestResponseDto {
  Long id;
  Long householdId;
  UserResponseDto sender;
  UserResponseDto recipient;
  RequestType requestType;
  RequestStatus status;

  public MembershipRequestResponseDto(Long id, Long householdId, UserResponseDto sender,
                                      UserResponseDto recipient,
                                      RequestType requestType, RequestStatus status) {
    this.id = id;
    this.householdId = householdId;
    this.sender = sender;
    this.recipient = recipient;
    this.requestType = requestType;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(Long householdId) {
    this.householdId = householdId;
  }

  public UserResponseDto getSender() {
    return sender;
  }

  public void setSender(UserResponseDto sender) {
    this.sender = sender;
  }

  public UserResponseDto getRecipient() {
    return recipient;
  }

  public void setRecipient(UserResponseDto recipient) {
    this.recipient = recipient;
  }

  public RequestType getRequestType() {
    return requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public void setStatus(RequestStatus status) {
    this.status = status;
  }
}
