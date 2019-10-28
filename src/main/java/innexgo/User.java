package innexgo;

public class User {
  static final int ADMINISTRATOR = 0;
  static final int TEACHER = 1;

  public long id;
  public String name;
  public String email;
  // not public so they don't get serialized to jackson
  String passwordHash;
  public int ring;
}
