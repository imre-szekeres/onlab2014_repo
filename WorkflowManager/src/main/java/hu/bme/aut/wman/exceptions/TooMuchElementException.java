package hu.bme.aut.wman.exceptions;

public class TooMuchElementException extends Exception {

	private int nbrOfElements;

	public TooMuchElementException(String message, int nbrOfElements) {
		super(message);
		this.nbrOfElements = nbrOfElements;
	}

	public int getNbrOfElements() {
		return nbrOfElements;
	}

	public void setNbrOfElements(int nbrOfElements) {
		this.nbrOfElements = nbrOfElements;
	}

}
