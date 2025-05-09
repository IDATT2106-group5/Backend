package edu.ntnu.idatt2106.krisefikser.persistance.repository.household;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.household.Household;
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

  /**
   * Find a household by its name.
   *
   * @param name the name of the household
   * @return an Optional containing the Household if found
   */
  Optional<Household> findByName(String name);
}