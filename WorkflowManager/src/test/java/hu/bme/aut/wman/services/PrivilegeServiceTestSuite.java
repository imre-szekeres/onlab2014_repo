/**
 * PrivilegeServiceTestSuite.java
 */
package hu.bme.aut.wman.services;


import static hu.bme.aut.wman.utils.StringUtils.asString;
import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.service.PrivilegeService;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class PrivilegeServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( PrivilegeServiceTestSuite.class );
	private static PrivilegeService privilegeService;

	
	private Privilege privilege;
	private String original;
	private String changeTo;
	
	public PrivilegeServiceTestSuite(String original, String changeTo) {
		this.original = original;
		this.changeTo = changeTo;
	}

	@BeforeClass
	public static void initBeans() {
		privilegeService = new PrivilegeService();
		privilegeService.setEntityManager( ServicesTestLayer.em );
	}
	
	@Test
	public void testCreation() {
		privilege = new Privilege( original );

		EntityTransaction tx = ServicesTestLayer.em.getTransaction();
		try {
			tx.begin();
			privilegeService.save( privilege );
			LOGGER.info(format("Privielege %s was created..", privilege));
			tx.commit();
			assertTrue( privilegeService.selectByName(original) != null );
		} catch(Exception e) {
			tx.rollback();
			LOGGER.error(e);
			fail();
		}
	}

	@Test
	public void testManipulation() {
		EntityTransaction tx = ServicesTestLayer.em.getTransaction();
		try {

			tx.begin();
			privilege.setName( changeTo );
			privilegeService.save( privilege );
			tx.commit();

			LOGGER.info(format("The name of Privilege %s was changed to %s", original, asString(privilegeService.selectByName( changeTo ))));
			
			assertTrue(privilegeService.selectByName(original) == null);
			assertTrue(privilegeService.selectByName(changeTo) != null);

		} catch(Exception e) {
			LOGGER.error(e);
			tx.rollback();
			fail();
		}
	}
	
	@Test
	public void testRemoval() {
		EntityTransaction tx = ServicesTestLayer.em.getTransaction();
		try {
			
			tx.begin();
			privilegeService.delete(privilege);
			LOGGER.info(format("Priviliege %s was removed.", asString(privilege)));
			tx.commit();
			
			assertTrue( (privilegeService.selectByName(changeTo) == null) && 
					    (privilegeService.selectByName(original) == null) );
		} catch(Exception e) {
			tx.rollback();
			LOGGER.error(e);
			fail();
		}
	}

	@Parameters
	public static final Collection<Object[]> names() {
		return Arrays.asList(new Object[][] {
				{"View User", "Create Role"},
				{"Assign Privilege", "Assign User"}
		});
	}
}
