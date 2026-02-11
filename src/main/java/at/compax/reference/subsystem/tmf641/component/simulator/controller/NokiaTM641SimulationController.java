package at.compax.reference.subsystem.tmf641.component.simulator.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.reference.subsystem.tmf641.component.client.ApiClient;
import at.compax.reference.subsystem.tmf641.component.exceptions.SimulationNotAllowedException;
import at.compax.reference.subsystem.tmf641.component.model.Error;
import at.compax.reference.subsystem.tmf641.component.model.InstallationCallbackModel;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderCreate;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItem;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderPatch;
import at.compax.reference.subsystem.tmf641.component.rest.Oauth2ClientRestTemplate;
import at.compax.reference.subsystem.tmf641.component.service.ErrorSimulationService;
import at.compax.reference.subsystem.tmf641.component.simulator.model.GetSimulationAutomationConfigResponse;
import at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomation;
import at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomationRequest;
import at.compax.reference.subsystem.tmf641.component.simulator.model.WfmRequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomation.ALL_WFM_PROCESSES;
import static at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomation.EVERYTHING;
import static at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomation.WFM_INSTALL;
import static at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomation.WFM_REMOVE;
import static at.compax.reference.subsystem.tmf641.component.simulator.model.SimulationAutomation.WFM_REPLACE;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/simulation/api/v1")
@ConditionalOnProperty(name = "swagger.show-simulator-requests", havingValue = "true")
public class NokiaTM641SimulationController {

  private static final Random random = new Random();
  private static final List<String> allowedEnvironments = Arrays.asList("INT", "TUA");
  private static final List<String> simulationSwitchOnKeywords = List.of("on", "1", "eischoltn");
  private static final List<String> simulationSwitchOffKeywords = List.of("off", "0", "owafoan");
  private static final String REMOVE_ONT_CHARACTERISTIC_NAME = "REMOVE_ONT";
  private static final List<SimulationAutomation> simulationAutomationConfig = new ArrayList<>();
  private static final List<SimulationAutomation> scheduledSimulationAutomation = new ArrayList<>();
  private static final Map<String, Long> ontStatusConfigs = Map.of("response_error", -99L,"not_found", 0L,"ont_down", 2L);
  private static final String UNKNOWN_ONT_STATUS_ERROR = "Please provide a proper response status for configuration! valid values: %s";
  private static final String SWAGGER_TAG_TMF641_SIMULATIONS = "TMF641 API Simulation Endpoints";
  private static final String SWAGGER_TAG_AUTOMATION_CONFIG = "Simulator Automation Configuration";
  private static final String SWAGGER_TAG_ERROR_CONFIG_REQUESTS = "TMF641 API Error Response Configuration";
  private Long currentOntStatusConfig = null;
  private int ontStatusConfigIterations = 0;

  @Value("${AAX_TARGET}")
  private String currentEnvironment;
  @Value("${FLOWONE_ASYNC_RESPONSE_API_URL:[unset]}")
  private String wfmCallbackSimulationApiEndpoint;

  private final ErrorSimulationService errorSimulationService;
  private final Oauth2ClientRestTemplate wfmCallbackSimulationRestTemplate;
  private final ObjectMapper objectMapper;
  private final ApiClient apiClient;

  private void checkIfSimulationIsAllowed(String calledOperation) {
    if (!allowedEnvironments.contains(currentEnvironment)) {
      log.warn("Blocked attempt to simulate {} on environment {}", calledOperation, currentEnvironment);
      throw new SimulationNotAllowedException("Simulation requests not allowed on this environment!");
    }
  }

  @Tag(name = SWAGGER_TAG_TMF641_SIMULATIONS)
  @PostMapping(value = "/serviceorder", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> serviceOrderSimulationEndpoint(@RequestBody String payload) {
    checkAndLogRequest(null, payload, "service order", "POST");
    Optional<ServiceOrderCreate> serviceOrderCreate = parseServiceActionPayload(payload);
    if (serviceOrderCreate.isPresent()) {
      ServiceOrderItem serviceOrderItem = serviceOrderCreate.get().getServiceOrderItem().get(0);
      String serviceAction = Objects.requireNonNull(serviceOrderItem.getAction());
      if (isIn(serviceAction, List.of("CONNECT", "ACTIVATION"))) {
        if (isAtLeastOneSimulationAutomationActive(WFM_INSTALL, ALL_WFM_PROCESSES, EVERYTHING)) {
          sendWfmCallback("install", serviceOrderCreate.get().getExternalId());
        }
        return ResponseEntity.ok(newReturnObjectWithRandomId());
      } else if (serviceAction.equals("GETONTSTATUS")) {
        ResponseEntity<Object> response = ResponseEntity.ok(newOntStatusResponse(currentOntStatusConfig));
        resetOntStatusResponseConfigAtLastIteration();
        return response;
      }
    }
    ResponseEntity<Object> response = ResponseEntity.ok(payload.contains("GETONTSTATUS") ? newOntStatusResponse(currentOntStatusConfig) : newReturnObjectWithRandomId());
    resetOntStatusResponseConfigAtLastIteration();
    return response;
  }

  private String newOntStatusResponse(Long ontStatus) {
    return TMF641SimulationResponseGenerator.getOntStatusResponse(ontStatus);
  }

  @Tag(name = SWAGGER_TAG_TMF641_SIMULATIONS)
  @PostMapping(value = "/config/ontStatus/{ontStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> configureGetOntStatusResponse(
		  @PathVariable String ontStatus,
      @RequestParam(required = false, defaultValue = "1")
      int iterations) {
    if (StringUtils.isBlank(ontStatus) || !ontStatusConfigs.containsKey(ontStatus)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UNKNOWN_ONT_STATUS_ERROR.formatted(ontStatusConfigs.keySet()));
    }
    currentOntStatusConfig = ontStatusConfigs.get(ontStatus);
    ontStatusConfigIterations = Math.max(iterations, 1);
    return ResponseEntity.ok("Configured ont status response %s for the next %d getOntStatus requests! Enjoy".formatted(ontStatus, ontStatusConfigIterations));
  }

  private void resetOntStatusResponseConfigAtLastIteration() {
    currentOntStatusConfig = --ontStatusConfigIterations > 0 ? currentOntStatusConfig : null;
  }

  @Tag(name = SWAGGER_TAG_TMF641_SIMULATIONS)
  @PostMapping(value = "/serviceorder/{serviceUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReturnObject> serviceOrderChangesSimulationEndpoint(@RequestBody String payload, @PathVariable String serviceUuid) {
    checkAndLogRequest(serviceUuid, payload, "service order", "POST");
    parseServiceActionPayload(payload).ifPresent(sa -> {
      ServiceOrderItem serviceOrderItem = sa.getServiceOrderItem().get(0);
      String serviceOrderId = sa.getExternalId(); // will be used below in WFM requests
	    switch (Objects.requireNonNull(serviceOrderItem.getAction())) {
		    case "ACTIVATION", "RESUME" -> {
			    if (isAtLeastOneSimulationAutomationActive(WFM_INSTALL, ALL_WFM_PROCESSES, EVERYTHING)) {
				    sendWfmCallback("install", serviceOrderId);
			    }
		    }
		    case "MOVE" -> {
			    if (isAtLeastOneSimulationAutomationActive(WFM_REPLACE, ALL_WFM_PROCESSES, EVERYTHING) && hasRemoveOntCharacteristicSet(serviceOrderItem)) {
				    sendWfmCallback("replace", serviceOrderId);
			    }
		    }
		    case "DISCONNECT" -> {
			    if (isAtLeastOneSimulationAutomationActive(WFM_REMOVE, ALL_WFM_PROCESSES, EVERYTHING) && hasRemoveOntCharacteristicSet(serviceOrderItem)) {
				    sendWfmCallback("uninstall", serviceOrderId);
			    }
		    }
		    default -> {
		    }
	    }
    });
    return ResponseEntity.ok(newReturnObjectWithRandomId());
  }

  @Tag(name = SWAGGER_TAG_TMF641_SIMULATIONS)
  @PostMapping(value = "/cancelserviceorder/{serviceUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReturnObject> serviceCancellationSimulationEndpoint(
      @RequestBody String payload,
		  @PathVariable String serviceUuid) {
    checkAndLogRequest(serviceUuid, payload, "service cancellation", "POST");
    return ResponseEntity.ok(newReturnObjectWithRandomId());
  }

  @Tag(name = SWAGGER_TAG_TMF641_SIMULATIONS)
  @PostMapping(value = "/wfm/response", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReturnObject> InstallationCallbackSimulationEndpoint(@RequestBody InstallationCallbackModel payload) {
    log.info("Installation Callback request received.\n{}", payload.toString());
    return ResponseEntity.ok(newReturnObjectWithRandomId());
  }

  @Tag(name = SWAGGER_TAG_TMF641_SIMULATIONS)
  @PatchMapping(value = "/serviceorder/{serviceUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReturnObject> serviceOrderAmendment(
      @RequestBody ServiceOrderPatch serviceOrder,
		  @PathVariable String serviceUuid) {
    checkAndLogRequest(serviceUuid, String.valueOf(serviceOrder), "service order amendment", "PATCH");
    return ResponseEntity.ok(newReturnObjectWithRandomId());
  }

  @Operation(summary = "Simulate planned outage file sending to Nokia")
  @PostMapping(value = "/api/upload-csv", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> configureSimulationAutomation(@RequestParam("file") MultipartFile file) {
    if (file == null || file.isEmpty()) {
      log.error("Planned outage file simulation received null or empty file");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File Missing or Empty");
    } else {
      log.info("Planned outage file simulation received file {}", file.getName());
      return ResponseEntity.ok("OK");
    }
  }

  @Tag(name = SWAGGER_TAG_AUTOMATION_CONFIG)
  @Operation(summary = "Configure a certain simulation to trigger automatically")
  @PostMapping(value = "/automation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> configureSimulationAutomation(@RequestBody SimulationAutomationRequest automationRequest) {
    if (automationRequest == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("Request Body is required");
    }
    Optional<SimulationAutomation> simulationAutomation = SimulationAutomation.findByString(automationRequest.getSimulation());
    if (simulationAutomation.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or unknown simulation use case; valid ones are: %s".formatted(Arrays.asList(SimulationAutomation.values())));
    }
    if (isIn(automationRequest.getTrigger(), simulationSwitchOnKeywords)) {
      return ResponseEntity.ok(switchSimulationAutomation(simulationAutomation.get(), true));
    } else if (isIn(automationRequest.getTrigger(), simulationSwitchOffKeywords)) {
      return ResponseEntity.ok(switchSimulationAutomation(simulationAutomation.get(), false));
    }
    List<String> allTriggerKeywords = new ArrayList<>();
    allTriggerKeywords.addAll(simulationSwitchOnKeywords);
    allTriggerKeywords.addAll(simulationSwitchOffKeywords);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid simulation automation switch keyword. Valid ones are: %s".formatted(allTriggerKeywords));
  }

  @Tag(name = SWAGGER_TAG_AUTOMATION_CONFIG)
  @GetMapping(value = "/automation", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Retrieve current configuration of automated simulation processes")
  public ResponseEntity<GetSimulationAutomationConfigResponse> getSimulationAutomationConfig() {
    GetSimulationAutomationConfigResponse response = new GetSimulationAutomationConfigResponse();
    response.setCurrentAutomations(simulationAutomationConfig);
    response.setScheduledAutomations(scheduledSimulationAutomation);
    return ResponseEntity.ok(response);
  }

  @Tag(name = SWAGGER_TAG_AUTOMATION_CONFIG)
  @PostMapping(value = "/automation/shelve-and-clear", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Shelve the current simulation automation configuration and turn it off for now", description = "In case there are no currently configured simulation automations, nothing will be shelved")
  public ResponseEntity<Void> shelveCurrentSimulationAutomationConfigAndClear() {
    if (!simulationAutomationConfig.isEmpty()) {
      scheduledSimulationAutomation.clear();
      scheduledSimulationAutomation.addAll(simulationAutomationConfig);
      simulationAutomationConfig.clear();
    }
    return ResponseEntity.ok().build();
  }

  @Tag(name = SWAGGER_TAG_AUTOMATION_CONFIG)
  @PostMapping(value = "/automation/reinstate-shelved", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "A possibly previously shelved simulation automation configuration will be put into place", description = "If no simulation automation configuration has been shelved before, nothing will be changed")
  public ResponseEntity<Void> reinstateShelved() {
    if (!scheduledSimulationAutomation.isEmpty()) {
      simulationAutomationConfig.clear();
      simulationAutomationConfig.addAll(scheduledSimulationAutomation);
      scheduledSimulationAutomation.clear();
    }
    return ResponseEntity.ok().build();
  }

  private Optional<ServiceOrderCreate> parseServiceActionPayload(String payload) {
    try {
      ServiceOrderCreate serviceOrderCreate = objectMapper.readValue(payload, ServiceOrderCreate.class);
      if (serviceOrderCreate.getServiceOrderItem() != null && !serviceOrderCreate.getServiceOrderItem().isEmpty()) {
        return Optional.of(serviceOrderCreate);
      }
      log.warn("Received service action payload without included service order item");
      return Optional.empty();
    } catch (Exception e) {
      log.warn("Parsing service action payload failed", e);
      // since we receive generic String payloads, the received object may not be a ServiceOrderCreate.
      return Optional.empty();
    }
  }

  private boolean hasRemoveOntCharacteristicSet(ServiceOrderItem serviceOrderItem) {
    return hasOntCharacteristicWithValue(serviceOrderItem);
  }

  private boolean hasOntCharacteristicWithValue(ServiceOrderItem serviceOrderItem) {
    if (serviceOrderItem == null || serviceOrderItem.getService() == null || serviceOrderItem.getService().getServiceCharacteristic() == null) {
      return false;
    }
    if (StringUtils.isBlank(NokiaTM641SimulationController.REMOVE_ONT_CHARACTERISTIC_NAME)) {
      return false;
    }
    String expectedValue = StringUtils.isBlank("Y") ? "" : "Y";
    return serviceOrderItem.getService().getServiceCharacteristic().stream()
        .anyMatch(sc -> sc.getName().equals(NokiaTM641SimulationController.REMOVE_ONT_CHARACTERISTIC_NAME) && sc.getValue().equals(expectedValue));
  }

  private void sendWfmCallback(String subPath, String processId) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("%s/wfm/%s".formatted(wfmCallbackSimulationApiEndpoint, subPath));
    HttpEntity<WfmRequestBody> httpEntity = new HttpEntity<>(WfmRequestBody.withServiceId(processId), apiClient.buildBaseHeaders());
    try {
      wfmCallbackSimulationRestTemplate.exchange(builder.build().toUri(), HttpMethod.POST, httpEntity, String.class);
    } catch (RestClientException e) {
      log.error("An Error occurred while triggering downstream {} WFM process.", subPath);
    }
  }

  private String switchSimulationAutomation(SimulationAutomation automation, boolean turnOn) {
    if (turnOn) {
      if (SimulationAutomation.NONE.equals(automation)) {
        simulationAutomationConfig.clear();
      } else {
        simulationAutomationConfig.add(automation);
      }
    } else {
      simulationAutomationConfig.remove(automation);
    }

    if (automation.isWfmProcess() && !turnOn && simulationAutomationConfig.contains(ALL_WFM_PROCESSES)) {
      simulationAutomationConfig.remove(ALL_WFM_PROCESSES);
      simulationAutomationConfig.addAll(
          SimulationAutomation.getAllWfmProcesses().stream()
              .filter(a -> !a.equals(automation) && !simulationAutomationConfig.contains(a))
              .toList()
      );
    }
    return "%s automation '%s'".formatted(turnOn ? "Added" : "Removed", automation.toString());
  }

  private boolean isAtLeastOneSimulationAutomationActive(SimulationAutomation... simulationAction) {
    return Arrays.stream(simulationAction).anyMatch(simulationAutomationConfig::contains);
  }

  private boolean isIn(String candidate, List<String> strings) {
    return strings.stream().anyMatch(candidate::equalsIgnoreCase);
  }

  @Tag(name = SWAGGER_TAG_ERROR_CONFIG_REQUESTS)
  @PostMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Configure expected error response", description = "Allows to configure an error response that will be returned on subsequent requests", security = @SecurityRequirement(name = "oauth2"))
  public ResponseEntity<String> setExpectedError(
      @RequestBody
      Error expectedError,
      @RequestParam(required = false, defaultValue = "1")
      int iterations,
      @RequestParam(required = false, defaultValue = "false")
      boolean toggle) {
    checkIfSimulationIsAllowed("POST expected error");
    if (expectedError == null || (iterations <= 0 && !toggle)) {
      throw new IllegalArgumentException("expected error response and either the number of iterations or infinity toggle must be set");
    }
    errorSimulationService.setExpectedError(expectedError, iterations, toggle);
    return ResponseEntity.ok("Successfully configured the provided error response to take effect "
        + (toggle ? "until being turned off" : "for the next " + iterations + " requests"));
  }

  @Tag(name = SWAGGER_TAG_ERROR_CONFIG_REQUESTS)
  @GetMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "See currently configured error response", description = "Returns the currently configured error response",security = @SecurityRequirement(name = "oauth2"))
  public ResponseEntity<Error> peekExpectedError() {
    checkIfSimulationIsAllowed("GET peek expected error");
    return ResponseEntity.ok(Objects.requireNonNull(errorSimulationService.peekExpectedError().orElse(null)));
  }

  @Tag(name = SWAGGER_TAG_ERROR_CONFIG_REQUESTS)
  @DeleteMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Clear configured error response", description = "Removes the currently configured error response", security = @SecurityRequirement(name = "oauth2"))
  public ResponseEntity<String> clearExpectedError() {
    checkIfSimulationIsAllowed("DELETE clear expected error");
    errorSimulationService.clearExpectedError();
    return ResponseEntity.ok("Configured error response successfully removed");
  }

  private void checkAndLogRequest(String serviceUuid, String payload, String requestMethod, String operationType) {
    checkIfSimulationIsAllowed(operationType + " " + requestMethod);
    logRequestReceipt(requestMethod, serviceUuid);
    log.debug(payload);
    errorSimulationService.checkForExpectedError();
  }

  private void logRequestReceipt(String requestType, String serviceUuid) {
    log.info("Received a {} request{}{} - responding with simulated success message...",
        requestType, serviceUuid != null ? " for serviceUuid " : "", serviceUuid != null ? serviceUuid : "");
  }

  private ReturnObject newReturnObjectWithRandomId() {
    return new ReturnObject(Integer.toString(Math.abs(random.nextInt())));
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ReturnObject {
    private String id;
  }

}
