package com.davidpe.tasker.bootstrap;

import java.util.logging.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggingConfig {

  /**
   * Provide a java.util.logging.Logger instance per injection point. Use prototype scope so each
   * injection gets a logger named for the target class.
   */
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public Logger logger(InjectionPoint injectionPoint) {
    String name = injectionPoint.getMember().getDeclaringClass().getName();
    return Logger.getLogger(name);
  }
}
