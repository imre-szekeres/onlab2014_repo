<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<c:if test='${empty workflows}'>
	<div id='workflows-empty-wrapper' class='warning-panel'>
		<div class="warning-panel-header">
			Ooops, we can't find any workflow.
		</div>
		<div class="warning-panel-body">
			You don't have any workflow yet. Click <a role='button' href="new/workflow" class='btn btn-link inline-link'>here</a> to create one...
		</div>
	</div>
</c:if>

<c:if test='${not empty workflows}'>
	<div id="workflows-content-wrapper" class="panel panel-default panel-body">
		<c:forEach var="workflow" items="${workflows}">
			<div class="panel-group workflows-wrapper" id="accordion${workflow.id}">
				<div class="panel panel-default">
					<div class="panel-heading workflows-name">
						<h4 class="panel-title">
							<a data-toggle="collapse" style="text-decoration: blink;" data-parent="#accordion${workflow.id}" href="#collapse${workflow.id}">
								${workflow.name}
							</a>
						</h4>
					</div>
					<div id="collapse${workflow.id}" class="panel-collapse collapse workflows-description">
						<div class="panel-body">
							${workflow.description}
						</div>
						<div class="panel-body projects-workflow">
							Domain: ${workflow.domain.name} <br/>
						</div>
					</div>
					<div class="workflows-id">
						${workflow.id}
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</c:if>
	
<script language="javascript">
	$( "div.workflows-wrapper" )
		.mouseenter(function() {
			var name = $(this).find('div.workflows-id').text();
			$(this).find('div.workflows-name').append( 
							"<div id='delete-button' class='delete-button'><a class='no-decor-link little-button-link' href='delete/workflow?id="+name+"'><span class='glyphicon glyphicon-trash' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='edit-button' class='edit-button'><a class='no-decor-link little-button-link' href=''><span class='glyphicon glyphicon-pencil' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
							"<div id='open-button' class='open-button'><a class='no-decor-link little-button-link' href='workflow?id="+name+"'><span class='glyphicon glyphicon-share-alt' style='line-height: 26px;position:absolute;left:7px;'></a></div>" );
		})
		.mouseleave(function() {
			$("#delete-button").remove();
			$("#edit-button").remove();
			$("#open-button").remove();
		});
</script>
