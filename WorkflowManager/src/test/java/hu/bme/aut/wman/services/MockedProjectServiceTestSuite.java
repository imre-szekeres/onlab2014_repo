/**
 * MockedPrivilegeServiceTestSuite.java
 */
package hu.bme.aut.wman.services;

import static org.mockito.Mockito.mock;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.User;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.WorkflowService;
import hu.bme.aut.wman.view.objects.NewProjectVO;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MockedProjectServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( MockedProjectServiceTestSuite.class );

	private final Long projectMockId = new Long(1024);
	private ProjectService projectService;
	private EntityManager entityManagerMock;

	private Project spyProject;
	private Workflow workflowMock;
	private User ownerMock;

	public MockedProjectServiceTestSuite() {
	}

	@Before
	public void initContext() {
		projectService = new ProjectService();
		entityManagerMock = mock(EntityManager.class);
		WorkflowService workflowService = mock(WorkflowService.class);
		projectService.setEntityManager( entityManagerMock );
		projectService.setTestWorkflowService(workflowService);

		ownerMock = Mockito.mock(User.class);
		workflowMock = Mockito.mock(Workflow.class);
		Project project = new Project("Test project", "Description of testproject", workflowMock, ownerMock);
		spyProject = Mockito.spy(project);

		Mockito.when(entityManagerMock.merge(spyProject)).thenReturn( spyProject );
		Mockito.when(spyProject.getId()).thenReturn(projectMockId);
	}

	@Test
	public void testSaveNew() {
		GraphNode nodeMock = Mockito.mock(GraphNode.class);
		Mockito.when(projectService.getTestWorkflowService().selectById(workflowMock.getId())).thenReturn( workflowMock );
		NewProjectVO newProjectVO = new NewProjectVO();
		newProjectVO.setName("Project");
		newProjectVO.setDescription("Project description test");
		newProjectVO.setWorkflowId(workflowMock.getId());
		try {
			projectService.save( newProjectVO );
			Mockito.verify(entityManagerMock, Mockito.times(1)).persist(Mockito.anyObject() );
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testClose() {
		Mockito.when(spyProject.isActive()).thenReturn(true);
		try {
			projectService.close( spyProject);
			Mockito.verify(spyProject, Mockito.times(1)).setActive(false);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testOpen() {
		Mockito.when(spyProject.isActive()).thenReturn(false);
		try {
			projectService.reopen( spyProject);
			Mockito.verify(spyProject, Mockito.times(1)).setActive(true);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testSetOwner() {
		Mockito.when(spyProject.getOwner()).thenReturn(new User("Owner", "Pass", "email", "Description"));
		User newOwner = new User();
		try {
			projectService.setOwnerOnProject( spyProject, newOwner);
			Mockito.verify(spyProject, Mockito.times(1)).setOwner(newOwner);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
}
