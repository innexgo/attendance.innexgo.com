package innexgo;

public class User {
  public int id;
  public int cardId;
  public String name;
  public String email;
  // not public so they don't get serialized to jackson
  String passwordHash;
  public int ring;
  public String prefstring;
}
