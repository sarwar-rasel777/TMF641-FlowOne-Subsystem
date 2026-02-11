package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ServiceCharacteristicNames;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.RelatedParty;
import at.compax.reference.subsystem.tmf641.component.model.ReplyToAddress;
import at.compax.reference.subsystem.tmf641.component.model.Service;
import at.compax.reference.subsystem.tmf641.component.model.ServiceCharacteristic;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderCreate;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItem;
import at.compax.reference.subsystem.tmf641.component.model.ServiceSpecification;

@Component
public class ServiceCreateOrderRequestTranslator implements Translator<ServiceOrderCreate> {

  @Value("${CALLBACK_AUTH_ENABLED:false}")
  private String authEnabled;
  @Value("${CALLBACK_USERNAME:}")
  private String authUsername;
  @Value("${CALLBACK_PASSWORD:}")
  private String authPassword;

  @Override
  public ServiceOrderCreate translate(ValuesConsumer consumer) {
    ServiceOrderCreate request = createServiceOrderRequest(consumer);

    RelatedParty relatedParty = new RelatedParty();
    relatedParty.setId(consumer.stringValue(ValueKeys.ACCOUNT_ID));
    relatedParty.setRole(consumer.stringValue(ValueKeys.ROLE));
    request.setRelatedParty(List.of(relatedParty));

    ReplyToAddress replyToAddress = new ReplyToAddress();
    replyToAddress.setId(1);
    replyToAddress.setUrl(consumer.stringValueOptional("callbackUrlForNotifications"));
    replyToAddress.setIsAuthEnabled(authEnabled);
    replyToAddress.setAuthUsername(authUsername);
    replyToAddress.setAuthPassword(authPassword);
    request.setReplyTo(replyToAddress);

    ServiceOrderItem serviceOrderItem = new ServiceOrderItem();
    serviceOrderItem.setId("1");
    serviceOrderItem.setAction(consumer.stringValue(ValueKeys.ACTION));
    serviceOrderItem.setAtType(consumer.stringValue(ValueKeys.AT_TYPE));
    addServiceToServiceOrderItem(serviceOrderItem, consumer);
    request.getServiceOrderItem().add(serviceOrderItem);

    return request;
  }

  protected ServiceOrderCreate createServiceOrderRequest(ValuesConsumer consumer) {
    ServiceOrderCreate request = new ServiceOrderCreate();
    request.setExternalId(consumer.stringValue(ValueKeys.EXTERNAL_ID));
    request.setServiceUuid(consumer.stringValue(ValueKeys.PARENT_SERVICE_UUID));
    request.setAtType(consumer.stringValue(ValueKeys.AT_TYPE));
    request.setCategory(consumer.stringValue(ValueKeys.CATEGORY));
    request.setDescription(consumer.stringValue(ValueKeys.DESCRIPTION));
    request.setPriority(consumer.stringValue(ValueKeys.PRIORITY));
    return request;
  }

  protected void addServiceToServiceOrderItem(ServiceOrderItem serviceOrderItem, ValuesConsumer consumer) {
    Service service = new Service();
    List<ServiceCharacteristic> serviceCharacteristics = service.getServiceCharacteristic();
    addServiceCharacteristic(serviceCharacteristics, consumer, ValueKeys.TENANT, ServiceCharacteristicNames.TENANT);
    addServiceCharacteristic(serviceCharacteristics, consumer, ValueKeys.MARKET_ID, ServiceCharacteristicNames.MARKET_ID);
    addServiceCharacteristic(serviceCharacteristics, consumer, ValueKeys.ADDRESS_KEY, ServiceCharacteristicNames.ADDRESS_KEY);
    if(!addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.MBIT_UP_NEW, ServiceCharacteristicNames.MBIT_UP)
    || !addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.MBIT_DOWN_NEW, ServiceCharacteristicNames.MBIT_DOWN)){
      addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.MBIT_UP, ServiceCharacteristicNames.MBIT_UP);
      addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.MBIT_DOWN, ServiceCharacteristicNames.MBIT_DOWN);
    }
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.REMOVE_ONT, ServiceCharacteristicNames.REMOVE_ONT);
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.TRANSFERRED_STATE, ServiceCharacteristicNames.TRANSFERRED_STATE);
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.STATIC_IPV4, ServiceCharacteristicNames.STATIC_IPV4);
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.STATIC_IPV6, ServiceCharacteristicNames.STATIC_IPV6);
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.ONT_SERIALNUMBER, ServiceCharacteristicNames.ONT_SERIALNUMBER);
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.STATIC_IPV4_VALUE, ServiceCharacteristicNames.STATIC_IPV4_VALUE);
    addServiceCharacteristicIfPresent(serviceCharacteristics, consumer, ValueKeys.STATIC_IPV6_VALUE, ServiceCharacteristicNames.STATIC_IPV6_VALUE);

    ServiceSpecification serviceSpecification = new ServiceSpecification();
    serviceSpecification.setId("1");
    serviceSpecification.setName("CFS_HSISERVICE"); // clarify, if this is a static value or should be part of product config
    serviceSpecification.setVersion("1.0");
    service.setServiceSpecification(serviceSpecification);
    serviceOrderItem.setService(service);
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, ServiceOrderCreate model) {
    return builder;
  }

  protected ServiceCharacteristic newServiceCharacteristic(String name, String value) {
    ServiceCharacteristic serviceCharacteristic = new ServiceCharacteristic();
    serviceCharacteristic.setName(name);
    serviceCharacteristic.setValue(value);
    return serviceCharacteristic;
  }

  protected void addServiceCharacteristic(List<ServiceCharacteristic> serviceCharacteristics, ValuesConsumer consumer, String consumerKey, String characteristicName) {
    serviceCharacteristics.add(newServiceCharacteristic(characteristicName, consumer.stringValue(consumerKey)));
  }

  protected boolean addServiceCharacteristicIfPresent(List<ServiceCharacteristic> serviceCharacteristics, ValuesConsumer consumer, String consumerKey, String characteristicName) {
    AtomicBoolean added = new AtomicBoolean(false);
    Optional.ofNullable(consumer.stringValueOptional(consumerKey)).ifPresent(
        cv -> {
          serviceCharacteristics.add(newServiceCharacteristic(characteristicName, cv));
          added.set(true);
        }
    );
    return added.get();
  }

}
