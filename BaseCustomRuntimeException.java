package com.epam.charity.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseCustomRuntimeException extends RuntimeException {
  protected String detail;
  protected String title;
  protected String pointer;
  protected HttpStatus httpStatus;
}
