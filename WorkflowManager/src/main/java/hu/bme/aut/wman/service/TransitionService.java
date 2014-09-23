package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.State;
import hu.bme.aut.wman.model.Transition;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Helps make operations with <code>StateNavigationEntry</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class TransitionService extends AbstractDataService<Transition> {

	public List<Transition> selectByParentId(Long parentId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("parentId", parentId));
		return callNamedQuery(Transition.NQ_FIND_BY_PARENT_ID, parameterList);
	}

	public List<Transition> selectByNextStateId(Long nextId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("nextId", nextId));
		return callNamedQuery(Transition.NQ_FIND_BY_NEXT_STATE_ID, parameterList);
	}

	public List<Transition> selectByActionTypeId(Long typeId, Long parentId) {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("typeId", typeId));
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("parentId", parentId));
		return callNamedQuery(Transition.NQ_FIND_BY_ACTIONTYPE_AND_PARENT_ID, parameterList);
	}

	public Collection<State> selectNextStates(State state) {
		return Collections2.transform(selectByParentId(state.getId()),
				new Function<Transition, State>() {

					@Override
					public State apply(Transition navigationEntry) {
						return navigationEntry.getNextState();
					}
				});
	}

	public Collection<State> selectParents(State state) {
		return Collections2.transform(selectByNextStateId(state.getId()),
				new Function<Transition, State>() {

					@Override
					public State apply(Transition navigationEntry) {
						return navigationEntry.getParent();
					}
				});
	}

	@Override
	protected Class<Transition> getEntityClass() {
		return Transition.class;
	}
}
