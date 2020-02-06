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
public class ScheduleService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Schedule getById(long id) {
    String sql = "SELECT id, student_id, course_id, has_start, start_time, has_end, end_time FROM schedule WHERE id=?";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    List<Schedule> schedules = jdbcTemplate.query(sql, rowMapper, id);
    return schedules.size() == 0 ? null : schedules.get(0);
  }

  public Schedule getScheduleByStudentIdCourseId(long studentId, long courseId) {
    String sql =
        "SELECT id, student_id, course_id, has_start, start_time, has_end, end_time FROM schedule WHERE student_id=? AND course_id=?";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    List<Schedule> schedules = jdbcTemplate.query(sql, rowMapper, studentId, courseId);
    return schedules.size() == 0 ? null : schedules.get(0);
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM schedule WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public boolean existsByStudentIdCourseId(long studentId, long courseId) {
    String sql = "SELECT count(*) FROM schedule WHERE student_id=? AND course_id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, courseId);
    return count != 0;
  }

  public List<Schedule> getAll() {
    String sql = "SELECT id, student_id, course_id, has_start, start_time, has_end, end_time FROM schedule";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public Schedule add(Schedule schedule) {
    // check if it doesnt exist yet
    if (!existsByStudentIdCourseId(schedule.studentId, schedule.courseId)) {
      // Add schedule
      String sql = "INSERT INTO schedule (id, student_id, course_id, has_start, start_time, has_end, end_time) values (?, ?, ?, ?, ?, ?, ?)";
      jdbcTemplate.update(sql, schedule.id, schedule.studentId, schedule.courseId, schedule.hasStart, schedule.startTime, schedule.hasEnd, schedule.endTime);

      // Fetch schedule id
      sql = "SELECT id FROM schedule WHERE student_id=? AND course_id=? AND has_start=? AND start_time=? AND has_end=? AND end_time=?";
      long id = jdbcTemplate.queryForObject(sql, Long.class, schedule.studentId, schedule.courseId, schedule.hasStart, schedule.startTime, schedule.hasEnd, schedule.endTime);
      schedule.id = id;
      return schedule;
    } else {
      return getScheduleByStudentIdCourseId(schedule.studentId, schedule.courseId);
    }
  }

  public void update(Schedule schedule) {
    String sql = "UPDATE schedule SET id=?, student_id=?, course_id=?, has_start=?, start_time=?, has_end=?, end_time=? WHERE id=?";
    jdbcTemplate.update(sql, schedule.id, schedule.studentId, schedule.courseId, schedule.startTime, schedule.endTime, schedule.id);

    // Fetch schedule id
    sql = "SELECT id FROM schedule WHERE student_id=? AND course_id=? AND has_start=? AND start_time=? AND has_end=? AND end_time=?";
    long id = jdbcTemplate.queryForObject(sql, Long.class, schedule.studentId, schedule.courseId, schedule.hasStart, schedule.startTime, schedule.hasEnd, schedule.endTime);
    schedule.id = id;
  }

  public Schedule deleteById(long id) {
    Schedule schedule = getById(id);
    String sql = "DELETE FROM schedule WHERE id=?";
    jdbcTemplate.update(sql, id);
    return schedule;
  }

  public List<Schedule> query(
      Long scheduleId,
      Long studentId,
      Long courseId,
      Boolean hasStart,
      Boolean hasEnd,
      Long teacherId,
      Long time,
      Long locationId,
      Long period,
      long offset,
      long count) {
    String sql =
        "SELECT sch.id, sch.student_id, sch.course_id, sch.has_start, sch.start_time, sch.has_end, sch.end_time FROM schedule sch"
            + " JOIN course c ON sch.course_id = c.id"
            + " WHERE 1=1 "
            + (scheduleId == null ? "" : " AND sch.id = " + scheduleId)
            + (studentId == null ? "" : " AND sch.student_id = " + studentId)
            + (courseId == null ? "" : " AND sch.course_id = " + courseId)
            + (hasStart == null ? "" : " AND sch.has_start = " + hasStart)
            + (hasEnd == null ? "" : " AND sch.has_end = " + hasEnd)
            + (time == null ? "" : " AND (!sch.has_start OR sch.start_time <= "+time+") AND (!sch.has_end OR sch.end_time > "+time+")")
            + (teacherId == null ? "" : " AND c.teacher_id = " + teacherId)
            + (locationId == null ? "" : " AND c.location_id = " + locationId)
            + (period == null ? "" : " AND c.period = " + period)
            + (" ORDER BY sch.id")
            + (" LIMIT " + offset + ", "  + count)
            + ";";

    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }


  public List<Schedule> getByCourseId() {
    return null;
  }
}
