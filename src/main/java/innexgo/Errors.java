/*
 * Innexgo Website
 * Copyright (C) 2020 Innexgo LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package innexgo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum Errors {

  OK("operation completed successfully", HttpStatus.OK),
  MUST_BE_ROOT("this api key is not registered to a root user", HttpStatus.UNAUTHORIZED),
  MUST_BE_ADMIN("this api key is not registered to an admin user", HttpStatus.UNAUTHORIZED),
  MUST_BE_USER("this api key is not registered to a user", HttpStatus.UNAUTHORIZED),
  DATABASE_INITIALIZED("the database already contains some users", HttpStatus.UNAUTHORIZED),
  PASSWORD_INCORRECT("this password is not valid for this user", HttpStatus.UNAUTHORIZED),
  PASSWORD_INSECURE("this password does not meet security requirements", HttpStatus.UNAUTHORIZED),
  USER_NONEXISTENT("the user you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  APIKEY_NONEXISTENT("the api key you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  LOCATION_NONEXISTENT("the location you are trying to perform this operation on does not exist",
      HttpStatus.BAD_REQUEST),
  COURSE_NONEXISTENT("the course you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  OFFERING_EXISTENT("the offering your are trying to create already exists", HttpStatus.BAD_REQUEST),
  SEMESTER_NONEXISTENT("the semester you are trying to perform this operation on does not exist",
      HttpStatus.BAD_REQUEST),
  LOCATION_EXISTENT("the location you are trying to create already exists", HttpStatus.BAD_REQUEST),
  SCHEDULE_EXISTENT("the schedule you are trying to create already exists", HttpStatus.BAD_REQUEST),
  STUDENT_EXISTENT("the student you are trying to create already exists", HttpStatus.BAD_REQUEST),
  USER_EXISTENT("a user with this email already exists", HttpStatus.BAD_REQUEST),
  STUDENT_NONEXISTENT("the student you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  PERIOD_NONEXISTENT("the period you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  INVALID_PERIOD_TYPE("this is not a valid type for a period", HttpStatus.BAD_REQUEST),
  INVALID_ENCOUNTER_TYPE("this is not a valid type for an encounter", HttpStatus.BAD_REQUEST),
  INVALID_SEMESTER_TYPE("this is not a valid type for a semester", HttpStatus.BAD_REQUEST),
  INVALID_IRREGULARITY_TYPE("this is not a valid type for an irregularity", HttpStatus.BAD_REQUEST),
  GRADE_NONEXISTENT("the grade you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  SCHEDULE_NONEXISTENT("the schedule you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  IRREGULARITY_NONEXISTENT("the irregularity you are trying to perform this operation on does not exist", HttpStatus.BAD_REQUEST),
  GRADE_EXISTENT("the grade you are trying to create already exists", HttpStatus.BAD_REQUEST),
  COURSE_SUBJECT_EMPTY("the course subject must not be empty", HttpStatus.BAD_REQUEST),
  LOCATION_NAME_EMPTY("the location name must not be empty", HttpStatus.BAD_REQUEST),
  STUDENT_NAME_EMPTY("the student name must not be empty", HttpStatus.BAD_REQUEST),
  USER_NAME_EMPTY("the user name must not be empty", HttpStatus.BAD_REQUEST),
  USER_EMAIL_EMPTY("the user email must not be empty", HttpStatus.BAD_REQUEST),
  UNKNOWN("an unknown error has occured", HttpStatus.INTERNAL_SERVER_ERROR);

  final private HttpStatus httpStatus;
  final String description;

  private Errors(String description, HttpStatus status) {
    this.httpStatus = status;
    this.description = description;
  }

  public ResponseEntity<?> getResponse() {
    return new ResponseEntity<>(new ApiError(httpStatus, description), httpStatus);
  }
}

