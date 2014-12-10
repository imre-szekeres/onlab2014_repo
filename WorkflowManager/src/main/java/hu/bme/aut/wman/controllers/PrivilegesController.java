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
 * Responsible for handling requests regarding the CRUD operations executed on the <code>Privilege</code> 
 * instances. 
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@Controller
public class PrivilegesController {

	public static final String ROOT_URL  = "/privileges";
	
	@EJB(mappedName = "java:module/PrivilegeService")
	private PrivilegeService privilegeService;

	/**
	 * Returns the available <code>Privilege</code>s as a Drag-n-Droppable <code>List</code>
	 * rendered by the dnd_elements view.
	 * <p>
	 * For that, it sets the required model attributes.
	 * 
	 * @param model
	 * */
	@RequestMapping(value = ROOT_URL, method = RequestMethod.GET)
	public String listPrivileges(Model model) {
		model.addAttribute("elements", privilegeService.selectAll());
		model.addAttribute("elementBodyClass", "privilege-body");
		return "fragments/dnd_elements";
	}
}
