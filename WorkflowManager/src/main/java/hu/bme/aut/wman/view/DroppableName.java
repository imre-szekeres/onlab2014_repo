/**
 * DroppableName.java
 */
package hu.bme.aut.wman.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable helper class for representing <code>Domain</code> names in a Drag-n-Droppable manner.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class DroppableName implements DragNDroppable {
	private final String name;

	public DroppableName(String name) {
		this.name = name;
	}

	/**
	 * @return name
	 * */
	@Override
	public String getValue() {
		return name;
	}

	/**
	 * @return name
	 * */
	@Override
	public String getLabel() {
		return name;
	}
	
	/**
	 * Transforms a <code>List</code> of domain names to a <code>List</code> of 
	 * <code>DroppableDomainName</code>s in order to allow standard representation.
	 * 
	 * @param domainNames
	 * @return the droppabelDomainNames
	 * @see {@link DroppableDomainName}
	 * */
	public static final List<DroppableName> namesOf(List<String> names) {
		List<DroppableName> droppableNames = new ArrayList<>();
		for(String name : names)
			droppableNames.add(new DroppableName( name ));
		return droppableNames;
	}
}
