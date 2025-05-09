package edu.ntnu.idatt2106.krisefikser.persistance.repository.unregisteredhouseholdmember;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.household.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.unregisteredhouseholdmember.UnregisteredHouseholdMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Unregistered member entities.
 */
@Repository
public interface UnregisteredHouseholdMemberRepository
    extends JpaRepository<UnregisteredHouseholdMember, Long> {

  /**
   * Find unregistered household member by id.
   *
   * @param id the id of the unregistered member
   * @return the unregistered member
   */
  Optional<UnregisteredHouseholdMember> findById(Long id);

  /**
   * Find unregistered household members by household.
   *
   * @param household the household
   * @return the list of unregistered members
   */
  List<UnregisteredHouseholdMember> findUnregisteredHouseholdMembersByHousehold(
      Household household);
}
