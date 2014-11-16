<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id="workflows-content-wrapper" class="panel-body">
	<c:forEach var="workflow" items="${workflows}">
		<div class="panel-group workflows-wrapper" id="accordion${workflow.id}">
			<div class="panel panel-default">
				<div class="panel-heading workflows-name" style="background-color: #5700B3; color: white; line-height:30px;">
					<h4 class="panel-title">
						<a data-toggle="collapse" data-parent="#accordion${workflow.id}" href="#collapse${workflow.id}">
							${workflow.name}
						</a>
					</h4>
				</div>
				<div id="collapse${workflow.id}" class="panel-collapse collapse workflows-description">
					<div class="panel-body">
						${workflow.description}
					</div>
				</div>
				<div class="workflows-id">
					${workflow.id}
				</div>
			</div>
		</div>
	</c:forEach>
</div>
	
<script language="javascript">
	$( "div.workflows-wrapper" )
		.mouseenter(function() {
			var name = $(this).find('div.workflows-id').text();
			$(this).find('div.workflows-name').append( 
							"<a id='delete-button' class='no-decor-link' href='delete/workflow?id="+name+"'><div class='delete-button'><span class='glyphicon glyphicon-trash' style='line-height: 26px;'></div></a>" +
							"<a id='edit-button' href=''><div class='edit-button'><span class='glyphicon glyphicon-pencil' style='line-height: 26px;'></div></a>" +
							"<a id='open-button' class='no-decor-link' href='workflow?id="+name+"'><div class='open-button'><span class='glyphicon glyphicon-share-alt' style='line-height: 26px;'></div></a>" );
		})
		.mouseleave(function() {
			$("#delete-button").remove();
			$("#edit-button").remove();
			$("#open-button").remove();
		});
</script>
