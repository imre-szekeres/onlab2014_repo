/**
 * DomainsController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Role;
import hu.bme.aut.wman.service.DomainService;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class DomainsController extends AbstractController {

	public static final String ROOT_URL = "/domains";
	public static final String CREATE = ROOT_URL + "/create";
	
	@EJB(mappedName = "java:module/DomainService")
	private DomainService domainService;
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = CREATE, method = RequestMethod.POST)
	public String createDomain(@ModelAttribute("newDomain") Domain newDomain, Model model) {
		Map<String, String> errors = domainService.validate( newDomain );
		
		if (errors.isEmpty()) {
			List<Role> defaults = domainService.selectByName(DomainService.DEFAULT_DOMAIN).getRoles();
			
			for(Role role : defaults)
				newDomain.addRole(role);
			
			domainService.save( newDomain );
		} else {
			model.addAttribute("errorMessages", errors);
		}
		return redirectTo(AdminViewController.DOMAINS);
	}
}
