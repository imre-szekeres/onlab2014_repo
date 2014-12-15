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
	MockedDomainServiceTestSuite.class,
	MockedWorkflowServiceTestSuite.class,
	MockedStateServiceTestSuite.class,
	MockedProjectServiceTestSuite.class,
	MockedAbstractDataServiceTestSuite.class
})
public class ServicesTestLayer {
}
