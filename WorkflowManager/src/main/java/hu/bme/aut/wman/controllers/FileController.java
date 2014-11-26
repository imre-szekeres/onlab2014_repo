package hu.bme.aut.wman.controllers;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.BlobFile;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.service.BlobFileService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.view.objects.ErrorMessageVO;
import hu.bme.aut.wman.view.objects.FileUploadVO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	@RequestMapping(value = UPLOAD_FILE_ON_PROJECT, method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam("id") Long projectId, @ModelAttribute(value="fileUploadVO") FileUploadVO fileVO, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

		// TODO check if the user is on the project

		String contentType = fileVO.getFile().getContentType();
		byte[] bytes = fileVO.getFile().getBytes();
		String originalFilename = fileVO.getFile().getOriginalFilename();
		Project project = projectService.selectById(projectId);

		BlobFile blobFile = new BlobFile(originalFilename, contentType, bytes, project);
		project.getFiles().add(blobFile);
		projectService.save(project);

		ModelAndView view = redirectToFrame("project", redirectAttributes);
		view.setViewName("redirect:/project?id=" + projectId);
		return view;
	}

	@RequestMapping(value = DOWNLOAD_FILE, method = RequestMethod.GET)
	public void download(@RequestParam("id") Long fileId, Model model, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {

		BlobFile file = blobFileService.selectById(fileId);
		byte[] content = file.getContent();

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
	public ModelAndView deleteWorkflow(@RequestParam("id") Long fileId,@RequestParam("projectId") Long projectId, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		List<ErrorMessageVO> errors = new ArrayList<ErrorMessageVO>();

		try {
			blobFileService.deleteById(fileId);
		} catch (EntityNotDeletableException e) {
			errors.add(new ErrorMessageVO("The workflow is not deletable.", e.getMessage()));
		}

		ModelAndView view = redirectToFrame(ProjectViewController.PROJECT, errors, redirectAttributes);
		view.setViewName(view.getViewName() + "?id=" + projectId);
		return view;
	}

	@Override
	public Map<String, String> getNavigationTabs() {
		return new HashMap<String, String>();
	}
}
