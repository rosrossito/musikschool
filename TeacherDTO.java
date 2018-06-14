package com.epam.charity.dto.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherDTO {

  private String id;
  private String email;

  @JsonProperty("about")
  private String slogan;

  @JsonProperty("photo")
  private ImageDTO img;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("position")
  private String positionTitle;


  // New ft requested by FE. Uncomment only if definitely needed by FE and approved by other parties
  @JsonProperty("head")
  private Boolean isHead;

  @JsonProperty("lang_Id")
  private String languageId;

  @JsonProperty("department")
  private Set<DepartmentDTO> departmentDTOSet;
}
