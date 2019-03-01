
package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PermissionService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Permission getById(int id) {
    String sql = "SELECT id, trusted_user, administrator FROM permission WHERE id=?";
    RowMapper<Permission> rowMapper = new PermissionRowMapper();
    Permission permission = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return permission;
  }

  public List<Permission> getAll() {
    String sql = "SELECT id, trusted_user, administrator FROM permission";
    RowMapper<Permission> rowMapper = new PermissionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }	

  public void add(Permission permission) {
    //Add permission
    String sql = "INSERT INTO permission (trusted_user, administrator) values (?, ?)";
    jdbcTemplate.update(sql, permission.isTrustedUser, permission.isAdministrator);

    //Fetch permission id
    sql = "SELECT id FROM permission WHERE trusted_user=? AND administrator=? ORDER BY id DESC";
    List<Integer> id = jdbcTemplate.queryForList(sql, Integer.class, permission.isTrustedUser, permission.isAdministrator);

    //Set permission id 
    if(!id.isEmpty()) {
      permission.id = id.get(0);
    }
  }

  public void update(Permission permission) {
    String sql = "UPDATE permission trusted_user=?, administrator=? WHERE id=?";
    jdbcTemplate.update(sql, permission.isTrustedUser, permission.isAdministrator, permission.id);
  }

  public void delete(int id) {
    String sql = "DELETE FROM permission WHERE id=?";
    jdbcTemplate.update(sql, id);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM permission WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }
}
