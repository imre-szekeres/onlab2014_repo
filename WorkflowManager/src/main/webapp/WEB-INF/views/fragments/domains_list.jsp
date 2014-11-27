<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib tagdir='/WEB-INF/tags/' prefix='wman' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />


<c:forEach var='domain' items='${ domains }' >  
            <div class='panel panel-default admin-role-panel admin-domain-panel' >
                <div class='panel-heading' role='tab' id='domain-${ domain.id }-heading' >
                    <h3 class='panel-title'>
                        <a class='collapsed' 
                           aria-expanded='false' 
                           aria-controls='collapse-${ domain.id }' 
                           data-toggle='collapse' 
                           data-parent='#admin-roles-content-wrapper' 
                           href='#collapse-${ domain.id }' >${ domain.name }</a>
                    </h3>
                    <c:if test='${ domain.name ne "System" }' >
                        <wman:iconRow parentId='domain-${ domain.id }-heading' 
                                      editPath='#1' 
                                      detailsPath='#2' 
                                      deletePath='#3' 
                                      iconRowClass='role-icon-row' />
                    </c:if>
                </div>
        
                <div id='collapse-${ domain.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='domain-${ domain.id }-heading' >
                    <div class='list-group privileges-list-group roles-list-group' >
                    
                        <div class='privilege-list-wrapper role-list-wrapper' >
                        <c:forEach var='role' items='${ domain.roles }' >
                            <div class='privilege-row role-row' >
                               ${ role.name }
                            </div>
                        </c:forEach>
                        </div>
                    
                    </div>
                </div>
            </div>
</c:forEach>