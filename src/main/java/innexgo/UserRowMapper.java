package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet row, int rowNum) throws SQLException {
    User u = new User();
    u.id = row.getInt("id");
    u.cardId = row.getInt("card_id");
    u.name = row.getString("name");
    u.email = row.getString("email");
    u.passwordHash = row.getString("password_hash");
    u.ring = row.getInt("ring");
    u.prefstring = row.getString("prefstring");
    return u;
  }
}
