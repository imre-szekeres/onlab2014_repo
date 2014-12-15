<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="project-content-wrapper">
	<div id="project-header" class="content-header">
		<c:choose>
			<c:when test='${param.mode=="edit"}'>
				<div id="edit-mode-name" style="width:600px;">
					<h3 class='project-name' style='line-height:1.4em;'> 
						<strong style='float:left;'>Project:</strong>
						<input id="edit-mode-name-input" class="form-control" style='width:200px;float:left;' value="${project.name}"/>
					</h3>
					<a id='project-save-button' href='#' role='button' class='btn btn-primary header-button' style='margin-left:10px;float:left;'>
						<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
						<span class='button-text'>Save</span>
					</a>
					<a href='project?id=${project.id}' role='button' class='btn btn-warning header-button' style='float:left;'>
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 
						<span class='button-text'>Cancel</span>
					</a>	
					<div style="clear:both;"></div>
				</div> 
			</c:when>
			<c:otherwise>
				<h3 class='project-name'> <strong>Project:</strong> <c:out value='${project.name}' /> </h3>
				<a href='project?id=${project.id}&mode=edit' role='button' class='btn btn-primary header-button'> 
					<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 
					<span class='button-text'>Edit</span> 
				</a>
				<a href='#' data-toggle="modal" data-target="#assign-user-modal" role='button' class='btn btn-primary header-button'> 
					<span class="glyphicon glyphicon-user" aria-hidden="true"></span> 
					<span class='button-text'>Assign user</span> 
				</a>
				<c:choose>
					<c:when test='${project.active}'>
						<a href='close/project?id=${project.id}' role='button' class='btn btn-warning header-button'> 
							<span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 
							<span class='button-text'>Close</span> 
						</a>
					</c:when>
					<c:otherwise>
						<a href='reopen/project?id=${project.id}' role='button' class='btn btn-success header-button'> 
							<span class="glyphicon glyphicon-repeat" aria-hidden="true"></span> 
							<span class='button-text'>Reopen</span> 
						</a>
						<a href='delete/project?id=${project.id}' role='button' class='btn btn-danger header-button'> 
							<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 
							<span class='button-text'>Delete</span> 
						</a>
					</c:otherwise>
				</c:choose>				
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
			<h3 class="panel-title" style="display:inline;">Properties</h3>
			<c:choose>
				<c:when test='${project.active}'>
					<h4 style="display:inline;margin-left:20px;"><span class="label label-success">Active</span></h4>
				</c:when>
				<c:otherwise>
					<h4 style="display:inline;margin-left:20px;"><span class="label label-danger">Closed</span></h4>
				</c:otherwise>
			</c:choose>
		</div>
		<div id="project-base-properties" class="panel-body double-panel-left">
			<c:choose>
				<c:when test='${param.mode=="edit"}'>
					<label for='edit-mode-description' class='col-sm-1 control-label' style='text-align:left;'><strong>Description:</strong></label>
					<div id="edit-mode-description">
						<textarea id="edit-mode-description-input" class="form-control" rows="4" cols="50" style="width:100%;height:100%;"><c:out value='${project.description}' /></textarea>
					</div>
				</c:when>
				<c:otherwise>
					<div id="project-description" class="text-justify panel-body-section">
						<strong>Description:</strong>
						<p><c:out value='${project.description}' /></p>
					</div>
				</c:otherwise>
			</c:choose>
			<div id="project-owner" class='panel-body-section'>
				<strong>Owner:</strong>
				<a href='users/profile?user=${project.owner.id}' class='no-decor-link'><c:out value='${project.owner.username}' /></a>
			</div>
			<div id="project-assigned-users" class='panel-body-section'>
				<strong>Assigned users:</strong> 
				<c:if test='${empty assignedUsers}'>
					<div>-</div>
				</c:if>
				<c:forEach var="aUser" items="${assignedUsers}">
					<div class="row assignment-row">
						<div class="col-md-10 assigned-user-link-wrapper">
							<a href='users/profile?user=${aUser.id}' class='no-decor-link' data-userid='${aUser.id}'><c:out value='${aUser.username}' /></a>
						</div>
						<div class="col-md-2">
							<a id='assign-delete-button' class='assign-delete-button' href='#' role='button'>
								<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
							</a>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
		<div class="panel-body">
			<div id="project-workflow" class='panel-body-section'>
				<strong>Workflow:</strong> <c:out value='${project.workflow.name}' />
			</div>
			<div id="project-state" class='panel-body-section'>
				<strong>State:</strong> <c:out value='${project.currentState.name}' />
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
									<li> <a href="do/action?projectId=${project.id}&actionId=${action.id}" > <c:out value='${action.actionTypeName}' /> </a> </li>
								</c:forEach>
							</ul>
						</c:when>
						<c:when test='${not project.active}'>
							<button type="button" class="btn btn-default btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false" disabled="true">
								Action <span class="caret"></span>
							</button> 
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
						<c:choose>
							<c:when test='${historyEntry.event == "DONE_ACTION"}'>
								<li> <span style="color:#9A9A9A;"> <fmt:formatDate value="${historyEntry.when}" pattern="YYYY-MM-dd"/> - </span><strong><c:out value='${historyEntry.userName}' />: </strong> <c:out value='${historyEntry.message}' />. New state is <i><c:out value='${historyEntry.state}' />.</i></li>
							</c:when>
							<c:otherwise>
								<li> <span style="color:#9A9A9A;"> <fmt:formatDate value="${historyEntry.when}" pattern="YYYY-MM-dd"/> - </span><strong><c:out value='${historyEntry.userName}' />: </strong> <c:out value='${historyEntry.message}' /> in state: <i><c:out value='${historyEntry.state}' />.</i></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</div>
		</div>
		<div id="project-attachments" class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">Attachments</h3>
			</div>
			<div class="panel-body" style="padding-right:0px;">
				<form:form id='upload-file-form' method='POST' commandName="fileUploadVO" action='/WorkflowManager/project/upload?id=${project.id}&${_csrf.parameterName}=${_csrf.token}' enctype="multipart/form-data" class='form-horizontal'>
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
								<c:out value='${file.fileName}' /> 
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
						<a href='users/profile?user=${comment.user.id}' class='no-decor-link'><strong><c:out value='${comment.user.username}' /></strong></a> <span style="color:#9A9A9A;">- <fmt:formatDate value="${comment.postDate}" pattern="YYYY-MM-dd HH:mm"/><span>
					</div>
					<div class="project-comment-message">
						<c:out value='${comment.description}' />
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
	
	<div class="modal fade" id="assign-user-modal" tabindex="-1" role="dialog" aria-labelledby="assign-user-model-label" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
					<h4 class="modal-title" id="assign-user-model-label">Assign user</h4>
				</div>
				<div class="modal-body">
					<div class="assign-user-form-wrapper">
						<div class='form-group'>
							<div>
								<label for='input-assign-user-select' class='control-label' style="padding-bottom:5px;">User</label>
								<select id='assign-user-select' class="form-control"  placeholder="User" style='width:265px;'>
									<c:forEach var="user" items="${assignableUsers}">
										<option value="${user.id}">
											<c:out value='${user.username}' />
										</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
					<input id='assign-user-modal-submit' type="submit" class="btn btn-primary" value="Assign" />
				</div>
			</div>
		</div>
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
		
		$('#project-save-button').click(function(e) {
			var name = $('#edit-mode-name-input').val();
			var description = $('#edit-mode-description-input').val();
		
			var project = {
				"name" : name,
				"description" : description
			}
			$.ajax({
				type: "Get",
				contentType : 'application/json',
				dataType : 'json',
				accepts: 'json',
				url: "save/project?id="+${project.id} +"&name=" + project["name"] + "&description=" + project["description"],
				//data: JSON.stringify(project),
				success :function(response) {
					window.location.href = "project?id="+${project.id};
				},
				error :function(response) {
					window.location.href = "project?id="+${project.id};
				}
			});
		});
		
		$('#assign-user-modal-submit').click(function(e) {
			var id = $('#assign-user-select').val();
			
			$.ajax({
				type: "GET",
				url: "assign/user?projectId="+${project.id}+"&id="+id,
				success :function(response) {
					window.location.href = "project?id="+${project.id};
				},
				error :function(response) {
					window.location.href = "project?id="+${project.id};
				}
			});
		});
		
		$('.assign-delete-button').click(function(e) {
			var id = $(this).parent().siblings('.assigned-user-link-wrapper').children('a').data('userid');
			
			$.ajax({
				type: "GET",
				url: "unassign/user?projectId="+${project.id}+"&id="+id,
				success :function(response) {
					window.location.href = "project?id="+${project.id};
				},
				error :function(response) {
					window.location.href = "project?id="+${project.id};
				}
			});
		});
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
