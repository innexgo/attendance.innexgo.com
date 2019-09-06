package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CardRowMapper implements RowMapper<Card> {

  @Override
  public Card mapRow(ResultSet row, int rowNum) throws SQLException {
    Card card = new Card();
    card.id = row.getInt("id");
    card.studentId = row.getInt("student_id");
    return card;
  }
}
