<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import='hu.bme.aut.wman.view.DragNDroppable' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<c:set var='elementRowClass' value="${ empty elementRowClass ? '' : elementRowClass }" />
<c:set var='elementBodyClass' value="${ empty elementBodyClass ? '' : elementBodyClass }" />

<c:forEach var='element' items='${ elements }'>
    
    <div class='drag-n-drop-row ${ elementRowClass }' id='${ element.value }' >
        <div class='drag-n-drop-body ${ elementBodyClass }' draggable='true' ondragstart='onElementDragged(event)' >
            <c:out value='${ element.label }' />
        </div>
    </div>
    
</c:forEach>

<script>

   function onElementDragged(event) {
	   var $_target = $(event.target).parent();
	   event.dataTransfer.setData('elementID', $_target.attr('id'));
   }

</script>