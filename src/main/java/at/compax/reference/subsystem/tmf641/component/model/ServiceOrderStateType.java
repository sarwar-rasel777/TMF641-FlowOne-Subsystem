package at.compax.reference.subsystem.tmf641.component.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceOrderStateType {

  ACKNOWLEDGED("acknowledged"),
  REJECTED("rejected"),
  PENDING("pending"),
  INPROGRESS("inProgress"),
  CANCELLED("cancelled"),
  COMPLETED("completed"),
  FAILED("failed");

  private final String value;

  ServiceOrderStateType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ServiceOrderStateType fromValue(String value) {
    for (ServiceOrderStateType b : ServiceOrderStateType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }

}
