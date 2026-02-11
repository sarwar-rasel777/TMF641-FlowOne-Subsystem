package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.model.InstallationCallbackModel;
import at.compax.reference.subsystem.tmf641.component.rest.Oauth2ClientRestTemplate;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractCallbackSender extends AbstractService {

  @Autowired
  private Oauth2ClientRestTemplate restTemplate;

  @Override
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    log.info("Creating request for {} callback.", getAction());
    ValuesConsumer modelValues = valuesConsumerFactory.consumer(model.getValues());
    InstallationCallbackModel installationCallback = getTranslator().translate(modelValues);
    try {
      return new PayloadCreationResponseModel().payload(objectMapper.writeValueAsBytes(installationCallback));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    try {
      InstallationCallbackModel InstallationCallback = objectMapper.readValue(model.getPayload(), InstallationCallbackModel.class);
      ResponseEntity<Void> exchange = restTemplate.exchange(apiClient.buildInstallationCallbackUri(), HttpMethod.POST, new HttpEntity<>(InstallationCallback, apiClient.buildBaseHeaders()), Void.class);
      return this.payloadSuccessResponseModel(objectMapper.writeValueAsBytes(exchange));
    } catch (Exception e) {
      log.error("Error while sending {} callback.\n{}", getAction(), e);
      return this.payloadErrorResponseModel();
    }
  }

  protected abstract Translator<InstallationCallbackModel> getTranslator();

  protected abstract String getAction();

}
