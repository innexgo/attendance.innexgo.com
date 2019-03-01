
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
public class LocationService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Location getById(int id) {
    String sql = "SELECT id, name, tags FROM location WHERE id=?";
    RowMapper<Location> rowMapper = new LocationRowMapper();
    Location location = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return location;
  }

  public List<Location> getAll() {
    String sql = "SELECT id, name, tags FROM location";
    RowMapper<Location> rowMapper = new LocationRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }	

  public void add(Location location) {
    //Add location
    String sql = "INSERT INTO location (id, name, tags) values (?, ?, ?)";
    jdbcTemplate.update(sql, location.id, location.name, location.tags);

    //Fetch location id
    sql = "SELECT id FROM location WHERE name=? AND tags=?";
    int id = jdbcTemplate.queryForObject(sql, Integer.class, location.name, location.tags);

    //Set location id 
    location.id = id;
  }

  public void update(Location location) {
    String sql = "UPDATE location SET id=?, name=?, tags=? WHERE id=?";
    jdbcTemplate.update(sql, location.id, location.name, location.tags, location.id);
  }

  public void delete(int id) {
    String sql = "DELETE FROM location WHERE id=?";
    jdbcTemplate.update(sql, id);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM location WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    if(count == 0) {
      return false;
    } else {
      return true;
    }
  }
}
