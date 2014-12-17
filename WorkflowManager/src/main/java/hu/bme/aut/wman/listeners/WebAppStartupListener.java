/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import hu.bme.aut.wman.listeners.services.StartupService;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ResourceLoader;


/**
 * Initializes the database being used with the default <code>Domain</code> with the initial
 * <code>Role</code>s and <code>User</code>s and also the <code>Privilege</code>s.
 *
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("all")
public class WebAppStartupListener
implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger LOGGER = Logger.getLogger(WebAppStartupListener.class);
	//	public static final String XML_DB_CONFIG = "./WEB-INF/classes/configs/wman-db-init.xml";
	//	public static final String LOG4J_PROPERTIES = "./WEB-INF/classes/log4j.properties";
	public static final String XML_DB_CONFIG = ResourceLoader.class.getResource("/configs/wman-db-init.xml").getPath();
	public static final String LOG4J_PROPERTIES = ResourceLoader.class.getResource("/log4j.properties").getPath();

	@EJB(mappedName = "java:module/StartupService")
	private StartupService startupService;

	static {
		PropertyConfigurator.configure( LOG4J_PROPERTIES );
	}

	/**
	 * The event handler method called on occurrences of <code>ContextRefreshedEvent</code>.
	 *
	 * @param event
	 * */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.debug("WebAppStartupListener.onApplicationEvent: start");
		try {

			if ("Root WebApplicationContext".equals( event.getApplicationContext().getDisplayName() )) {
				startupService.setupWebapp(XML_DB_CONFIG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.fatal(e);
		}
	}
}
