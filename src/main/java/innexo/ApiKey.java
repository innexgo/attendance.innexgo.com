package innexo;

import java.sql.Timestamp;

public class ApiKey {
  public int id;
  int creatorId;
  public Timestamp creationTime;
  public Timestamp expirationTime;

  // not public
  String keyHash;

  // Initialized by jackson during serialization, but not persisted
  public String key;
  public User creator;
}
