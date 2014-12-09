/**
 * ServicesTestLayer.java
 */
package hu.bme.aut.wman.services;

import static java.lang.String.format;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.apache.openjpa.enhance.RuntimeUnenhancedClassesModes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@SuiteClasses({
	PrivilegeServiceTestSuite.class,
	UserServiceTestSuite.class
})
public class ServicesTestLayer {

	public static final String DB_INIT_XML;
	private static final Logger LOGGER;
		
	static {
		DB_INIT_XML = "src/test/resources/configs/wman-db-init.xml";
		LOGGER = Logger.getLogger( ServicesTestLayer.class );
	}
	
	public static EntityManagerFactory factory;
	public static EntityManager em;
	public static UserService userService;
	public static DomainService domainService;
	public static RoleService roleService;
	public static PrivilegeService privilegeService;
	public static DomainAssignmentService daService;

	@BeforeClass
	public static void setupPersistence() throws Exception {
		EntityTransaction tx = null;
		try {
			Properties props = new Properties();
			props.put("openjpa.RuntimeUnenhancedClasses", RuntimeUnenhancedClassesModes.SUPPORTED);
			factory = Persistence.createEntityManagerFactory("integrationTest", props);
			em = factory.createEntityManager();
			
			setupServices( em );
			LOGGER.info(format("%s set up the Persistence Unit(s) for further testing..", ServicesTestLayer.class.getSimpleName()));
		} catch(Exception e) {
			LOGGER.error(format("Unexpected: %s", e), e);
			e.printStackTrace();
			tryClean(tx, em);
			throw e;
		}
	}

	private static final void tryClean(EntityTransaction tx, EntityManager em) {
		if (tx != null) {
			try {
				tx.rollback();
			} catch(Exception e) {
				LOGGER.info(format("Unable to rollback: %s", e), e);
			}
		}
		
		if (em != null) {
			try {
				em.close();
			} catch(Exception e) {
				LOGGER.info(format("Unable to close em: %s", e), e);
			}
		}
	}

	private static final void setupServices(EntityManager em) {
		userService = new UserService();
		userService.setEntityManager( em );
		
		domainService = new DomainService();
		domainService.setEntityManager( em );
		
		roleService = new RoleService();
		roleService.setEntityManager( em );
		
		privilegeService = new PrivilegeService();
		privilegeService.setEntityManager( em );
		
		daService = new DomainAssignmentService();
		daService.setEntityManager( em );
		
		domainService.set( daService );
	}

	@AfterClass
	public static void teardownPersistence() {
		try {
			if (em != null)
				em.close();
		} catch(Exception e) {
			LOGGER.error(format("Unexpected exception while closing %s: %s", em, e), e);
		}
	}
}
