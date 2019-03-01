package innexo;

import java.sql.Timestamp;

public class Request{
	public int id;
	public int targetId;
	public int creatorId;
	public int userId;
	public boolean authorized;
	public Timestamp creationDate;
	public Timestamp authorizationDate;
}
