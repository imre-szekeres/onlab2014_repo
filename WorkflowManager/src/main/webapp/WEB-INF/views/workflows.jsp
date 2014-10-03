<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<div id="workflow-content-wrapper">
	<c:forEach var="workflow" items="${workflows}">
	<div class="panel-group workflow-wrapper" id="accordion${workflow.id}">
		<div class="panel panel-default">
			<div class="panel-heading workflow-name" style="background-color: #5700B3; color: white; line-height:30px;">
				<h4 class="panel-title">
					<a data-toggle="collapse" data-parent="#accordion${workflow.id}" href="#collapse${workflow.id}">
						${workflow.name}
					</a>
				</h4>
			</div>
			<div id="collapse${workflow.id}" class="panel-collapse collapse workflow-description">
				<div class="panel-body">
					${workflow.description}
				</div>
			</div>
		</div>
	</div>
	</c:forEach>
	

<script language="javascript">
	$( "div.workflow-wrapper" )
		.mouseenter(function() {
			object=this;
			$(this).find('div.workflow-name').append( "<div class='delete-button'><span class='glyphicon glyphicon-trash' style='margin:7px';></div>" +
							"<div class='edit-button'><span class='glyphicon glyphicon-pencil' style='margin:7px';></div>" +
							"<div class='open-button'><span class='glyphicon glyphicon-share-alt' style='margin:7px';></div>" );
		})
		.mouseleave(function() {
			$(".delete-button").remove();
			$(".edit-button").remove();
			$(".open-button").remove();
		});
</script>
