package innexo;

import java.sql.Timestamp;

public class Target {
	public int id;
	int userId;
	int locationId;
	public String name;
	public Timestamp minTime;
	public Timestamp maxTime;
	
	public Location location;
	public User responsibleUser;
}
