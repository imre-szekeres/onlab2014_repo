<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib tagdir='/WEB-INF/tags/' prefix='wman' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<c:forEach var='user' items='${ users }' >  
        <div class='panel panel-default admin-role-panel admin-user-panel' >
                <div class='panel-heading' role='tab' id='user-${ user.id }-heading' >
                    <h3 class='panel-title'>
                        <a class='collapsed user-collapsed-href' 
                           aria-expanded='false' 
                           aria-controls='collapse-${ user.id }' 
                           data-toggle='collapse' 
                           data-parent='#admin-roles-content-wrapper' 
                           href='#collapse-${ user.id }' 
                           name='${ user.id }' >${ user.username }</a>
                    </h3>
                    <wman:iconRow parentId='user-${ user.id }-heading' 
                                  editPath='#1' 
                                  detailsPath='${ appRoot }${ detailsUserAction }?user=${ user.id }' 
                                  deletePath='${ appRoot }${ deleteUserAction }?user=${ user.id }' 
                                  iconRowClass='role-icon-row' />
                </div>
                <div id='collapse-${ user.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='user-${ user.id }-heading' >
                     
                     <div class='role-list-wrapper' requestable='true' name='${ user.id }' >
                     </div>
                     
                </div>
        </div>        
</c:forEach>