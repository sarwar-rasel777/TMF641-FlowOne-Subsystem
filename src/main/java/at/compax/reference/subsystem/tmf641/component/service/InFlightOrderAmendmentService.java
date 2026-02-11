package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderPatch;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestModel;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestResponseModel;
import at.compax.reference.subsystem.tmf641.component.translator.InFlightOrderAmendmentRequestTranslator;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("inFlightOrderAmendment")
public class InFlightOrderAmendmentService extends AbstractService {

  @Autowired
  private InFlightOrderAmendmentRequestTranslator translator;

  @Override
  @SneakyThrows
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    ValuesConsumer valuesConsumer = valuesConsumerFactory.consumer(model.getValues());
    ServiceOrderPatch request = translator.translate(valuesConsumer);

    SubsystemRestModel<ServiceOrderPatch> restModel = new SubsystemRestModel<>();
    restModel.setUrl(apiClient.buildBaseUriForServiceAmend(valuesConsumer.stringValue(ValueKeys.SERVICE_ORDER_EXTERNAL_ID)));
    restModel.setMethod(HttpMethod.PATCH.name());
    restModel.setBody(request);
    restModel.setHeaders(apiClient.buildBaseHeaders());

    return new PayloadCreationResponseModel().payload(objectMapper.writeValueAsBytes(restModel));
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    log.info("Sending inFlightOrderAmendment request...");
    try {
      SubsystemRestModel<?> request = objectMapper.readValue(model.getPayload(), SubsystemRestModel.class);
      SubsystemRestResponseModel<Void> exchange = apiClient.exchange(request);
      if (!exchange.getHttpStatus().is2xxSuccessful()) {
        ValuesBuilder valuesBuilder = valuesBuilderFactory.builder().addValue("returnCode", "NOK");
        return new PayloadSendingResponseModel().values(valuesBuilder.toValues()).response("Did not receive 2xx Code".getBytes());
      }

      return this.payloadSuccessResponseModel(objectMapper.writeValueAsBytes(exchange));
    } catch (Exception e) {
      log.error("Error while sending inFlightOrderAmendment request.", e);
      return this.payloadErrorResponseModel();
    }
  }

}
