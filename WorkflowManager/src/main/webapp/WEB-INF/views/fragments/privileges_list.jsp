<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>

<!DOCTYPE html>
<c:forEach var='privilege' items='${ privileges }' >
        
        <div class='panel panel-default admin-role-panel admin-privilege-panel' >
                <div class='panel-heading' role='tab' id='privilege-${ privilege.id }-heading' >
                    <h3 class='panel-title'>
                        <a class='collapsed' 
                           aria-expanded='false' 
                           aria-controls='collapse-${ privilege.id }' 
                           data-toggle='collapse' 
                           data-parent='#admin-roles-content-wrapper' 
                           href='#collapse-${ privilege.id }' ><c:out value='${ privilege.name }' /></a>
                    </h3>
                </div>
                <div id='collapse-${ privilege.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='privilege-${ privilege.id }-heading' >
                </div>
        </div>
</c:forEach>