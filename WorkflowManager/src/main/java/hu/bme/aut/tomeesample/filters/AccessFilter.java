/**
 * AccessFilter.java
 * */
package hu.bme.aut.tomeesample.filters;

import hu.bme.aut.tomeesample.model.User;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class AccessFilter
 *
 * @author Imre Szekeres
 * @version "%I%. %G%"
 */
@WebFilter(dispatcherTypes = {
		DispatcherType.REQUEST,
		DispatcherType.FORWARD,
		DispatcherType.INCLUDE
}, urlPatterns = { "/*" })
public class AccessFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public AccessFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq = ((HttpServletRequest) request);
		String uri = hreq.getRequestURI();
		chain.doFilter(request, response);

		HttpSession session = hreq.getSession();
		try {
			if (uri.contains("/faces/fragments/")) {
				String target = session.getAttribute("subject") == null ? "/faces/login.xhtml?faces-redirect=true" :
						"/faces/auth/index.xhtml?faces-redirect=true";
				hreq.getRequestDispatcher(target).forward(request, response);
			}
			if (session.getAttribute("subject") == null && uri.contains("/faces/auth/"))
				hreq.getRequestDispatcher("/faces/login.xhtml?faces-redirect=true").forward(request, response);
			else if ((uri.contains("/faces/auth/man/") && !((User) session.getAttribute("subject")).hasRole("manager")) ||
					(uri.contains("/faces/auth/admin/") && !((User) session.getAttribute("subject")).hasRole("administrator")))
				hreq.getRequestDispatcher("/faces/auth/index.xhtml?faces-redirect=true").forward(request, response);
		} catch (Exception e) {
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
