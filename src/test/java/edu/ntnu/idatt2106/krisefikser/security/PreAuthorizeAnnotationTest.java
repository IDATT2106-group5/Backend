package edu.ntnu.idatt2106.krisefikser.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idatt2106.krisefikser.api.dto.AdminInviteRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.AdminSetupRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.IncidentRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.AdminInvitationService;
import edu.ntnu.idatt2106.krisefikser.service.IncidentService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PreAuthorizeAnnotationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AdminInvitationService adminInvitationService;

  @MockBean
  private IncidentService incidentService;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(
            org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
        .build();
  }

  @Test
  public void inviteAdmin_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
    AdminInviteRequest request = new AdminInviteRequest("admin@example.com", "Admin User");

    mockMvc.perform(post("/api/admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  public void inviteAdmin_whenRoleUser_shouldReturnForbidden() throws Exception {
    AdminInviteRequest request = new AdminInviteRequest("admin@example.com", "Admin User");

    mockMvc.perform(post("/api/admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void inviteAdmin_whenRoleAdmin_shouldReturnForbidden() throws Exception {
    AdminInviteRequest request = new AdminInviteRequest("admin@example.com", "Admin User");

    mockMvc.perform(post("/api/admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "SUPERADMIN")
  public void inviteAdmin_whenRoleSuperAdmin_shouldReturnOk() throws Exception {
    AdminInviteRequest request = new AdminInviteRequest("admin@example.com", "Admin User");

    mockMvc.perform(post("/api/admin/invite")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  public void setupAdmin_whenUnauthenticated_shouldBeAllowed() throws Exception {
    // Setup test data
    AdminSetupRequest request = new AdminSetupRequest();
    request.setToken("valid-token");
    request.setPassword("ValidP@ssword1");

    // Perform the test with no authentication
    mockMvc.perform(post("/api/admin/setup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())  // Change from status().is4xxClientError() to status().isOk()
        .andDo(print());
  }

  @Test
  public void createIncident_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
    IncidentRequestDto request = new IncidentRequestDto();
    request.setName("Test Incident");
    request.setDescription("Test Description");
    request.setLatitude(63.4305);
    request.setLongitude(10.3951);
    request.setImpactRadius(5.0);
    request.setSeverity("HIGH");
    request.setStartedAt(LocalDateTime.now());
    request.setScenarioId(1L);

    mockMvc.perform(post("/api/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  public void createIncident_whenRoleUser_shouldReturnForbidden() throws Exception {
    IncidentRequestDto request = new IncidentRequestDto();
    request.setName("Test Incident");
    request.setDescription("Test Description");
    request.setLatitude(63.4305);
    request.setLongitude(10.3951);
    request.setImpactRadius(5.0);
    request.setSeverity("HIGH");
    request.setStartedAt(LocalDateTime.now());
    request.setScenarioId(1L);

    mockMvc.perform(post("/api/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  public void createIncident_whenRoleAdmin_shouldBeAllowed() throws Exception {
    IncidentRequestDto request = new IncidentRequestDto();
    request.setName("Test Incident");
    request.setDescription("Test Description");
    request.setLatitude(63.4305);
    request.setLongitude(10.3951);
    request.setImpactRadius(5.0);
    request.setSeverity("HIGH");
    request.setStartedAt(LocalDateTime.now());
    request.setScenarioId(1L);

    mockMvc.perform(post("/api/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "SUPERADMIN")
  public void createIncident_whenRoleSuperAdmin_shouldBeAllowed() throws Exception {
    // Testing if SUPERADMIN can also access ADMIN endpoints
    IncidentRequestDto request = new IncidentRequestDto();
    request.setName("Test Incident");
    request.setDescription("Test Description");
    request.setLatitude(63.4305);
    request.setLongitude(10.3951);
    request.setImpactRadius(5.0);
    request.setSeverity("HIGH");
    request.setStartedAt(LocalDateTime.now());
    request.setScenarioId(1L);

    mockMvc.perform(post("/api/incidents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }
}