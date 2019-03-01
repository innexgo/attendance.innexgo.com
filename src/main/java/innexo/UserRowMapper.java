package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet row, int rowNum) throws SQLException {
		User u = new User ();
    u.id = row.getInt("id");
    u.name = row.getString("name");
    u.passwordHash= row.getString("password_hash");
    u.permissionId = row.getInt("permission_id");
	return u;
  }
}
