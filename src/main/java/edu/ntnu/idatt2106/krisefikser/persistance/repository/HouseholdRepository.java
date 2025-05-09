package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The interface Household repository.
 */
@Repository
public interface HouseholdRepository extends JpaRepository<Household, String> {
  /**
   * Update the number of members in a household.
   *
   * @param id              the household id
   * @param numberOfMembers the new number of members
   */
  @Modifying
  @Transactional
  @Query("UPDATE Household h SET h.numberOfMembers = :numberOfMembers WHERE h.id = :id")
  void updateNumberOfMembers(@Param("id") String id, @Param("numberOfMembers") int numberOfMembers);

  Optional<Household> findByName(String name);

  Optional<Household> getHouseholdById(String id);


  boolean existsByName(String householdName);

  Household findByOwner(User owner);
}