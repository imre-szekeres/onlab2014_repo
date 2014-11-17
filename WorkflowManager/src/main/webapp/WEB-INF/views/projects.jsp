<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id="projects-content-wrapper" class="panel-body">
	<c:forEach var="project" items="${projects}">
		<div class="panel-group projects-wrapper" id="accordion${project.id}">
			<div class="panel panel-default">
				<div class="panel-heading projects-name" style="background-color: #5700B3; color: white; line-height:30px;">
					<h4 class="panel-title">
						<a data-toggle="collapse" data-parent="#accordion${project.id}" href="#collapse${project.id}">
							${project.name}
						</a>
					</h4>
				</div>
				<div id="collapse${project.id}" class="panel-collapse collapse">
					<div class="panel-body projects-description">
						${project.description}
					</div>
					<div class="panel-body projects-workflow">
						Workflow: ${project.workflow.name} <br/>
						State: ${project.currentState.name} <br/>
						Owner: ${project.owner.name} 
					</div>
				</div>
				<div class="projects-id">
					${project.id}
				</div>
			</div>
		</div>
	</c:forEach>
</div>
	
<script language="javascript">
	$( "div.projects-wrapper" )
		.mouseenter(function() {
			var name = $(this).find('div.projects-id').text();
			var isActive = getUrlParameter('active');
			var div = $(this).find('div.projects-name');
			
			if (isActive == 'true') {
				div.append( "<a id='close-button' class='no-decor-link' href='close/project?id="+name+"'><div class='close-button'><span class='glyphicon glyphicon-remove' style='line-height: 26px;'></div></a>" +
							"<a id='edit-button' class='no-decor-link' href=''><div class='edit-button'><span class='glyphicon glyphicon-pencil' style='line-height: 26px;'></div></a>" +
							"<a id='open-button' class='no-decor-link' href='project?id="+name+"'><div class='open-button'><span class='glyphicon glyphicon-share-alt' style='line-height: 26px;'></div></a>" );
			} else {
				div.append( "<a id='close-button' class='no-decor-link' href='delete/project?id="+name+"'><div class='delete-button'><span class='glyphicon glyphicon-trash' style='line-height: 26px;'></div></a>" +
							"<a id='edit-button' class='no-decor-link' href=''><div class='edit-button'><span class='glyphicon glyphicon-pencil' style='line-height: 26px;'></div></a>" +
							"<a id='open-button' class='no-decor-link' href='project?id="+name+"'><div class='open-button'><span class='glyphicon glyphicon-share-alt' style='line-height: 26px;'></div></a>" );
			}
		})
		.mouseleave(function() {
			$("#delete-button").remove();
			$("#close-button").remove();
			$("#edit-button").remove();
			$("#open-button").remove();
		});
</script>
