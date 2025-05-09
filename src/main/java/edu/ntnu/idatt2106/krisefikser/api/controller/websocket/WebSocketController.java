package edu.ntnu.idatt2106.krisefikser.api.controller.websocket;

import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Controller for handling WebSocket related requests.
 */
@Tag(name = "WebSocket", description = "Endpoint for web socket related requests")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Controller
public class WebSocketController {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(WebSocketController.class);
  private final UserService userService;

  /**
   * Constructor for WebSocketController.
   *
   * @param userService the service for handling user related requests
   */
  public WebSocketController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Updates a user's position.
   *
   * @param position the position of the user
   */
  @Operation(summary = "Updates a user's position",
      description = "Updates a user's position. This is used for real-time location tracking.")
  @MessageMapping("/position")
  public void updatePosition(@Payload PositionDto position) {
    try {
      logger.info("Received position update: {}", position);
      userService.updatePosition(position);
    } catch (IllegalArgumentException e) {
      logger.error("Error updating position: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Error updating position", e);
    }
  }
}