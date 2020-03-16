package innexgo;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;

public class ApiError {
  public int status;
  public String error;
  public String message;
  public Timestamp timestamp;

  public ApiError(HttpStatus status, String description) {
    this.timestamp = new Timestamp(System.currentTimeMillis());
    this.status = status.value();
    this.error = status.toString();
    this.message = description;
  }
}
