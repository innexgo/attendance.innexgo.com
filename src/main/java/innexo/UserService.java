
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
public class UserService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public User getById(int id) {
    String sql = "SELECT id, name, password_hash, permission_id FROM user WHERE id=?";
    RowMapper<User> rowMapper = new UserRowMapper();
    User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return user;
  }
  
  public List<User> getByName(String name) {
    String sql = "SELECT id, name, password_hash, permission_id FROM user WHERE name=?";
    RowMapper<User> rowMapper = new UserRowMapper();
    List<User> users = jdbcTemplate.query(sql, rowMapper, name);
    return users;
  }
  
  public List<User> getAll() {
    String sql = "SELECT id,  name, password_hash, permission_id FROM user";
    RowMapper<User> rowMapper = new UserRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }	

  public void add(User user) {
    //Add user
    String sql = "INSERT INTO user (id,  name, password_hash, permission_id) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, user.id, user.name, user.passwordHash, user.permissionId);

    //Fetch user id
    sql = "SELECT id FROM user WHERE name=? AND password_hash=? AND permission_id=?";
    int id = jdbcTemplate.queryForObject(sql, Integer.class, user.name, user.passwordHash, user.permissionId);

    //Set user id 
    user.id = id;
  }

  public void update(User user) {
    String sql = "UPDATE user SET id=?, name=?, password_hash=?, permission_id=? WHERE id=?";
    jdbcTemplate.update(sql, user.id, user.name, user.passwordHash, user.permissionId, user.id);
  }

  public void delete(int id) {
    String sql = "DELETE FROM user WHERE id=?";
    jdbcTemplate.update(sql, id);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM user WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    if(count == 0) {
      return false;
    } else {
      return true;
    }
  }
}
