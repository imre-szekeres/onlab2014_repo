<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   

<div id="workflow-content-wrapper">
	<div id="workflow-header" class="content-header">
		<c:choose>
			<c:when test='${param.mode=="edit"}'>
				<div id="edit-mode-name" style="width:600px;">
					<h3 class='workflow-name' style='line-height:1.4em;'> 
						<strong style='float:left;'>Workflow:</strong>
						<input id="edit-mode-name-input" class="form-control" style='width:200px;float:left;' value="${workflow.name}"/>
					</h3>
					<a id='workflow-save-button' href='#' role='button' class='btn btn-primary header-button' style='margin-left:10px;float:left;'>
						<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
						<span class='button-text'>Save</span>
					</a>
					<a href='workflow?id=${workflow.id}' role='button' class='btn btn-warning header-button' style='float:left;'>
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 
						<span class='button-text'>Cancel</span>
					</a>	
					<div style="clear:both;"></div>
				</div> 
			</c:when>
			<c:otherwise>
				<h3 class='workflow-name'> <strong>Workflow:</strong> ${workflow.name} </h3>
				<a href='workflow?id=${workflow.id}&mode=edit' role='button' class='btn btn-primary header-button'>
					<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
					<span class='button-text'>Edit</span>
				</a>
				<a href='delete/workflow?id=${workflow.id}' role='button' class='btn btn-danger header-button'>
					<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 
					<span class='button-text'>Delete</span>
				</a>				
			</c:otherwise>
		</c:choose>
	</div>
	<div id="workflow-properties-panel" >
		<div id="workflow-description" class="panel panel-default double-panel-left">
			<div class="panel-heading">
				<h3 class="panel-title">Description</h3>
			</div>
			<c:choose>
				<c:when test='${param.mode=="edit"}'>
					<div id="edit-mode-description">
						<textarea id="edit-mode-description-input" class="form-control" rows="4" cols="50" style="width:100%;height:100%;">${workflow.description}</textarea>
					</div>
				</c:when>
				<c:otherwise>
					<div class="panel-body text-justify">
						${workflow.description}
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<div id="workflow-projects" class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">Related projects</h3>
			</div>
			<div class="panel-body">
				<ul class="list-unstyled">
					<c:forEach var="project" items="${projects}">
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
		<div id='workflow-states-graph' class="panel-body">
		</div>
		<div id='workflow-states-btns' class="panel-body">
			<button id="open-new-state-modal-btn" type="button" class="btn btn-primary header-button" data-toggle="modal" data-target="#new-state-modal">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> State
			</button>
		</div>
	</div>
</div>

<div class="modal fade" id="new-state-modal" tabindex="-1" role="dialog" aria-labelledby="new-state-modal-label" aria-hidden="true">
	<div class="modal-dialog">
		<form:form id='new-state-form' modelAttribute='newState' method='POST' action='/WorkflowManager/new/state?workflowId=${workflow.id}&stateId=-1' class='form-horizontal'>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
					<h4 class="modal-title" id="new-state-modal-label">New state</h4>
				</div>
				<div class="modal-body">
					<div class="state-new-form-wrapper">
						<div class='form-group'>
							<div>
								<label for='input-new-state-name' class='control-label' style="padding-bottom:5px;">Name</label>
								<form:input id='new-state-name' path='name' placeholder='Name of the state' value='TestState'/>
							</div>
							<div>
								<label for='input-new-state-description' class='control-label' style="padding-bottom:5px;">Description</label>
								<form:textarea id='new-state-description' path='description' placeholder='Description of the state' value='TestAction description'/>
							</div>
							<form:input type='hidden' path="initial" value="false"/>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
					<input id="new-state-submit" type="submit" class="btn btn-primary" value="Create" />
				</div>
			</div>
		</form:form>
	</div>
</div>

<div class="modal fade" id="new-transition-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<form:form id='new-transition-form' modelAttribute='newTransition' method='POST' action='/WorkflowManager/new/transition?' class='form-horizontal'>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
					<h4 class="modal-title" id="myModalLabel">New transition</h4>
				</div>
				<div class="modal-body">
					<div class="transition-new-form-wrapper">
						<div class='form-group'>
							<div>
								<label for='input-new-transition-action' class='control-label' style="padding-bottom:5px;">Action</label>
								<form:select id='new-transition-action' path="actionId" class="form-control"  placeholder="Action" items="${actionMap}" style='width:265px;'/>
							</div>
							<form:input id="new-transition-workflowId" path='workflowId' value='${workflow.id}' type='hidden'/>
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

<div class="modal fade" id="transition-settings-modal" tabindex="-1" role="dialog" aria-labelledby="transition-settings-modal-label" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
				<h4 class="modal-title" id="transition-settings-modal-label">Transition settings</h4>
			</div>
			<div class="modal-body">
				<div id="transition-settings-modal-start">
					<div id="transition-settings-modal-start-title">
					</div>
					<div id="transition-settings-modal-start-actions">
					</div>
				</div>
				<div id="transition-settings-modal-end">
					<div id="transition-settings-modal-end-title">
					</div>
					<div id="transition-settings-modal-end-actions">
					</div>
				</div>
				<div style="width: 15px; clear:both;"></div>
			</div>
		</div>
	</div>
</div>
	
<script language="javascript">
	var edges;
	var nodes;

	function enablePopovers() {
		var popovers = $("[data-toggle='popover']");
		popovers.popover({
			 html:true,
			 template: '<div class="popover graph-node-popover" role="tooltip"><div class="arrow graph-node-popover-arrow"></div><h3 class="popover-title graph-node-popover-title"></h3><div class="popover-content"></div></div>'
		});
		popovers.hover(function(){
			$('.graph-node-popover').css('top','');
			$('.graph-node-popover').css('left','');
		});
	}
	
	function disablePopovers(nodeId) {
		$("#state-node-"+nodeId).children("[data-toggle='popover']").popover("toggle");
	}
	
	function getIndexById(array, id) {
		for(var i = 0; i < array.length; i += 1) {
			if(array[i]["id"] === id) {
				return i;
			}
		}
	}
	
	function getEdgesByNodeId(nodeId) {
		var selectedEdges = [];
		for(var i in edges) {
			if((edges[i]["start"]["id"] === parseInt(nodeId)) || (edges[i]["end"]["id"] === parseInt(nodeId))) {
				selectedEdges.push(edges[i]);
			}
		}
		return selectedEdges;
	}
	
	function deleteBtnsHandler() {
		$("#delete-button").remove();
		$("#init-button").remove();
		$("#add-button").remove();
		$("#state-edit-button").remove();
	}
	
	function addBtnsHandler() {
		var name = $(this).find(".graph-node").data("nodeid");
		var workflowId = ${workflow.id};
		$(this).append( 
						"<div id='delete-button'  class='graph-button delete-button' style='left:26px;top:-7px'><a class='no-decor-link little-button-link' href='delete/state?nodeId="+name+"&workflowId="+workflowId+"'><span class='glyphicon glyphicon-trash' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
						"<div id='add-button'  class='graph-button add-button' style='left:62px; top:-75px; z-index:1100;'><a class='no-decor-link little-button-link' href=''><span class='glyphicon glyphicon-transfer' style='line-height: 26px;position:absolute;left:7px;'></a></div>" +
						"<div id='init-button'  class='graph-button init-button' style='left:26px; top:-143px;'><a class='no-decor-link little-button-link' href='initial/state?nodeId="+name+"&workflowId="+workflowId+"'><span class='glyphicon glyphicon-star' style='line-height: 26px;position:absolute;left:8px;'></a></div>" +
						"<div id='state-edit-button'  class='graph-button state-edit-button' style='left:-13px; top:-134px; z-index:1100;'><a data-toggle='modal' data-target='#new-state-modal' class='no-decor-link little-button-link' href=''><span class='glyphicon glyphicon-pencil' style='line-height: 26px;position:absolute;left:7px;'></a></div>");
		$('#add-button').draggable({
			appendTo: "body",
			zIndex: 1100,
			opacity: 1,
			containment: "#workflow-states-graph",
			revert: "invalid",
			start: function() {
				$(".buttons-placeholder").unbind('mouseenter');
				$(".buttons-placeholder").unbind('mouseleave');
			},
			stop: function() {
				deleteBtnsHandler();
				$(".buttons-placeholder").bind('mouseenter', addBtnsHandler);
				$(".buttons-placeholder").bind('mouseleave', deleteBtnsHandler);
			}
		});
		$('#state-edit-button').click(function(e){
			var nodeId = $('#state-edit-button').parents('.buttons-placeholder').children('.graph-node').data('nodeid');
			
			var stateId = nodes[getIndexById(nodes, nodeId)].stateId;
			var name = nodes[getIndexById(nodes, nodeId)].label;
			var description = nodes[getIndexById(nodes, nodeId)].content;
		
			var asd = $('#new-state-name');
			$('#new-state-name').val(name);
			$('#new-state-description').val(description);
			$('#new-state-form').attr("action", "/WorkflowManager/new/state?workflowId="+${workflow.id}+"&stateId="+stateId)
			$('#new-state-modal-label').text('Edit state');
			$('#new-state-submit').val("Save")
		});
	}
	
	function getNbrOfSiblingsMatch(element, array) {
		var siblings = element.prevAll().filter(".graph-transition");
		var nbr = 0;
		for (var i = 0; i < siblings.length; i++) {
			for (var j = 0; j < array.length; j++) {
				if (siblings[i]["attributes"]["id"]["value"] === array[j]["attributes"]["id"]["value"]) {
					nbr++;
				}
			}
		}
		return nbr;
	}

	function drawNodes() {
		for (var i in nodes) {
			if (nodes[i].x == -1 && nodes[i].y == -1) {
				$("#workflow-states-graph").append("<div class='buttons-placeholder buttons-placeholder-end' style='position:relative;left:50%;'>" +
														"<div id='state-node-"+nodes[i].id+"' data-nodeid='"+nodes[i].id+"' class='graph-node graph-node-end' > " +
															"<a href='#' class='no-decor-link' data-placement='bottom'"+
																"data-toggle='popover' title='"+nodes[i].label+"' " +
																"data-content='"+ nodes[i].content +"'>" +
																"<div class='graph-node-text'>" +
																	nodes[i].label + "" +
																"</div> " +
															"</a>" +
														"</div>" +
													"</div>");
				var select ="#state-node-"+nodes[i].id;
				var btnsPlaceholder = $(select).parent();
				btnsPlaceholder.draggable({
					appendTo: "body",
					zIndex: 1001,
					opacity: 0.7,
					handle: ".graph-node",
					containment: "parent",
					start: nodeDragStart,
					stop: nodeDragStop,
				}).mouseenter(addBtnsHandler)
				  .mouseleave(deleteBtnsHandler)
				  .droppable({
						accept: "#add-button",
						drop: function( event, ui ) {
							var from = ui.draggable[0].parentElement.children[0];
							var fromId = from.getAttribute('data-nodeid');
							var to = event.target.children[0];
							var toId = to.getAttribute('data-nodeid');
							$('#new-transition-form').attr("action", "/WorkflowManager/new/transition?from="+fromId+"&to="+toId)
						
							$('#new-transition-modal').modal('toggle');
						}
					});
				if (nodes[i].initial) {
					btnsPlaceholder.addClass('buttons-placeholder-initial');
					btnsPlaceholder.removeClass('buttons-placeholder-end');
					$(select).addClass('graph-node-initial');
					$(select).removeClass('graph-node-end');
				}				
			} else {
				$("#workflow-states-graph").append("<div class='buttons-placeholder buttons-placeholder-end' style='position:relative;top:"+nodes[i].y+"px;left:"+nodes[i].x+"px;'>" +
														"<div id='state-node-"+nodes[i].id+"' data-nodeid='"+nodes[i].id+"' class='graph-node graph-node-end'> " +
															"<a href='#' class='no-decor-link' data-placement='bottom'"+
																"data-toggle='popover' title='"+nodes[i].label+"' " +
																"data-content='"+ nodes[i].content +"'>" +
																"<div class='graph-node-text'>" +
																	nodes[i].label + "" +
																"</div> " +
															"</a>" +
														"</div> " +
													"</div>");
				var select ="#state-node-"+nodes[i].id;
				var btnsPlaceholder = $(select).parent();
				btnsPlaceholder.draggable({
					appendTo: "body",
					zIndex: 1001,
					opacity: 0.7,
					handle: ".graph-node", 
					containment: "parent",
					start: nodeDragStart,
					stop: nodeDragStop
				})	.mouseenter(addBtnsHandler)
					.mouseleave(deleteBtnsHandler)
					.droppable({
						accept: "#add-button",
						drop: function( event, ui ) {
							var from = ui.draggable[0].parentElement.children[0];
							var fromId = from.getAttribute('data-nodeid');
							var to = event.target.children[0];
							var toId = to.getAttribute('data-nodeid');
							$('#new-transition-form').attr("action", "/WorkflowManager/new/transition?from="+fromId+"&to="+toId)
						
							$('#new-transition-modal').modal('toggle');
						}
					});
				if (nodes[i].initial) {
					btnsPlaceholder.addClass('buttons-placeholder-initial');
					btnsPlaceholder.removeClass('buttons-placeholder-end');
					$(select).addClass('graph-node-initial');
					$(select).removeClass('graph-node-end');
				}
			}
		}
	}
	
	function nodeDragStart(event, ui) {					
		var nodeId = ui.helper[0].children[0]['attributes']['data-nodeid'].value;
		var fromTransitions = $('[data-fromId="'+nodeId+'"]');
		var toTransitisons = $('[data-toId="'+nodeId+'"]');
		
		var removedNbr = fromTransitions.length + toTransitisons.length;
		var remainedTransitions = $('.graph-transition');
		
		for (var i = 0; i < remainedTransitions.length; i++) {
			var transformValue = remainedTransitions[i].style.transform;
			var translateValue = transformValue.substring(transformValue.indexOf("translateX(")+11, transformValue.length);
			translateValue = translateValue.substring(0, translateValue.indexOf('px)'));
			
			var element = $('#'+remainedTransitions[i]["attributes"]["id"]["value"]);
			var removedNbr = getNbrOfSiblingsMatch(element, $.unique($.merge(fromTransitions, toTransitisons)));
			
			var newTranslateValue = translateValue + ((removedNbr-1) * 5);
			
			transformValue = transformValue.replace("translateX("+translateValue+"px)", "translateX("+newTranslateValue+"px)");
			remainedTransitions;
		}
		
		fromTransitions.remove();
		toTransitisons.remove();
		deleteBtnsHandler();
	}
	
	function nodeDragStop(event, ui) {
		deleteBtnsHandler();
		var nodeId = ui.helper[0].children[0]['attributes']['data-nodeid'].value;
		var nodeIndex = getIndexById(nodes, parseInt(nodeId));
		
		disablePopovers(nodeId);
		
		var newTop = ui.helper[0].style.top.replace("px", "");
		var newLeft = ui.helper[0].style.left.replace("px", "");
		
		nodes[nodeIndex]["x"] = parseInt(newLeft);
		nodes[nodeIndex]["y"] = parseInt(newTop);
		
		var selectedEdges = getEdgesByNodeId(nodeId);
		nbrOfDrawedEdges = -1;
		for(var i in selectedEdges) {
			var id = getIndexById(edges, selectedEdges[i].id);
			drawEdge(selectedEdges[i]);
		}
		$('.graph-transition').remove();
		drawEdges();
	}
	
	var nbrOfDrawedEdges = 0;
	
	function drawEdge(edge) {
		var sameDirection = $('[data-fromId="'+edge.start.id+'"]')
								.filter('[data-toId="'+edge.end.id+'"]')
								.children('[data-side="end"]');
		if (sameDirection.length != 0) {
			var span = "<span class='edge-label' data-edgeId="+edge.id+">";
			if (sameDirection.text().trim().length) {
				span = span+", ";
			}
			span = span+ "" +edge.label +"</span>";
			sameDirection.append(span);
			return;
		}
		
		var reverseDirection = $('[data-fromId="'+edge.end.id+'"]')
								.filter('[data-toId="'+edge.start.id+'"]')
								.children('[data-side="start"]');
		if (reverseDirection.length != 0) {
			var span = "<span class='edge-label' data-edgeId="+edge.id+">";
			if (reverseDirection.text().trim().length) {
				span = span+", ";
			}
			span = span+ "" +edge.label +"</span>";
			reverseDirection.append(span);
			reverseDirection.siblings().css('top', 
					function(i, v) {
						return (parseFloat(v) - 20) + 'px';
					});
			return;
		}
		
		var startNodeI = getIndexById(nodes, edge.start.id);
		var endNodeI = getIndexById(nodes, edge.end.id);
		
		var posAT = nodes[startNodeI].y;
		var posAL = nodes[startNodeI].x;
		var posBT = nodes[endNodeI].y;
		var posBL = nodes[endNodeI].x;
		
		//if(startNodeI < endNodeI) {
			posBT += 80 * endNodeI;
		//} else {
			posAT += 80 * startNodeI;
		//}
		
		var yD = Math.abs(posAT-posBT);
		var xD = Math.abs(posAL-posBL);
		var transWidth = Math.sqrt(Math.pow(yD,2)+Math.pow(xD,2));
		
		var deg = (Math.asin(yD/transWidth) * 180)/Math.PI;
		
		if ((posAT < posBT && posAL > posBL) || (posBT < posAT) && (posBL > posAL)) {
			deg = deg * -1;
		}
		
		nbrOfDrawedEdges++;
		if (posAL < posBL) {
			transPosLeft = posAL + 40;
			transPosTop = posAT + 40 - (80 * nodes.length) - (5 * nbrOfDrawedEdges);
		} else {
			transPosLeft = posBL + 40;
			transPosTop = posBT + 40 - (80 * nodes.length) - (5 * nbrOfDrawedEdges);
		}
		
		$("#workflow-states-graph").append("<div id='graph-transition-"+edge.id+"' class='graph-transition' data-edgeId="+edge.id+" data-fromId='"+edge.start.id+"' data-toId='"+edge.end.id+"'>" +
												"<span id='graph-transition-"+edge.id+"-label-start' class='graph-transition-label' data-side='start' >" +
												"</span>" +
												"<span id='graph-transition-"+edge.id+"-label-end' class='graph-transition-label' data-side='end' >" +
													"<span class='edge-label' data-edgeId="+edge.id+">" +edge.label +"</span>"+
												"</span>" +
											"</div>");
											
		if (posAL < posBL) {
			$('#graph-transition-'+edge.id+'-label-end')
			.css({"left": "calc("+(transWidth)+"px - 10em)"});
		} else {
			$('#graph-transition-'+edge.id+'-label-start')
			.css({"left": "calc("+(transWidth)+"px - 10em)"});
		}
		$("#graph-transition-"+edge.id).addClass("graph-transition")
		.css({	"width": transWidth,
				"transform": "translateY("+transPosTop+"px) translateX("+transPosLeft+"px) rotateZ("+deg+"deg)",
				"transform-origin" : "left top"})
		.mouseenter(addEdgeBtnsHandler)
		.mouseleave(deleteEdgeBtnsHandler);
	}
	
	function addEdgeBtnsHandler() {
		var top = $(this).position().top;
		var left = $(this).position().left;
		var width = parseInt($(this).css("width").replace("px",""))/2-40;
		var sumHeight = 0;
		$(this).append( "<div id='edge-edit-button'  class='graph-button edge-edit-button' style='left:-13px; top:-134px; z-index:1100;'><a data-toggle='modal' data-target='#transition-settings-modal' class='no-decor-link little-button-link' href=''><span class='glyphicon glyphicon-transfer' style='line-height: 26px;position:absolute;left:7px;'></a></div>");
		$('#edge-edit-button').siblings().each( function(){ sumHeight += parseInt($(this).css("height").replace("px","")); });
		sumHeight += 12;
		$('#edge-edit-button').css({
						"left": width+"px",
						"top": "-"+sumHeight+"px",
		});
		$('#edge-edit-button').click(function(e){
			$('#transition-settings-modal-start-actions').empty();
			$('#transition-settings-modal-end-actions').empty();
			var edgeStartLabels = $('#edge-edit-button').siblings('.graph-transition-label').filter('[data-side="start"]').children('.edge-label');
			var titlesSetted = false;
			for (var i = 0; i < edgeStartLabels.length; i++) {
				var edgeId = parseInt(edgeStartLabels[i]['attributes']['data-edgeid']['value']);
				var edge = edges[getIndexById(edges, edgeId)];
				
				if (!titlesSetted) {
					$('#transition-settings-modal-start-title').html("<strong>" + edge.start.label + "</strong>");
					$('#transition-settings-modal-end-title').html("<strong>" + edge.end.label + "</strong>");
					titlesSetted = true;
				}
				
				$('#transition-settings-modal-end-actions').append('<div class="transition-settings-modal-action" data-edgeid="'+ edge.id +'">' + edge.label + 
																		"<a href='#' role='button' class='btn btn-danger btn-xs transition-delete-button' onclick='deleteEdge("+edge.id+")'>" +
																			"<span class='glyphicon glyphicon-trash' aria-hidden='true'></span> " +
																			"<span class='button-text'>Delete</span>" +
																		"</a>" +
																	"</div>");
			}
			
			var edgeEndLabels = $('#edge-edit-button').siblings('.graph-transition-label').filter('[data-side="end"]').children('.edge-label');
			for (var i = 0; i < edgeEndLabels.length; i++) {
				var edgeId = parseInt(edgeEndLabels[i]['attributes']['data-edgeid']['value']);
				var edge = edges[getIndexById(edges, edgeId)];
				
				if (!titlesSetted) {
					$('#transition-settings-modal-start-title').html("<strong>" + edge.start.label + "</strong>");
					$('#transition-settings-modal-end-title').html("<strong>" + edge.end.label + "</strong>");
					titlesSetted = true;
				}
				
				$('#transition-settings-modal-start-actions').append('<div class="transition-settings-modal-action" data-edgeid="'+ edge.id +'">' + edge.label + 
																		"<a href='#' role='button' class='btn btn-danger btn-xs transition-delete-button' onclick='deleteEdge("+edge.id+")'>" +
																			"<span class='glyphicon glyphicon-trash' aria-hidden='true'></span> " +
																			"<span class='button-text'>Delete</span>" +
																		"</a>" +
																	"</div>");
			}
		});
	}
	
	function deleteEdge(id) {
		$.ajax({
			type: "post",
			url: "delete/transition",
			cache: false,
			data:'edgeId=' + id,
			success: function(response){
				var edgeId = parseInt(this['data'].replace("edgeId=",""));
				$(".transition-settings-modal-action").filter('[data-edgeid="'+edgeId+'"]').remove();
				$(".graph-transition-label").children(".edge-label").filter('[data-edgeid="'+edgeId+'"]').remove();
				delete edges[getIndexById(edges, edgeId)];
				/*$('.graph-transition-label').empty();
				drawEdges();*/
			},
			error: function(){
			}
		});
	}
	
	function deleteEdgeBtnsHandler() {
		$('#edge-edit-button').remove();
	}
	
	function drawEdges() {
		for (var i in edges) {
			drawEdge(edges[i]);
		}
	}
	
	function removeEndClasses() {
		for(var i in edges) {
			var nodeId = edges[i].start.id;
			var nodeDiv = $("#state-node-"+parseInt(nodeId));
			nodeDiv.parent().removeClass('buttons-placeholder-end');
			nodeDiv.removeClass('graph-node-end');
		}
	}
	
	$(function() {	
		var workflowId = ${workflow.id};
		var url = "workflow/stategraph?id=" + workflowId;
		$.ajax({
			type: "get",
			url: url,
			dataType: "json",
			data:'',
			success: function(response){
				nodes = response.points;
				drawNodes();
				
				edges = response.edges;
				edges.reverse();

				removeEndClasses();
				drawEdges();
				
				enablePopovers();
			},
			error: function(){
			}
		});
		
		$("#workflow-states-graph").droppable({
			accept: ".buttons-placeholder",
			drop: function( event, ui ) {
				var newX = ui.position.left;
				var newY = ui.position.top;
				var id = ui.draggable[0].children[0].attributes['data-nodeid'].value;
		 
				$.ajax({
					type: "post",
					url: "save/node",
					cache: false,
					data:'nodeId=' + id + "&newX=" + newX + "&newY=" + newY,
					success: function(response){
					},
					error: function(){
					}
				});		
			}
		});
		
		$('#open-new-state-modal-btn').click(function(e){
			$('#new-state-name').val("State name");
			$('#new-state-description').val("");
			$('#new-state-form').attr("action", "/WorkflowManager/new/state?workflowId="+${workflow.id}+"&stateId=-1")
			$('#new-state-modal-label').text('New state');
			$('#new-state-submit').val("Create")
		});
		
		$('#workflow-save-button').click(function(e) {
			var name = $('#edit-mode-name-input').val();
			var description = $('#edit-mode-description-input').val();
		
			var workflow = {
				"name" : name,
				"description" : description
			}
			$.ajax({
				type: "POST",
				contentType : 'application/json; charset=utf-8',
				dataType : 'json',
				url: "save/workflow?id="+${workflow.id},
				data: JSON.stringify(workflow),
				success :function(response) {
					window.location.href = "workflow?id="+${workflow.id};
				},
				error: function(){
					window.location.href = "workflow?id="+${workflow.id};
				}
			});
		});
	});
</script>
