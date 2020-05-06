package gov.usgs.wma.mlrlegacy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class UserAuthUtil {
	private static final transient Logger LOG = LoggerFactory.getLogger(UserAuthUtil.class);
    
	@Value("${security.token.claims.username:preferred_username}")
    private String USER_NAME_CLAIM_KEY;
    
	public static final String UNKNOWN_USERNAME = "unknown ";
    
	public String getUsername(Authentication auth) {
		String username = UNKNOWN_USERNAME;
		if (null != auth && auth instanceof JwtAuthenticationToken) {
			JwtAuthenticationToken token = (JwtAuthenticationToken) auth;
			Object usernameObj = token.getTokenAttributes().get(USER_NAME_CLAIM_KEY);
			username = usernameObj != null ? usernameObj.toString() : null;
		} else {
			LOG.warn("Unable to extract username from auth token.");
		}
		return username;
	}
}