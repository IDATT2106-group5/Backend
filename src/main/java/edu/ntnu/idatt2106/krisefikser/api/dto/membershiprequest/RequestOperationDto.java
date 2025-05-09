package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

/**
 * Data Transfer Object (DTO) for membership invite requests. This class is used to encapsulate the
 * data sent from the client to the server when a user invites another user to join a group.
 */

public class RequestOperationDto {

  Long requestId;

  public Long getRequestId() {
    return requestId;
  }
}
