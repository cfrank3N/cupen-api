package se.cupen.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.lang.NonNull;

import org.testcontainers.containers.PostgreSQLContainer;

@Profile("dev")
public class DevPostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("dev")
      .withUsername("dev")
      .withPassword("dev");

  @Override
  public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
    if (!POSTGRES.isRunning()) {
      POSTGRES.start();
      Runtime.getRuntime().addShutdownHook(new Thread(POSTGRES::stop));
    }

    Map<String, Object> props = new HashMap<>();
    props.put("spring.datasource.url", POSTGRES.getJdbcUrl());
    props.put("spring.datasource.username", POSTGRES.getUsername());
    props.put("spring.datasource.password", POSTGRES.getPassword());
    props.put("spring.datasource.driver-class-name", "org.postgresql.Driver");

    MutablePropertySources sources = applicationContext.getEnvironment().getPropertySources();
    sources.addFirst(new MapPropertySource("devPostgresTestcontainer", props));

  }

}
