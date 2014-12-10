<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<!DOCTYPE html>
<c:if test='${ not empty errorMessages }'>
    <div id='main-error-panel' class='error-panel'>
        <div class="error-panel-header">
            Error occurred
        </div>
        <div class="error-panel-body">
            <c:forEach var="message" items="${ errorMessages }">
                <div class="alert alert-danger alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert" onclick="errorClosed();"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <c:out value='${ message }' />
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>