/**
 * MockedDomainSerivceTestSuite.java
 */
package hu.bme.aut.wman.services;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class MockedDomainServiceTestSuite {

	private static DomainService domainService;
	
	
	@BeforeClass
	public static void createDomainService() {
		domainService = new DomainService(); 
	}
	
	@Before
	public void initDomainService() {
		domainService.setEntityManager( mock(EntityManager.class) );

		DomainAssignmentService daService = new DomainAssignmentService();
		daService.setEntityManager( mock(EntityManager.class) );
	}

	@Test
	public void testCreation() {
		Domain domain = new Domain("System");
		domainService.save( domain );
		
		EntityManager mocksDomain = domainService.getEntityManager();
		verify(mocksDomain, times(1)).persist( domain );
	}
}
