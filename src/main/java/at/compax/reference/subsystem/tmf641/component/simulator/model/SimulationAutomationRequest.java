package at.compax.reference.subsystem.tmf641.component.simulator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SimulationAutomationRequest {

  private String simulation;
  @JsonProperty("switch")
  private String trigger;

}
