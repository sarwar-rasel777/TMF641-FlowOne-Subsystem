package at.compax.reference.subsystem.tmf641.component.service;

import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestModel;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("queryAllListener")
public class QueryAllListenerService extends AbstractService {

  @Override
  @SneakyThrows
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    SubsystemRestModel<Void> restModel = new SubsystemRestModel<>();
    restModel.setUrl(apiClient.buildBaseUriHub().encode(StandardCharsets.UTF_8).build().toUri());
    restModel.setMethod(HttpMethod.GET.name());
    restModel.setHeaders(apiClient.buildBaseHeaders());

    return new PayloadCreationResponseModel().payload(objectMapper.writeValueAsBytes(restModel));
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    log.info("Sending queryAllListener request...");
    try {
      SubsystemRestModel<?> request = objectMapper.readValue(model.getPayload(), SubsystemRestModel.class);
      return this.payloadSuccessResponseModel(objectMapper.writeValueAsBytes(apiClient.exchange(request)));
    } catch (Exception e) {
      log.error("Error while sending queryAllListener request.", e);
      return this.payloadErrorResponseModel();
    }
  }

}
