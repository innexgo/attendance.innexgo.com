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
public class SessionService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Session getById(long id) {
    String sql = "SELECT id, in_encounter_id, out_encounter_id FROM session WHERE id=?";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    Session session = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return session;
  }

  public List<Session> getAll() {
    String sql = "SELECT id, in_encounter_id, out_encounter_id FROM session";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Session session) {
    // Add session
    String sql = "INSERT INTO session (in_encounter_id, out_encounter_id) values (?, ?)";
    jdbcTemplate.update(
        sql,
        session.inEncounterId,
        session.outEncounterId);

    // Fetch session id
    sql = "SELECT id FROM session WHERE in_encounter_id=?";
    long id = jdbcTemplate.queryForObject(
        sql,
        Long.class,
        session.inEncounterId);

    // Set session id
    session.id = id;
  }

  public void update(Session session) {
    String sql = "UPDATE session SET in_encounter_id=?, out_encounter_id=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        session.inEncounterId,
        session.outEncounterId,
        session.id);
  }

  public Session delete(long id) {
    Session session = getById(id);
    String sql = "DELETE FROM session WHERE id=?";
    jdbcTemplate.update(sql, id);
    return session;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM session WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  // Order by oldest to newest
  public List<Session> query(
      Long id,
      Long inEncounterId,
      Long outEncounterId,
      Long anyEncounterId,
      Boolean complete,
      Long studentId,
      Long locationId,
      Long time,
      Long inTimeBegin,
      Long inTimeEnd,
      Long outTimeBegin,
      Long outTimeEnd,
      long offset,
      long count) {

    String sql = "SELECT DISTINCT ses.id, ses.in_encounter_id, ses.out_encounter_id"
        + " FROM session ses"
        + " INNER JOIN encounter inen ON ses.in_encounter_id = inen.id"
        + " LEFT JOIN encounter outen ON ses.out_encounter_id = outen.id"
        + " WHERE 1 = 1 "
        + (id == null ? "" : " AND ses.id = " + id)
        + (inEncounterId == null ? "" : " AND ses.in_encounter_id = " + inEncounterId)
        + (outEncounterId == null ? "" : " AND ses.out_encounter_id = " + outEncounterId)
        + (anyEncounterId == null ? ""
            : " AND (ses.in_encounter_id =" + anyEncounterId + " OR ses.out_encounter_id = " + anyEncounterId + ")")
        + (complete == null ? "" : " AND ses.out_encounter_id" + (complete ? " IS NOT NULL" : " IS NULL"))
        + (studentId == null ? "" : " AND inen.student_id = " + studentId)
        + (locationId == null ? "" : " AND inen.location_id = " + locationId)
        + (time == null ? "" : " AND " + time + " BETWEEN inen.time AND outen.time")
        + (inTimeBegin == null ? "" : " AND inen.time >= " + inTimeBegin)
        + (inTimeEnd == null ? "" : " AND inen.time <= " + inTimeEnd)
        + (outTimeBegin == null ? "" : " AND outen.time >= " + outTimeBegin)
        + (outTimeEnd == null ? "" : " AND outen.time <= " + outTimeEnd)
        + (" ORDER BY ses.id DESC")
        + (" LIMIT " + count + " OFFSET " + offset)
        + ";";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  // TODO get sessions by course
}
