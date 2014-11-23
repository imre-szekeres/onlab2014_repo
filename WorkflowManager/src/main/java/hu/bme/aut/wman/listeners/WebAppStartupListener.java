/**
 * WebAppStartupListener.java
 */
package hu.bme.aut.wman.listeners;


import hu.bme.aut.wman.handlers.UserHandlerLocal;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.DomainAssignment;
import hu.bme.aut.wman.model.Privilege;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.service.AbstractDataService;
import hu.bme.aut.wman.service.DomainAssignmentService;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.PrivilegeService;
import hu.bme.aut.wman.service.RoleService;
import hu.bme.aut.wman.service.UserService;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
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
	private static final QName DOMAIN;
	private static final QName IMPORT;

	private static final QName PASSWORD;
	private static final QName EMAIL;
	private static final QName DESCRIPTION;
	
	static {
		NAME = new QName("name");
		REMOVABLE = new QName("removable");
		DOMAIN = new QName("domain");
		IMPORT = new QName("import");
		
		PASSWORD = new QName("password");
		EMAIL = new QName(User.PR_EMAIL);
		DESCRIPTION = new QName(User.PR_DESCRIPTION);
	}
	

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	
	@EJB(mappedName = "java:module/RoleService")
	private RoleService roleService;
	
	
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	
	@EJB(mappedName = "java:module/DomainAssignmentService")
	private DomainAssignmentService domainAssignmentService;
	
	@EJB(mappedName = "java:module/UserHandlerImpl")
	private UserHandlerLocal userHandler;
	
	
	static {
		PropertyConfigurator.configure( LOG4J_PROPERTIES );
	}

	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.debug("WebAppStartupListener.onApplicationEvent: start");
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
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
		}
		
		printDB();
	}
	
	private final void clearDomains() throws Exception {
		List<Domain> domains = domainService.selectAll();
		for(Domain d : domains)
			domainService.delete( d );
	}
	
	private final void clearRoles() throws Exception {
		List<Role> roles = roleService.selectAll();
		for(Role r : roles)
			roleService.delete( r );
	}
	
	private final void clearUsers() throws Exception {
		List<User> users = userService.selectAll();
		for(User u : users) {
			List<DomainAssignment> assignments = domainAssignmentService.selectByUserID( u.getId() );
			for(DomainAssignment da : assignments) {
				domainAssignmentService.delete( da );
			}
			userService.delete( u );
		}
	}
	
	private final void printDB() {
		LOGGER.debug("printing database..");
		List<Domain> domains = domainService.selectAll();
		List<Privilege> privileges = privilegeService.selectAll();
		List<Role> roles = roleService.selectAll();
		List<User> users = userService.selectAll();
		
		printAs(domains, "Domains: ");
		printAs(privileges, "Privileges: ");
		printAs(roles, "Roles: ");
		printUsers();
	}
	
	private final <T extends AbstractEntity> void printAs(List<T> entities, String title) {
		LOGGER.debug(title + String.format("(%d)", entities.size()));
		for(T entity : entities) 
			LOGGER.debug("\t" + entity.toString());
	}
	
	private final void printUsers() {
		List<User> users = userService.selectAll();
		LOGGER.debug("Users: " + String.format("(%d)", users.size()));
		for(User u : users)
			print(u);
	}
	
	private final void print(User user) {
		List<DomainAssignment> assignments = domainAssignmentService.selectByUserID( user.getId() );
		LOGGER.debug("\t" + user.toString());
		for(DomainAssignment da : assignments) {
			LOGGER.debug("\t\tin " + da.getDomain().toString() + " has");
			for(Role r : da.getUserRoles()) {
				LOGGER.debug("\t\t\t" + r.toString());
			}
		}
	}
	
	private final void tryReadingElement(XMLEventReader reader) throws Exception {
		XMLEvent event = reader.nextEvent();
		
		Map<String, Domain> domains = new HashMap<String, Domain>();
		Map<String, Privilege> privileges = new HashMap<String, Privilege>();
		Map<String, Role> roles = new HashMap<String, Role>();

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
			case "roles":
				tryParseRoles(roles, domains, privileges, localName, reader);
				break;
			case "users":
				tryParseUsers(roles, domains, localName, reader);
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
				
				LOGGER.debug("\t" + d.toString() + " was found");
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

				LOGGER.debug("\t" + p.toString() + " was found");
				if (isRemovable) 
					removables.add(p);
				else
					privileges.put(privilegeName, p);
			}
		}
		
		removeAll(removables, privilegeService);
		insertAll(new ArrayList<Privilege>(privileges.values()), privilegeService);
	}
	
	private final void tryParseRoles(Map<String, Role> roles, 
									 Map<String, Domain> domains, 
									 Map<String, Privilege> privileges, 
									 String localName, 
									 XMLEventReader reader) throws Exception {
		LOGGER.debug("parsing roles..");
		List<Role> removables = new ArrayList<Role>();
		
		while (canReadMore(localName, reader)) {
			XMLEvent event = reader.nextEvent();
			
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement role = event.asStartElement();
				
				String roleName = role.getAttributeByName( NAME ).getValue();
				String removable = role.getAttributeByName( REMOVABLE ).getValue();
				String domain = role.getAttributeByName( DOMAIN ).getValue();
				String parent = role.getAttributeByName( IMPORT ).getValue();
				
				Role r = roleService.selectByName(roleName);
				r = (r == null) ? new Role(roleName) : r;

				LOGGER.debug("\t" + r.toString() + " was found");
				if (Boolean.valueOf(removable)) {
					removables.add(r);
				} else {
					
					Set<Privilege> inherited = privilegesOf(parent, roles);
					for(Privilege p : inherited) {
						r.addPrivilege( p );
						LOGGER.debug("\t" + p.toString() + " was inherited and added to " + r.toString());
					}
					
					List<String> names = parsePrivilegeNames(reader);
					for(String name : names) {
						Privilege p = privilegeOf(privileges, name);
						r.addPrivilege( p );
						LOGGER.debug("\t" + p.toString() + " was parsed and added to " + r.toString());
					}
					
					roles.put(roleName, r);
					Domain d = domainOf(domains, domain);
					d.addRole( r );
					domainService.save( d );
					LOGGER.debug(r.toString() + " was added to " + d.toString());
				}
			}
		}
		removeAll(removables, roleService);
		insertAll(new ArrayList<Role>(roles.values()), roleService);
	}
	
	private final Privilege privilegeOf(Map<String, Privilege> privileges, String privilegeName) throws Exception {
		Privilege p = privileges.get( privilegeName );
		p = (p == null) ? privilegeService.selectByName( privilegeName ) : p;
		
		if (p == null)
			throw new RuntimeException("Privilege " + privilegeName + " does not exist!");
		return p;
	}
	
	private final Domain domainOf(Map<String, Domain> domains, String domainName) throws Exception {
		Domain domain = domains.get(domainName);
		domain = (domain == null) ? domainService.selectByName(domainName) : domain;

		if (domain == null)
			throw new RuntimeException("Domain " + domainName + " does not exist!");
		return domain;
	}
	
	private final Set<Privilege> privilegesOf(String roleName, Map<String, Role> roles) throws Exception {
		if (roleName == null || roleName.isEmpty())
			return new HashSet<Privilege>(0);
		Role role = roles.get( roleName );
		if (role == null)
			throw new RuntimeException("XML semantic is invalid. Only previously defined roles can be imported!");
		return role.getPrivileges();
	}
	
	private final List<String> parsePrivilegeNames(XMLEventReader reader) throws Exception {
		List<String> names = new ArrayList<String>(5);
		
		/* while we do not reach the end tag of the current role.. */
		while (canReadMore("role", reader)) {
			XMLEvent event = reader.nextEvent();
			
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement privilege = event.asStartElement();
				String removable = privilege.getAttributeByName( REMOVABLE ).getValue();
				
				if (!Boolean.valueOf( removable ))
					names.add( privilege.getAttributeByName(NAME).getValue() );
			}
		}
		return names;
	}

	private final void tryParseUsers(Map<String, Role> roles, Map<String, Domain> domains, String localName, XMLEventReader reader) throws Exception {
		LOGGER.debug("parsing users..");
		List<User> removables = new ArrayList<>(4);
		List<User> insertables = new ArrayList<>(4);
		List<DomainAssignment> assignments = new ArrayList<>(4);
		
		while (canReadMore(localName, reader)) {
			XMLEvent event = reader.nextEvent();
			
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement user = event.asStartElement();
				
				String username = user.getAttributeByName( NAME ).getValue();
				String password = user.getAttributeByName( PASSWORD ).getValue();
				String email = user.getAttributeByName( EMAIL ).getValue();
				String desc = user.getAttributeByName( DESCRIPTION ).getValue();
				String removable = user.getAttributeByName( REMOVABLE ).getValue();
				
				User u = userService.selectByName(username);
				u = (u == null) ? new User(username, password, email, desc) : u;
				
				LOGGER.debug(u.toString() + " was found");
				if (Boolean.valueOf( removable ))
					removables.add( u );
				else {
					Map<String, String> names = parseRoleNames(username, reader);
					
					for(String name : names.keySet()) {
						Domain domain = domainOf(domains, names.get(name));
						Role role = domain.roleOf( name );
						
						assignRoleTo(u, domain, role, assignments);
					}
					insertables.add( u );
				}
			}
		}
		removeAll(removables, userService);
		insertAll(insertables, userService);
		insertAll(assignments, domainAssignmentService);
	}
	
	private final void assignRoleTo(User user, Domain domain, Role role, List<DomainAssignment> assignments) throws Exception {
		DomainAssignment da = domainAssignmentService.selectByDomainFor(user.getUsername(), domain.getName());
		
		if (da == null)
			da = new DomainAssignment(user, domain, role);
		else if (!da.getUserRoles().contains( role ))
			da.addUserRole( role );
		assignments.add( da );
		LOGGER.debug(user.toString() + " was added to " + domain.toString() + " as " + role.toString());
	}
	
	private final Map<String, String> parseRoleNames(String username, XMLEventReader reader) throws Exception {
		Map<String, String> names = new HashMap<>(1);

		/* reading roles until the terminating </user> tag.. */
		while (canReadMore("user", reader)) {
			XMLEvent event = reader.nextEvent();
			
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement role = event.asStartElement();
				
				String removable = role.getAttributeByName( REMOVABLE ).getValue();
				String name = role.getAttributeByName(NAME).getValue();
				String domain = role.getAttributeByName( DOMAIN ).getValue();
				if (!Boolean.valueOf( removable ))
					names.put(name, domain);
				else {
					DomainAssignment da = domainAssignmentService.selectByDomainFor(username, domain);
					Role r = roleService.selectByName(name);
					da.removeUserRole( r );
					domainAssignmentService.save( da );
				}
			}
		}
		return names;
	}
	
	private final Role roleOf(Map<String, Role> roles, String roleName) throws Exception {
		Role role = roles.get(roleName);
		role = (role == null) ? roleService.selectByName(roleName) : role;

		if (role == null)
			throw new RuntimeException("Role " + roleName + " does not exist!");
		return role;
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
