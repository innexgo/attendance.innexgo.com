package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class UserService {
  @Autowired private JdbcTemplate jdbcTemplate;

  static final int ADMINISTRATOR = 0;
  static final int TEACHER = 1;

  public User getById(int id) {
    String sql = "SELECT id, name, email, password_hash, ring, prefstring FROM user WHERE id=?";
    RowMapper<User> rowMapper = new UserRowMapper();
    User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return user;
  }

  public List<User> getByName(String name) {
    String sql = "SELECT id, name, email, password_hash, ring, prefstring FROM user WHERE name=?";
    RowMapper<User> rowMapper = new UserRowMapper();
    List<User> users = jdbcTemplate.query(sql, rowMapper, name);
    return users;
  }

  public User getByEmail(String email) {
    String sql = "SELECT id, name, email, password_hash, ring, prefstring FROM user WHERE email=?";
    RowMapper<User> rowMapper = new UserRowMapper();
    User user = jdbcTemplate.queryForObject(sql, rowMapper, email);
    return user;
  }

  public List<User> getAll() {
    String sql = "SELECT  id, name, email, password_hash, ring, prefstring FROM user";
    RowMapper<User> rowMapper = new UserRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(User user) {
    // Add user
    String sql =
        "INSERT INTO user (id, name, email, password_hash, ring, prefstring ) values (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, user.id, user.name, user.email, user.passwordHash, user.ring, user.prefstring);

    // Fetch user id
    sql =
        "SELECT id FROM user WHERE name=? AND email=? AND password_hash=? AND ring=? AND prefstring=?";
    int id =
        jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            user.name,
            user.email,
            user.passwordHash,
            user.ring,
            user.prefstring);

    // Set user id
    user.id = id;
  }

  public void update(User user) {
    String sql =
        "UPDATE user SET id=?, name=?, email=?, password_hash=?, ring=?, prefstring=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        user.id,
        user.name,
        user.email,
        user.passwordHash,
        user.ring,
        user.prefstring,
        user.id);
  }

  public User delete(int id) {
    User user = getById(id);
    String sql = "DELETE FROM user WHERE id=?";
    jdbcTemplate.update(sql, id);
    return user;
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM user WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public boolean existsByEmail(String email) {
    String sql = "SELECT count(*) FROM user WHERE email=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
    return count != 0;
  }

  public List<User> query(Integer id, String name, String email, Integer ring) {
    String sql =
        "SELECT u.id, u.name, u.email, u.ring FROM user u"
            + " WHERE 1=1 "
            + (id == null ? "" : " AND l.id = " + id)
            + (name == null ? "" : " AND l.name = \'" + Utils.escapeSQLString(name) + "\'")
            + (email == null ? "" : " AND l.email = \'" + Utils.escapeSQLString(email) + "\'")
            + (ring == null ? "" : " AND l.ring = " + ring)
            + ";";

    RowMapper<User> rowMapper = new UserRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
