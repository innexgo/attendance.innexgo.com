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
public class OfferingService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Offering getOfferingBySemesterStartTimeCourseId(long semesterStartTime, long courseId) {
    String sql = "SELECT semester_start_time, course_id FROM offering WHERE semester_start_time=? AND course_id=?";
    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    List<Offering> offerings = jdbcTemplate.query(sql, rowMapper, semesterStartTime, courseId);
    return offerings.size() == 0 ? null : offerings.get(0);
  }

  public boolean existsBySemesterStartTimeCourseId(long semesterStartTime, long courseId) {
    String sql = "SELECT count(*) FROM offering WHERE semester_start_time=? AND course_id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, semesterStartTime, courseId);
    return count != 0;
  }

  public List<Offering> getAll() {
    String sql = "SELECT semester_start_time, course_id FROM offering";
    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public Offering add(Offering offering) {
    // check if it doesnt exist yet
    if (!existsBySemesterStartTimeCourseId(offering.semesterStartTime, offering.courseId)) {
      // Add offering
      String sql = "INSERT INTO offering (semester_start_time, course_id) values (?, ?)";
      jdbcTemplate.update(sql, offering.semesterStartTime, offering.courseId);
      return offering;
    } else {
      return getOfferingBySemesterStartTimeCourseId(offering.semesterStartTime, offering.courseId);
    }
  }

  public Offering deleteBySemesterStartTimeCourseId(long semesterStartTime, long courseId) {
    Offering offering = getOfferingBySemesterStartTimeCourseId(semesterStartTime, courseId);
    String sql = "DELETE FROM offering WHERE semester_start_time=? AND course_id=?";
    jdbcTemplate.update(sql, semesterStartTime, courseId);
    return offering;
  }

  public List<Offering> query(
      Long semesterStartTime,
      Long courseId,
      long offset,
      long count) {
    String sql = "SELECT o.semester_start_time, o.course_id FROM offering o"
        + " WHERE 1=1 "
        + (semesterStartTime == null ? "" : " AND o.semester_start_time = " + semesterStartTime)
        + (courseId == null ? "" : " AND o.course_id = " + courseId)
        + (" ORDER BY o.semester_start_time, o.course_id")
        + (" LIMIT " + count + " OFFSET " + offset)
        + ";";

    RowMapper<Offering> rowMapper = new OfferingRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
