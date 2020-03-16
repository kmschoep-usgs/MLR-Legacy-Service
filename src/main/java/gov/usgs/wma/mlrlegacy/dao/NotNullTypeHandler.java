package gov.usgs.wma.mlrlegacy.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;

public class NotNullTypeHandler extends StringTypeHandler {

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setString(i, " ");
		} else {
			setNonNullParameter(ps, i, parameter, jdbcType);
		}
	}

}
