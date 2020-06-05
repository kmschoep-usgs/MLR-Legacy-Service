package gov.usgs.wma.mlrlegacy.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import gov.usgs.wma.mlrlegacy.model.LoggedAction;
import gov.usgs.wma.mlrlegacy.model.LoggedTransaction;
import gov.usgs.wma.mlrlegacy.model.LoggedTransactionSummary;

@Component
public class LoggedActionsDao {

	private final SqlSession sqlSession;

	public LoggedActionsDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public List<LoggedAction> findActions(Map<String, Object> queryParams) {
		return sqlSession.selectList("findActions", queryParams);
	}

	public List<LoggedTransaction> findTransactions(Map<String, Object> queryParams) {
		return sqlSession.selectList("findTransactions", queryParams);
	}

	public Integer countTransactions(Map<String, Object> queryParams) {
		return sqlSession.selectOne("countTransactions", queryParams);
	}

	public List<LoggedTransactionSummary> transactionSummaryByDC(Map<String, Object> queryParams) {
		return sqlSession.selectList("transactionSummaryByDC", queryParams);
	}
}
