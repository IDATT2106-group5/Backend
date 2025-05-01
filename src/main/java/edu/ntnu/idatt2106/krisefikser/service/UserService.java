package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  public UserResponseDto getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.getUserByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user logged in"));

    UserResponseDto userDto = new UserResponseDto(
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getTlf(),
        user.getRole()
    );

    return userDto;
  }

  public Long checkIfMailExists(String email) {
    User user = userRepository.getUserByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No user with this email"));
    return user.getId();
  }
}
