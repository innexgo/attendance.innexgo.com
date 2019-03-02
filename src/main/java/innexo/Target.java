package innexo;

import java.sql.Timestamp;

public class Target {
	public int id;
	int organizerId;
	public String name;
	public Timestamp minTime;
	public Timestamp maxTime;
	
	public User organizer;
}

