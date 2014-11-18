<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   



<div id='actions-header'>
	<h3><strong>Actions</strong></h3>
	<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#new-action-modal">
		 <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New
	</button>
</div>


<c:if test='${empty actions}'>
	<div id='actions-empty-wrapper' class='warning-panel'>
		<div class="warning-panel-header">
			Ooops, we can't find any action.
		</div>
		<div class="warning-panel-body">
			You don't have any action yet. Click on the "New" button to create one.
		</div>
	</div>
</c:if>

<c:if test='${not empty actions}'>
	<div id="actions-content-wrapper" class="panel panel-default panel-body">
		<c:forEach var="action" items="${actions}">
			<div class="panel-group actions-wrapper" id="accordion${action.id}">
				<div class="panel panel-default">
					<div class="panel-heading actions-name" style="background-color: #5700B3; color: white; line-height:30px;">
						<h4 class="panel-title">
							<a data-toggle="collapse" data-parent="#accordion${action.id}" href="#collapse${action.id}">
								${action.actionTypeName}
							</a>
						</h4>
					</div>
					<div id="collapse${action.id}" class="panel-collapse collapse actions-description">
						<div class="panel-body">
							<c:forEach var="role" items="${actionsMap['action.id']}">
								${role.name}
							</c:forEach>
						</div>
					</div>
					<div class="actions-id">
						${action.id}
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</c:if>

<div class="modal fade" id="new-action-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<form:form id='new-action-form' modelAttribute='newAction' method='POST' action='/WorkflowManager/new/action' class='form-horizontal'>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
					<h4 class="modal-title" id="myModalLabel">New action</h4>
				</div>
				<div class="modal-body">
						<div class='form-group'>
							<label for='input-new-action-name' class='control-label'>Name</label>
							<div>
								<form:input id='new-action-name' path='actionTypeName' placeholder='Name of the action' value='TestAction'/>
							</div>
						</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
					<input type="submit" class="btn btn-primary" value="Create" />
				</div>
			</div>
		</form:form>
	</div>
</div>
	
<script language="javascript">
	$( "div.actions-wrapper" )
		.mouseenter(function() {
			var name = $(this).find('div.actions-id').text();
			$(this).find('div.actions-name').append( 
							"<div id='delete-button'  class='delete-button' style='left:390px;'><a class='no-decor-link little-button-link' href='delete/action?id="+name+"'><span class='glyphicon glyphicon-trash' style='line-height: 26px;'></a></div>");
		})
		.mouseleave(function() {
			$("#delete-button").remove();
			$("#edit-button").remove();
			$("#open-button").remove();
		});
</script>
