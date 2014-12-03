package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.service.GraphNodeService;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @version "%I%, %G%"
 */
@Controller
public class StateGraphController extends AbstractController {

	public static final String SAVE_NODE = "/save/node";

	@EJB(mappedName = "java:module/GraphNodeService")
	private GraphNodeService graphNodeService;

	@RequestMapping(value = SAVE_NODE, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void saveNode(HttpServletRequest request, RedirectAttributes redirectAttributes) {

		Long nodeId = Long.parseLong(request.getParameter("nodeId"));
		Integer X = Integer.parseInt(request.getParameter("newX"));
		Integer Y = Integer.parseInt(request.getParameter("newY"));

		GraphNode node = graphNodeService.selectById(nodeId);
		node.setX(X);
		node.setY(Y);
		graphNodeService.save(node);
	}

}