/**
 * Messages.java
 */
package hu.bme.aut.wman.view;

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

	public static final void flash(String message, Severity severity, Model model) {
		model.addAttribute(severity.toString(), message);
	}
}
