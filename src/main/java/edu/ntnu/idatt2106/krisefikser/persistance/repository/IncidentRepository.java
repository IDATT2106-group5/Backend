package edu.ntnu.idatt2106.krisefikser.persistance.repository;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Incident repository. This interface extends JpaRepository to provide CRUD
 * operations for the Incident entity.
 */

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

}
