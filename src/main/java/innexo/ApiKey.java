package innexo;
import java.sql.Timestamp;

public class ApiKey {
  public int id;
  int creatorId;
  public Timestamp creationTime;
  public Timestamp expirationTime;
  public String key;

  // Initialized by jackson during serialization
  public User creator;
}
