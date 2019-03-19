
package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class RequestService {

	@Autowired 
	private JdbcTemplate jdbcTemplate;

	public Request getById(int id) {
		String sql =
				"SELECT id, target_id, creator_id, user_id, authorizer_id, reviewed, authorized, creation_date, authorization_date, reason FROM request WHERE id=?";
		RowMapper<Request> rowMapper = new RequestRowMapper();
		Request request = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return request;
	}

	public List<Request> getAll() {
		String sql =
				"SELECT id, target_id, creator_id, user_id,  authorizer_id, reviewed, authorized, creation_date, authorization_date, reason FROM request";
		RowMapper<Request> rowMapper = new RequestRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
	}

	public void add(Request request) {
		// Add request
		String sql =
				"INSERT INTO request (target_id, creator_id, user_id, authorizer_id, reviewed, authorized, creation_date, authorization_date, reason) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, request.targetId, request.creatorId, request.userId, request.authorizerId,
				request.reviewed, request.authorized, request.creationDate, request.authorizationDate, request.reason);

		// Fetch request id
		sql =
				"SELECT id FROM request WHERE target_id=? AND creator_id=? AND user_id=? AND authorizer_id=? AND reviewed=? AND authorized=? AND creation_date=? AND authorization_date=? AND reason=? ORDER BY id DESC";
		List<Integer> id = jdbcTemplate.queryForList(
				sql, Integer.class, request.targetId, request.creatorId, request.userId, request.authorizerId, request.reviewed, request.authorized, request.creationDate, request.authorizationDate, request.reason);

		// Set request id
		if (!id.isEmpty()) {
			request.id = id.get(0);
		}
	}


  public List<Request> query(Integer requestId, Integer authorizerId, Boolean reviewed) {
		String sql = "SELECT  id, target_id, creator_id, user_id, authorizer_id, reviewed, authorized, creation_date, authorization_date, reason FROM request WHERE 1=1 " + 
				(requestId == null ?       "" : " AND id="+requestId) +
				(reviewed == null ?        "" : " AND reviewed="+reviewed) +
				(authorizerId == null ?    "" : " AND authorizer_id="+authorizerId) +
				                                " ;" ;
		RowMapper<Request> rowMapper = new RequestRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
  }

	public void update(Request request) {
		String sql = "UPDATE request SET target_id=?, creator_id=?, user_id=?, authorizer_id=?, reviewed=?, authorized=?, creation_date=?, authorization_date=?, reason=? WHERE id=?";
		jdbcTemplate.update(
				sql, request.targetId, request.creatorId, request.userId, request.authorizerId, request.reviewed, request.authorized, request.creationDate, request.authorizationDate, request.reason, request.id);
	}

	public void delete(int id) {
		String sql = "DELETE FROM request WHERE id=?";
		jdbcTemplate.update(sql, id);
	}

	public boolean exists(int id) {
		String sql = "SELECT count(*) FROM request WHERE id=?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}
}
