package innexo;

public class User {
  public int id;
  int managerId;
  public String name;
  // not public so they don't get serialized to jackson
  String passwordHash;
  public boolean administrator;
  public boolean trustedUser;
}
