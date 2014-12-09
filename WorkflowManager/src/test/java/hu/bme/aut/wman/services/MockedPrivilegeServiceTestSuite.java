/**
 * MockedPrivilegeServiceTestSuite.java
 */
package hu.bme.aut.wman.services;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.service.PrivilegeService;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class MockedPrivilegeServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( MockedPrivilegeServiceTestSuite.class );
	
	private PrivilegeService privilegeService;
	private Privilege privilege;
	private String original;
	private String changeTo;

	public MockedPrivilegeServiceTestSuite(String original, String changeTo) {
		this.original = original;
		this.changeTo = changeTo;
	}

	
	@Parameters
	public static final Collection<Object[]> privilegeNames() {
		return Arrays.asList(new Object[][] {
				{ "View User", "Assign Role" },
				{ "Create Role", "View Privilege" }
		});
	}
	
	@Before
	public void initContext() {
		privilege = new Privilege( original );
		privilegeService = new PrivilegeService();
		EntityManager mocksEm = mock(EntityManager.class);
		privilegeService.setEntityManager( mocksEm );
		
		when(mocksEm.merge( privilege )).thenReturn( privilege );
	}
	
	@Test
	public void testCreation() {
		try {
			
			privilegeService.save( privilege );
			EntityManager mocksEm = privilegeService.getEntityManager();
			verify(mocksEm, times(1)).persist( privilege );
			LOGGER.info(format("Privilege %s was created", privilege));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testManipulation() {
		initManipulationContext();
		try {
			Privilege p = privilegeService.selectByName( original );
			
			Assert.assertNotNull( p );
			LOGGER.info(format("Privilege %s was found before changing it's name to %s", p, changeTo));
			p.setName(changeTo);
			privilegeService.save( p );
			
			Privilege p2 = privilegeService.selectByName( changeTo );
			Assert.assertNotNull( p2 );
			LOGGER.info(format("Privilege %s after modification was found as %s", original, p2));
			
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	private void initManipulationContext() {
		privilegeService = mock(PrivilegeService.class);
		when(privilegeService.selectByName(original)).thenReturn( original.equals(privilege.getName()) ? privilege : null );
		when(privilegeService.selectByName(changeTo)).thenReturn( original.equals(privilege.getName()) ? privilege : null );
	}

	@Test
	public void testRemoval() {
		try {
			
			privilegeService.delete( privilege );

			EntityManager mocksEm = privilegeService.getEntityManager();
			verify(mocksEm, times(1)).merge( privilege );
			verify(mocksEm, times(1)).remove( privilege );
			LOGGER.info(format("Privilege %s was removed", privilege));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		} 
	}
}
