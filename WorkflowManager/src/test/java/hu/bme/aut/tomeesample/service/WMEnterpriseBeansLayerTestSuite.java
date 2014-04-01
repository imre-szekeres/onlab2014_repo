/**
 * WMEnterpriseBeansLayerTestSuite.java
 */
package hu.bme.aut.tomeesample.service;

import org.junit.runners.*;
import org.junit.runner.*;
import org.junit.*;
import org.apache.log4j.*;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	UserServiceTestSuite.class
})
public class WMEnterpriseBeansLayerTestSuite {

	@BeforeClass
	public static void setup(){
		BasicConfigurator.configure();
	}
}
