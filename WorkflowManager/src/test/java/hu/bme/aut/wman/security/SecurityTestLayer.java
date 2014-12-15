/**
 * SecurityTestLayer.java
 */
package hu.bme.aut.wman.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Suite.class)
@SuiteClasses({
	AuthenticationServiceTestSuite.class
})
public class SecurityTestLayer {
}
