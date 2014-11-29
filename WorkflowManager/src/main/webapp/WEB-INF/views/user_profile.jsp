<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored='false' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<div id='user-profile-positioner' class='positioner' >
    
    <div class='panel panel-default' >
        <span class='username-part' >
	        <strong>
	            <span id='username-span'>
	                <span class='glyphicon glyphicon-user'> ${ user.username }</span>
	            </span>
	        </strong>
        </span>
        <span class='button-part'>
            <c:choose>
                <c:when test="${ isEditable eq 'true' }" >
                    <button type='button' id='edit-profile-trigger' class='btn btn-danger' > 
                        <span id='edit-trigger-icon' class='glyphicon glyphicon-pencil' ></span>  Edit
                    </button>
                </c:when>
                <c:otherwise>
                    <button type='button' id='edit-profile-trigger' class='btn btn-danger btn' disabled >
                        <span id='edit-trigger-icon' class='glyphicon glyphicon-pencil' ></span>  Edit
                    </button>
                </c:otherwise>
            </c:choose>
        </span>
    </div>

</div>