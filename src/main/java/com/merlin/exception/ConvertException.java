package com.merlin.exception;

public class ConvertException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConvertException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConvertException(String message) {
    super(message);
  }

  public ConvertException(Throwable cause) {
    super(cause);
  }

  public ConvertException() {}
}
