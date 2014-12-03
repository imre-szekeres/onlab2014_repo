/**
 * DragNDroppable.java
 */
package hu.bme.aut.wman.view;

/**
 * Represents any <code>Object</code> that can be displayed as a Drag-n-Drop element.
 * 
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public interface DragNDroppable {

	/**
	 * Specifies a unique value to be held by a DragNDroppable element.
	 * 
	 * @return a unique {@link String}
	 * */
	String getValue();
	
	/**
	 * Returns a <code>String</code> value to be displayed by the DragNDroppable element.
	 * 
	 * @returns a {@link String} value
	 * */
	String getLabel();
	
	/**
	 * Allows segregation and distinction between DragNDroppable elements according to their 
	 * specified owner. Empty <code>String</code> represents no segregation.
	 * 
	 * @return the name of the owner this DragNDroppable element corresponds to
	 * */
	String getOwner();
}
