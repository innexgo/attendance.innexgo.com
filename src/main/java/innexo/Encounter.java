package innexo;
import java.sql.Timestamp;

public class Encounter {
	public int id;
	public Timestamp time;
	int locationId;
	int userId;
	public String type;
	
	//Initialized by jackson during serialization
	public User user;
	public Location location;
	

}
