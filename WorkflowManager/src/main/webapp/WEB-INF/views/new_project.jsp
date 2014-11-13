<%@ page language='java' contentType='text/html; charset=UTF-8'	pageEncoding='UTF-8' %>
    
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id='new-project-wrapper' class='panel-body'>
	<form:form id='new-project-form' modelAttribute='project' method='POST' action='/WorkflowManager/new/project' class='form-horizontal'>
		<div class='form-group'>
			<label for='input-name' class='col-sm-1 control-label' style='text-align:left;'>Name</label>
			<div class='col-sm-12' >
				<form:input id='name' class='form-control' path='name' placeholder='Name of the project' value='TestProject' style='width:300px;'/>
			</div>
		</div>
		<div class='form-group'>
			<label for='input-description' class='col-sm-1 control-label'>Description</label>
			<div class='col-sm-12' >
				<form:textarea id='description' class='form-control' path='description' placeholder='Write here a short description.' rows='4' style='resize:none;width:400px;' value='TestDescription for test project' />
			</div>
		</div>
		<div class="form-group">
			<label for='select-workflow' class='col-sm-1 control-label'>Workflow</label>
			<div class='col-sm-12 input-group' style='position:relative;left:15px;'>
				<span class="input-group-addon glyphicon glyphicon-random" style='position:relative;top:-0.5px;'/></span>
				<form:select id='select-workflow' path="workflowId" class="form-control"  placeholder="Workflow" items="${availableWorkflows}" style='width:265px;'/>
			</div>
		</div>
		<div class='form-group'>
			<div class='col-sm-2'>
				<input type='submit' class='btn btn-primary' value='Create'/>
			</div>
		</div>
		
    </form:form>
</div>