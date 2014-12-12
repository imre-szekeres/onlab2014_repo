/**
 * DomainManagerTestSuite.java
 */
package hu.bme.aut.wman.integration;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hu.bme.aut.wman.managers.DomainManager;
import hu.bme.aut.wman.model.BeanValidationTestSuite;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.repositories.EntityRepository;
import hu.bme.aut.wman.repositories.MultiValuedEntityRepository;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class DomainManagerTestSuite {

	private static final Logger LOGGER = Logger.getLogger( DomainManagerTestSuite.class );
	public static final String SUBJECT_NAME = "sudoer";
	
	private static MultiValuedEntityRepository<Long, DomainAssignment> daRepo;
	private static EntityRepository<String, User> userRepo;
	private static EntityRepository<String, Domain> domainRepo;
	private static EntityRepository<String, Role> roleRepo;
	private DomainManager.TestWrapper wrapper;
	private String domainName;
	
	public DomainManagerTestSuite(String domainName) {
		this.domainName = domainName;
	}
	
	@Parameters
	public static final Collection<Object[]> domainNames() {
		return Arrays.asList(new Object[][] {
				{ "Rich Tenant" }
		});
	}

	@BeforeClass
	public static final void initInMemoryPersistence() {
		daRepo = new MultiValuedEntityRepository<Long, DomainAssignment>() {
			@Override
			protected List<DomainAssignment> newList() {
				return new ArrayList<DomainAssignment>();
			}
		};

		userRepo = new EntityRepository<String, User>();
		userRepo.create(SUBJECT_NAME, BeanValidationTestSuite.newValidUser());
		
		roleRepo = initRoleRepo();
		domainRepo = initDomainRepo( roleRepo );
	}

	private static final EntityRepository<String, Role> initRoleRepo() {
		EntityRepository<String, Role> roleRepo = new EntityRepository<>();
		roleRepo.create("System Administrator", new Role("System Administrator"));
		roleRepo.create("System Manager", new Role("System Manager"));
		roleRepo.create("System Developer", new Role("System Developer"));
		roleRepo.create("System Viewer", new Role("System Viewer"));
		return roleRepo;
	}

	private static final EntityRepository<String, Domain> initDomainRepo(EntityRepository<String, Role> roleRepo) {
		EntityRepository<String, Domain> domainRepo = new EntityRepository<>();
		Domain d = new Domain( DomainService.DEFAULT_DOMAIN );
		for(String r : new String[] {"System Administrator", "System Manager", "System Developer", "System Viewer"})
			d.addRole( roleRepo.read(r) );
		domainRepo.create(d.getName(), d);
		return domainRepo;
	}

	@Before
	public void initContext() {
		wrapper = new DomainManager.TestWrapper(new DomainManager());
		wrapper.setUserService( createMockedUserService() );
		wrapper.setDomainService( createMockedDomainService() );
		wrapper.setRoleService( createMockedRoleService() );
		wrapper.setDomainAssignmentService( createMockedDomainAssignmentService() );
	}
	
	private static final UserService createMockedUserService() {
		UserService mocksUS = mock(UserService.class);

		when(mocksUS.selectByName( anyString() )).thenAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return userRepo.read(args[0].toString());
			}
		});

		Mockito.doAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				User user = (User) args[0];
				return userRepo.update(user.getUsername(), user);
			}
		}).when(mocksUS).save( (User)any() );
		return mocksUS;
	}
	
	private static final DomainService createMockedDomainService () {
		DomainService mocksDS = mock(DomainService.class);
		
		Mockito.doAnswer(new Answer<Domain>() {
			@Override
			public Domain answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Domain domain = (Domain) args[0];
				return domainRepo.update(domain.getName(), domain);
			}
		}).when(mocksDS).save( (Domain)any() );
		
		when(mocksDS.selectByName( anyString() )).thenAnswer(new Answer<Domain>() {
			@Override
			public Domain answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return domainRepo.read( args[0].toString() );
			}
		});
		return mocksDS;
	}

	private static final RoleService createMockedRoleService() {
		RoleService mocksRS = mock(RoleService.class);
		
		Mockito.doAnswer(new Answer<Role>() {
			@Override
			public Role answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Role role = (Role) args[0];
				return roleRepo.update(role.getName(), role);
			}
		}).when( mocksRS ).save( (Role)any() );
		return mocksRS;
	}

	private static final DomainAssignmentService createMockedDomainAssignmentService() {
		DomainAssignmentService mocksDAS = mock(DomainAssignmentService.class);
		
		Mockito.doAnswer(new Answer<DomainAssignment>() {
			@Override
			public DomainAssignment answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				DomainAssignment da = (DomainAssignment) args[0];
				return daRepo.update(da.getDomain().getId(), da);
			}
		}).when(mocksDAS).save( (DomainAssignment)any() );;
		return mocksDAS;
	}

	@Test
	public void testCreation() {
		Domain domain = new Domain( domainName );
		try {
			User subject = wrapper.getUserService().selectByName(SUBJECT_NAME);
			wrapper.manager().create(subject, domain);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testRemoval() {
	}
}
