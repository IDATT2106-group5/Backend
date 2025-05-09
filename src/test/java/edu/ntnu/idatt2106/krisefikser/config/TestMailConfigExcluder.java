package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
@EnableAutoConfiguration(exclude = {MailSenderAutoConfiguration.class})
public class TestMailConfigExcluder {
    // This class simply excludes mail auto-configuration in test profile
}
