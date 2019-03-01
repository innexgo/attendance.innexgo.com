package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PermissionRowMapper implements RowMapper<Permission> {
  @Override
  public Permission mapRow(ResultSet row, int rowNum) throws SQLException {
    Permission p = new Permission();
    p.id = row.getInt("id");
    p.isTrustedUser= row.getBoolean("trusted_user");
    p.isAdministrator = row.getBoolean("administrator");
    return p;
  }
}
