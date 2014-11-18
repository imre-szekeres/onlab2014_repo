<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id="project-content-wrapper">
	<div id="project-header" class="content-header">
		<h3 class='project-name'> <strong>Project:</strong> ${project.name} </h3>
		<a role='button' class='btn btn-primary header-button'> <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> <span class='button-text'>Edit</span> </a>
		<c:if test='${project.active}'>
			<a href='close/project?id=${project.id}' role='button' class='btn btn-warning header-button'> <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> <span class='button-text'>Close</span> </a>
		</c:if>
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
				<strong>Owner:</strong> ${project.owner.name}
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
									<li> <a href="#"> ${action.name} </a> </li>
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
			<div class="panel-body">
				<div id="project-file-chooser" class='panel-body-section'>
					<strong>Select a file: </strong>
					<div class="input-group">
						<span class="input-group-btn">
							<span class="btn-primary btn btn-default btn-file">Browse<input id="input-attachment" type="file"> </span>
						</span>
						<span class="input-group-btn">
							<span class="btn btn-default btn-file" style="border-radius:0px;">Upload<input id="input-attachment" type="file"> </span>
						</span>
						<input id="attachment-placeholder" type="text" class="form-control" readonly="true" placeholder="file">
					</div>
				</div>
				<ul class="list-unstyled" class='panel-body-section'>
					<c:forEach var="file" items="${project.files}">
						<li> ${file.name} [DOWNLOAD BUTTON] </li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	
	<div id="project-comments-panel" class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">Comments</h3>
		</div>
		<div id='project-comments' class="panel-body">
			<ul class="list-unstyled">
				<c:forEach var="comment" items="${project.comments}">
					<li> <strong> ${comment.postDate} - ${comment.user.name} </strong> ${comment.description}</li>
				</c:forEach>
			</ul>
		</div>
		<div id='project-add-comment' class="panel-body">
			<span class="form-group"> Write a comment: </span>
			<textarea id='comment-input' class="form-control"> </textarea>
			<input id="comment-btn" class="btn btn-primary" type="submit" value="Comment"/>
		</div>
	</div>
</div>
	
<script language="javascript">
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
