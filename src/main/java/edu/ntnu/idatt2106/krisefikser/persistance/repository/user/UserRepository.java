package edu.ntnu.idatt2106.krisefikser.persistance.repository.user;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.household.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Find a user by their unique email address.
   *
   * @param email the email to search for
   * @return an Optional containing the User if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Check if a user exists by their email address.
   *
   * @param email the email to check
   * @return true if a user with the given email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Find a user by their confirmation token.
   *
   * @param token the confirmation token to search for
   * @return an Optional containing the User if found
   */
  Optional<User> findByConfirmationToken(String token);

  /**
   * Update household id.
   *
   * @param userId      the user id
   * @param householdId the household id
   */
  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.household.id = :householdId WHERE u.id = :userId")
  void updateHouseholdId(@Param("userId") String userId, @Param("householdId") String householdId);

  /**
   * Find a user by their reset password token.
   *
   * @param token the reset password token to search for
   * @return an Optional containing the User if found
   */
  Optional<User> findByResetPasswordToken(String token);

  /**
   * Find users by a given household.
   *
   * @param household the given household
   * @return the list of users associated with the household
   */
  List<User> getUsersByHousehold(Household household);

  /**
   * Find users by a given household id.
   *
   * @param householdId the given household id
   * @return the list of users associated with the household
   */
  List<User> getUsersByHouseholdId(String householdId);

  /**
   * Find a user by their id.
   *
   * @param userId the user id
   * @return an Optional containing the User if found
   */
  Optional<User> getUsersById(String userId);

  /**
   * Find users within a certain radius of a given latitude and longitude.
   *
   * @param latitude  the latitude
   * @param longitude the longitude
   * @param radius    the radius in kilometers
   * @return a list of users within the specified radius
   */
  @Query(value = "SELECT u.* FROM user u WHERE "
      + "(6371 * acos(cos(radians(:latitude)) * cos(radians(u.latitude)) * "
      + "cos(radians(u.longitude) - radians(:longitude)) + "
      + "sin(radians(:latitude)) * sin(radians(u.latitude)))) <= :radius",
      nativeQuery = true)
  List<User> findUsersWithinRadius(@Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("radius") double radius);
}