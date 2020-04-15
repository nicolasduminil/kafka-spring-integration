package fr.simplex_software.tests.kafka.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Metadata
{
  private String call;
  private String date;
  private float version;
}
