/**
 * RolesController.java
 */
package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.view.objects.transfer.RoleTransferObject;

import org.apache.log4j.Logger;
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
public class RolesController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger( RolesController.class );
	
	public static final String ROLES_ROOT = "/roles";
	public static final String ROLES_CREATE = ROLES_ROOT + "/create";
	

	@SuppressWarnings("deprecation")
	@RequestMapping(value = ROLES_CREATE, method = RequestMethod.POST)
	public String createRole(@ModelAttribute("newRole")RoleTransferObject newRole, Model model) {
		LOGGER.debug("createRole() reached");
		LOGGER.debug(newRole.toString() + " was sent to RolesController.createRole()");
		
		model.addAttribute("message", "createRole reached");
		return redirectTo(AdminViewController.ROLES_ROOT);
	}
}
