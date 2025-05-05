package edu.ntnu.idatt2106.krisefikser.config;

import java.security.Principal;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * Configures the message broker for WebSocket communication.
   * <p>
   * Sets up destination prefixes for messages:
   * - "/topic" and "/queue" for broker destinations (server-to-client)
   * - "/app" for application destinations (client-to-server)
   * - "/user" for user-specific destinations
   *
   * @param registry the MessageBrokerRegistry to configure
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user/");  // Add trailing slash
  }

  /**
   * Registers STOMP endpoints for WebSocket connections.
   * <p>
   * Configures the "/ws" endpoint with SockJS fallback support and
   * sets CORS allowed origins to permit connections from the frontend application.
   *
   * @param registry the StompEndpointRegistry to configure
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins("http://localhost:5173")
        .setHandshakeHandler(new DefaultHandshakeHandler() {
          @Override
          protected Principal determineUser(ServerHttpRequest request,
                                            WebSocketHandler wsHandler,
                                            Map<String, Object> attributes) {
            // Get user ID from request headers or query parameters
            String userId = getUserIdFromRequest(request);
            if (userId != null) {
              return new StompPrincipal(userId);
            }
            return null;
          }
        })
        .withSockJS();
  }

  private String getUserIdFromRequest(ServerHttpRequest request) {
    // Extract from headers or query parameters
    String query = request.getURI().getQuery();
    if (query != null) {
      String[] params = query.split("&");
      for (String param : params) {
        if (param.startsWith("userId=")) {
          return param.substring(7);
        }
      }
    }
    return null;
  }

  private static class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}