package gov.usgs.wma.mlrlegacy.db;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.database.rider.junit5.api.DBRider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import gov.usgs.wma.mlrlegacy.Application;
import gov.usgs.wma.mlrlegacy.config.MethodSecurityConfig;
import gov.usgs.wma.mlrlegacy.config.WebSecurityConfig;
import gov.usgs.wma.mlrlegacy.dao.LoggedActionsDao;
import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;

@SpringBootTest(
		classes={DBTestConfig.class, Application.class,
				WebSecurityConfig.class,
				MethodSecurityConfig.class},
		webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties={"maintenanceRoles=ROLE_DBA_55",
				"security.require-ssl=false",
				"server.ssl.enabled=false"
		}
	)
@DBRider
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Import({MonitoringLocationDao.class, LoggedActionsDao.class, DBTestConfig.class})
public abstract class BaseControllerIT extends BaseIT {

	@Autowired
	RSAPublicKey testPublicKey;

	@Autowired
	RSAPrivateKey testPrivateKey;

	@Autowired
	protected TestRestTemplate restTemplate;

	public String createToken(String username, String email, String ... roles) throws Exception {	
		String jwt = JWT.create()
				.withAudience("mlr")
				.withExpiresAt(Date.from(Instant.now().plusSeconds(1000)))
				.withClaim("sub", username)
				.withArrayClaim("authorities", roles)
				.withClaim("email", email)
				.sign(Algorithm.RSA256(testPublicKey, testPrivateKey))
				;
		return jwt;
	}

	public HttpHeaders getAuthorizedHeaders() {
		return getHeaders(KNOWN_USER, "known@usgs.gov", "ROLE_DBA_55");
	}

	public HttpHeaders getUnauthorizedHeaders() {
		return getHeaders(KNOWN_USER, "known@usgs.gov", "ROLE_UNKNOWN");
	}

	public HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public HttpHeaders getHeaders(String username, String email, String ... roles) {
		HttpHeaders headers = getHeaders();
		try {
			headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + createToken(username, email, roles));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return headers;
	}
}
