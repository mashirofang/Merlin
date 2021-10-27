package cn.org.merlin.exception;

public class SPIException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public SPIException(String message, Throwable cause) {
    super(message, cause);
  }

  public SPIException(String message) {
    super(message);
  }

  public SPIException(Throwable cause) {
    super(cause);
  }

  public SPIException() {}
}
