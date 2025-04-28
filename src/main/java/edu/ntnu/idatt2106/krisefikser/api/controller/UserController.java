package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Endpoints for managing a user")
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class UserController {
  private final UserService userService;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<User> getUser(@RequestParam String confirmationToken) {
    try {
      User user = userService.getUserByConfirmationToken(confirmationToken);
      LOGGER.info("User found: {}", user.getEmail());
      return ResponseEntity.ok(user);
    } catch (IllegalArgumentException e) {
      LOGGER.error("User not found: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      LOGGER.error("An error occurred while fetching user: ", e);
      return ResponseEntity.status(500).build();
    }
  }
}
