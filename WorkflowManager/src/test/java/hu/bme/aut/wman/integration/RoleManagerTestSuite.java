/**
 * RoleManagerTestSuite.java
 */
package hu.bme.aut.wman.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import hu.bme.aut.wman.managers.RoleManager;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class RoleManagerTestSuite {

	private static final Logger LOGGER = Logger.getLogger( RoleManagerTestSuite.class );
	
	private RoleManager.TestWrapper wrapper;

	private Set<String> invalidNames;
	private Set<String> validNames;
	private String domainName;
	private String roleName;
	private String invalidRoleName;

	private Set<Privilege> valids;
	private Role invalidRole;
	private Role role;


	public RoleManagerTestSuite(Set<String> invalidNames, Set<String> validNames, String domainName, String roleName, String invalidRoleName) {
		this.invalidNames = invalidNames;
		this.validNames = validNames;
		this.domainName = domainName;
		this.roleName = roleName;
		this.invalidRoleName = invalidRoleName;
	}


	@Parameters
	public static final Collection<Object[]> names() {
		return Arrays.asList(new Object[][] {
				{ new HashSet<>(Arrays.asList(new String[] {"View User", "Create Role", "Create User"})),
			      new HashSet<>(Arrays.asList(new String[] {"View User", "Assign Role", "Create User"})),
				  "Rich Tenant",
				  "Rich Tenant Administrator",
				  "This" },

				{ new HashSet<>(Arrays.asList(new String[] {"Assign User", "Create Role", "Create User", "Delete Domain", "Update Domain", "View Project"})),
				  new HashSet<>(Arrays.asList(new String[] {"Assign User", "Create Role", "Create User", "Delete Domain", "Update Domain", "View User", "Assign Role", "Create Workflow"})),
				  "Siemens",
				  "Siemens Employee",
				  "L" }
		});
	}

	@Before
	public void initContext() {
		wrapper = new RoleManager.TestWrapper( new RoleManager() );

		RoleService mocksRS = mock(RoleService.class);
		DomainService mocksDS = mock(DomainService.class);
		PrivilegeService mocksPS = mock(PrivilegeService.class);

		wrapper.setRoleService( mocksRS );
		wrapper.setDomainService( mocksDS );
		wrapper.setPrivilegeService( mocksPS );
		
		Privilege p;
		valids = new HashSet<>();
		for(String name : validNames) {
			p = new Privilege( name );
			valids.add( p );
			when(mocksPS.selectByName( name )).thenReturn( p );
		}
		
		invalidRole = new Role( invalidRoleName );
		role = new Role( roleName );
		when(mocksRS.selectByName(roleName, domainName)).thenReturn( role );

		EntityManager mocksPSEm = mock(EntityManager.class);
		Mockito.doCallRealMethod().when(mocksPS).setEntityManager( mocksPSEm );
		mocksPS.setEntityManager( mocksPSEm );

		when(mocksPS.privilegesOf( validNames )).thenCallRealMethod();
		when(mocksPS.privilegesOf( invalidNames )).thenCallRealMethod();
		when(mocksPS.getEntityManager()).thenCallRealMethod();
	}

	private final void initValidationContext() {
		RoleService mocksRS = wrapper.getRoleService();
		Mockito.doCallRealMethod().when(mocksRS).setup();
		mocksRS.setup();
		when(wrapper.manager().validate(role, domainName)).thenCallRealMethod();
		when(wrapper.manager().validate(invalidRole, domainName)).thenCallRealMethod();
		when(mocksRS.selectByName(roleName, domainName)).thenReturn( null );
	}
	
	@Test
	public void testValidation() {
		initValidationContext();
		Map<String, String> errors = wrapper.manager().validate(invalidRole, domainName);
		Assert.assertTrue(errors.size() > 0);
		
		errors = wrapper.manager().validate(role, domainName);
		Assert.assertTrue(errors.size() == 0);
	}

	@Test
	public void testAssignValid() {
		try {
			wrapper.manager().assign(role, validNames);
			Assert.assertTrue(role.getPrivileges().size() == validNames.size());

			PrivilegeService mocksPS = wrapper.getPrivilegeService();
			RoleService mocksRS = wrapper.getRoleService();
			verify(mocksPS, times(1)).privilegesOf( validNames );
			verify(mocksRS, times(1)).save( role );
		} catch(EntityNotFoundException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test(expected = EntityNotFoundException.class)
	public void testAssignInvalid() {
		wrapper.manager().assign(role, invalidNames);
	}

	@Test
	public void testAssignNew() {
		Domain domain = initAssignNewContext();
		wrapper.manager().assignNew(role, domainName, validNames);
		
		Assert.assertTrue(domain.getRoles().contains( role ));

		Set<Privilege> privileges = role.getPrivileges();
		Assert.assertArrayEquals(privileges.toArray(), valids.toArray());
		
		PrivilegeService mocksPS = wrapper.getPrivilegeService();
		DomainService mocksDS = wrapper.getDomainService();
		verify(mocksPS, times(1)).privilegesOf(validNames);
		verify(mocksDS, times(1)).selectByName(domain.getName());
		verify(mocksDS, times(1)).save( domain );
		
		for(Privilege p : valids)
			verify(mocksPS, times(1)).selectByName(p.getName());
	}

	private final Domain initAssignNewContext() {
		Domain d = new Domain(domainName);		
		DomainService mocksDS = wrapper.getDomainService();
		when(mocksDS.selectByName( domainName )).thenReturn( d );
		return d;
	}
}
