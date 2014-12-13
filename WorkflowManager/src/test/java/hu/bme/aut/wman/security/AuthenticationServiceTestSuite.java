/**
 * AuthenticationServiceTestSuite.java
 */
package hu.bme.aut.wman.security;

import static hu.bme.aut.wman.model.BeanValidationTestSuite.newValidUser;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.repositories.EntityRepository;
import hu.bme.aut.wman.repositories.MultiValuedEntityRepository;
import hu.bme.aut.wman.security.services.AuthenticationService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class AuthenticationServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( AuthenticationServiceTestSuite.class );
	private static MultiValuedEntityRepository<String, Privilege> privilegesRepo;
	private static EntityRepository<String, User> userRepo;
	private static AuthenticationService.TestWrapper wrapper;
	private String invalidUsername;
	private String validUsername;

	public AuthenticationServiceTestSuite(String invalidUsername, String validUsername, String validPassword, List<String> privilegeNames) {
		this.invalidUsername = invalidUsername;
		this.validUsername = validUsername;
		
		userRepo.update(validUsername, newValidUser(validUsername, validPassword));
		update(validUsername, privilegeNames, privilegesRepo);
	}

	@Parameters
	public static final Collection<Object[]> userNames() {
		return Arrays.asList(new Object[][] {
				{ "johns73", "smith66", "Simith_77",
				  Arrays.asList(new String[] { "View User", "Create Role" }) },
				{ "luthor_l", "clark_k", "33sm_canFly$",
				  Arrays.asList(new String[] { "Create Domain", "Assign User", "Assign Role", "Create Project", "View Workflow" }) }
		});
	}

	private static final void update(String username, List<String> privilegeNames, MultiValuedEntityRepository<String, Privilege> privilegesRepo) {
		for(String name : privilegeNames)
			privilegesRepo.update(username, new Privilege( name ));
	}

	@BeforeClass
	public static final void setupInMemoryPersistenceContext() {
		userRepo = new EntityRepository<>();
		privilegesRepo = new MultiValuedEntityRepository<String, Privilege>() {
			@Override
			public List<Privilege> newList() {
				return new ArrayList<Privilege>();
			}
		};
		wrapper = new AuthenticationService.TestWrapper(new AuthenticationService());
	}
	
	@AfterClass
	public static final void teardownInMemoryPersistenceContext() {
		userRepo = null;
		privilegesRepo = null;
		wrapper = null;
	}

	@Before
	public void initContext() {
		wrapper.setUserService( createMockedUserService() );
		wrapper.setPrivilegeService( createMockedPrivilegeService() );
	}

	private static final UserService createMockedUserService() {
		UserService mocksUS = mock(UserService.class);
		
		when(mocksUS.selectPasswordOf( anyString() )).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				User user = userRepo.read((String) args[0]);
				return (user == null) ? null : user.getPassword();
			}
		});
		return mocksUS;
	}

	private static final PrivilegeService createMockedPrivilegeService() {
		PrivilegeService mocksPS = mock(PrivilegeService.class);
		
		when(mocksPS.privilegesOf( anyString() )).thenAnswer(new Answer<List<Privilege>>() {
			@Override
			public List<Privilege> answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				String username = (String) args[0];
				return privilegesRepo.readAll( username );
			}
		});
		return mocksPS;
	}

	
	@Test
	public void testValidUsername() {
		try {
			
			UserDetails subject = wrapper.service().loadUserByUsername( validUsername );
			Assert.assertNotNull( subject );
			Assert.assertArrayEquals(subject.getAuthorities().toArray(), wrapper.getPrivilegeService().privilegesOf( subject.getUsername() ).toArray());
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testInvalidUsername() {
		wrapper.service().loadUserByUsername( invalidUsername );
	}
}
