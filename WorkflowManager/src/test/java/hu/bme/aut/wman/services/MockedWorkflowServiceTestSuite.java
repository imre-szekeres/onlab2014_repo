/**
 * MockedPrivilegeServiceTestSuite.java
 */
package hu.bme.aut.wman.services;

import static org.mockito.Mockito.mock;
import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.model.graph.StateGraph;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.StateGraphService;
import hu.bme.aut.wman.service.WorkflowService;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

//@RunWith(Parameterized.class)
public class MockedWorkflowServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( MockedWorkflowServiceTestSuite.class );

	private WorkflowService workflowService;
	private EntityManager entityManagerMock;

	private Workflow spyWorkflow;

	private Domain domainMock;

	public MockedWorkflowServiceTestSuite() {
	}


	//	@Parameters
	//	public static final Collection<Object[]> privilegeNames() {
	//		return Arrays.asList(new Object[][] {
	//				{ "View User", "Assign Role" },
	//				{ "Create Role", "View Privilege" }
	//		});
	//	}

	@Before
	public void initContext() {
		workflowService = new WorkflowService();
		entityManagerMock = mock(EntityManager.class);
		DomainService domainService = mock(DomainService.class);
		StateGraphService graphService = mock(StateGraphService.class);
		ProjectService projectService = mock(ProjectService.class);
		workflowService.setEntityManager( entityManagerMock );
		workflowService.setTestDomainService(domainService);
		workflowService.setTestGraphService(graphService);
		workflowService.setTestProjectService(projectService);

		domainMock = mock(Domain.class);
		Workflow workflow = new Workflow("Test WF", "Description of the test WF.", domainMock);
		spyWorkflow = Mockito.spy(workflow);

		Mockito.when(entityManagerMock.merge(spyWorkflow)).thenReturn( spyWorkflow );
	}

	@Test
	public void testSave() {
		Mockito.when(workflowService.getTestDomainService().selectByName(Mockito.anyString())).thenReturn( domainMock );
		Mockito.when(spyWorkflow.getId()).thenReturn(new Long(1024));

		try {
			workflowService.save( spyWorkflow );
			Mockito.verify(entityManagerMock, Mockito.times(1)).merge( spyWorkflow );
			Mockito.verify(workflowService.getTestGraphService(), Mockito.times(1)).save((StateGraph) Mockito.any());
			LOGGER.info("Workflow was created");
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testDeleteWithActiveProject() {
		Project testProject1 = Mockito.mock(Project.class);
		Project testProject2 = Mockito.mock(Project.class);
		Project testProject3 = Mockito.mock(Project.class);

		Mockito.when(workflowService.getTestProjectService().selectAllByWorkflowName(spyWorkflow.getName())).thenReturn(Lists.newArrayList(testProject1,testProject2,testProject3));
		Mockito.when(testProject1.getActive()).thenReturn(false);
		Mockito.when(testProject2.getActive()).thenReturn(true);
		Mockito.when(testProject3.getActive()).thenReturn(false);

		try {
			workflowService.delete(spyWorkflow);
		} catch (EntityNotDeletableException e) {
			// Expected
			Mockito.verify(entityManagerMock, Mockito.never()).remove(spyWorkflow);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testDeleteWithoutActiveProject() {
		Project testProject1 = Mockito.mock(Project.class);
		Project testProject2 = Mockito.mock(Project.class);
		Project testProject3 = Mockito.mock(Project.class);

		Mockito.when(workflowService.getTestProjectService().selectAllByWorkflowName(spyWorkflow.getName())).thenReturn(Lists.newArrayList(testProject1,testProject2,testProject3));
		Mockito.when(testProject1.getActive()).thenReturn(false);
		Mockito.when(testProject2.getActive()).thenReturn(false);
		Mockito.when(testProject3.getActive()).thenReturn(false);

		try {
			workflowService.delete(spyWorkflow);
			Mockito.verify(entityManagerMock, Mockito.times(1)).remove(spyWorkflow);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testVerifyWithoutStates() {
		Mockito.when(spyWorkflow.getStates()).thenReturn(new ArrayList<State>());

		Assert.assertFalse(workflowService.verify(spyWorkflow));
	}

	@Test
	public void testVerifyWithoutInitialState() {
		State stateMock = Mockito.mock(State.class);
		Mockito.when(stateMock.isInitial()).thenReturn(false);
		Mockito.when(spyWorkflow.getStates()).thenReturn(Lists.newArrayList(stateMock));

		try {
			workflowService.verify(spyWorkflow);
			Assert.fail("IllegalArgumentException was expected");
		} catch(IllegalArgumentException e) {
			// Expected
		}
	}

	@Test
	public void testVerify() {
		State stateMock = Mockito.mock(State.class);
		State stateMock2 = Mockito.mock(State.class);
		Mockito.when(stateMock.isInitial()).thenReturn(false);
		Mockito.when(stateMock2.isInitial()).thenReturn(true);
		spyWorkflow.setStates(Lists.newArrayList(stateMock,stateMock2));

		Assert.assertTrue(workflowService.verify(spyWorkflow));
	}
}
