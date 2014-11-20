/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import hu.bme.aut.wman.handlers.UserHandlerLocal;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.service.AbstractDataService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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
	
	private static final QName NAME;
	private static final QName REMOVABLE;
	
	static {
		NAME = new QName("name");
		REMOVABLE = new QName("removable");
	}
	

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	
	
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	
	@EJB(mappedName = "java:module/UserHandlerImpl")
	private UserHandlerLocal userHandler;
	
	
	static {
		PropertyConfigurator.configure( LOG4J_PROPERTIES );
	}

	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.debug("WebAppStartupListener.onApplicationEvent: start");
		
		/*XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = null;
		InputStream resource = null;
		try {

			resource = new FileInputStream(XML_DB_CONFIG);
			eventReader = inputFactory.createXMLEventReader(resource);
			LOGGER.debug("processing " + XML_DB_CONFIG);
			
			while (eventReader.hasNext()) {
				tryReadingElement( eventReader );
			}
		} catch(Exception e) {
			LOGGER.fatal("WebAppStartupListener -- parsing " + XML_DB_CONFIG + " threw ", e);
		} finally {
			tryClose( eventReader );
		}*/
	}
	
	private final void tryReadingElement(XMLEventReader reader) throws Exception {
		XMLEvent event = reader.nextEvent();
		
		Map<String, Domain> domains = new HashMap<String, Domain>();
		Map<String, Privilege> privileges = new HashMap<String, Privilege>();
		if (event.getEventType() == XMLEvent.START_ELEMENT) {
			StartElement element = event.asStartElement();
			String localName = element.getName().getLocalPart();
			
			switch (localName) {
			case "domains":
				tryParseDomains(domains, localName, reader);
				break;
			case "privileges":
				tryParsePrivileges(privileges, localName, reader);
				break;
			}
		}
	}
	
	private final boolean canReadMore(String localName, XMLEventReader reader) throws Exception {
		XMLEvent event = reader.hasNext() ? reader.peek() : null;
		if (event == null)
			return false;
		return (event.getEventType() == XMLEvent.END_ELEMENT) ? 
					!event.asEndElement().getName().getLocalPart().equals( localName ) :
					true;
	}
	
	private final void tryClose(XMLEventReader reader) {
		if (reader != null)
			try {
				reader.close(); 
			} catch(Exception e) {
				LOGGER.error(" while closing XMLEventReader for " + XML_DB_CONFIG, e);
			}
	}
	
	private final void tryParseDomains(Map<String, Domain> domains, String localName, XMLEventReader reader) throws Exception {
		LOGGER.debug("parsing domains..");
		List<Domain> removables = new ArrayList<Domain>();
		while (canReadMore(localName, reader)) {
			XMLEvent event = reader.nextEvent();
			
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement domain = event.asStartElement();
				String domainName = domain.getAttributeByName( NAME ).getValue();
				String removable = domain.getAttributeByName( REMOVABLE ).getValue();
				Domain d = domainService.selectByName(domainName);
				d = (d == null) ? new Domain(domainName) : d;
				
				LOGGER.debug(d.toString() + " was found");
				if (Boolean.valueOf(removable))
					removables.add(d);
				else 
					domains.put(domainName, d);
			}
		}
		
		removeAll(removables, domainService);
		insertAll(new ArrayList<Domain>(domains.values()), domainService);
	}
	
	private final void tryParsePrivileges(Map<String, Privilege> privileges, String localName, XMLEventReader reader) throws Exception {
		LOGGER.debug("parsing privileges..");
		List<Privilege> removables = new ArrayList<Privilege>();
		
		while (canReadMore(localName, reader)) {
			XMLEvent event = reader.nextEvent();
			
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement privilege = event.asStartElement();
				String privilegeName = privilege.getAttributeByName( NAME ).getValue();
				String removable = privilege.getAttributeByName( REMOVABLE ).getValue();
				
				Privilege p = privilegeService.selectByName(privilegeName);
				p = (p == null) ? new Privilege(privilegeName) : p;
				boolean isRemovable = Boolean.valueOf(removable);

				LOGGER.debug(p.toString() + " was found");
				if (isRemovable) 
					removables.add(p);
				else
					privileges.put(privilegeName, p);
			}
		}
		
		removeAll(removables, privilegeService);
		insertAll(new ArrayList<Privilege>(privileges.values()), privilegeService);
	}

	private final void removeAll(List<? extends AbstractEntity> removables, AbstractDataService service) throws Exception {
		for(AbstractEntity entity : removables) {
			service.delete(entity);
			LOGGER.debug(entity.toString() + " was removed");
		}
	}
	
	private final void insertAll(List<? extends AbstractEntity> insertables, AbstractDataService service) throws Exception {
		for(AbstractEntity entity : insertables) {
			service.save(entity);
			LOGGER.debug(entity.toString() + " was inserted");
		}
	}
}
