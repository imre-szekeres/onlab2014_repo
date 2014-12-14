<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<div id='domains-and-roles-table-wrapper' >

    <div class='panel panel-default' >
        <div class='panel-heading'>
            <c:set var='username' value='${ empty assignments ? "" : assignments[0].user.username }' />
            <h4>Assigned to <a href='#user-profile-positioner' class='username-href-to-positioner' >${ username }</a></h4>
        </div>
        
        <table class='table table-hover table-striped' >
            <thead>
                <tr>
                    <th>#</th>
                    <th class='dnr-td' >
                        Role Name
                    </th>
                    <th class='dnr-td' >
                        Domain Name
                    </th>
                </tr>
            </thead>
            
            <tbody>
                <c:set var='count' value='0' />
                <c:forEach var='assignment' items='${ assignments }' >
                <c:forEach var='role' items='${ assignment.userRoles }' >
                    <tr class='dnr-row'>
                        <td>
                            <c:out value='${ count }' />
                            <c:set var='count' value='${ count + 1 }' />
                        </td>
                        <td class='role-row dnr-td' >
                            <c:out value='${ role.name }' />
                        </td>
                        <td class='domain-row dnr-td' >
                            <c:out value='${ assignment.domain.name }' />
                        </td>
                    </tr>
                </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>