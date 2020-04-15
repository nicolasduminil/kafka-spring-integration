package fr.simplex_software.tests.kafka.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class GetAllDestinationsResponse
{
  @JsonProperty("result")
  private Result result;
  @JsonProperty("_metadata")
  private Metadata metadata;
}
