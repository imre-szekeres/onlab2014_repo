/**
 * MockedDomainSerivceTestSuite.java
 */
package hu.bme.aut.wman.services;


import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.service.DomainService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

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
public class MockedDomainServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( MockedDomainServiceTestSuite.class );

	private DomainService domainService;
	private Domain domain;
	private String domainName;
	private Properties privilegeTestProperties;
	
	public MockedDomainServiceTestSuite(String domainName, Properties privilegeTestProperties) {
		this.domainName = domainName;
		this.privilegeTestProperties = privilegeTestProperties;
	}

	@Parameters
	public static final Collection<Object[]> domainNames() {
		Properties[] properties = createPropertiesArray();
		return Arrays.asList(new Object[][] {
				{ "System", properties[0] },
				{ "BME.Aut", properties[1] },
				{ "Rich Tenant", properties[2] }
		});
	}

	private static final Properties[] createPropertiesArray() {
		List<String[]> propertyPairs = listPairs();
		Properties[] results = new Properties[propertyPairs.size()];
		
		String[] pair;
		for(int i = 0; i < results.length; ++i) {
			results[i] = new Properties();
			
			pair = propertyPairs.get(i);
			results[i].put("username", pair[0]);
			results[i].put("owned", pair[1]);
			results[i].put("not-owned", pair[2]);
		}
		return results;
	}

	private static final List<String[]> listPairs() {
		return Arrays.asList(new String[][] {
				{ "Mr Johns", "View User", "Assign Role" },
				{ "sudoer", "Create Domain", "Create Project" },
				{ "Clark Kent", "Assign Privilege", "View Privilege" }
		});
	} 
	
	@Before
	public void initContext() {
		domainService = new DomainService();
		domain = new Domain( domainName );

		EntityManager mocksEm = mock(EntityManager.class);
		domainService.setEntityManager( mocksEm );
		
		when(mocksEm.merge( domain )).thenReturn( domain );
	}

	@Test
	public void testCreation() {
		domainService.save( domain );
		EntityManager mocksDomain = domainService.getEntityManager();
		verify(mocksDomain, times(1)).persist( domain );
		LOGGER.info(format("Domain %s was created", domain));
	}

	@Test
	public void testHasPrivilege() {
		String username = (String) privilegeTestProperties.get("username");
		String owned = (String) privilegeTestProperties.get("owned");
		String notOwned = (String) privilegeTestProperties.get("not-owned");
		
		initPrivilegeContext(username, owned, notOwned);
		try {

			Assert.assertTrue( domainService.hasPrivilege(username, domain.getName(), owned) );
			LOGGER.info(format("User %s owns Privilege %s in Domain %s", username, owned, domain.getName()));

			Assert.assertFalse( domainService.hasPrivilege(username, domain.getId(), notOwned) );
			LOGGER.info(format("User %s does not own Privilege %s in Domain %s", username, owned, domain.getName()));
			
		} catch(Exception  e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	private void initPrivilegeContext(String username, String owned, String notOwned) {
		domainService = mock(DomainService.class);

		when(domainService.domainNameOf(username, domain.getName(), owned)).thenReturn(Arrays.asList(new String[] { domain.getName() }));
		when(domainService.domainNameOf(username, domain.getName(), notOwned)).thenReturn(Arrays.asList(new String[0]));
		
		when(domainService.hasPrivilege(username, domain.getName(), owned)).thenCallRealMethod();
		when(domainService.domainNameOf(username, domain.getName(), notOwned)).thenCallRealMethod();
	}
	
	@Test
	public void testRemoval() {
		try {

			domainService.delete( domain );			
			EntityManager mocksDomain = domainService.getEntityManager();

			verify(mocksDomain, times(1)).merge( domain );
			verify(mocksDomain, times(1)).remove( domain );
			LOGGER.info(format("Domain %s was removed", domain));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
}
