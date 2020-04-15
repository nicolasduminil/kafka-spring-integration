package fr.simplex_software.tests.kafka.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Destination
{
  @JsonProperty("name")
  private String stationName;
  @JsonProperty("way")
  private String platformId;
}
