
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
public class TargetService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Target getById(int id) {
    String sql = "SELECT id, user_id, location_id, min_time, max_time, name FROM target WHERE id=?";
    RowMapper<Target> rowMapper = new TargetRowMapper();
    Target target = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return target;
  }

  public List<Target> getAll() {
    String sql = "SELECT id, user_id, location_id, name, min_time, max_time FROM target";
    RowMapper<Target> rowMapper = new TargetRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }	

  public void add(Target target) {
    //Add target
    String sql = "INSERT INTO target (user_id, location_id, name, min_time, max_time) values (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, target.userId, target.locationId, target.name, target.minTime, target.maxTime);

    //Fetch target id
    sql = "SELECT id FROM target WHERE user_id=? AND location_id=? AND name=? AND min_time=? AND max_time=? ORDER BY id DESC";
    List<Integer> id = jdbcTemplate.queryForList(sql, Integer.class, target.userId, target.locationId, target.name, target.minTime, target.maxTime);

    //Set target id 
    if(!id.isEmpty()) {
      target.id = id.get(0);
    }
  }
  
  public void update(Target target) {
    String sql = "UPDATE target SET user_id=?, location_id=?, name=? min_time=?, max_time=? WHERE id=?";
    jdbcTemplate.update(sql, target.userId, target.locationId, target.name, target.minTime, target.maxTime, target.id);
  }

  public void delete(int id) {
    String sql = "DELETE FROM target WHERE id=?";
    jdbcTemplate.update(sql, id);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM target WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    if(count == 0) {
      return false;
    } else {
      return true;
    }
  }
}
