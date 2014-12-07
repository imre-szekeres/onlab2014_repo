/**
 * AuthorizationService.java
 */
package hu.bme.aut.wman.service;

import static hu.bme.aut.wman.utils.StringUtils.asString;
import hu.bme.aut.wman.model.Domain;

import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;

/**
 * Responsible for checking the owned authorities of a <code>User</code>.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class AuthorizationService {

	/**
	 * Determines whether the given <code>User</code> has all the required authorities specified by the <code>List</code> of <code>String</code>s
	 * passed as argument, in the given <code>Domain</code>.
	 * 
	 * @param userID
	 * @param authorities
	 * @param domain
	 * @param domainService
	 * @throws {@link AccessDeniedException}
	 * */
	public static final void hasAuthority(Long userID, Collection<? extends String> authorities, String domain, DomainService domainService) 
									throws AccessDeniedException {
		if (!domainService.hasPrivileges(userID, domain, authorities))
			throw new AccessDeniedException(String.format("User is does not own %s in %s!", asString(authorities), domain));
	}

	/**
	 * TODO:
	 * */
	public static final void hasAuthority(Long userID, Collection<? extends String> authorities, Collection<? extends String> domains, DomainService domainService) 
			throws AccessDeniedException {

	}
	
	/**
	 * TODO:
	 * */
	public static final void hasAuthority(Long userID, Collection<? extends String> authorities, Domain domain, DomainService domainService) 
			throws AccessDeniedException {
		hasAuthority(userID, authorities, domain.getName(), domainService);
	}
}
