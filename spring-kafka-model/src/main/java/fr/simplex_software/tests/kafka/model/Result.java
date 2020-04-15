package fr.simplex_software.tests.kafka.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Result
{
  @JsonProperty("destinations")
  private ArrayList<Destination> destinations;
}
