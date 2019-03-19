package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RequestRowMapper implements RowMapper<Request> {

//	there are three people responsible for each requesti
//
//	creator = person who creates the request for student to go to classroom
//	user = student who is intended to go to the classroom
//	authorizer = person who must authorize the request

  @Override
  public Request mapRow(ResultSet row, int rowNum) throws SQLException {
    Request r = new Request();
    r.id = row.getInt("id");
    r.targetId = row.getInt("target_id");
    r.creatorId = row.getInt("creator_id");
    r.userId = row.getInt("user_id");
    r.authorizerId = row.getInt("authorizer_id");
    r.reviewed = row.getBoolean("reviewed");
    r.authorized = row.getBoolean("authorized");
    r.creationDate = row.getTimestamp("creation_date");
    r.authorizationDate = row.getTimestamp("authorization_date");
    r.reason = row.getString("reason");

    return r;
  }
}
