<%@ tag language="java" pageEncoding="UTF-8" body-content='empty' isELIgnored='false' %>

<%@ tag import='hu.bme.aut.wman.view.DragNDroppable' %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>

<%@ attribute name='options' type='java.util.List' required='true' %>
<%@ attribute name='optionClass'  %>

<!DOCTYPE html>
<c:forEach var='option' items='${ options }' >
    <option value='${ option.value }' class='option-list-option ${ optionClass }' >
        ${ option.label }
    </option>
</c:forEach>