/**
 * Messages.java
 */
package hu.bme.aut.wman.view;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
public class Messages {
	
	public enum Severity {
		ERROR {
			@Override
			public String toString() {
				return "errorMessage";
			}

		}, 
		WARNING {
			@Override
			public String toString() {
				return "warningMessage";
			}

		}, 
		INFO {
			@Override
			public String toString() {
				return "infoMessage";
			}
		}
	}

	/**
	 * Provides means to transfer messages from the controller layer to the view in a fine grained manner.
	 * 
	 * @param message
	 * @param severity
	 * @param model
	 * */
	public static final void flash(String message, Severity severity, Model model) {
		List<String> messages = messagesOf(severity, model);
		messages.add(message);
		model.addAttribute(severity.toString(), messages);
	}

	/**
	 * Retrieves the messages from the <code>Model</code> instance.
	 * 
	 * @param severity
	 * @param model
	 * @return the {@link List} of messages corresponding to the given {@link Severity}
	 * */
	private static final List<String> messagesOf(Severity severity, Model model) {
		@SuppressWarnings("unchecked")
		List<String> messages = (List<String>) model.asMap().get(severity.toString());
		if (messages == null)
			return new ArrayList<String>();
		return messages;
	}
}
