<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>

<c:forEach var='assignment' items='${ assignments }' >
    <c:forEach var='role' items='${ assignment.userRoles }' >
    
        <div class='privilege-row role-row' >
            <span title='in domain ${ assignment.domain.name }' data-toggle='tooltip' data-placement='right' >
                <c:out value='${ role.name }' />
            </span>
        </div>

    </c:forEach>
</c:forEach>