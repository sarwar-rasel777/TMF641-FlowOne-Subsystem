package at.compax.reference.subsystem.tmf641.component.simulator.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize
public class WfmRequestBody {

  private String serviceId;

  public static WfmRequestBody withServiceId(String serviceId) {
    WfmRequestBody wfmRequestBody = new WfmRequestBody();
    wfmRequestBody.setServiceId(serviceId);
    return wfmRequestBody;
  }

}
