package at.compax.reference.subsystem.tmf641.component.service;

import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.reference.subsystem.tmf641.component.model.CancelServiceOrder;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestModel;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestResponseModel;
import at.compax.reference.subsystem.tmf641.component.translator.InFlightCancelOrderRequestTranslator;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("inFlightCancelServiceOrder")
public class InFlightCancelServiceOrderService extends AbstractService {

  @Autowired
  private InFlightCancelOrderRequestTranslator translator;

  @Override
  @SneakyThrows
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    ValuesConsumer consumer = valuesConsumerFactory.consumer(model.getValues());
    CancelServiceOrder request = translator.translate(consumer);

    SubsystemRestModel<CancelServiceOrder> restModel = new SubsystemRestModel<>();
    restModel.setUrl(apiClient.buildBaseUriCancelServiceOrderWithFlowoneId(consumer.stringValue(ValueKeys.SERVICE_ORDER_EXTERNAL_ID)));
    restModel.setMethod(HttpMethod.POST.name());
    restModel.setBody(request);
    restModel.setHeaders(apiClient.buildBaseHeaders());

    return new PayloadCreationResponseModel().payload(objectMapper.writeValueAsBytes(restModel));
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    log.info("Sending inFlightCancelServiceOrder request...");
    try {
      SubsystemRestModel<?> request = objectMapper.readValue(model.getPayload(), SubsystemRestModel.class);
      SubsystemRestResponseModel<Void> exchange = apiClient.exchange(request);
      if (!exchange.getHttpStatus().is2xxSuccessful()) {
        ValuesBuilder valuesBuilder = valuesBuilderFactory.builder().addValue("returnCode", "NOK");
        return new PayloadSendingResponseModel().values(valuesBuilder.toValues()).response("Did not receive 2xx Code".getBytes());
      }

      return this.payloadSuccessResponseModel(objectMapper.writeValueAsBytes(exchange));
    } catch (Exception e) {
      log.error("Error while sending inFlightCancelServiceOrder request.", e);
      return payloadErrorResponseModel();
    }
  }

}
