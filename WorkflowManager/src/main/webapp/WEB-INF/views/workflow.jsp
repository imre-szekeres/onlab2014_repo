<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id="workflow-content-wrapper">
	<div id="workflow-header" class="content-header">
		<h3 class='workflow-name'> <strong>Workflow:</strong> ${workflowVO.workflow.name} </h3>
		<a role='button' class='btn btn-primary header-button'> <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> <span class='button-text'>Edit</span> </a>
		<a href='delete/workflow?id=${workflowVO.workflow.id}' role='button' class='btn btn-danger header-button'> <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> <span class='button-text'>Delete</span> </a>
	</div>
	<div id="workflow-properties-panel" >
		<div id="workflow-description" class="panel panel-default double-panel-left">
			<div class="panel-heading">
				<h3 class="panel-title">Description</h3>
			</div>
			<div class="panel-body text-justify">
				${workflowVO.workflow.description}
			</div>
		</div>
		<div id="workflow-projects" class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">Related projects</h3>
			</div>
			<div class="panel-body">
				<ul class="list-unstyled">
				<c:forEach var="project" items="${workflowVO.projects}">
					<li> ${project.name} </li>
				</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	<div id="workflow-states-panel" class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">States</h3>
		</div>
		<div id='workflow-states' class="panel-body">
			<c:forEach var="state" items="${workflowVO.workflow.states}">
				${state.name}</br>
			</c:forEach>
		</div>
		<div id='workflow-states' class="panel-body">
			<a role='button' class='btn btn-primary header-button'> <span>State</span> </a>
			<a role='button' class='btn btn-primary header-button'> <span>Transition</span> </a>
		</div>
	</div>
</div>
	
<script language="javascript">
</script>
