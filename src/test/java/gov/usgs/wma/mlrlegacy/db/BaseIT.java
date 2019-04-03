package gov.usgs.wma.mlrlegacy.db;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetModifier;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional(propagation=Propagation.NOT_SUPPORTED)
public abstract class BaseIT {

	public static final String KNOWN_USER = "knownusr";
	public static final BigInteger ONE_MILLION = BigInteger.valueOf(1000000);
	public static final String DEFAULT_AGENCY_CODE = "USGS ";
	public static final String DEFAULT_SITE_NUMBER = "123456789012345";
	public static final String DEFAULT_CREATED_DATE_M = "2017-08-24 09:15";
	public static final String DEFAULT_CREATED_DATE_S = DEFAULT_CREATED_DATE_M + ":23";
	public static final String DEFAULT_CREATED_BY = "site_cn ";
	public static final String DEFAULT_UPDATED_DATE_S = "2017-08-25 15:45:59";
	public static final String DEFAULT_UPDATED_BY = "site_mn ";

	public static final String UPDATED_AGENCY_CODE = "USGSX";
	public static final String UPDATED_SITE_NUMBER = "123456789012346";
	public static final String UPDATED_CREATED_BY = "site_cnx";
	public static final String UPDATED_MODIFIED_BY = "site_mnx";

	public static final String QUERY_ALL_SELECT = "select legacy_location_id,agency_cd, site_no, station_nm, station_ix,"
			+ "lat_va, long_va, dec_lat_va, dec_long_va, coord_meth_cd, coord_acy_cd,"
			+ "coord_datum_cd, district_cd, land_net_ds, map_nm, country_cd,"
			+ "state_cd, county_cd, map_scale_fc, alt_va, alt_meth_cd, alt_acy_va,"
			+ "alt_datum_cd, huc_cd, agency_use_cd, basin_cd, site_tp_cd, topo_cd,"
			+ "data_types_cd, instruments_cd, site_rmks_tx, inventory_dt, drain_area_va,"
			+ "contrib_drain_area_va, tz_cd, local_time_fg, gw_file_cd, construction_dt,"
			+ "reliability_cd, aqfr_cd, nat_aqfr_cd, site_use_1_cd, site_use_2_cd,"
			+ "site_use_3_cd, water_use_1_cd, water_use_2_cd, water_use_3_cd,"
			+ "nat_water_use_cd, aqfr_type_cd, well_depth_va, hole_depth_va,"
			+ "depth_src_cd, project_no, site_web_cd, site_cn, site_mn, mcd_cd";

	public static final String QUERY_ALL_FROM = " from mlr_legacy_data.legacy_location";

	public static final String QUERY_ALL_TO_SECOND = QUERY_ALL_SELECT + ", to_char(site_cr,'YYYY-MM-DD HH24:MI:SS') site_crm, to_char(site_md,'YYYY-MM-DD HH24:MI:SS') site_mdm" + QUERY_ALL_FROM;
	public static final String QUERY_ALL_TO_MINUTE = QUERY_ALL_SELECT + ", to_char(site_cr,'YYYY-MM-DD HH24:MI') site_crm, to_char(site_md,'YYYY-MM-DD HH24:MI') site_mdm" + QUERY_ALL_FROM;

	protected String id;
	protected String agency;
	protected String siteNbr;
	protected class KeyModifier extends ReplacementDataSetModifier {
		@Override
		protected void addReplacements(ReplacementDataSet dataset) {
			dataset.addReplacementSubstring("[id]", id == null ? "1" : id);
			dataset.addReplacementSubstring("[agency]", agency == null ? DEFAULT_AGENCY_CODE : agency);
			dataset.addReplacementSubstring("[siteNbr]", siteNbr == null ? DEFAULT_SITE_NUMBER : siteNbr);
		}
	}

	protected String createdDate;
	protected String createdBy;
	protected class CreatedModifier extends ReplacementDataSetModifier {
		@Override
		protected void addReplacements(ReplacementDataSet dataset) {
			dataset.addReplacementSubstring("[createdDate]", createdDate== null ? DEFAULT_CREATED_DATE_S : createdDate);
			dataset.addReplacementSubstring("[createdBy]", createdBy== null ? DEFAULT_CREATED_BY : createdBy);
		}
	}

	protected String updatedDate;
	protected String updatedBy;
	protected class UpdatedModifier extends ReplacementDataSetModifier {
		@Override
		protected void addReplacements(ReplacementDataSet dataset) {
			dataset.addReplacementSubstring("[updatedDate]", updatedDate== null ? DEFAULT_UPDATED_DATE_S : updatedDate);
			dataset.addReplacementSubstring("[updatedBy]", updatedBy== null ? DEFAULT_UPDATED_BY : updatedBy);
		}
	}

	protected String getCompareFile(String folder, String file) throws IOException {
		return new String(FileCopyUtils.copyToByteArray(new ClassPathResource(folder + file).getInputStream()));
	}

	protected String nowMinutes;
	@Before
	public void baseInit() {
		nowMinutes = LocalDateTime.now(ZoneId.of("UTC")).toString().replace("T", " ").substring(0, 16);
	}

	public String getInputJson(String file) throws IOException {
		createdDate = DEFAULT_CREATED_DATE_S;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = DEFAULT_UPDATED_DATE_S;
		updatedBy = DEFAULT_UPDATED_BY;
		return replaceKey(getCompareFile("testData/", file))
				.replace("[createdDate]", createdDate).replace("[createdBy]", createdBy)
				.replace("[updatedDate]", updatedDate).replace("[updatedBy]", updatedBy);
	}

	public String getExpectedReadJson(String file) throws IOException {
		createdDate = DEFAULT_CREATED_DATE_S;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = DEFAULT_UPDATED_DATE_S;
		updatedBy = DEFAULT_UPDATED_BY;
		return getExpectedJson(file);
	}

	public String getExpectedCreateJson(String response, String file, String username) throws IOException {
		createdDate = JsonPath.read(response, "$.created").toString();
		createdBy = username;
		updatedDate = JsonPath.read(response, "$.updated").toString();
		updatedBy = username;
		return getExpectedJson(file);
	}

	public String getExpectedUpdateJson(String response, String file, String username) throws IOException {
		createdDate = DEFAULT_CREATED_DATE_S;
		createdBy = DEFAULT_CREATED_BY;
		updatedDate = JsonPath.read(response, "$.updated").toString();
		updatedBy = username;
		return getExpectedJson(file);
	}

	public String getExpectedJson(String file) throws IOException {
		return replaceKey(getCompareFile("testResult/", file))
				.replace("[createdDate]", createdDate).replace("[createdBy]", createdBy)
				.replace("[updatedDate]", updatedDate).replace("[updatedBy]", updatedBy);
	}

	public String replaceKey(String json) {
		return json.replace("\"[id]\"", id == null ? "1" : id)
				.replace("[agency]", agency == null ? DEFAULT_AGENCY_CODE : agency)
				.replace("[siteNbr]", siteNbr == null ? DEFAULT_SITE_NUMBER : siteNbr);
	}

}
