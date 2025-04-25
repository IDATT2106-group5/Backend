package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    /**
     * Find a household by its unique name.
     *
     * @param name the name to search for
     * @return an Optional containing the Household if found
     */
    Optional<Household> findByName(String name);

    void editNumberOfMembers(Long id, int numberOfMembers);
}
