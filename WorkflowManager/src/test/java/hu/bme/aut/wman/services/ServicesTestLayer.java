/**
 * ServicesTestLayer.java
 */
package hu.bme.aut.wman.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@SuiteClasses({
	MockedPrivilegeServiceTestSuite.class,
	MockedDomainServiceTestSuite.class
})
public class ServicesTestLayer {
//
//	private static final Logger LOGGER = Logger.getLogger( ServicesTestLayer.class );
//	
//	public static EntityManagerFactory factory;
//	public static EntityManager em;
//
//	@BeforeClass
//	public static void setupPersistence() throws Exception {
//		EntityTransaction tx = null;
//		try {
//			Properties props = new Properties();
//			props.put("openjpa.RuntimeUnenhancedClasses", RuntimeUnenhancedClassesModes.SUPPORTED);
//			factory = Persistence.createEntityManagerFactory("integrationTest", props);
//			em = factory.createEntityManager();
//			
//			LOGGER.info(format("%s set up the Persistence Unit(s) for further testing..", ServicesTestLayer.class.getSimpleName()));
//		} catch(Exception e) {
//			LOGGER.error(format("Unexpected: %s", e), e);
//			e.printStackTrace();
//			tryClean(tx, em);
//			throw e;
//		}
//	}
//
//	private static final void tryClean(EntityTransaction tx, EntityManager em) {
//		if (tx != null) {
//			try {
//				tx.rollback();
//			} catch(Exception e) {
//				LOGGER.info(format("Unable to rollback: %s", e), e);
//			}
//		}
//		
//		if (em != null) {
//			try {
//				em.close();
//			} catch(Exception e) {
//				LOGGER.info(format("Unable to close em: %s", e), e);
//			}
//		}
//	}
//
//	@AfterClass
//	public static void teardownPersistence() {
//		try {
//			if (em != null)
//				em.close();
//		} catch(Exception e) {
//			LOGGER.error(format("Unexpected exception while closing %s: %s", em, e), e);
//		}
//	}
}
