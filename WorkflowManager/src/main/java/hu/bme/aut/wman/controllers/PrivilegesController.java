/**
 * PrivilegesController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.service.PrivilegeService;

import javax.ejb.EJB;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class PrivilegesController {

	public static final String ROOT_URL  = "/privileges";
	
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	@RequestMapping(value = ROOT_URL, method = RequestMethod.GET)
	public String listPrivileges(Model model) {
		model.addAttribute("elements", privilegeService.selectAll());
		model.addAttribute("elementBodyClass", "privilege-body");
		return "fragments/dnd_elements";
	}
}
