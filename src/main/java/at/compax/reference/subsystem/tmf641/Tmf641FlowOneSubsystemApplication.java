package at.compax.reference.subsystem.tmf641;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(scanBasePackageClasses = Tmf641FlowOneSubsystemApplication.class)
@PropertySources(value = {
    @PropertySource(name = "tl-tmf641-flowone-subsystem", value = "classpath:tl-tmf641-flowone-subsystem.properties", ignoreResourceNotFound = true),
    @PropertySource(name = "tl-tmf641-flowone-subsystem", value = "classpath:tl-tmf641-flowone-subsystem_${environment}.properties", ignoreResourceNotFound = true),
    @PropertySource(name = "tl-tmf641-flowone-subsystem", value = "file:${user.home}/.aax2cfg/tl-tmf641-flowone-subsystem.properties", ignoreResourceNotFound = true),
    @PropertySource(name = "tl-tmf641-flowone-subsystem", value = "file:/vol1/aax2cfg/tl-tmf641-flowone-subsystem.properties", ignoreResourceNotFound = true),
    @PropertySource(name = "tl-tmf641-flowone-subsystem", value = "file:${user.home}/appconfig/tl-tmf641-flowone-subsystem.properties", ignoreResourceNotFound = true) })
public class Tmf641FlowOneSubsystemApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Tmf641FlowOneSubsystemApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Tmf641FlowOneSubsystemApplication.class);
  }

}
