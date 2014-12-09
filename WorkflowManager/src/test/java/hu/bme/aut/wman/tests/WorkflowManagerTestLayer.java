/**
 * WorkflowManagerTestLayer.java
 */
package hu.bme.aut.wman.tests;

import hu.bme.aut.wman.model.ModelTestLayer;
import hu.bme.aut.wman.services.ServicesTestLayer;
import hu.bme.aut.wman.utils.UtilityTestLayer;
import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@SuiteClasses({
	UtilityTestLayer.class,
	ModelTestLayer.class,
	ServicesTestLayer.class
})
public class WorkflowManagerTestLayer extends TestCase {

	static {
		BasicConfigurator.configure();
	}
}
