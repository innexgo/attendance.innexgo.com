package innexo;


public class User {
  public int id;
  public String name;
  //not public so they don't get serialized to jackson
  String passwordHash;
  int permissionId; 
  int groupId; //User group user belongs to
}
