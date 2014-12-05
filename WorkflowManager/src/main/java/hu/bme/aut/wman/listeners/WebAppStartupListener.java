/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import hu.bme.aut.wman.listeners.services.StartupService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


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
	public static final String XML_DB_CONFIG = "../WorkflowManager/WEB-INF/classes/configs/wman-db-init.xml";
	public static final String LOG4J_PROPERTIES = "../WorkflowManager/WEB-INF/classes/log4j.properties";
	
	@EJB(mappedName = "java:module/StartupService")
	private StartupService startupService;
	private volatile boolean isSetupNeeded;
	
	static {
		PropertyConfigurator.configure( LOG4J_PROPERTIES );
	}

	/**
	 * Initializes the <code>WebAppStartupListener</code> to execute the setup operation.
	 * */
	@PostConstruct
	public void init() {
		this.isSetupNeeded = true;
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

			/*if (isSetupNeeded()) {
				synchronized (this) {
					startupService.setupWebapp(XML_DB_CONFIG);
				}
			}
			setSetupNeeded( false );*//* TODO: uncomment */
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.fatal(e);
		}
	}

	/**
	 * @return isSetupNeeded
	 * */
	public synchronized boolean isSetupNeeded() {
		return isSetupNeeded;
	}

	/**
	 * @param isSetupNeeded
	 * */
	public synchronized void setSetupNeeded(boolean isSetupNeeded) {
		this.isSetupNeeded = isSetupNeeded;
	}
}
