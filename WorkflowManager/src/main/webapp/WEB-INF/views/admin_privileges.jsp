<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<div id='roles-list-panel' class='panel panel-default' >
<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
    <c:forEach var='privilege' items='${ privileges }' >  
        
        <div class='panel panel-default admin-role-panel admin-privilege-panel' >
                <div class='panel-heading' role='tab' id='privilege-${ privilege.id }-heading' >
                    <h3 class='panel-title'>
                        <a class='collapsed' 
                           aria-expanded='false' 
                           aria-controls='collapse-${ privilege.id }' 
                           data-toggle='collapse' 
                           data-parent='#admin-roles-content-wrapper' 
                           href='#collapse-${ privilege.id }' >${ privilege.name }</a>
                    </h3>
                </div>
                <div id='collapse-${ privilege.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='privilege-${ privilege.id }-heading' >
                </div>
        </div>
        
    </c:forEach>
</div>
</div>
