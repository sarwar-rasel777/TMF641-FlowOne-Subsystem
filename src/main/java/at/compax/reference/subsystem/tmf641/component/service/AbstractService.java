package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.service.component.model.ValuesBuilderFactory;
import at.compax.foundation.subsystem.service.component.model.ValuesConsumerFactory;
import at.compax.foundation.subsystem.service.component.service.SubsystemService;
import at.compax.reference.subsystem.tmf641.component.client.ApiClient;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderCreate;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestModel;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestResponseModel;
import at.compax.reference.subsystem.tmf641.component.translator.ServiceCreateOrderRequestTranslator;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractService implements SubsystemService {

  protected static final String RC_OK = "OK";
  protected static final String RC_NOK = "NOK";

  @Autowired
  protected ValuesConsumerFactory valuesConsumerFactory;
  @Autowired
  protected ValuesBuilderFactory valuesBuilderFactory;
  @Autowired
  protected ObjectMapper objectMapper;
  @Autowired
  protected ApiClient apiClient;

  protected PayloadSendingResponseModel payloadErrorResponseModel() {
    ValuesBuilder builder = valuesBuilderFactory.builder();
    builder.addValue(ValueKeys.RETURN_CODE, RC_NOK);
    return new PayloadSendingResponseModel().values(builder.toValues());
  }

  PayloadCreationResponseModel getPayloadCreationResponseModel(PayloadCreationModel model, ServiceCreateOrderRequestTranslator serviceCreateOrderRequestTranslator) throws JsonProcessingException {
    ServiceOrderCreate request = serviceCreateOrderRequestTranslator.translate(valuesConsumerFactory.consumer(model.getValues()));
    SubsystemRestModel<ServiceOrderCreate> restModel = new SubsystemRestModel<>();
    restModel.setUrl(apiClient.buildBaseUriForServiceOrders());
    restModel.setMethod(HttpMethod.POST.name());
    restModel.setBody(request);
    restModel.setExternalId(request.getExternalId());
    restModel.setHeaders(apiClient.buildBaseHeaders());
    return new PayloadCreationResponseModel().payload(objectMapper.writeValueAsBytes(restModel));
  }

  PayloadSendingResponseModel getPayloadSendingResponseModel(PayloadSendingModel model) {
    try {
      SubsystemRestModel<?> request = objectMapper.readValue(model.getPayload(), SubsystemRestModel.class);
      SubsystemRestResponseModel<Void> exchange = apiClient.exchange(request);
      log.info(exchange.getHttpStatus());
      log.info(exchange.getHttpStatusCode());
      if (!exchange.getHttpStatus().is2xxSuccessful()) {
        ValuesBuilder valuesBuilder = valuesBuilderFactory.builder().addValue(ValueKeys.RETURN_CODE, RC_NOK);
        return new PayloadSendingResponseModel().values(valuesBuilder.toValues()).response("Did not receive 2xx Code".getBytes());
      }
      ValuesBuilder valuesBuilder = valuesBuilderFactory.builder().addValue(ValueKeys.RETURN_CODE, RC_OK).addValue(ValueKeys.EXTERNAL_ID, request.getExternalId());
      return new PayloadSendingResponseModel().values(valuesBuilder.toValues());
    } catch (Exception e) {
      log.error(e);
      ValuesBuilder valuesBuilder = valuesBuilderFactory.builder().addValue(ValueKeys.RETURN_CODE, RC_NOK);
      return new PayloadSendingResponseModel().values(valuesBuilder.toValues()).response(e.getMessage().getBytes());
    }
  }

  @SneakyThrows
  protected PayloadSendingResponseModel payloadSuccessResponseModel(byte[] response) {
    ValuesBuilder builder = valuesBuilderFactory.builder();
    builder.addValue(ValueKeys.RETURN_CODE, "OK");
    builder.addValue(ValueKeys.RETURN_DESCRIPTION, "Success response, no error");
    return new PayloadSendingResponseModel().values(builder.toValues()).response(response);
  }

}
