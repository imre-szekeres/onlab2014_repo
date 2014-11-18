package hu.bme.aut.wman.view.objects;

public class ErrorMessageVO {

	private String title;

	private String message;

	public ErrorMessageVO(){
	}

	public ErrorMessageVO(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
