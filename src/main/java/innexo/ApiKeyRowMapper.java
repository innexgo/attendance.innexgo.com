package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ApiKeyRowMapper implements RowMapper<ApiKey> {
  @Override
  public ApiKey mapRow(ResultSet row, int rowNum) throws SQLException {
    ApiKey k = new ApiKey();
    k.id = row.getInt("id");
    k.userId = row.getInt("user_id");
    k.creationTime = row.getLong("creation_time");
    k.expirationTime = row.getLong("expiration_time");
    k.keyHash = row.getString("key_hash");
    return k;
  }
}
