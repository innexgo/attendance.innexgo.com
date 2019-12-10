package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class GradeService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Grade getById(long id) {
    String sql = "SELECT id, student_id, semester_start_time, number FROM grade WHERE id=?";
    RowMapper<Grade> rowMapper = new GradeRowMapper();
    List<Grade> grades = jdbcTemplate.query(sql, rowMapper, id);
    return grades.size() == 0 ? null : grades.get(0);
  }

  public Grade getGradeByStudentIdSemesterStartTime(long studentId, long semesterStartTime) {
    String sql =
        "SELECT id, student_id, semester_start_time, number FROM grade WHERE student_id=? AND semester_start_time=?";
    RowMapper<Grade> rowMapper = new GradeRowMapper();
    List<Grade> grades = jdbcTemplate.query(sql, rowMapper, studentId, semesterStartTime);
    return grades.size() == 0 ? null : grades.get(0);
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM grade WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public boolean existsByStudentIdSemesterStartTime(long studentId, long semesterStartTime) {
    String sql = "SELECT count(*) FROM grade WHERE student_id=? AND semester_start_time=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, semesterStartTime);
    return count != 0;
  }

  public List<Grade> getAll() {
    String sql = "SELECT id, student_id, semester_start_time, number FROM grade";
    RowMapper<Grade> rowMapper = new GradeRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public Grade add(Grade grade) {
    // check if it doesnt exist yet
    if (!existsByStudentIdSemesterStartTime(grade.studentId, grade.semesterStartTime)) {
      // Add grade
      String sql = "INSERT INTO grade (id, student_id, semester_start_time, number) values (?, ?, ?, ?)";
      jdbcTemplate.update(sql, grade.id, grade.studentId, grade.semesterStartTime, grade.number);

      // Fetch grade id
      sql = "SELECT id FROM grade WHERE student_id=? AND semester_start_time=? AND number=?";
      long id = jdbcTemplate.queryForObject(sql, Long.class, grade.studentId, grade.semesterStartTime, grade.number);
      grade.id = id;
      return grade;
    } else {
      return getGradeByStudentIdSemesterStartTime(grade.studentId, grade.semesterStartTime);
    }
  }

  public void update(Grade grade) {
    String sql = "UPDATE grade SET id=?, student_id=?, semester_start_time=?, number=? WHERE id=?";
    jdbcTemplate.update(sql, grade.id, grade.studentId, grade.semesterStartTime, grade.number, grade.id);

    // Fetch grade id
    sql = "SELECT id FROM grade WHERE student_id=? AND semester_start_time=? AND number=?";
    long id = jdbcTemplate.queryForObject(sql, Long.class, grade.studentId, grade.semesterStartTime, grade.number);
    grade.id = id;
  }

  public Grade deleteById(long id) {
    Grade grade = getById(id);
    String sql = "DELETE FROM grade WHERE id=?";
    jdbcTemplate.update(sql, id);
    return grade;
  }

  public List<Grade> query(
      Long gradeId,
      Long studentId,
      Long semesterStartTime,
      Long number) {
    String sql =
      "SELECT grd.id, grd.student_id, grd.semester_start_time, grd.number FROM grade grd"
      + " WHERE 1=1 "
      + (gradeId == null ? "" : " AND grd.id = " + gradeId)
      + (studentId == null ? "" : " AND grd.student_id = " + studentId)
      + (semesterStartTime == null ? "" : " AND grd.semester_start_time = " + semesterStartTime)
      + (number == null ? "" : " AND grd.number = " + number)
      + ";";

    RowMapper<Grade> rowMapper = new GradeRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
