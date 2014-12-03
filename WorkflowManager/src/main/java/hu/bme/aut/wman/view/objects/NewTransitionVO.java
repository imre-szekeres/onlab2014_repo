package hu.bme.aut.wman.view.objects;



public class NewTransitionVO {
	private Long actionId;
	private Long workflowId;

	public NewTransitionVO(){};

	public Long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

}
