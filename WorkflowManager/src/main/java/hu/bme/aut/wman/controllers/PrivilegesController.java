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

	public static final String ASYNC_SELECT  = "/async/privileges";
	
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;
	
	@RequestMapping(value = ASYNC_SELECT, method = RequestMethod.GET)
	public String listPrivilegesAsync(Model model) {
		model.addAttribute("elements", privilegeService.selectAll());
		return "fragments/dnd_elements";
	}
}
