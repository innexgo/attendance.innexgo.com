package innexo;

import java.util.List;

public class User {
  public int id;
  public String name;
  // not public so they don't get serialized to jackson
  String passwordHash;
  public boolean administrator;
  public boolean trustedUser;

  // For jackson only
  public List<User> managers;
}
