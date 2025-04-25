package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {

  /**
   * Find a household by its unique name.
   *
   * @param name the name to search for
   * @return an Optional containing the Household if found
   */
  Optional<Household> findByName(String name);

  /**
   * Update the number of members in a household.
   *
   * @param id              the household id
   * @param numberOfMembers the new number of members
   */
  @Modifying
  @Transactional
  @Query("UPDATE Household h SET h.numberOfMembers = :numberOfMembers WHERE h.id = :id")
  void updateNumberOfMembers(@Param("id") Long id, @Param("numberOfMembers") int numberOfMembers);
}