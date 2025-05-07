package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.ScenarioResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Scenario;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.ScenarioRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for handling scenario related operations.
 */
@Service
public class ScenarioService {

  private static final Logger logger = LoggerFactory.getLogger(ScenarioService.class);
  private final ScenarioRepository scenarioRepository;

  /**
   * Constructor for ScenarioService.
   *
   * @param scenarioRepository The repository for scenario related operations.
   */
  public ScenarioService(ScenarioRepository scenarioRepository) {
    this.scenarioRepository = scenarioRepository;
  }

  /**
   * Creates a new scenario.
   *
   * @param dto The scenario request DTO
   */
  public void createScenario(ScenarioRequestDto dto) {
    if (dto.getName() == null || dto.getName().isBlank()) {
      logger.error("Scenario name is required");
      throw new IllegalArgumentException("Scenario name is required");
    }
    Scenario scenario = dto.toEntity();
    scenarioRepository.save(scenario);
    logger.info("Scenario created successfully: {}", scenario.getName());
  }

  /**
   * Updates an existing scenario by ID.
   *
   * @param id  The ID of the scenario to update
   * @param dto The updated scenario data
   */
  public void updateScenario(Long id, ScenarioRequestDto dto) {
    if (dto.getName() == null || dto.getName().isBlank()) {
      logger.error("Scenario name is required for update");
      throw new IllegalArgumentException("Scenario name is required");
    }

    Scenario scenario = scenarioRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Scenario not found with ID: {}", id);
          return new IllegalArgumentException("Scenario not found with ID: " + id);
        });

    scenario.setName(dto.getName());
    scenario.setDescription(dto.getDescription());
    scenario.setToDo(dto.getToDo());
    scenario.setPackingList(dto.getPackingList());
    scenario.setIconName(dto.getIconName());

    scenarioRepository.save(scenario);
    logger.info("Scenario with ID {} updated successfully", id);
  }

  /**
   * Retrieves all scenarios.
   *
   * @return A list of all scenarios
   */
  public List<ScenarioResponseDto> getAllScenarios() {
    return scenarioRepository.findAll().stream()
        .map(ScenarioResponseDto::fromEntity)
        .toList();
  }

  /**
   * Retrieves a scenario by ID.
   *
   * @param id The ID of the scenario to retrieve
   * @return The scenario response DTO
   */
  public ScenarioResponseDto getScenarioById(Long id) {
    Scenario scenario = scenarioRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Scenario not found with ID: {}", id);
          return new IllegalArgumentException("Scenario not found with ID: " + id);
        });
    return ScenarioResponseDto.fromEntity(scenario);
  }
}
