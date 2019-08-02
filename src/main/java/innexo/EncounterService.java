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
public class EncounterService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Encounter getById(int id) {
    String sql =
        "SELECT id, time, location_id, course_id, student_id, type FROM encounter WHERE id=?";
    RowMapper<Encounter> rowMapper = new BeanPropertyRowMapper<Encounter>(Encounter.class);
    Encounter encounter = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return encounter;
  }

  public List<Encounter> getAll() {
    String sql = "SELECT id, time, location_id, course_id, student_id, type FROM encounter";
    RowMapper<Encounter> rowMapper = new EncounterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public List<Encounter> query(
      Integer count,
      Integer encounterId,
      Integer studentId,
      Integer locationId,
      Integer teacherId,
      Integer minTime,
      Integer maxTime,
      String studentName,
      String type) {
    String sql =
        "SELECT e.id, e.time, e.location_id, e.course_id, e.student_id, e.type "
            + " FROM encounter e"
            + " JOIN student s ON e.student_id = s.id"
            + " JOIN course c ON e.course_id = c.id"
            + " WHERE 1=1 "
            + (encounterId == null ? "" : " AND e.id=" + encounterId)
            + (studentId == null ? "" : " AND e.student_id=" + studentId)
            + (locationId == null ? "" : " AND e.location_id=" + locationId)
            + (teacherId == null ? "" : " AND c.teacher_id=" + teacherId)
            + (type == null ? "" : " AND e.type= " + Utils.escape(type))
            + (minTime == null ? "" : " AND e.time >= " + minTime)
            + (maxTime == null ? "" : " AND e.time <= " + maxTime)
            + (studentName == null
                ? ""
                : " AND s.name=" + Utils.escape(studentName))
            + " ORDER BY e.time DESC"
            + (count == null ? "" : " LIMIT " + count)
            + ";";
    RowMapper<Encounter> rowMapper = new EncounterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Encounter encounter) {
    // Add encounter
    String sql =
        "INSERT INTO encounter (id, time, location_id, course_id, student_id, type) values (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql,
        encounter.id,
        encounter.time,
        encounter.locationId,
        encounter.courseId,
        encounter.studentId,
        encounter.type);

    RowMapper<Encounter> rowMapper = new EncounterRowMapper();

    // Fetch encounter id
    sql =
        "SELECT id, time, location_id, course_id, student_id, type FROM encounter WHERE time=? AND location_id=? AND course_id=? AND student_id=? AND type=? ORDER BY id DESC";
    List<Encounter> encounters =
        this.jdbcTemplate.query(
            sql,
            rowMapper,
            encounter.time,
            encounter.locationId,
            encounter.courseId,
            encounter.studentId,
            encounter.type);
    if (!encounters.isEmpty()) {
      for (int i = 0; i < encounters.size(); i++) {
        if (encounters.get(i).courseId == encounter.courseId) {
          encounter.id = encounters.get(i).id;
        } else {
          System.out.println("ERROR: encounter went missing");
        }
      }
    }
  }

  public void update(Encounter encounter) {
    String sql =
        "UPDATE encounter SET id=?, time=?, location_id=?, course_id=? student_id=?, type=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        encounter.id,
        encounter.time,
        encounter.locationId,
        encounter.courseId,
        encounter.studentId,
        encounter.type,
        encounter.id);
  }

  public Encounter delete(int id) {
    Encounter e = getById(id);
    String sql = "DELETE FROM encounter WHERE id=?";
    jdbcTemplate.update(sql, id);
    return e;
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM encounter WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }
}
