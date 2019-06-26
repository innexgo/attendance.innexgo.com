package innexo;

public class User {
  public int id;
  public int managerId;
  public String name;
  // not public so they don't get serialized to jackson
  String passwordHash;
  boolean administrator;
  boolean trustedUser;
}
