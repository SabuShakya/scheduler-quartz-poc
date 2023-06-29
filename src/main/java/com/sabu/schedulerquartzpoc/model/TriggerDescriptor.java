package com.sabu.schedulerquartzpoc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : sabu.shakya
 * @created : 2023-06-27
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TriggerDescriptor {

  private String cron;

  private LocalDateTime fireTime;

  @JsonProperty("data")
  private Map<String, Object> data = new LinkedHashMap<>();

}
