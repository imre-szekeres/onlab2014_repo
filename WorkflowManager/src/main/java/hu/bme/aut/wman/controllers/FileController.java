package hu.bme.aut.wman.controllers;

import static java.lang.String.format;
import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.BlobFile;
import hu.bme.aut.wman.model.HistoryEntryEventType;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.security.SecurityToken;
import hu.bme.aut.wman.service.BlobFileService;
import hu.bme.aut.wman.service.HistoryEntryService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.UserService;
import hu.bme.aut.wman.view.Messages.Severity;
import hu.bme.aut.wman.view.objects.FileUploadVO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController extends AbstractController{

	public static final String UPLOAD_FILE_ON_PROJECT = "/project/upload";
	public static final String DOWNLOAD_FILE = "/download/file";
	public static final String DELETE_FILE = "/delete/file";

	private static final int BUFFER_SIZE = 4096;

	@EJB(mappedName = "java:module/ProjectService")
	private ProjectService projectService;
	@EJB(mappedName = "java:module/BlobFileService")
	private BlobFileService blobFileService;
	@EJB(mappedName="java:module/HistoryEntryService")
	private HistoryEntryService historyService;
	@EJB(mappedName="java:module/UserService")
	private UserService userService;

	@RequestMapping(value = UPLOAD_FILE_ON_PROJECT, method = RequestMethod.POST)
	@PreAuthorize("hasRole('View Project')")
	public ModelAndView upload(@RequestParam("id") Long projectId, @ModelAttribute(value="fileUploadVO") FileUploadVO fileVO, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		String contentType = fileVO.getFile().getContentType();
		byte[] bytes = fileVO.getFile().getBytes();
		String originalFilename = fileVO.getFile().getOriginalFilename();
		Project project = projectService.selectById(projectId);

		BlobFile blobFile = new BlobFile(originalFilename, contentType, bytes, project);
		project.getFiles().add(blobFile);
		projectService.save(project);

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.UPLOADED_FILE, "uploaded a file: " + originalFilename, projectId);

		ModelAndView view = redirectToFrame("project", redirectAttributes);
		view.setViewName("redirect:/project?id=" + projectId);
		return view;
	}

	@RequestMapping(value = DOWNLOAD_FILE, method = RequestMethod.GET)
	@PreAuthorize("hasRole('View Project')")
	public void download(@RequestParam("id") Long fileId, Model model, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {


		BlobFile file = blobFileService.selectById(fileId);
		byte[] content = file.getContent();

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.DOWNLOADED_FILE, "downloaded a file: " + file.getFileName(), file.getProject().getId());

		response.setContentType(file.getContentType());
		response.setContentLength(content.length);

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", file.getFileName());
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		OutputStream outStream = response.getOutputStream();
		outStream.write(content, 0, content.length);

		outStream.close();

	}

	@RequestMapping(value = DELETE_FILE, method = RequestMethod.GET)
	@PreAuthorize("hasRole('View Project')")
	public ModelAndView deleteFile(@RequestParam("id") Long fileId,@RequestParam("projectId") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		BlobFile file = blobFileService.selectById(fileId);

		try {
			blobFileService.delete(file);
		} catch (EntityNotDeletableException e) {
			flash(format("The Workflow is not deletable due to: %s", e.getMessage()), Severity.ERROR, model);
		}

		User user = userService.selectById(((SecurityToken) request.getSession().getAttribute("subject")).getUserID());
		historyService.log(user.getUsername(), new Date(), HistoryEntryEventType.REMOVED_FILE, "deleted a file: " + file.getFileName(), projectId);

		ModelAndView view = redirectToFrame(ProjectViewController.PROJECT, redirectAttributes);
		view.setViewName(view.getViewName() + "?id=" + projectId);
		return view;
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}
}
