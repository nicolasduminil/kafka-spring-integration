package fr.simplex_software.tests.kafka.integration.controllers.impl;

import fr.simplex_software.tests.kafka.integration.controllers.*;
import fr.simplex_software.tests.kafka.model.*;
import fr.simplex_software.tests.kafka.producer.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.web.client.*;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import javax.annotation.*;
import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("/paris-data")
@CrossOrigin
@Api(value = "Paris Data API", description = "Operations pertaining on the Paris Data API")
@Slf4j
public class ParisDataControllerImpl implements ParisDataController
{
  private final String pierreGrimaudRatpApiUrl = "https://api-ratp.pierre-grimaud.fr/v4";
  private RestTemplate restTemplate = new RestTemplateBuilder().rootUri(pierreGrimaudRatpApiUrl).build();
  @Autowired
  private KafkaProducer kafkaProducer;

  @PostConstruct
  public void init()
  {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build());
    restTemplate.setRequestFactory(requestFactory);
  }

  @Override
  @ApiOperation(value = "Returns the list of all the destinations for a given transport type and a line ID", response = Destination.class, responseContainer = "List")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list", response = List.class), @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
  @RequestMapping(method = RequestMethod.GET, path = "/destinations/{type}/{line}", produces = "application/json")
  public @ResponseBody
  ResponseEntity<GetAllDestinationsResponse> getAllDestinationsByTypeAndLine(@ApiParam(value = "The transport type", required = true) @Valid @PathVariable(value = "type") TransportType transportType, @ApiParam(value = "The line ID", required = true) @Valid @PathVariable(value = "line") String lineId)
  {
    GetAllDestinationsResponse getAllDestinationsResponse = null;
    try
    {
      getAllDestinationsResponse = restTemplate.getForObject("/destinations/{type}/{line}", GetAllDestinationsResponse.class, transportType.getTransportTypeName(), lineId);
      log.debug("### Have got {}", getAllDestinationsResponse);
      kafkaProducer.publishKafkaMessage(getAllDestinationsResponse);

    }
    catch (HttpStatusCodeException e)
    {
      log.error("### Fatal: {}", e.getResponseBodyAsString());
    }
    catch (RestClientException e)
    {
      log.error("### Fatal: No response payload {},{}", e.getStackTrace());
      e.printStackTrace();
    }
    return new ResponseEntity<GetAllDestinationsResponse>(getAllDestinationsResponse, HttpStatus.OK);
  }
}
