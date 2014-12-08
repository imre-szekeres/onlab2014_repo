/**
 * DetailedAccessDeniedExceptionHandler.java
 */
package hu.bme.aut.wman.security;

import static hu.bme.aut.wman.utils.StringUtils.asString;
import hu.bme.aut.wman.controllers.LoginController;
import hu.bme.aut.wman.exceptions.DetailedAccessDeniedException;
import hu.bme.aut.wman.exceptions.MessagedAccessDeniedException;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class DetailedAccessDeniedHandler extends AccessDeniedHandlerImpl {

	private static final Logger LOGGER;

	static {
		LOGGER = Logger.getLogger( DetailedAccessDeniedHandler.class );
	}

	public DetailedAccessDeniedHandler() {
		super();
		super.setErrorPage(LoginController.ACCESS_DENIED);
	}

	/**
	 * Extends the basic behavior of the super class by setting the <code>HttpServletRequest</code> attributes in case a 
	 * <code>DetailedAccessDeniedException</code> was delegated to be handled.
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) 
					throws IOException, ServletException {
		if (exception instanceof DetailedAccessDeniedException)
			handleDetailed(request, (DetailedAccessDeniedException) exception);

		else if (exception instanceof MessagedAccessDeniedException)
			handleMessaged(request, (MessagedAccessDeniedException) exception);
		super.handle(request, response, exception);
	}

	/**
	 * Handles any <code>DetailedAccessDeniedException</code> thrown by the <code>SecurityFilterChain</code>, fetches the required <code>ConfigAttribute</code>s
	 * or <code>GrantedAuthority</code>s (a.k.a. <code>Privilege</code>s) to obtain access to the given page then redirects to the access denied URL, 
	 * and sets it as a <code>HttpServletRequest</code> attribute by the key "authoritiesRequired".
	 * 
	 * @param request
	 * @param exception
	 * */
	@SuppressWarnings("unchecked")
	private void handleDetailed(HttpServletRequest request, DetailedAccessDeniedException exception) {
		request.setAttribute("authoritiesRequired", exception.getRequiredAuthorities());
		LOGGER.info(String.format( "Access Denied due to lacking %s", 
				                   asString((List<? extends ConfigAttribute>) request.getAttribute("authoritiesRequired"))) );
	}

	/**
	 * Handles any <code>MessagedAccessDeniedException</code> thrown by the <code>SecurityFilterChain</code>, fetches the message set by the thrower 
	 * and sets it as a <code>HttpServletRequest</code> attribute by the key "denialMessage".
	 * 
	 * @param request
	 * @param exception
	 * */
	private void handleMessaged(HttpServletRequest request, MessagedAccessDeniedException exception) {
		request.setAttribute("denialMessage", exception.getMessage());
		LOGGER.info(String.format( "Access Denied due to %s", (String) request.getAttribute("denialMessage") ));
	}
}
