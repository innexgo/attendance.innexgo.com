package innexgo;

public class User {
  public long id;
  public String name;
  public String email;
  // not public so they don't get serialized to jackson
  String passwordHash;
  public int ring;
  String prefstring;
}
