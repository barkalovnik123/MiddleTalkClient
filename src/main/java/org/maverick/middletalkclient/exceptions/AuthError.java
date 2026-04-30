package org.maverick.middletalkclient.exceptions;

public class AuthError extends RuntimeException {
  public AuthError(String message) {
    super(message);
  }
}
