package at.compax.reference.subsystem.tmf641.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import at.compax.foundation.subsystem.service.config.SubsystemServiceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@EnableAsync
@Configuration
@Import(SubsystemServiceConfig.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PropertySource(name = "aax", value = "classpath:application.properties", ignoreResourceNotFound = true)
public class ApplicationConfig {

  private static final int SIMULATION_THREAD_POOL_SIZE = 5;

  @Bean
  @Primary
  public ObjectMapper getObjectMapper() {
    return new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean(name = "simulationThreadPool")
  public TaskExecutor processReplicaThreadPoolTaskExecutor() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setMaxPoolSize(SIMULATION_THREAD_POOL_SIZE);
    executor.setThreadNamePrefix("simulationThreadPool");
    executor.afterPropertiesSet();
    return executor;
  }

}
