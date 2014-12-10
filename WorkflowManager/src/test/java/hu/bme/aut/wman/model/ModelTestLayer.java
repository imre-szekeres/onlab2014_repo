/**
 * WorkflowManagerModelTestSuite.java
 */
package hu.bme.aut.wman.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
/**
 * Main test class that runs the other test suites and configures the logger.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	BeanValidationTestSuite.class
})
public class ModelTestLayer {
}
