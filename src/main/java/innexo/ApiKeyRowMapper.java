package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ApiKeyRowMapper implements RowMapper<ApiKey> {
  @Override
  public ApiKey mapRow(ResultSet row, int rowNum) throws SQLException {
    ApiKey k = new ApiKey();
    k.id = row.getInt("id");
    k.creatorId = row.getInt("creator_id");
    k.creationTime = row.getTimestamp("creation_time");
    k.expirationTime = row.getTimestamp("expiration_time");
    k.keydata = row.getString("keydata");
    return k;
  }
}
