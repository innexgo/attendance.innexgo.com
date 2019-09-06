
package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class CardService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Card getById(int id) {
    String sql = "SELECT id, student_id FROM card WHERE id=?";
    RowMapper<Card> rowMapper = new CardRowMapper();
    Card card = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return card;
  }

  public List<Card> getAll() {
    String sql = "SELECT id, student_id FROM card";
    RowMapper<Card> rowMapper = new CardRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Card card) {
    // Add card
    String sql = "INSERT INTO card (id,student_id) values (?, ?)";
    jdbcTemplate.update(sql, card.id, card.studentId);
  }

  public void update(Card card) {
    String sql = "UPDATE card SET id=?, student_id=? WHERE id=?";
    jdbcTemplate.update(sql, card.id, card.studentId, card.id);
  }

  public Card deleteById(int id) {
    Card card = getById(id);
    String sql = "DELETE FROM card WHERE id=?";
    jdbcTemplate.update(sql, id);
    return card;
  }

  public boolean existsById(int id) {
    String sql = "SELECT count(*) FROM card WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Card> query(Integer id, Integer studentId) {
    String sql =
        "SELECT c.id, c.student_id FROM card c"
            + " WHERE 1=1 "
            + (id == null ? "" : " AND c.id = " + id)
            + (studentId == null ? "" : " AND c.student_id = " + studentId)
            + ";";

    RowMapper<Card> rowMapper = new CardRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
