package at.compax.reference.subsystem.tmf641.component.simulator.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum SimulationAutomation {

  NONE,
  WFM_INSTALL,
  WFM_REMOVE,
  WFM_REPLACE,
  ALL_WFM_PROCESSES,
  EVERYTHING;

  public static Optional<SimulationAutomation> findByString(String str) {
    if (StringUtils.isBlank(str)) {
      return Optional.empty();
    }
    return Arrays.stream(values()).filter(v -> v.toString().equalsIgnoreCase(str)).findFirst();
  }

  public boolean isWfmProcess() {
    return switch (this) {
      case WFM_INSTALL, WFM_REMOVE, WFM_REPLACE -> true;
      default -> false;
    };
  }

  public static List<SimulationAutomation> getAllWfmProcesses() {
    return Arrays.stream(values()).filter(SimulationAutomation::isWfmProcess).toList();
  }

}
