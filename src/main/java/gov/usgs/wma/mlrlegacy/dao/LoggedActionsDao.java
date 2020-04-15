package gov.usgs.wma.mlrlegacy.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import gov.usgs.wma.mlrlegacy.model.LoggedAction;

@Component
public class LoggedActionsDao {

	private final SqlSession sqlSession;

	public LoggedActionsDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public List<LoggedAction> find(Map<String, Object> queryParams) {
		return sqlSession.selectList("find", queryParams);
	}

}
