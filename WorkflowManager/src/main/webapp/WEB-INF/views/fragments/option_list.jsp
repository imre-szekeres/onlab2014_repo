<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import='hu.bme.aut.wman.view.DragNDroppable' %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>

<!DOCTYPE html>
<c:forEach var='option' items='${ options }' >
    <option value='${ option.value }' class='option-list-option ${ optionClass }' >
        ${ option.label }
    </option>
</c:forEach>