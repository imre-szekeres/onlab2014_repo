<%@ page language='java' contentType='text/html; charset=UTF-8'	pageEncoding='UTF-8' %>
    
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id='new-workflow-wrapper' class='panel panel-default panel-body container-fluid'>
	<div style="width:500px;margin-left:20px">
		<form:form id='new-workflow-form' modelAttribute='workflow' method='POST' action='/WorkflowManager/new/workflow' class='form-horizontal'>
			<div class='form-group'>
				<label for='input-name' class='control-label'>Name</label>
				<div >
					<form:input id='name' class='form-control' path='name' placeholder='Name of the workflow' value='TestWorkflow'/>
				</div>
			</div>
			<div class='form-group'>
				<label for='input-description' class='control-label'>Description</label>
				<div >
					<form:textarea id='description' class='form-control' path='description' placeholder='Write here a short description.' rows='4' style='resize:none;' value='TestDescription for test workflow' />
				</div>
			</div>
			<div class="form-group">
				<label for='select-domain-div' class=' control-label'>Domain</label>
				<div id='select-domain-div' class='input-group' style='position:relative;'>
					<span class="input-group-addon glyphicon glyphicon-tower" style='position:relative;top:-0.5px;'/></span>
					<form:select id='select-domain' path="domain" class="form-control"  placeholder="Domain" items="${domains}" style='width:265px;'/>
				</div>
			</div>
			<div class='form-group'>
				<div class='col-sm-2'>
					<input type='submit' class='btn btn-primary' value='Create'/>
				</div>
			</div>
		</form:form>
	</div>
</div>