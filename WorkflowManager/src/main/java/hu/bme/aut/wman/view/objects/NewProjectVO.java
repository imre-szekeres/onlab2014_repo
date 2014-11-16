package hu.bme.aut.wman.view.objects;


public class NewProjectVO {
	private String name;
	private String description;
	private Long workflowId;

	public NewProjectVO(){};

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
}
