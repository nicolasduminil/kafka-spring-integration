package fr.simplex_software.tests.kafka.integration.controllers;

import fr.simplex_software.tests.kafka.model.*;
import org.springframework.http.*;

public interface ParisDataController
{
  public ResponseEntity<GetAllDestinationsResponse> getAllDestinationsByTypeAndLine (TransportType transportType, String lineId);
}
