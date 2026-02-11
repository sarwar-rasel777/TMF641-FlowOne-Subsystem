package at.compax.reference.subsystem.tmf641.component.simulator.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSimulationAutomationConfigResponse {

  private List<SimulationAutomation> currentAutomations = new ArrayList<>();
  private List<SimulationAutomation> scheduledAutomations;

}
