<%@ tag body-content='empty' isELIgnored='false' 
        import="hu.bme.aut.wman.view.DragNDroppable" %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<%@ attribute name='elements' required='true' type='java.util.List' %>
<%@ attribute name='selectId' required='true' %>
<%@ attribute name='selectClass' required='true' %>
<%@ attribute name='path' required='true' %>
<%@ attribute name='isReadonly' required='true' %>

<%@ attribute name='readonlyValue' required='true' %>


<c:choose >
<c:when test="${ isReadonly eq 'true' }" >

	<form:select id='${ selectId }' path="${ path }" class='${ selectClass }' readonly='true' value='${ readonlyValue }' >
	   <option value="${ readonlyValue }" > ${ readonlyValue } </option>
	</form:select>

</c:when>
<c:otherwise>

    <form:select id='${ selectId }' path="${ path }" class='${ selectClass }' >
        <c:forEach var='element' items='${ elements }' >
            <option value="${ element.value }" > ${ element.label } </option>
        </c:forEach>
    </form:select>

</c:otherwise>
</c:choose>