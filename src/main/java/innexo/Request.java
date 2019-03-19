package innexo;

import java.sql.Timestamp;

public class Request{
	public int id;
	int targetId;
	int creatorId;
	int userId;
	int authorizerId;
	public boolean reviewed = false;
	public Boolean authorized;
	public Timestamp creationDate;
	public Timestamp authorizationDate;
	public String reason;
	
	//For jackson only
	public Target target;
	public User creator;
	public User user;
	
	
}
