package innexo;

import java.util.List;
import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class EncounterService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Encounter getById(int id) {
		String sql = "SELECT id, time, location_id, user_id, type FROM encounter WHERE id=?";
		RowMapper<Encounter> rowMapper = new BeanPropertyRowMapper<Encounter>(Encounter.class);
		Encounter encounter = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return encounter;
	}
	public List<Encounter> getAll() {
		String sql = "SELECT id, time, location_id, user_id, type FROM encounter";
		RowMapper<Encounter> rowMapper = new EncounterRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
	}	

	public List<Encounter> query(Integer count,
			Integer encounterId, 
			Integer userId, 
			Integer locationId, 
			Timestamp minTime, 
			Timestamp maxTime, 
			String userName, 
			String type) {
		String sql = "SELECT e.id, e.time, e.location_id, e.user_id, e.type FROM encounter e JOIN user u ON e.user_id = u.id WHERE 1=1 " + 
				(encounterId == null ? "" : " AND e.id="+encounterId) +
				(userId == null ?      "" : " AND e.user_id="+userId) +
				(userName == null ?    "" : " AND u.name=\'"+Utils.valString(userName)+"\'") +
				(locationId == null ?  "" : " AND e.location_id="+locationId) + 
				(type == null ?        "" : " AND e.type=\'"+Utils.valString(type)+"\'") + 
				(minTime == null ?     "" : " AND e.time >= FROM_UNIXTIME(" + minTime.toInstant().getEpochSecond() + ")") + 
				(maxTime == null ?     "" : " AND e.time <= FROM_UNIXTIME(" + maxTime.toInstant().getEpochSecond() + ")") +
			                                " ORDER BY time DESC" +
				(count == null ?       "" : " LIMIT "+count) +
				";" ;
		RowMapper<Encounter> rowMapper = new EncounterRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
	}

	public void add(Encounter encounter) {
		//Add encounter
		String sql = "INSERT INTO encounter (id, time, location_id, user_id, type) values (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, encounter.id, encounter.time, encounter.locationId, encounter.userId, encounter.type);

		//Fetch encounter id
		sql = "SELECT id FROM encounter WHERE time=? AND location_id=? AND user_id=? AND type=? ORDER BY id DESC";
		List<Integer> id = jdbcTemplate.queryForList(sql, Integer.class, encounter.time, encounter.locationId, encounter.userId, encounter.type);

		if(!id.isEmpty()) {
			encounter.id = id.get(0);
		}
	}

	public void update(Encounter encounter) {
		String sql = "UPDATE encounter SET id=?, time=?, location_id=?, user_id=?, type=? WHERE id=?";
		jdbcTemplate.update(sql, encounter.id, encounter.time, encounter.locationId, encounter.userId, encounter.type, encounter.id);
	}

	public void delete(int id) {
		String sql = "DELETE FROM encounter WHERE id=?";
		jdbcTemplate.update(sql, id);
	}

	public boolean exists(int id) {
		String sql = "SELECT count(*) FROM encounter WHERE id=?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		if(count == 0) {
			return false;
		} else {
			return true;
		}
	}
}
