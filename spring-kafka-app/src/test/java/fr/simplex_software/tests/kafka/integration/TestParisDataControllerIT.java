package fr.simplex_software.tests.kafka.integration;

import fr.simplex_software.tests.kafka.model.*;
import fr.simplex_software.tests.kafka.model.Result;
import lombok.extern.slf4j.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.web.client.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.*;
import org.springframework.web.client.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class TestParisDataControllerIT
{
  private RestTemplate restTemplate;

  @Before
  public void init()
  {
    restTemplate = new RestTemplateBuilder().rootUri("http://localhost:8080/paris-data").build();
  }

  @Test
  public void testParisDataController()
  {
    ResponseEntity<GetAllDestinationsResponse> getAllDestinationsResponseResponseEntity = restTemplate.exchange("/destinations/{type}/{line}", HttpMethod.GET, null, GetAllDestinationsResponse.class, TransportType.SUBWAY.toString(), "8");
    assertNotNull(getAllDestinationsResponseResponseEntity);
    assertEquals(getAllDestinationsResponseResponseEntity.getStatusCode(), HttpStatus.OK);
    GetAllDestinationsResponse getAllDestinationsResponse = getAllDestinationsResponseResponseEntity.getBody();
    assertNotNull(getAllDestinationsResponse);
    Result result = getAllDestinationsResponse.getResult();
    assertNotNull(result);
    List<Destination> destinations = result.getDestinations();
    assertNotNull(destinations);
    assertTrue(destinations.size() > 0);
    Destination destination = destinations.get(0);
    assertNotNull(destination);
    assertEquals("Pointe du Lac",destination.getStationName());
  }
}
