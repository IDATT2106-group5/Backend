package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.UnregisteredHouseholdMember;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UnregisteredHouseholdMemberRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class for managing household-related operations.
 * This service handles the creation and management of households,
 * including validation and persistence operations.
 */
@Service
public class HouseholdService {
  /**
   * Repository for household entity operations.
   */
  private final HouseholdRepository householdRepository;

  /**
   * Repository for user entity operations.
   */
  private final UserRepository userRepository;

  /**
   * Repository for unregistered household member entity operations.
   */
  private final UnregisteredHouseholdMemberRepository unregisteredHouseholdMemberRepository;

  /**
   * Logger for this class.
   */
  private static final Logger logger = LoggerFactory.getLogger(HouseholdService.class);

  /**
   * Constructs a new HouseholdService with required repositories.
   *
   * @param householdRepository                   Repository for household operations
   * @param userRepository                        Repository for user operations
   * @param unregisteredHouseholdMemberRepository the unregistered household member repository
   */
  public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository,
                          UnregisteredHouseholdMemberRepository unregisteredHouseholdMemberRepository) {
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
    this.unregisteredHouseholdMemberRepository = unregisteredHouseholdMemberRepository;
  }

  /**
   * Creates a new household with the given name, address, and creator's user ID.
   * The creator automatically becomes the owner of the household.
   *
   * @param request DTO containing household name, address, and owner ID
   * @throws IllegalArgumentException if a household with the same name already exists
   * @throws IllegalArgumentException if the specified owner ID does not match any existing user
   */
  public void createHousehold(CreateHouseholdRequestDto request) {
    if (householdRepository.findByName(request.getName()).isPresent()) {
      logger.warn("Household with name {} already exists", request.getName());
      throw new IllegalArgumentException("Household with this name already exists");
    }

    Household household = new Household();
    household.setName(request.getName());
    household.setAddress(request.getAddress());
    household.setNumberOfMembers(1);
    household.setOwner(userRepository.findById(request.getOwnerId())
        .orElseThrow(() -> new IllegalArgumentException("User not found")));
    householdRepository.save(household);
    logger.info("Household created successfully: {}", householdRepository.findByName(
        request.getName()));
  }

  /**
   * Adds a registered member to a household.
   */
  public void addUserToHousehold(UserHouseholdAssignmentRequestDto request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Household household = householdRepository.findById(request.getNewHouseholdId())
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));

    if (Objects.equals(user.getHousehold().getId(), request.getNewHouseholdId())) {
      logger.warn("User {} is already a member of household {}", user.getFullName(),
          householdRepository.findById(request.getNewHouseholdId()));
      throw new IllegalArgumentException("User is already a member of this household");
    }
    if (user.getHousehold() != null) {
      // Remove user from current household if they are in one
      householdRepository.setNumberOfMembers(user.getHousehold().getId(),
          household.getNumberOfMembers() - 1);
    }
    // add user to new household
    userRepository.editHouseholdId(user.getId(), household.getId());
    householdRepository.setNumberOfMembers(household.getId(),
        household.getNumberOfMembers() + 1);
  }

  /**
   * Removes a registered member from a household.
   *
   * @param user the user
   */
  public void removeUserFromHousehold(User user) {
    if (user.getHousehold() == null) {
      logger.warn("User {} is not a member of any household", user.getFullName());
      throw new IllegalArgumentException("User is not a member of any household");
    }
    householdRepository.setNumberOfMembers(user.getHousehold().getId(),
        user.getHousehold().getNumberOfMembers() - 1);
    userRepository.editHouseholdId(user.getId(), null);
  }

  /**
   * Adds a new unregistered member to a household.
   *
   * @param member    the member
   * @param household the household
   */
  public void addUnregisteredMemberToHousehold(UnregisteredHouseholdMember member,
                                               Household household) {
    if (unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId(
        member.getFullName(), household.getId()).isPresent()) {
      logger.warn("Unregistered member {} already exists in household {}", member.getFullName(),
          household.getName());
      throw new IllegalArgumentException("Unregistered member already exists in this household");
    }

    unregisteredHouseholdMemberRepository.save(member);
    householdRepository.setNumberOfMembers(household.getId(),
        household.getNumberOfMembers() + 1);

    logger.info("Unregistered member {} added to household {}",
        unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId(member.getFullName(),
            household.getId()), household.getName());
  }

  /**
   * Removes an unregistered member from a household.
   *
   * @param member the unregistered member
   */
  public void removeUnregisteredMemberFromHousehold(UnregisteredHouseholdMember member) {
    if (member.getHousehold() != null) {
      logger.warn("Unregistered member {} doesnt belong to any household", member.getFullName());
    }
    unregisteredHouseholdMemberRepository.delete(member);
    householdRepository.setNumberOfMembers(member.getId(),
        member.getHousehold().getNumberOfMembers() - 1);
  }
}
