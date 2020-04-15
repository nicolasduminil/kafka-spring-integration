package fr.simplex_software.tests.kafka.model;

import lombok.*;
import lombok.extern.slf4j.*;

public enum TransportType
{
  SUBWAY("metros"), BUS("buses"), TRAMWAY("tramways"), SUB_URBAN_TRAIN("rers");
  private String transportTypeName;

  TransportType(String transportTypeName)
  {
    this.transportTypeName = transportTypeName;
  }

  public String getTransportTypeName()
  {
    return transportTypeName;
  }
}
