/**
 * FacesMessageUtils.java
 */
package hu.bme.aut.tomeesample.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * 
 * @author redcloud
 * @version "%I%, %G%"
 */
public class FacesMessageUtils {

	/**
	 * Adds a new <code>FacesMessages.SEVERITY_INFO</code> message to the
	 * specified <code>FacesContext</code>.
	 * 
	 * @param context
	 * @param message
	 * */
	public static void infoMessage(FacesContext context, String message) {
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	/**
	 * Adds a new <code>FacesMessages.SEVERITY_ERROR</code> message to the
	 * specified <code>FacesContext</code>.
	 * 
	 * @param context
	 * @param message
	 * */
	public static void errorMessage(FacesContext context, String message) {
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}
}
