package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class OfferingService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Offering getById(long id) {
    String sql = "SELECT id, semester_id, course_id FROM offering WHERE id=?";
    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    List<Offering> offerings = jdbcTemplate.query(sql, rowMapper, id);
    return offerings.size() == 0 ? null : offerings.get(0);
  }

  public Offering getOfferingBySemesterIdCourseId(long semesterId, long courseId) {
    String sql =
        "SELECT id, semester_id, course_id FROM offering WHERE semester_id=? AND course_id=?";
    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    List<Offering> offerings = jdbcTemplate.query(sql, rowMapper, semesterId, courseId);
    return offerings.size() == 0 ? null : offerings.get(0);
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM offering WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public boolean existsBySemesterIdCourseId(long semesterId, long courseId) {
    String sql = "SELECT count(*) FROM offering WHERE semester_id=? AND course_id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, semesterId, courseId);
    return count != 0;
  }

  public List<Offering> getAll() {
    String sql = "SELECT id, semester_id, course_id FROM offering";
    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public Offering add(Offering offering) {
    // check if it doesnt exist yet
    if (!existsBySemesterIdCourseId(offering.semesterId, offering.courseId)) {
      // Add offering
      String sql = "INSERT INTO offering (id, semester_id, course_id) values (?, ?, ?)";
      jdbcTemplate.update(sql, offering.id, offering.semesterId, offering.courseId);

      // Fetch offering id
      sql = "SELECT id FROM offering WHERE semester_id=? AND course_id=?";
      long id = jdbcTemplate.queryForObject(sql, Long.class, offering.semesterId, offering.courseId);
      offering.id = id;
      return offering;
    } else {
      return getOfferingBySemesterIdCourseId(offering.semesterId, offering.courseId);
    }
  }

  public void update(Offering offering) {
    String sql = "UPDATE offering SET id=?, semester_id=?, course_id=? WHERE id=?";
    jdbcTemplate.update(sql, offering.id, offering.semesterId, offering.courseId, offering.id);
  }

  public Offering deleteById(long id) {
    Offering offering = getById(id);
    String sql = "DELETE FROM offering WHERE id=?";
    jdbcTemplate.update(sql, id);
    return offering;
  }

  public List<Offering> query(
      Long offeringId,
      Long semesterId,
      Long courseId) {
    String sql =
        "SELECT o.id, o.semester_id, o.course_id FROM offering o"
            + " WHERE 1=1 "
            + (offeringId == null ? "" : " AND s.id = " + offeringId)
            + (semesterId == null ? "" : " AND s.semester_id = " + semesterId)
            + (courseId == null ? "" : " AND s.course_id = " + courseId)
            + ";";

    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
