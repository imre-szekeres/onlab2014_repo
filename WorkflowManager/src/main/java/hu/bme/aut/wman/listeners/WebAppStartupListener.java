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


/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("all")
public class WebAppStartupListener 
					implements ApplicationListener<ContextRefreshedEvent>{
	
	private static final Logger LOGGER = Logger.getLogger(WebAppStartupListener.class);
	public static final String XML_DB_CONFIG = "../WorkflowManager/WEB-INF/classes/configs/wman-db-init.xml";
	public static final String LOG4J_PROPERTIES = "../WorkflowManager/WEB-INF/classes/log4j.properties";
	
	@EJB(mappedName = "java:module/StartupService")
	private StartupService startupService;
	
	static {
		PropertyConfigurator.configure( LOG4J_PROPERTIES );
	}

	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.debug("WebAppStartupListener.onApplicationEvent: start");
		try {
			
			startupService.setupWebapp(XML_DB_CONFIG);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.fatal(e);
		}
	}
}
