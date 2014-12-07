<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<!DOCTYPE html>
<div id='access-denined-panel' class='warning-panel'>
    <c:choose >
        <c:when test='${ not empty denialMessage }' >
            
            <div id='access-denied-alert' class='alert alert-danger access-denied-alert' id='denial-message-wrapper' >
                <h3>Access Denied</h3>
		        <c:out value='${ denialMessage }' />
		    </div>
        
        </c:when>
        <c:when test='${ not empty detailedAccessDenied }' >
        
            <div id='access-denied-alert' class='alert alert-danger access-denied-alert' id='denial-message-wrapper' >
                <h3>Access Denied</h3>
                <c:out value='${ detailedAccessDenied }' />
                <br />
                <c:out value='${ personellLine }' />
            </div>
        
        </c:when>
        <c:otherwise>
        
            <div id='access-denied-alert' class='alert alert-danger access-denied-alert' id='default-access-denied-wrapper' >
                <h3>Access Denied</h3>
		        Sorry, you are not authorized to execute that operation or visit that page!
		    </div>
        
        </c:otherwise>
    </c:choose>
</div>
