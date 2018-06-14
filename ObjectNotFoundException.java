package com.epam.charity.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ObjectNotFoundException extends BaseCustomRuntimeException {
  @Builder
  public ObjectNotFoundException(String detail, String title, String pointer) {
    super(detail, title, pointer, HttpStatus.NOT_FOUND);
  }
}
