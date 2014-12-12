/**
 * IntegrationTestLayer.java
 */
package hu.bme.aut.wman.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@SuiteClasses({
	RoleManagerTestSuite.class,
	DomainManagerTestSuite.class,
	WorkflowServiceTestSuite.class
})
public class IntegrationTestLayer {
}
