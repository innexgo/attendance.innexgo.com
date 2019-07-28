package innexo;

public class ApiKey {
  public int id;
  int userId;
  public int creationTime;
  public int expirationTime;

  // not public
  String keyHash;

  // Initialized by jackson during serialization, but not persisted
  public String key;
  public User user;
}
