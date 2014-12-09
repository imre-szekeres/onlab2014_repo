/**
 * AuthenticationService.java
 */
package hu.bme.aut.wman.service;

import static hu.bme.aut.wman.utils.StringUtils.isEmpty;

import java.util.List;

import javax.ejb.EJB;

import org.springframework.security.core.GrantedAuthority;
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

	/**
	 * Supplies the <code>UserDetails</code> of the <code>User</code> specified by its username.
	 * 
	 * @param username
	 * @return an implementation of the {@link UserDetails} interface
	 * @throws {@link UsernameNotFoundException}
	 * */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String password = userService.selectPasswordOf(username);
		if (isEmpty( password ))
			throw new UsernameNotFoundException(username);
		return new User(username, password, privilegeService.privilegesOf(username));
	}

	/**
	 * Retrieves the <code>GrantedAuthority</code>s owned by the <code>User</code>.
	 * 
	 * @param username
	 * @param privilegeService
	 * @return the {@link List} of {@link GrantedAuthorities} the given {@link User} has
	 * */
	public static List<? extends GrantedAuthority> authoritiesOf(String username, PrivilegeService privilegeService) {
		return privilegeService.privilegesOf(username);
	}
}
