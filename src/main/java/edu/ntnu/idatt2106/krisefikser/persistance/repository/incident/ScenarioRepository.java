package edu.ntnu.idatt2106.krisefikser.persistance.repository.incident;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.incident.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Scenario repository.
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

}
