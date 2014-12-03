<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id="project-content-wrapper">
	<div id="project-header" class="content-header">
		<h3 class='project-name'> <strong>Project:</strong> ${project.name} </h3>
		<a role='button' class='btn btn-primary header-button'> <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> <span class='button-text'>Edit</span> </a>
		<c:if test='${project.active}'>
		</c:if>
		<c:choose>
			<c:when test='${project.active}'>
				<a href='close/project?id=${project.id}' role='button' class='btn btn-warning header-button'> <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class='button-text'>Close</span> </a>
			</c:when>
			<c:otherwise>
				<a href='reopen/project?id=${project.id}' role='button' class='btn btn-success header-button'> <span class="glyphicon glyphicon-repeat" aria-hidden="true"></span> <span class='button-text'>Reopen</span> </a>
				<a href='delete/project?id=${project.id}' role='button' class='btn btn-danger header-button'> <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> <span class='button-text'>Delete</span> </a>
			</c:otherwise>
		</c:choose>
	</div>
	
	<c:if test='${empty actions}'>
		<div id='project-no-more-action' class='info-panel'>
			<div class="info-panel-header">
				There can be no more action done.
			</div>
			<div class="info-panel-body">
				There are no further actions available from this state of the project. Would you like to close it?
			</div>
		</div>
	</c:if>
	
	<div id="project-properties-panel" class='panel panel-default'>
		<div class="panel-heading">
			<h3 class="panel-title">Properties</h3>
		</div>
		<div id="project-base-properties" class="panel-body double-panel-left">
			<div id="project-description" class="text-justify panel-body-section">
				<strong>Description:</strong>
				<p>${project.description}</p>
			</div>
			<div id="project-owner" class='panel-body-section'>
				<strong>Owner:</strong> ${project.owner.username}
			</div>
			<div id="project-active" class='panel-body-section'>
				<c:choose>
					<c:when test='${project.active}'>
						<h4><span class="label label-success">Active</span></h4>
					</c:when>
					<c:otherwise>
						<h4><span class="label label-danger">Closed</span></h4>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="panel-body">
			<div id="project-workflow" class='panel-body-section'>
				<strong>Workflow:</strong> ${project.workflow.name}
			</div>
			<div id="project-state" class='panel-body-section'>
				<strong>State:</strong> ${project.currentState.name}
			</div>
			<div id="project-actions" class='panel-body-section'>
				<strong>Do: </strong>
				<div class="btn-group">
					<c:choose>
						<c:when test='${not empty actions}'>
							<button type="button" class="btn btn-default btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
								Action <span class="caret"></span>
							</button>
							<ul class="dropdown-menu" role="menu">
								<c:forEach var="action" items="${actions}">
									<li> <a href="do/action?projectId=${project.id}&actionId=${action.id}" > ${action.actionTypeName} </a> </li>
								</c:forEach>
							</ul>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn btn-default btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false" disabled="true">
								Action <span class="caret"></span>
							</button> 
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
	
	<div id="project-history-attachments-panel" >
		<div id="project-history" class="panel panel-default double-panel-left">
			<div class="panel-heading">
				<h3 class="panel-title">History</h3>
			</div>
			<div class="panel-body">
				<ul class="list-unstyled">
					<c:forEach var="historyEntry" items="${project.historyEntries}">
						<li> <strong> ${historyEntry.when}: </strong> ${historyEntry.userName} ${historyEntry.actionTypeName} ${historyEntry.state.name}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
		<div id="project-attachments" class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">Attachments</h3>
			</div>
			<div class="panel-body" style="padding-right:0px;">
				<form:form id='upload-file-form' method='POST' commandName="fileUploadVO" action='/WorkflowManager/project/upload?id=${project.id}' enctype="multipart/form-data" class='form-horizontal'>
					<div id="project-file-chooser" class='attachment-input-wrapper panel-body-section'>
						<strong>Select a file: </strong>
						<div class="input-group">
							<span class="input-group-btn">
								<span class="btn-primary btn btn-default btn-file">Browse<form:input id="input-attachment" type="file" path="file"/> </span>
							</span>
							<span class="input-group-btn">
								<input class="btn btn-default btn-file" style="border-radius:0px;" id="input-attachment-submit" type="submit" Value="Upload">
							</span>
							<input id="attachment-placeholder" type="text" class="form-control" readonly="true" placeholder="file">
						</div>
					</div>
				</form:form>
				<div class="attachment-list container-fluid">
					<c:forEach var="file" items="${project.files}">
						<div class="attachment-row row"> 
							<div class="col-xs-8">
								${file.fileName} 
							</div>
							<div class="col-xs-2">
								<a href='download/file?id=${file.id}' role='button' class='btn btn-primary header-button btn-clear-hover-primary'><span class="glyphicon glyphicon-download btn-clear-hover-primary" aria-hidden="true"></span></a>
							</div>
							<div class="col-xs-2">
								<a href='delete/file?id=${file.id}&projectId=${project.id}' role='button' class='btn btn-danger header-button btn-clear-hover-danger'><span class="glyphicon glyphicon-trash btn-clear-hover-danger" aria-hidden="true"></span></a>
							</div>
						</div>
					</c:forEach>
				<div>
			</div>
		</div>
	</div>
	
	<div id="project-comments-panel" class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">Comments</h3>
		</div>
		<div id='project-comments'>
			<c:forEach var="comment" items="${project.comments}">
				<div class="project-comment-body panel-body">
					<div class="project-comment-inform">
						<strong>${comment.user.username}</strong> <span style="color:#9A9A9A;">- ${comment.postDate}<span>
					</div>
					<div class="project-comment-message">
						${comment.description}
					</div>
				</div>
			</c:forEach>
		</div>
		<form:form id='add-comment-form' modelAttribute='commentMessage' method='POST' action='/WorkflowManager/project/comment?id=${project.id}' class='form-horizontal'>
			<div id='project-add-comment' class="panel-body">
				<span class="form-group"> Write a comment: </span>
				<form:textarea id='comment-input' class='form-control' path='value' placeholder='Write your comment here.' rows='4' style='resize:none;width:400px;' value='TestDescription for test project' />
				<input id="comment-btn" class="btn btn-primary project-comment-btn" type="submit" value="Comment"/>
			</div>
		</form:form>
	</div>
</div>
	
<script language="javascript">
	$( function() {
		var h1 = $('#project-attachments').height();
		var h2 = $('#project-history').height();
		if (h1 > h2) {
			$('#project-history').height(h1);
			$('#project-attachments').height(h1);
		} else {
			$('#project-history').height(h2);
			$('#project-attachments').height(h2);
		}
	});

	$(document).ready( function() {
		$('.btn-file :file').on('fileselect', function(event, label) {
			$( "#attachment-placeholder" ).val(label);
			//console.log(label);
		});
	});

	$(document).on('change', '.btn-file :file', function() {
		var input = $(this);
        //numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [label]);
	});
</script>
