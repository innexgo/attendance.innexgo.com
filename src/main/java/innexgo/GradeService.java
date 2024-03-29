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
public class GradeService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Grade getById(long id) {
    String sql = "SELECT id, student_id, semester_start_time, numbering FROM grade WHERE id=?";
    RowMapper<Grade> rowMapper = new GradeRowMapper();
    List<Grade> grades = jdbcTemplate.query(sql, rowMapper, id);
    return grades.size() == 0 ? null : grades.get(0);
  }

  public Grade getGradeByStudentIdSemesterStartTime(long studentId, long semesterStartTime) {
    String sql = "SELECT id, student_id, semester_start_time, numbering FROM grade WHERE student_id=? AND semester_start_time=?";
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
    String sql = "SELECT id, student_id, semester_start_time, numbering FROM grade";
    RowMapper<Grade> rowMapper = new GradeRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public Grade add(Grade grade) {
    // check if it doesnt exist yet
    if (!existsByStudentIdSemesterStartTime(grade.studentId, grade.semesterStartTime)) {
      // Add grade
      String sql = "INSERT INTO grade (student_id, semester_start_time, numbering) values (?, ?, ?)";
      jdbcTemplate.update(sql, grade.studentId, grade.semesterStartTime, grade.numbering);

      // Fetch grade id
      sql = "SELECT id FROM grade WHERE student_id=? AND semester_start_time=? AND numbering=?";
      long id = jdbcTemplate.queryForObject(sql, Long.class, grade.studentId, grade.semesterStartTime, grade.numbering);
      grade.id = id;
      return grade;
    } else {
      return getGradeByStudentIdSemesterStartTime(grade.studentId, grade.semesterStartTime);
    }
  }

  public void update(Grade grade) {
    String sql = "UPDATE grade SET student_id=?, semester_start_time=?, numbering=? WHERE id=?";
    jdbcTemplate.update(sql, grade.studentId, grade.semesterStartTime, grade.numbering, grade.id);
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
      Long numbering,
      long offset,
      long count) {
    String sql = "SELECT grd.id, grd.student_id, grd.semester_start_time, grd.numbering FROM grade grd"
        + " WHERE 1=1 "
        + (gradeId == null ? "" : " AND grd.id = " + gradeId)
        + (studentId == null ? "" : " AND grd.student_id = " + studentId)
        + (semesterStartTime == null ? "" : " AND grd.semester_start_time = " + semesterStartTime)
        + (numbering == null ? "" : " AND grd.numbering = " + numbering)
        + " ORDER BY grd.id"
        + (" LIMIT " + count + " OFFSET " + offset)
        + ";";

    RowMapper<Grade> rowMapper = new GradeRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
