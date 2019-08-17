package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class StudentService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Student getById(int id) {
    String sql = "SELECT id, graduating_year, name, tags FROM student WHERE id=?";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    Student student = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return student;
  }

  public List<Student> getAll() {
    String sql = "SELECT id, graduating_year, name, tags FROM student";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Student student) {
    // Add student
    String sql = "INSERT INTO student (id, graduating_year, name, tags) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, student.id, student.graduatingYear, student.name, student.tags);

    // Fetch student id
    sql = "SELECT id FROM student WHERE graduating_year=? AND name=? AND tags=?";
    int id =
        jdbcTemplate.queryForObject(
            sql, Integer.class, student.graduatingYear, student.name, student.tags);

    // Set student id
    student.id = id;
  }

  public void update(Student student) {
    String sql = "UPDATE student SET id=?, graduating_year=?, name=?, tags=? WHERE id=?";
    jdbcTemplate.update(
        sql, student.id, student.graduatingYear, student.name, student.tags, student.id);
  }

  public Student delete(int id) {
    Student student = getById(id);
    String sql = "DELETE FROM student WHERE id=?";
    jdbcTemplate.update(sql, id);
    return student;
  }

  public boolean existsById(int id) {
    String sql = "SELECT count(*) FROM student WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Student> query(
      Integer id, Integer graduatingYear, String name, String tags, Integer courseId) {
    String sql =
        "SELECT st.id, st.graduating_year, st.name, st.tags FROM student st"
            + (courseId == null ? "" : " JOIN schedule sc ON st.id = sc.student_id ")
            + " WHERE 1=1 "
            + (id == null ? "" : " AND st.id = " + id)
            + (graduatingYear == null ? "" : " AND st.graduating_year = " + graduatingYear)
            + (name == null ? "" : " AND st.name = " + Utils.escape(name))
            + (tags == null ? "" : " AND st.tags = " + Utils.escape(tags))
            + (courseId == null ? "" : " AND sc.course_id = " + courseId)
            + ";";

    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
