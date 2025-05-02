package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket communication.
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * Registers STOMP endpoints for WebSocket connections.
   *
   * <p>Configures the "/ws" endpoint with SockJS fallback support and sets CORS allowed origins to
   * permit connections from the frontend application.
   *
   * @param registry the StompEndpointRegistry to configure
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins("http://localhost:5173")
        .withSockJS();
  }

  /**
   * Configures the message broker for WebSocket communication.
   *
   * <p>Sets up destination prefixes for messages: - "/topic" and "/queue" for broker destinations
   * (server-to-client) - "/app" for application destinations (client-to-server) - "/user" for
   * user-specific destinations
   *
   * @param registry the MessageBrokerRegistry to configure
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

}
