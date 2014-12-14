<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<c:if test='${empty projects}'>
	<div id='projects-empty-wrapper' class='warning-panel'>
		<div class="warning-panel-header">
			Ooops, we can't find any
			<strong>
				<c:choose>
					<c:when test='${param.active}'>
						active
					</c:when>
					<c:otherwise>
						closed
					</c:otherwise>
				</c:choose>
			</strong>
			project.
		</div>
		<div class="warning-panel-body">
			You don't have any 
			<c:choose>
				<c:when test='${param.active}'>
					<strong> active </strong> project yet. Click <a role='button' href="new/project" class='btn btn-link inline-link'>here</a> to create or <a role='button' href='projects?active=false' class='btn btn-link inline-link'>here</a> to reopen one.
				</c:when>
				<c:otherwise>
					<strong> closed </strong> project yet. Click <a role='button' href='projects?active=true' class='btn btn-link inline-link'>here</a> to see the active projects and close one if you want.
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>

<c:if test='${not empty projects}'>
	<div id="projects-content-wrapper" class="panel panel-default panel-body">
		<c:forEach var="project" items="${projects}">
			<div class="panel-group projects-wrapper" id="accordion${project.id}">
				<div class="panel panel-default">
					<div class="projects-name panel-heading" >
						<h4 class="panel-title">
							<a data-toggle="collapse" style="text-decoration: blink;" data-parent="#accordion${project.id}" href="#collapse${project.id}">
								<c:out value='${project.name}' />
							</a>
						</h4>
					</div>
					<div id="collapse${project.id}" class="panel-collapse collapse">
						<div class="panel-body projects-description">
							<c:out value='${project.description}' />
						</div>
						<div class="panel-body projects-workflow">
							Workflow: <c:out value='${project.workflow.name}' /> <br/>
							State: <c:out value='${project.currentState.name}' /> <br/>
							Owner: <c:out value='${project.owner.username}' /> 
						</div>
					</div>
					<div class="projects-id">
						<c:out value='${project.id}' />
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</c:if>

<script language="javascript">
	$( "div.projects-wrapper" )
		.mouseenter(function() {
			var name = $(this).find('div.projects-id').text();
			var isActive = getUrlParameter('active');
			var div = $(this).find('div.projects-name');
			
			if (isActive == 'true') {
				div.append( "<div id='close-button' class='close-button'><a class='no-decor-link little-button-link' href='close/project?id="+name+"'><span class='glyphicon glyphicon-remove' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='edit-button' class='edit-button'><a class='no-decor-link little-button-link' href=''><span class='glyphicon glyphicon-pencil' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='open-button' class='open-button'><a class='no-decor-link little-button-link' href='project?id="+name+"'><span class='glyphicon glyphicon-share-alt' style='line-height: 26px;position:absolute;left:7px;'></a></div>" );
			} else {
				div.append( "<div id='delete-button' class='delete-button'><a class='no-decor-link little-button-link' href='delete/project?id="+name+"'><span class='glyphicon glyphicon-trash' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='edit-button' class='edit-button'><a class='little-button-link no-decor-link ' href=''><span class='glyphicon glyphicon-pencil' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='open-button' class='open-button'><a class='no-decor-link little-button-link' href='project?id="+name+"'><span class='glyphicon glyphicon-share-alt' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='reopen-button' class='reopen-button'><a class='no-decor-link little-button-link' href='reopen/project?id="+name+"'><span class='glyphicon glyphicon-repeat' style='line-height: 26px;position:absolute;left:7px;'></a></div>");
			}
		})
		.mouseleave(function() {
			$("#delete-button").remove();
			$("#close-button").remove();
			$("#edit-button").remove();
			$("#open-button").remove();
			$("#reopen-button").remove();
		});
</script>
