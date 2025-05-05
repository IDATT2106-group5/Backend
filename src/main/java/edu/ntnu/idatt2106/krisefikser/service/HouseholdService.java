package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.household.CreateHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.EditHouseholdRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.EditMemberDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers.UnregisteredMemberResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserHouseholdAssignmentRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.UnregisteredHouseholdMember;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.HouseholdRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UnregisteredHouseholdMemberRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class for managing household-related operations. This service handles the creation,
 * management, and modification of households, including validation and persistence operations.
 */
@Service
public class HouseholdService {

  /**
   * Logger for this class to log important events and errors.
   */
  private static final Logger logger = LoggerFactory.getLogger(HouseholdService.class);

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
   * Constructs a new HouseholdService with required repositories.
   *
   * @param householdRepository                   Repository for household operations.
   * @param userRepository                        Repository for user operations.
   * @param unregisteredHouseholdMemberRepository Repository for unregistered household member
   *                                              operations.
   */
  public HouseholdService(HouseholdRepository householdRepository,
      UserRepository userRepository,
      UnregisteredHouseholdMemberRepository unregisteredHouseholdMemberRepository) {
    this.householdRepository = householdRepository;
    this.userRepository = userRepository;
    this.unregisteredHouseholdMemberRepository = unregisteredHouseholdMemberRepository;
  }

  /**
   * Creates a new household with the given name, address, and creator's user ID. The creator
   * automatically becomes the owner of the household.
   *
   * @param request DTO containing household name, address, and owner ID.
   * @throws IllegalArgumentException if a household with the same name already exists.
   * @throws IllegalArgumentException if the specified owner ID does not match any existing user.
   */
  public void createHousehold(CreateHouseholdRequestDto request) {
    if (householdRepository.findByName(request.getName()).isPresent()) {
      logger.warn("Household with name {} already exists", request.getName());
      throw new IllegalArgumentException("Household with this name already exists");
    }
    if (request.getOwnerId() == null) {
      throw new IllegalArgumentException("Owner id must not be null");
    }

    Household household = new Household();
    household.setName(request.getName());
    household.setAddress(request.getAddress());
    household.setNumberOfMembers(1);
    household.setOwner(userRepository.findById(request.getOwnerId())
        .orElseThrow(() -> new IllegalArgumentException("User not found")));
    householdRepository.save(household);
    userRepository.updateHouseholdId(request.getOwnerId(), household.getId());
    logger.info("Household created successfully: {}", householdRepository.findByName(
        request.getName()));
  }

  /**
   * Adds a registered member to a household.
   *
   * @param request DTO containing the id of the user and the ID of the household.
   * @throws IllegalArgumentException if the user is not found.
   * @throws IllegalArgumentException if the household is not found.
   * @throws IllegalArgumentException if the user is already a member of the specified household.
   */
  public void addUserToHousehold(UserHouseholdAssignmentRequestDto request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Household household = householdRepository.findById(request.getHouseholdId())
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));

    if (user.getHousehold() != null
        && Objects.equals(user.getHousehold().getId(), request.getHouseholdId())) {
      logger.warn("User {} is already a member of household {}", user.getFullName(), household);
      throw new IllegalArgumentException("User is already a member of this household");
    }

    if (user.getHousehold() != null) {
      householdRepository.updateNumberOfMembers(user.getHousehold().getId(),
          user.getHousehold().getNumberOfMembers() - 1);
    }
    userRepository.updateHouseholdId(user.getId(), household.getId());
    householdRepository.updateNumberOfMembers(household.getId(),
        household.getNumberOfMembers() + 1);
  }

  /**
   * Removes a registered member from a household.
   *
   * @param userId      the user id
   * @param householdId the household id
   * @throws IllegalArgumentException if the user with specified id is not found.
   * @throws IllegalArgumentException if the user is not a member of any household.
   */
  public void removeUserFromHousehold(Long userId, Long householdId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (user.getHousehold() == null) {
      logger.warn("User {} is not a member of any household", user.getFullName());
      throw new IllegalArgumentException("User is not a member of any household");
    }
    if (!user.getHousehold().getId().equals(householdId)) {
      logger.warn("User {} is not a member of household", user.getFullName());
      throw new IllegalArgumentException("User is not a member of this household");
    }
    householdRepository.updateNumberOfMembers(user.getHousehold().getId(),
        user.getHousehold().getNumberOfMembers() - 1);
    userRepository.updateHouseholdId(user.getId(), null);
  }

  /**
   * Adds a new unregistered member to a household.
   *
   * <p>This method checks if an unregistered member with the given full name already exists in the
   * specified household. If the member does not exist, it creates a new
   * `UnregisteredHouseholdMember` entity, associates it with the household, and updates the
   * household's number of members.
   *
   * @param request The DTO containing the full name of the unregistered member and the ID of the
   *                household to which the member should be added.
   * @throws IllegalArgumentException if the unregistered member already exists in the specified
   *                                  household or if the household is not found.
   */
  public void addUnregisteredMemberToHousehold(
      UnregisteredMemberHouseholdAssignmentRequestDto request) {
    if (unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId(
        request.getFullName(), request.getHouseholdId()).isPresent()) {
      logger.warn("Unregistered member {} already exists in household {}", request.getFullName(),
          householdRepository.findById(request.getHouseholdId()));
      throw new IllegalArgumentException("Unregistered member already exists in this household");
    }
    UnregisteredHouseholdMember member = new UnregisteredHouseholdMember();
    member.setFullName(request.getFullName());
    Household household = householdRepository.findById(request.getHouseholdId())
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));
    member.setHousehold(household);

    unregisteredHouseholdMemberRepository.save(member);
    householdRepository.updateNumberOfMembers(request.getHouseholdId(),
        household.getNumberOfMembers() + 1);

    logger.info("Unregistered member {} added to household {}",
        unregisteredHouseholdMemberRepository.findByFullNameAndHouseholdId(member.getFullName(),
            household.getId()), household.getName());
  }

  /**
   * Removes an unregistered member from a household.
   *
   * <p>This method deletes the unregistered member from the
   * system and updates the household's member count. If the member doesn't belong to any household,
   * a warning is logged but the member is still deleted.
   *
   * @param memberId the member id
   */
  public void removeUnregisteredMemberFromHousehold(Long memberId) {
    UnregisteredHouseholdMember member =
        unregisteredHouseholdMemberRepository.findById(memberId).orElseThrow(
            () -> new IllegalArgumentException("Unregistered member not found"));
    if (member.getHousehold() != null) {
      logger.warn("Unregistered member doesnt belong to any household");
    }

    unregisteredHouseholdMemberRepository.delete(member);
    householdRepository.updateNumberOfMembers(member.getHousehold().getId(),
        member.getHousehold().getNumberOfMembers() - 1);
  }

  /**
   * Gets the members of a household by household id.
   *
   * @param userId the user id.
   * @return A map containing household details, registered users, and unregistered members.
   */
  public Map<String, Object> getHouseholdDetails(Long userId) {
    Map<String, Object> resultMap = new HashMap<>();

    User user = userRepository.findById(userId).orElseThrow(
        () -> new IllegalArgumentException("User not found"));
    Household household = user.getHousehold();
    resultMap.put("household", new HouseholdResponseDto(
        household.getId(),
        household.getName(),
        household.getAddress(),
        new UserResponseDto(
            household.getOwner().getId(),
            household.getOwner().getEmail(),
            household.getOwner().getFullName(),
            household.getOwner().getTlf(),
            household.getOwner().getRole()
        )
    ));

    List<UserResponseDto> userResponseDtos =
        userRepository.getUsersByHousehold(household).stream()
            .map(u -> new UserResponseDto(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getTlf(),
                u.getRole()))
            .collect(Collectors.toList());

    List<UnregisteredMemberResponseDto> unregisteredMemberResponseDtos =
        unregisteredHouseholdMemberRepository.findUnregisteredHouseholdMembersByHousehold(household)
            .stream()
            .map(unregisteredMember -> new UnregisteredMemberResponseDto(
                unregisteredMember.getId(),
                unregisteredMember.getFullName()))
            .collect(Collectors.toList());

    resultMap.put("users", userResponseDtos);
    resultMap.put("unregisteredMembers", unregisteredMemberResponseDtos);

    return resultMap;
  }

  /**
   * Edits an unregistered member in a household.
   *
   * @param request The request containing the full name of the unregistered member and the new full
   *                name.
   */
  public void editUnregisteredMemberInHousehold(EditMemberDto request) {
    UnregisteredHouseholdMember member = unregisteredHouseholdMemberRepository
        .findById(request.getMemberId())
        .orElseThrow(
            () -> new IllegalArgumentException("Unregistered member not found in household"));

    if (request.getNewFullName() != null) {
      member.setFullName(request.getNewFullName());
    }
    unregisteredHouseholdMemberRepository.save(member);
    logger.info("Unregistered member {} edited in household {}", request.getNewFullName(),
        request.getHouseholdId());
  }

  /**
   * Changes the owner of a household.
   *
   * @param request the request
   */
  public void changeHouseholdOwner(UserHouseholdAssignmentRequestDto request) {
    User newOwner = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Household household = householdRepository.findById(request.getHouseholdId())
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));

    if (household.getOwner().getId().equals(newOwner.getId())) {
      logger.warn("User {} is already the owner of household {}", newOwner.getFullName(),
          household.getName());
      throw new IllegalArgumentException("User is already the owner of this household");
    }

    household.setOwner(newOwner);
    householdRepository.save(household);
    logger.info("Household owner changed to {}", newOwner.getFullName());
  }

  /**
   * Edits a household's details.
   *
   * @param request The request containing the household ID and new details.
   */
  public void editHousehold(EditHouseholdRequestDto request) {
    Household household = householdRepository.findById(request.getHouseholdId())
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));
    if (request.getName() != null) {
      household.setName(request.getName());
    }
    if (request.getAddress() != null) {
      household.setAddress(request.getAddress());
    }
    householdRepository.save(household);
  }

  /**
   * Gets a household by its ID.
   *
   * @param householdId The ID of the household to retrieve.
   * @return HouseholdResponseDto containing household details.
   */

  public HouseholdResponseDto getHousehold(Long householdId) {
    Household household = householdRepository.findById(householdId)
        .orElseThrow(() -> new IllegalArgumentException("Household not found"));
    return new HouseholdResponseDto(
        household.getId(),
        household.getName(),
        household.getAddress(),
        new UserResponseDto(
            household.getOwner().getId(),
            household.getOwner().getEmail(),
            household.getOwner().getFullName(),
            household.getOwner().getTlf(),
            household.getOwner().getRole()
        )
    );
  }
}