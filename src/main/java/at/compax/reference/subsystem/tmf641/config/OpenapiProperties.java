package at.compax.reference.subsystem.tmf641.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("custom.openapi")
public class OpenapiProperties {

  private String title;
  private String description;
  private String email;
  private String name;
  private String url;
  private String version;
  private String baseUrl;

}
