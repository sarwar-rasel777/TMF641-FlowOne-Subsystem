package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.reference.subsystem.tmf641.component.model.ListenerRequest;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestModel;
import at.compax.reference.subsystem.tmf641.component.translator.HubQueryRequestTranslator;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("queryListener")
public class QueryListenerService extends AbstractService {

  @Autowired
  private HubQueryRequestTranslator translator;

  @Override
  @SneakyThrows
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    ListenerRequest request = translator.translate(valuesConsumerFactory.consumer(model.getValues()));

    SubsystemRestModel<Void> restModel = new SubsystemRestModel<>();
    restModel.setUrl(apiClient.buildBaseUriHubId(request.getId()));
    restModel.setMethod(HttpMethod.GET.name());
    restModel.setHeaders(apiClient.buildBaseHeaders());

    return new PayloadCreationResponseModel().payload(objectMapper.writeValueAsBytes(restModel));
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    log.info("Sending queryListener request...");
    try {
      SubsystemRestModel<?> request = objectMapper.readValue(model.getPayload(), SubsystemRestModel.class);
      return this.payloadSuccessResponseModel(objectMapper.writeValueAsBytes(apiClient.exchange(request)));
    } catch (Exception e) {
      log.error("Error while sending queryListener request.", e);
      return this.payloadErrorResponseModel();
    }
  }

}
