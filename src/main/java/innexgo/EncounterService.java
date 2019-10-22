package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class EncounterService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Encounter getById(long id) {
    String sql = "SELECT id, time, virtual, location_id, student_id FROM encounter WHERE id=?";
    RowMapper<Encounter> rowMapper = new EncounterRowMapper();
    Encounter encounter = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return encounter;
  }

  public List<Encounter> getAll() {
    String sql = "SELECT id, time, virtual, location_id, student_id, FROM encounter";
    RowMapper<Encounter> rowMapper = new EncounterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Encounter encounter) {
    // Add encounter
    String sql = "INSERT INTO encounter(id, time, location_id, student_id) values (?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, encounter.id, encounter.time, encounter.locationId, encounter.studentId);

    RowMapper<Encounter> rowMapper = new EncounterRowMapper();

    // Fetch encounter id
    sql = "SELECT id FROM encounter WHERE time=? AND location_id=? AND student_id=?";
    long id =
        jdbcTemplate.queryForObject(
            sql, Long.class, encounter.time, encounter.locationId, encounter.studentId);
    encounter.id = id;
  }

  public void update(Encounter encounter) {
    String sql = "UPDATE encounter SET id=?, time=?, location_id=?, student_id=? WHERE id=?";
    jdbcTemplate.update(
        sql, encounter.id, encounter.time, encounter.locationId, encounter.studentId, encounter.id);
  }

  public Encounter delete(long id) {
    Encounter e = getById(id);
    String sql = "DELETE FROM encounter WHERE id=?";
    jdbcTemplate.update(sql, id);
    return e;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM encounter WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Encounter> query(
      Long count,
      Long encounterId,
      Long studentId,
      Long locationId,
      Long minTime,
      Long maxTime,
      Boolean virtual,
      String studentName) {
    String sql =
        "SELECT e.id, e.time, e.virtual, e.location_id, e.student_id "
            + " FROM encounter e"
            + " JOIN student s ON e.student_id = s.id"
            + " WHERE 1=1 "
            + (encounterId == null ? "" : " AND e.id=" + encounterId)
            + (studentId == null ? "" : " AND e.student_id=" + studentId)
            + (locationId == null ? "" : " AND e.location_id=" + locationId)
            + (virtual == null ? "" : " AND e.virtual=" + virtual)
            + (minTime == null ? "" : " AND e.time >= " + minTime)
            + (maxTime == null ? "" : " AND e.time <= " + maxTime)
            + (studentName == null ? "" : " AND s.name=" + Utils.escape(studentName))
            + " ORDER BY e.time DESC"
            + (count == null ? "" : " LIMIT " + count)
            + ";";
    RowMapper<Encounter> rowMapper = new EncounterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
