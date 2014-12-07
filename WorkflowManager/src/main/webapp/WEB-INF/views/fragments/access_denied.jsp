<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<!DOCTYPE html>
<div id='access-denined-panel' class='warning-panel'>
    <c:choose >
        <c:when test='${ not empty denialMessage }' >
            
            <div id='access-denied-alert' class='alert alert-danger access-denied-alert' id='denial-message-wrapper' >
                <h3><span class='glyphicon glyphicon-warning-sign' ></span> Access Denied</h3>
		        <c:out value='${ denialMessage }' />
		    </div>
        
        </c:when>
        <c:when test='${ not empty detailedAccessDenied }' >
        
            <div id='access-denied-alert' class='alert alert-danger access-denied-alert' id='denial-message-wrapper' >
                <h3><span class='glyphicon glyphicon-warning-sign' ></span> Access Denied</h3>
                <c:out value='${ detailedAccessDenied }' />
                <br />
                <c:out value='${ personellLine }' />
                <br />
                
                <ul id='personell-info-list' >
                <c:forEach var='info' items='${ personellInfo }' >
                    <li>
                       <span class='glyphicon glyphicon-envelope'></span> 
                       <span><a href='mailto:${ info[1] }' ><c:out value='${ info[1] }' /></a></span>
                    </li>
                </c:forEach>
                </ul>
            </div>
        
        </c:when>
        <c:otherwise>
        
            <div id='access-denied-alert' class='alert alert-danger access-denied-alert' id='default-access-denied-wrapper' >
                <h3><span class='glyphicon glyphicon-warning-sign' ></span> Access Denied</h3>
		        Sorry, you are not authorized to execute that operation or visit that page!
		    </div>
        
        </c:otherwise>
    </c:choose>
</div>
