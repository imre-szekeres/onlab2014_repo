<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib tagdir='/WEB-INF/tags/' prefix='wman' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<!DOCTYPE html>
<c:forEach var='domain' items='${ domains }' >  
        <c:forEach var='role' items='${ domain.roles }' >
            
            <div class='panel panel-default admin-role-panel' >
                <div class='panel-heading iconed-panel-heading' role='tab' id='role-${ role.id }-heading' title='in domain <c:out value="${ domain.name }" />' data-toggle='tooltip' data-placement='right' >                    
                    <h3 class='panel-title'>
                        <a class='collapsed' 
                           aria-expanded='false' 
                           aria-controls='collapse-${ role.id }' 
                           data-toggle='collapse' 
                           data-parent='#admin-roles-content-wrapper' 
                           href='#collapse-${ role.id }' ><c:out value='${ role.name }' /></a>
                    </h3>
                    <c:if test='${ domain.name ne "System" }' >
                        <wman:iconRow parentId='role-${ role.id }-heading' 
                                      editPath='${ appRoot }${ selectUpdateFormUrl }?role=${ role.id }' 
                                      deletePath='${ appRoot }${ deleteRoleAction }?role=${ role.id }' 
                                      iconRowClass='role-icon-row' />
                    </c:if>
                </div>
                <div id='collapse-${ role.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='role-${ role.id }-heading' >
                    <div class='list-group privileges-list-group' >
                    
                        <div class='privilege-list-wrapper' >
                        <c:forEach var='privilege' items='${ role.privileges }' >
                            <div class='privilege-row' >
                               <c:out value='${ privilege.name }' />
                            </div>
                        </c:forEach>
                        </div>
                    
                    </div>
                </div>
            </div>
            
        </c:forEach>
</c:forEach>