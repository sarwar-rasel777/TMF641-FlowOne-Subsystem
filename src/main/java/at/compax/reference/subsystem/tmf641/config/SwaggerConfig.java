package at.compax.reference.subsystem.tmf641.config;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(
    name = "oauth2",
    type = SecuritySchemeType.OAUTH2,
    paramName = "{api.auth.security.token-header-name}",
    in = SecuritySchemeIn.HEADER,
    flows = @OAuthFlows( clientCredentials = @OAuthFlow(
        scopes = { @OAuthScope(name = "all", description = "all access") },
        tokenUrl = "${custom.openapi.security-scheme.token-url}",
        refreshUrl = "${custom.openapi.security-scheme.token-url}")))
@EnableConfigurationProperties(OpenapiProperties.class)
public class SwaggerConfig implements WebMvcConfigurer {

  private static final String ACCEPT_LANGUAGE_HEADER_PARAM = "acceptLanguageHeader";
  private static final String ACCEPT_LANGUAGE = "Accept-Language";


  @Value("${springdoc.swagger-ui.path}")
  private String swaggerRedirect;
  @Autowired
  private OpenapiProperties openapiProperties;
  @Autowired
  private ObjectMapper objectMapper;

  @Bean
  public OpenAPI customOpenAPI() {
    final StringSchema schema = new StringSchema();

    final OpenAPI openApi = new OpenAPI()
        .components(new Components().addParameters(
            ACCEPT_LANGUAGE_HEADER_PARAM,
            new HeaderParameter()
                .required(false)
                .name(ACCEPT_LANGUAGE)
                .description("Retrieve the labels in the given language")
                .schema(schema)))
        .info(new Info()
            .title(openapiProperties.getTitle())
            .description(openapiProperties.getDescription())
            .contact(new Contact()
                .email(openapiProperties.getEmail())
                .name(openapiProperties.getName())
                .url(openapiProperties.getUrl()))
            .version(openapiProperties.getVersion()));

    openApi.addServersItem(new Server().url(StringUtils.trim(openapiProperties.getBaseUrl())));

    return openApi;
  }

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addRedirectViewController("/", swaggerRedirect);
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler(swaggerRedirect).addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Override
  public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
    converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter || c instanceof ByteArrayHttpMessageConverter);
    converters.add(new StringHttpMessageConverter());
    converters.add(new ByteArrayHttpMessageConverter());
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
    mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
    converters.add(mappingJackson2HttpMessageConverter);
  }

}
