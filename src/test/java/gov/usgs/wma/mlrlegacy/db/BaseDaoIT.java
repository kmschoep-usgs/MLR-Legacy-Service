package gov.usgs.wma.mlrlegacy.db;

import com.github.database.rider.junit5.api.DBRider;

import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

import gov.usgs.wma.mlrlegacy.dao.LoggedActionsDao;
import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;

@MybatisTest
@DBRider
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Import({MonitoringLocationDao.class, LoggedActionsDao.class, DBTestConfig.class})
public abstract class BaseDaoIT extends BaseIT {

}
