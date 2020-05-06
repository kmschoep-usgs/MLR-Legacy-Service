package gov.usgs.wma.mlrlegacy;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.NumberUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.wma.mlrlegacy.dao.LoggedActionsDao;
import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.model.LoggedAction;
import gov.usgs.wma.mlrlegacy.model.LoggedTransaction;
import gov.usgs.wma.mlrlegacy.model.LoggedTransactionQueryParams;
import gov.usgs.wma.mlrlegacy.model.LoggedTransactionSummary;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;
import gov.usgs.wma.mlrlegacy.util.UserAuthUtil;
import gov.usgs.wma.mlrlegacy.validation.UniqueMonitoringLocation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;

@Tag(name="Legacy Monitoring Locations", description="Display")
@RestController
@RequestMapping("/monitoringLocations")
public class Controller {
	private static final transient Logger LOG = LoggerFactory.getLogger(Controller.class);

	private UserAuthUtil userAuthUtil;
	private MonitoringLocationDao mLDao;
	private LoggedActionsDao lADao;
	private Validator validator;

	public static final String AGENCY_CODE = "agencyCode";
	public static final String SITE_NUMBER = "siteNumber";
	public static final String DISTRICT_CODE = "districtCode";
	public static final String DISTRICT_CODES = "districtCodes";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String DUPLICATE_SITE = "duplicate_site";
	public static final String STATE_FIPS_CODE = "stateFipsCode";
	public static final String UPDATED_BY = "updatedBy";
	public static final String NORMALIZED_STATION_NAME = "normalizedStationName";
	public static final String VALIDATION_ERRORS_KEY = "validation_errors";
	public static final String STATION_IX = "stationIx";


	@Autowired
	public Controller(
		UserAuthUtil userAuthUtil,
		MonitoringLocationDao mLDao,
		LoggedActionsDao lADao,
		Validator validator
	) {
		this.userAuthUtil = userAuthUtil;
		this.mLDao = mLDao;
		this.lADao = lADao;
		this.validator = validator;
	}

	@GetMapping(params = {AGENCY_CODE, SITE_NUMBER})
	@Operation(summary = "getMonitoringLocations", security = @SecurityRequirement(name = "bearerAuth"))
	public MonitoringLocation getMonitoringLocations(
		@RequestParam(name = AGENCY_CODE) String agencyCode,
		@RequestParam(name = SITE_NUMBER) String siteNumber,
		HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>();
		params.put(AGENCY_CODE, agencyCode.toUpperCase());
		params.put(SITE_NUMBER, siteNumber);
		MonitoringLocation ml = mLDao.getByAK(params);
		if (null == ml) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return ml;
	}
	
	@GetMapping(params = {START_DATE, END_DATE})
	@Operation(summary = "getMonitoringLocationsByDistrictCodeDateRange", security = @SecurityRequirement(name = "bearerAuth"))
	public List<MonitoringLocation> getMonitoringLocationsByDistrictCodeDateRange(
		@RequestParam(name = DISTRICT_CODE, required = false) List<String> districtCode,
		@RequestParam(name = START_DATE) String startDate,
		@RequestParam(name = END_DATE) String endDate,
		HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>();
		params.put(DISTRICT_CODES, districtCode);
		params.put(START_DATE, startDate);
		params.put(END_DATE, endDate);
		List<MonitoringLocation> mls = mLDao.getByDistrictCodeDateRange(params);
		if (null == mls || mls.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return mls;
	}

	/**
	 * 
	 * @param normalizedStationName also known as "station_ix"
	 * @param response
	 * @return an array of the matching monitoring locations
	 */
	@GetMapping(params = NORMALIZED_STATION_NAME)
	@Operation(summary = "getMonitoringLocationsByNormalizedName", security = @SecurityRequirement(name = "bearerAuth"))
	public List<MonitoringLocation> getMonitoringLocationsByNormalizedName(
		@RequestParam(name = NORMALIZED_STATION_NAME) String normalizedStationName,
		HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>();
		params.put(NORMALIZED_STATION_NAME, normalizedStationName);
		
		List<MonitoringLocation> ml = mLDao.getByNormalizedName(params);
		if (null == ml || ml.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return ml;
	}
	
	@PostMapping("/validate")
	@Operation(summary = "validateUniqueMonitoringLocation", security = @SecurityRequirement(name = "bearerAuth"))
	public Map<String, Object> validateUniqueMonitoringLocation(@RequestBody MonitoringLocation ml, HttpServletResponse response) throws IOException {
		Map<String, String> msgMap = new HashMap<>();
		Map<String, Object> errorMap = new HashMap<>();
		
		Set<ConstraintViolation<MonitoringLocation>> violations = validator.validate(ml, UniqueMonitoringLocation.class);
		if(violations.isEmpty()) {
			response.setStatus(200);
		} else {
			response.setStatus(200);
			Map<String, String> msgs = violations.stream()
					.map(
						v -> {
							msgMap.put(v.getPropertyPath().toString(), v.getMessage());
							return msgMap;
						})
					.collect(Collectors.toList()).get(0);
			errorMap.put(VALIDATION_ERRORS_KEY, msgs);
			LOG.debug("Returned the following validation messages:" + String.join(",", msgs.toString()));
		}
		return errorMap;
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "getMonitoringLocationById", security = @SecurityRequirement(name = "bearerAuth"))
	public MonitoringLocation getMonitoringLocation(@PathVariable("id") String id, HttpServletResponse response) {
		MonitoringLocation ml = mLDao.getById(NumberUtils.parseNumber(id, BigInteger.class));
		if (null == ml) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return ml;
	}

	@PreAuthorize("hasPermission(#ml, null)")
	@PostMapping()
	@Operation(summary = "createMonitoringLocation", security = @SecurityRequirement(name = "bearerAuth"))
	public MonitoringLocation createMonitoringLocation(@RequestBody MonitoringLocation ml, HttpServletResponse response) throws IOException {
		String username = getUsername();
		ml.setCreatedBy(username);
		ml.setUpdatedBy(username);
		
		if (validator.validate(ml).isEmpty()) {
			BigInteger newId = mLDao.create(ml);
			response.setStatus(HttpStatus.CREATED.value());
			return mLDao.getById(newId);
		} else {
			response.sendError(406, "Invalid data submitted to CRU.");
			return null;
		}
	}

	@PreAuthorize("hasPermission(#ml, null)")
	@PutMapping("/{id}")
	@Operation(summary = "updateMonitoringLocation", security = @SecurityRequirement(name = "bearerAuth"))
	public MonitoringLocation updateMonitoringLocation(@PathVariable("id") String id, @RequestBody MonitoringLocation ml,
			HttpServletResponse response) throws IOException {
		BigInteger idInt = NumberUtils.parseNumber(id, BigInteger.class);
		
		if (null == mLDao.getById(idInt)) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		else {
			ml.setId(idInt);
			ml.setUpdatedBy(getUsername());
			if (validator.validate(ml).isEmpty()) {
			mLDao.update(ml);
			} else {
				response.sendError(406, "Invalid data submitted to CRU.");
				return null;
			}
		}
		return mLDao.getById(idInt);
	}

	@PreAuthorize("hasPermission(#ml, null)")
	@PatchMapping()
	@Operation(summary = "patchMonitoringLocation", security = @SecurityRequirement(name = "bearerAuth"))
	public MonitoringLocation patchMonitoringLocation(@RequestBody Map<String, Object> ml, HttpServletResponse response) throws IOException {
		ml.put(UPDATED_BY, getUsername());
		if (validator.validate(ml).isEmpty()) {	
			mLDao.patch(ml);
			MonitoringLocation location = mLDao.getByAK(ml);
			if (location == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				return null;
			} else {
				return location;
			}
		} else {
			// Note - Because the input here is a map instead of a POJO there are 
			// currently no validations run and thus no way to get here.
			response.sendError(406, "Invalid data submitted to CRU.");
			return null;
		}
	}

	@GetMapping("/loggedTransactions/summary")
	@Operation(summary = "getLoggedTransactionSummary", security = @SecurityRequirement(name = "bearerAuth"))
	public List<LoggedTransactionSummary> getLoggedTransactionSummary(
		@RequestParam(required=true) @Pattern(regexp="\\d\\d\\d\\d-\\d\\d-\\d\\d") String startDate,
		@RequestParam(required=true) @Pattern(regexp="\\d\\d\\d\\d-\\d\\d-\\d\\d") String endDate,
		@RequestParam(required=false) @Size(min=0, max=3) String districtCode,
		HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>();
		params.put(START_DATE, startDate);
		params.put(END_DATE, endDate);
		params.put(DISTRICT_CODE, districtCode);
		List<LoggedTransactionSummary> result = lADao.transactionSummaryByDC(params);
		return result;
	}

	@GetMapping("/loggedTransactions")
	@Operation(summary = "getLoggedTransactions", security = @SecurityRequirement(name = "bearerAuth"))
	public List<LoggedTransaction> getLoggedTransactions(
		@Validated @ParameterObject LoggedTransactionQueryParams params, 
		HttpServletResponse response) {
		List<LoggedTransaction> result = lADao.findTransactions(params.getAsQueryParams());
		return result;
	}

	@GetMapping("/loggedActions")
	@Operation(summary = "getLoggedActions", security = @SecurityRequirement(name = "bearerAuth"))
	public List<LoggedAction> getLoggedActions(
			@RequestParam(name = AGENCY_CODE, required = false) String agencyCode,
			@RequestParam(name = SITE_NUMBER, required = false) String siteNumber,
			@RequestParam(name = START_DATE, required = false) String startDate,
			@RequestParam(name = END_DATE, required = false) String endDate,
			HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>();
		params.put(AGENCY_CODE, agencyCode);
		params.put(SITE_NUMBER, siteNumber);
		params.put(START_DATE, startDate);
		params.put(END_DATE, endDate);
		List<LoggedAction> las = lADao.findActions(params);
		if (null == las || las.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return las;
	}

	protected String getUsername() {
		return userAuthUtil.getUsername(SecurityContextHolder.getContext().getAuthentication());
	}
}
