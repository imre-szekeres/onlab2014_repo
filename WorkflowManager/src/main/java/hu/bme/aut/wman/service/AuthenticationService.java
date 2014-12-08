/**
 * AuthenticationService.java
 */
package hu.bme.aut.wman.service;

import static hu.bme.aut.wman.utils.StringUtils.isEmpty;

import javax.ejb.EJB;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class AuthenticationService implements UserDetailsService {

	@EJB(mappedName = "java:module/UserService")
	private UserService userService;
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String password = userService.selectPasswordOf(username);
		if (isEmpty( password ))
			throw new UsernameNotFoundException(username);
		return new User(username, password, privilegeService.privilegesOf(username));
	}
}
