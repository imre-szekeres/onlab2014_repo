/**
 * RoleManagerTestSuite.java
 */
package hu.bme.aut.wman.integration;

import hu.bme.aut.wman.exceptions.EntityNotDeletableException;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.Project;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.model.graph.StateGraph;
import hu.bme.aut.wman.service.DomainService;
import hu.bme.aut.wman.service.ProjectService;
import hu.bme.aut.wman.service.StateGraphService;
import hu.bme.aut.wman.service.StateService;
import hu.bme.aut.wman.service.WorkflowService;

import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class WorkflowServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( WorkflowServiceTestSuite.class );

	private WorkflowService workflowService;

	private DomainService spyDomainService;
	private StateGraphService spyGraphService;
	private ProjectService spyProjectService;
	private StateService spyStateService;

	private EntityManager entityManagerMock;

	private Domain spyDomain;
	private Workflow spyWorkflow;


	@Before
	public void initContext() {
		workflowService = new WorkflowService();

		spyDomainService = Mockito.spy(new DomainService());
		spyGraphService = Mockito.spy(new StateGraphService());
		spyProjectService = Mockito.spy(new ProjectService());
		spyStateService = Mockito.spy(new StateService());
		entityManagerMock = Mockito.mock(EntityManager.class);

		workflowService.setTestDomainService(spyDomainService);
		workflowService.setTestGraphService(spyGraphService);
		workflowService.setTestProjectService(spyProjectService);
		workflowService.setTestStateService(spyStateService);
		workflowService.setEntityManager(entityManagerMock);

		spyDomainService.setEntityManager(entityManagerMock);
		spyGraphService.setEntityManager(entityManagerMock);
		spyProjectService.setEntityManager(entityManagerMock);
		spyStateService.setEntityManager(entityManagerMock);

		spyDomain = Mockito.spy(new Domain("Test Domain"));
		Workflow workflow = new Workflow("Test WF", "Description of the test WF.", spyDomain);
		workflow.setStates(Workflow.getBasicStates());
		spyWorkflow = Mockito.spy(workflow);
		Mockito.when(entityManagerMock.merge(spyWorkflow)).thenReturn(spyWorkflow);
	}

	@Test
	public void testSave() {
		Set workflows = Mockito.mock(Set.class);
		Mockito.when(spyDomain.getWorkflows()).thenReturn(workflows);

		Mockito.doReturn(new Long(1024)).when(spyDomain).getId();
		Mockito.doReturn(spyDomain).when(spyDomainService).selectByName("Test Domain");

		try {
			workflowService.save(spyWorkflow);
			Mockito.verify(workflows, Mockito.times(1)).add(spyWorkflow);
			Mockito.verify(entityManagerMock, Mockito.times(1)).merge( spyWorkflow );
			Mockito.verify(workflowService.getTestGraphService(), Mockito.times(1)).save((StateGraph) Mockito.any());
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test(expected = EntityNotDeletableException.class)
	public void testDeleteWithActiveProject() throws EntityNotDeletableException {
		Mockito.doReturn(Lists.newArrayList(new Project("TestProject", "Description", spyWorkflow, null))).when(spyProjectService).selectAllByWorkflowName(Mockito.anyString());

		workflowService.delete(spyWorkflow);
	}

	@Test
	public void testDeleteWithoutActiveProject() {
		Project project = new Project("TestProject", "Description", spyWorkflow, null);
		project.setActive(false);
		Mockito.doReturn(Lists.newArrayList(project)).when(spyProjectService).selectAllByWorkflowName(Mockito.anyString());

		try {
			workflowService.delete(spyWorkflow);
			Mockito.verify(entityManagerMock, Mockito.times(1)).remove(spyWorkflow);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testAddState() {
		State stateMock2 = Mockito.spy(new State("S1", "Description S1", false));
		State stateMock1 = Mockito.spy(new State("S2", "Description S2", false));


		Set workflows = Mockito.mock(Set.class);
		Mockito.when(spyDomain.getWorkflows()).thenReturn(workflows);

		Mockito.doReturn(new Long(1024)).when(spyDomain).getId();
		Mockito.doReturn(spyDomain).when(spyDomainService).selectByName("Test Domain");

		try {
			workflowService.addStates(spyWorkflow, Lists.newArrayList(stateMock1, stateMock2));
			Mockito.verify(stateMock1, Mockito.times(1)).setWorkflow(spyWorkflow);
			Mockito.verify(stateMock2, Mockito.times(1)).setWorkflow(spyWorkflow);
			//			Mockito.verify(entityManagerMock, Mockito.times(1)).merge(spyWorkflow);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
}
