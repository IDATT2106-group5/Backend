package edu.ntnu.idatt2106.krisefikser.persistance.repository.incident;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.incident.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Incident repository.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

}
