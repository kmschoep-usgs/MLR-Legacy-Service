package gov.usgs.wma.mlrlegacy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import gov.usgs.wma.mlrlegacy.config.PermissionEvaluatorImpl;

@ExtendWith(SpringExtension.class)
public class PermissionEvaluatorTest {

	PermissionEvaluatorImpl permissionEvaluatorImpl = new PermissionEvaluatorImpl();
	//The @WithMockUser way of setting roles prefixes them with "ROLE_"
	String[] roles = Stream.of("ROLE_DOG", "ROLE_COW").toArray(String[]::new);

	@Test
	@WithMockUser(username="mlr",roles={"ldm"})
	public void npeTestRoles() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", null);
		assertFalse(permissionEvaluatorImpl.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, null));
	}

	@Test
	public void npeTestAuthentication() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", roles);
		assertFalse(permissionEvaluatorImpl.hasPermission(null, null, null));
	}

	@Test
	@WithMockUser(username="mlr")
	public void npeTestAuthorities() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", roles);
		assertFalse(permissionEvaluatorImpl.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, null));
	}

	@Test
	public void npeTestAuthoritiesAndRoles() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", null);
		assertFalse(permissionEvaluatorImpl.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, null));
	}

	@Test
	@WithMockUser(username="mlr",roles={"ldm","donkey"})
	public void notAuthorizedTest() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", roles);
		assertFalse(permissionEvaluatorImpl.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, null));
	}

	@Test
	@WithMockUser(username="mlr",roles={"ldm","donkey","cow"})
	public void oneMatchTest() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", roles);
		assertTrue(permissionEvaluatorImpl.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, null));
	}

	@Test
	@WithMockUser(username="mlr",roles={"ldm","cow","donkey","dog"})
	public void twoMatchTest() {
		ReflectionTestUtils.setField(permissionEvaluatorImpl, "roles", roles);
		assertTrue(permissionEvaluatorImpl.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, null));
	}

	@Test
	public void alwaysFalse() {
		assertFalse(permissionEvaluatorImpl.hasPermission(null, null, null, null));
	}

}
