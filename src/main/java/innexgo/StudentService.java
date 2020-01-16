/*
 * Innexgo Website
 * Copyright (C) 2020 Innexgo LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package innexgo;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class StudentService {

  @Autowired private PeriodService periodService;
  @Autowired private CourseService courseService;
  @Autowired private JdbcTemplate jdbcTemplate;

  public Student getById(long id) {
    String sql = "SELECT id, name FROM student WHERE id=?";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    Student student = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return student;
  }

  public List<Student> getAll() {
    String sql = "SELECT id, name FROM student";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Student student) {
    // Add student
    String sql = "INSERT INTO student(id, name) values (?, ?)";
    jdbcTemplate.update(sql, student.id, student.name);
  }

  public void update(Student student) {
    String sql = "UPDATE student SET id=?, name=? WHERE id=?";
    jdbcTemplate.update(
        sql, student.id, student.name, student.id);
  }

  public Student deleteById(long id) {
    Student student = getById(id);
    String sql = "DELETE FROM student WHERE id=?";
    jdbcTemplate.update(sql, id);
    return student;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM student WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  // Restrict students by
  public List<Student> query(
      Long id, // Exact match to id
      String name, // Exact match to name
      String partialName, // Partial match to name
      long offset,
      long count
      ) {
    String sql =
        "SELECT st.id, st.name FROM student st"
            + " WHERE 1=1 "
            + (id == null ? "" : " AND st.id = " + id)
            + (name == null ? "" : " AND st.name = " + Utils.escape(name))
            + (partialName == null ? "" : " AND st.name LIKE " + Utils.escape("%"+partialName+"%"))
            + (" ORDER BY st.id")
            + (" LIMIT " + offset + ", "  + count)
            + ";";

    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  // find students who are present at this location
  public List<Student> present(long locationId, long time) {
    // Find the sessions that have a start date before the  time
    // If they have an end date it must be after the time
    // find students who are in this list

    String sql =
              " SELECT DISTINCT st.id, st.name"
            + " FROM student st"
            + " INNER JOIN encounter inen ON st.id = inen.student_id"
            + " INNER JOIN session ses ON ses.in_encounter_id = inen.id"
            + " LEFT JOIN encounter outen ON ses.complete AND ses.out_encounter_id = outen.id"
            + " WHERE 1 = 1 "
            + (" AND inen.location_id = " + locationId)
            + (" AND inen.time < " + time)
            + (" AND outen.time IS NULL OR outen.time > " + time)
            + " ;";

    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  // Find students who are supposed to be here
  public List<Student> registeredForCourse(long courseId, long time) {
    Course course = courseService.getById(courseId);
    Period period = periodService.getByTime(time);

    // Do some sanity checks to ensure that the time's period is the same period as the course
    if(period.number != course.period) {
      // empty list return
      return new ArrayList<Student>();
    }

    // Find schedules which have the specified courseId
    // From these select schedules where the beginning of this period is before time
    // From these select schedules where the beginning of the next period is after time
    // Return the students of these schedules
    String sql =
        " SELECT DISTINCT st.id, st.name"
      + " FROM student st"
      + " INNER JOIN schedule sc ON st.id = sc.student_id"
      + " INNER JOIN course cs ON cs.id = sc.course_id "
      + " WHERE 1 = 1"
      + " AND cs.id = " + course.id
      + " AND (!sc.has_start OR sc.start_time <= "+time+ ") AND (!sc.has_end OR sc.end_time > "+time + ")"
      + " ;";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
