package com.epam.charity.dto.entity;

import com.epam.charity.jooq.dto.tables.Image;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {

  private String id;
  private long date;
  private String header;
  private String content;
  private String url;
  private ImageDTO img;

  @JsonProperty("lang")
  private Set<LanguageDTO> languageDTOSet;
}