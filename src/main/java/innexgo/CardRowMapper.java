package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CardRowMapper implements RowMapper<Card> {

  @Override
  public Card mapRow(ResultSet row, int rowNum) throws SQLException {
    Card card = new Card();
    card.id = row.getLong("id");
    card.studentId = row.getLong("student_id");
    return card;
  }
}
