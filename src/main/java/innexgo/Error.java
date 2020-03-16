package innexgo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum Error {

  OK(0, "operation completed successfully", HttpStatus.OK),
  MUST_BE_ROOT(2, "this api key is not registered to a root user", HttpStatus.UNAUTHORIZED),
  MUST_BE_ADMIN(3, "this api key is not registered to an admin user", HttpStatus.UNAUTHORIZED),
  MUST_BE_USER(4, "this api key is not registered to a user", HttpStatus.UNAUTHORIZED),
  PASSWORD_INCORRECT(5, "this password is not valid for this user", HttpStatus.UNAUTHORIZED),
  USER_NONEXISTENT(5, "the user you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  LOCATION_NONEXISTENT(6, "the location you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  COURSE_NONEXISTENT(6, "the course you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  OFFERING_EXISTENT(6, "the offering your are trying to create already exists", HttpStatus.BAD_REQUEST),
  SEMESTER_NONEXISTENT(6, "the semester you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  LOCATION_EXISTENT(6, "the location you are trying to create already exists", HttpStatus.BAD_REQUEST),
  STUDENT_NONEXISTENT(6, "the student you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  GRADE_NONEXISTENT(6, "the grade you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  GRADE_EXISTENT(6, "the grade you are trying to create already exists", HttpStatus.BAD_REQUEST),
  COURSE_SUBJECT_EMPTY(5, "the course subject must not be empty", HttpStatus.BAD_REQUEST),
  LOCATION_NAME_EMPTY(5, "the location name must not be empty", HttpStatus.BAD_REQUEST),
  UNKNOWN(1, "an unknown error has occured", HttpStatus.INTERNAL_SERVER_ERROR);

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
