/**
 * MockedPrivilegeServiceTestSuite.java
 */
package hu.bme.aut.wman.services;

import static org.mockito.Mockito.mock;
import hu.bme.aut.wman.model.ActionType;
import hu.bme.aut.wman.model.Domain;
import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;
import hu.bme.aut.wman.model.Workflow;
import hu.bme.aut.wman.model.graph.GraphNode;
import hu.bme.aut.wman.model.graph.StateGraph;
import hu.bme.aut.wman.service.GraphNodeService;
import hu.bme.aut.wman.service.StateGraphService;
import hu.bme.aut.wman.service.StateService;
import hu.bme.aut.wman.service.TransitionService;
import hu.bme.aut.wman.service.WorkflowService;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class MockedStateServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( MockedStateServiceTestSuite.class );

	private final Long stateMockId = new Long(1024);
	private StateService stateService;
	private EntityManager entityManagerMock;

	private State spyState;

	private Domain domainMock;

	public MockedStateServiceTestSuite() {
	}

	@Before
	public void initContext() {
		stateService = new StateService();
		entityManagerMock = mock(EntityManager.class);
		GraphNodeService nodeService = mock(GraphNodeService.class);
		WorkflowService workflowService = mock(WorkflowService.class);
		StateGraphService graphService = mock(StateGraphService.class);
		TransitionService transitionService = mock(TransitionService.class);
		stateService.setEntityManager( entityManagerMock );
		stateService.setTestNodeService(nodeService);
		stateService.setTestWorkflowService(workflowService);
		stateService.setTestGraphService(graphService);
		stateService.setTestTransitionService(transitionService);

		domainMock = mock(Domain.class);
		State state = new State("Test state", "Description of test state", true);
		spyState = Mockito.spy(state);

		Mockito.when(entityManagerMock.merge(spyState)).thenReturn( spyState );
		Mockito.when(spyState.getId()).thenReturn(stateMockId);
	}

	@Test
	public void testSave() {
		GraphNode nodeMock = Mockito.mock(GraphNode.class);
		Mockito.when(stateService.getTestNodeService().selectByStateId(stateMockId)).thenReturn( nodeMock );

		try {
			stateService.save( spyState );
			Mockito.verify(entityManagerMock, Mockito.times(1)).merge( spyState );
			LOGGER.info("Workflow was created");
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testSaveNew() {
		Workflow workflowMock = Mockito.mock(Workflow.class);
		Mockito.when(workflowMock.getId()).thenReturn(new Long(512));
		StateGraph graphMock = Mockito.mock(StateGraph.class);
		Mockito.when(stateService.getTestGraphService().selectByWorkflowId(workflowMock.getId())).thenReturn(Lists.newArrayList(graphMock));
		Mockito.when(stateService.getTestWorkflowService().selectById( workflowMock.getId())).thenReturn(workflowMock);

		try {
			stateService.saveNew( spyState,  workflowMock.getId());
			Mockito.verify(stateService.getTestWorkflowService(), Mockito.times(1)).save( workflowMock );
			Mockito.verify(stateService.getTestGraphService(), Mockito.times(1)).selectByWorkflowId(workflowMock.getId());
			LOGGER.info("Workflow was created");
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testAddTransitionAlreadyAddedAction() {
		ActionType actionMock = Mockito.mock(ActionType.class);
		Transition transitionMock1 = Mockito.mock(Transition.class);
		Transition transitionMock2 = Mockito.mock(Transition.class);
		Mockito.when(transitionMock1.getActionType()).thenReturn(actionMock);
		Mockito.when(transitionMock2.getActionType()).thenReturn(new ActionType());
		Mockito.when(stateService.getTestTransitionService().selectByParentId(stateMockId)).thenReturn(Lists.newArrayList(transitionMock1, transitionMock2));

		try {
			stateService.addTransition(spyState, actionMock, new State());
		} catch (IllegalArgumentException e) {
			// Expected
			Mockito.verify(stateService.getTestTransitionService(), Mockito.never()).save((Transition) Mockito.any());
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testAddTransitionNewAction() {
		//		ActionType actionMock = Mockito.mock(ActionType.class);
		Transition transitionMock1 = Mockito.mock(Transition.class);
		Transition transitionMock2 = Mockito.mock(Transition.class);
		Mockito.when(transitionMock1.getActionType()).thenReturn(new ActionType("Action1", new Domain()));
		Mockito.when(transitionMock2.getActionType()).thenReturn(new ActionType("Action2", new Domain()));
		Mockito.when(stateService.getTestTransitionService().selectByParentId(stateMockId)).thenReturn(Lists.newArrayList(transitionMock1, transitionMock2));

		try {
			stateService.addTransition(spyState, new ActionType("Action3", new Domain()),  new State());
			Mockito.verify(stateService.getTestTransitionService(), Mockito.times(1)).save((Transition) Mockito.any());
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testRemoveTransition() {
		Long id = new Long(512);
		Transition transitionMock = Mockito.mock(Transition.class);
		Mockito.when(stateService.getTestTransitionService().selectById(id)).thenReturn(transitionMock);
		try {
			stateService.removeTransition(id);
			Mockito.verify(stateService.getTestTransitionService(), Mockito.times(1)).delete(transitionMock);
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
}
