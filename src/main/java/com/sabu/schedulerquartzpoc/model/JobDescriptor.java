package com.sabu.schedulerquartzpoc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
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
public class JobDescriptor {

  @NotBlank
  private String type;

  @NotBlank
  private String name;

  private String group;

  private String description;

  @JsonProperty("data")
  private Map<String, Object> data = new LinkedHashMap<>();

  @JsonProperty("triggers")
  private List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();
}
