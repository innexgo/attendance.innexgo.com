package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RequestRowMapper implements RowMapper<Request> {
  @Override
  public Request mapRow(ResultSet row, int rowNum) throws SQLException {
    Request r = new Request();
    r.id = row.getInt("id");
    r.targetId = row.getInt("target_id");
    r.creatorId = row.getInt("creator_id");
    r.userId = row.getInt("user_id");
    r.reviewed = row.getBoolean("reviewed");
    r.authorized = row.getBoolean("authorized");
    r.creationDate = row.getTimestamp("creation_date");
    r.authorizationDate = row.getTimestamp("authorization_date");

    return r;
  }
}
