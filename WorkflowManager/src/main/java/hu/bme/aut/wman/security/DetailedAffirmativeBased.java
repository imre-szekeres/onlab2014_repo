/**
 * DetailedAffirmativeBased.java
 */
package hu.bme.aut.wman.security;

import hu.bme.aut.wman.exceptions.DetailedAccessDeniedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class DetailedAffirmativeBased extends AffirmativeBased {

	/**
	 * Sets up the default strategy for the AffirmativeBased AccessDecisionManager.
	 * */
	public DetailedAffirmativeBased() {
		this(Arrays.asList(new AccessDecisionVoter[] {new RoleVoter(), new AuthenticatedVoter()}));
	}

	public DetailedAffirmativeBased(@SuppressWarnings("rawtypes") List<AccessDecisionVoter> voters) {
		super( voters );
	}

	/**
	 * Executes the method defined by the superclass and catches any <code>AccessDeniedException</code> that is thrown and 
	 * wraps it into a <code>DetailedAccessDeniedException</code> with the "reasons" supplied.
	 * 
	 * @param authentication
	 * @param object
	 * @param configAttributes
	 * 
	 * @throws {@link AccessDeniedException} which in fact is an instance of a {@link DetailedAccessDeniedException}
	 * */
	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) 
					throws AccessDeniedException {
		try {
			super.decide(authentication, object, configAttributes);
		} catch(AccessDeniedException e) {
			throw new DetailedAccessDeniedException(String.format("Access denied for %s", object), configAttributes);
		}
	}
}
