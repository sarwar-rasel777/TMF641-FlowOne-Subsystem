package at.compax.reference.subsystem.tmf641.component.model;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
public class InstallationCallbackModel {

  private String requestId;
  private String serviceId;
  private String status;
  private String failureReason;
  private List<InstallationCharacteristics> installationCharacteristics;

}
