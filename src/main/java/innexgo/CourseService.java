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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class CourseService {

  @Autowired
  private PeriodService periodService;
  @Autowired
  private SemesterService semesterService;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Course getById(long id) {
    String sql = "SELECT id, teacher_id, location_id, period, subject FROM course WHERE id=?";
    RowMapper<Course> rowMapper = new CourseRowMapper();
    Course course = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return course;
  }

  public List<Course> getAll() {
    String sql = "SELECT id, teacher_id, location_id, period, subject FROM course";
    RowMapper<Course> rowMapper = new CourseRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Course course) {
    // Add course
    String sql = "INSERT INTO course (teacher_id, location_id, period, subject) values (?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, course.teacherId, course.locationId, course.period, course.subject);

    // Fetch course id
    sql = "SELECT id FROM course WHERE teacher_id=? AND location_id=? AND period=? AND subject=?";
    long id = jdbcTemplate.queryForObject(
        sql, Long.class, course.teacherId, course.locationId, course.period, course.subject);

    // Set course id
    course.id = id;
  }

  public void update(Course course) {
    String sql = "UPDATE course SET teacher_id=?, location_id=? period=?, subject=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        course.teacherId,
        course.locationId,
        course.period,
        course.subject,
        course.id);
  }

  public Course deleteById(long id) {
    Course course = getById(id);
    String sql = "DELETE FROM course WHERE id=?";
    jdbcTemplate.update(sql, id);
    return course;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM course WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Course> query(
      Long id,
      Long teacherId,
      Long locationId,
      Long studentId,
      Long periodNumber,
      String subject,
      Long semesterStartTime,
      long offset,
      long count) {
    String sql = "SELECT DISTINCT cs.id, cs.teacher_id, cs.location_id, cs.period, cs.subject"
        + " FROM course cs"
        + (semesterStartTime == null ? "" : " INNER JOIN offering of ON cs.id = of.course_id")
        + (studentId == null ? "" : " INNER JOIN schedule sc ON cs.id = sc.course_id")
        + " WHERE 1=1 "
        + (id == null ? "" : " AND cs.id = " + id)
        + (periodNumber == null ? "" : " AND cs.period = " + periodNumber)
        + (teacherId == null ? "" : " AND cs.teacher_id = " + teacherId)
        + (locationId == null ? "" : " AND cs.location_id = " + locationId)
        + (semesterStartTime == null ? "" : " AND of.semester_start_time = " + semesterStartTime)
        + (studentId == null ? "" : " AND sc.student_id = " + studentId)
        + (subject == null ? "" : " AND cs.subject = " + Utils.escape(subject))
        + " ORDER BY cs.id"
        + (" LIMIT " + count + " OFFSET " + offset)
        + ";";

    RowMapper<Course> rowMapper = new CourseRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  // Returns all courses that would be running at this period start time
  public List<Course> getByPeriodStartTime(long periodStartTime) {
    Period period = periodService.getByStartTime(periodStartTime);
    Semester semester = semesterService.getByTime(periodStartTime);

    String sql = "SELECT DISTINCT cs.id, cs.teacher_id, cs.location_id, cs.period, cs.subject FROM course cs"
        + " INNER JOIN offering ofr ON ofr.course_id = cs.id"
        + " INNER JOIN period pr ON pr.numbering = cs.period"
        + " WHERE 1 = 1"
        + " AND ofr.semester_start_time = " + semester.startTime
        + " AND pr.start_time = " + period.startTime
        + ";";
    RowMapper<Course> rowMapper = new CourseRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
