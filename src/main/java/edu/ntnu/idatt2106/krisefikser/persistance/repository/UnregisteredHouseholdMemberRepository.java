package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.UnregisteredHouseholdMember;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Unregistered member entities.
 */
@Repository
public interface UnregisteredHouseholdMemberRepository
    extends JpaRepository<UnregisteredHouseholdMember, Long> {

  /**
   * Find a unregistered member by its name and what household it .
   *
   * @param fullName the name to search for
   * @return an Optional containing the UnregisteredMember if found
   */
  Optional<UnregisteredHouseholdMember> findByFullNameAndHouseholdId(String fullName,
                                                                     String householdId);
  Optional<UnregisteredHouseholdMember> findById(Long id);

  List<UnregisteredHouseholdMember> findUnregisteredHouseholdMembersByHousehold(Household household);
}
