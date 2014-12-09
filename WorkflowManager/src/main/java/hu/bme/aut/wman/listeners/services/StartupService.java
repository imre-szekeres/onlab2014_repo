/**
 * StartupService.java
 */
package hu.bme.aut.wman.listeners.services;

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

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@SuppressWarnings("all")
@LocalBean
@Stateless
public class StartupService {

	private static final Logger LOGGER = Logger.getLogger( StartupService.class );

	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;
	@Inject
	private PrivilegeService privilegeService;
	@Inject
	private DomainService domainService;
	@Inject
	private DomainAssignmentService domainAssignmentService;
	
	@Autowired
	@Qualifier("bcryptEncoder")
	private PasswordEncoder encoder;

	
	private final QName NAME;
	private final QName REMOVABLE;
	private final QName DOMAIN;
	private final QName IMPORT;

	private final QName PASSWORD;
	private final QName EMAIL;
	private final QName DESCRIPTION;
	
	{
		NAME = new QName("name");
		REMOVABLE = new QName("removable");
		DOMAIN = new QName("domain");
		IMPORT = new QName("import");
		
		PASSWORD = new QName("password");
		EMAIL = new QName(User.PR_EMAIL);
		DESCRIPTION = new QName(User.PR_DESCRIPTION);
	}

	/**
	 * Sets the <code>PasswordEncoder</code> to a <code>BCryptPasswordEncoder</code> in case it was found
	 * as null.
	 * */
	@PostConstruct
	public void initService() {
		encoder = (encoder == null) ? new BCryptPasswordEncoder() : encoder;
	}
	
	public void setupWebapp(String xmlPath) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = null;
		InputStream resource = null;
		try {

			resource = new FileInputStream(xmlPath);
			eventReader = inputFactory.createXMLEventReader(resource);
			LOGGER.debug("processing " + xmlPath);
			
			while (eventReader.hasNext()) {
				tryReadingElement( eventReader );
			}
		} catch(Exception e) {
			LOGGER.fatal("WebAppStartupListener -- parsing " + xmlPath + " threw ", e);
		} finally {
			tryClose(eventReader, xmlPath);
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
		List<Privilege> privileges = privilegeService.selectAll();
		List<Role> roles = roleService.selectAll();
		
		printDomains();
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
	
	private final void printDomains() {
		List<Domain> domains = domainService.selectAll();
		LOGGER.debug("Domains: " + String.format("(%d)", domains.size()));
		for(Domain d : domains)
			print(d);
	}
	
	private final void print(Domain domain) {
		List<Role> roles = domain.getRoles();
		LOGGER.debug("\t" + domain.toString());
		for(Role r : roles) {
			LOGGER.debug("\t\t" + r.toString());
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
	
	private final void tryClose(XMLEventReader reader, String config) {
		if (reader != null)
			try {
				reader.close(); 
			} catch(Exception e) {
				LOGGER.error(" while closing XMLEventReader for " + config, e);
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
				else {
					domainService.save( d );
					d = domainService.selectByName(d.getName());
					LOGGER.info(d.toString() + " was inserted..");
					domains.put(domainName, d);
				}
			}
		}
		removeAll(removables, domainService);
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
				Domain d = domainOf(domains, domain);

				LOGGER.debug("\t" + r.toString() + " was found");
				if (Boolean.valueOf(removable)) {
					removables.add(r);
					d.removeRole( r );
					domainService.save( d );
					LOGGER.debug(r.toString() + " was removed from " + d.toString());
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
					
					roleService.save( r );
					LOGGER.debug(r.toString() + " was inserted");
					
					roles.put(roleName, r);
					if (!d.getRoles().contains( r )) {
						d.addRole( r );
						domainService.save( d );
						LOGGER.debug(r.toString() + " was was added to " + d.toString());
					}
				}
			}
		}
		removeAll(removables, roleService);
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
				String encoded = encoder.encode( password );
				u = (u == null) ? new User(username, encoded, email, desc) : u;
				u.setPassword( encoded );
				
				LOGGER.info(String.format("Password was set to %s for %s..", encoded, username)); /* TODO: remove */
				
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

	/**
	 * Only for testing purposes!
	 * 
	 * @param userService
	 * */
	public void set(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Only for testing purposes!
	 * 
	 * @param roleService
	 * */
	public void set(RoleService roleService) {
		this.roleService = roleService;
	}

	/**
	 * Only for testing purposes!
	 * 
	 * @param privilegeService
	 * */
	public void set(PrivilegeService privilegeService) {
		this.privilegeService = privilegeService;
	}

	/**
	 * Only for testing purposes!
	 * 
	 * @param domainService
	 * */
	public void set(DomainService domainService) {
		this.domainService = domainService;
	}

	/**
	 * Only for testing purposes!
	 * 
	 * @param domainAssignmentService
	 * */
	public void set(DomainAssignmentService domainAssignmentService) {
		this.domainAssignmentService = domainAssignmentService;
	}

	/**
	 * Only for testing purposes!
	 * 
	 * @param domainAssignmentService
	 * */
	public void set(PasswordEncoder encoder) {
		this.encoder = encoder;
	}
}
