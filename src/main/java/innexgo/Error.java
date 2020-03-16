package innexgo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum Error {

  OK(0, "operation completed successfully", HttpStatus.OK),
  UNKNOWN(1, "an unknown error has occured", HttpStatus.INTERNAL_SERVER_ERROR),
  MUST_BE_ROOT(2, "this api key is not registered to a root user", HttpStatus.UNAUTHORIZED),
  MUST_BE_ADMIN(3, "this api key is not registered to an admin user", HttpStatus.UNAUTHORIZED),
  MUST_BE_USER(4, "this api key is not registered to a user", HttpStatus.UNAUTHORIZED),
  USER_NONEXISTENT(5, "the user you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST);

  final private HttpStatus status;
  final long code;
  final String description;

  private Error(long code, String description, HttpStatus status) {
    this.status = status;
    this.code = code;
    this.description = description;
  }

  public ResponseEntity<?> getResponse() {
    return new ResponseEntity<>(this, status);
  }
}
