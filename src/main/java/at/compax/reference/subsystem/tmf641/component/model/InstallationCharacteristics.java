package at.compax.reference.subsystem.tmf641.component.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
public class InstallationCharacteristics {

  private String key;
  private String value;

}
