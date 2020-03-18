package gov.usgs.wma.mlrlegacy;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

@Component
public class MonitoringLocationDao {

	private final SqlSession sqlSession;

	public MonitoringLocationDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public MonitoringLocation getByAK(Map<String, Object> queryParams) {
		return sqlSession.selectOne("getByAK", queryParams);
	}

	public List<MonitoringLocation> getByNormalizedName(Map<String, Object> queryParams) {
		return sqlSession.selectList("getByNormalizedName", queryParams);
	}
	
	public List<MonitoringLocation> getByDistrictCodeDateRange(Map<String, Object> queryParams) {
		return sqlSession.selectList("getByDistrictCodeDateRange", queryParams);
	}

	public MonitoringLocation getById(BigInteger id) {
		return sqlSession.selectOne("getById", id);
	}

	public BigInteger create(MonitoringLocation ml) {
		sqlSession.insert("create", ml);
		return ml.getId();
	}

	public void update(MonitoringLocation ml) {
		sqlSession.update("update", ml);
	}

	public void patch(Map<String, Object> ml) {
		sqlSession.update("patch", ml);
	}

}
