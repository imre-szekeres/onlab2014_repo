<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<div id='roles-list-panel' class='panel panel-default'>
<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
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
</div>
</div>

<div id='admin-domains-create-wrapper' >
    <button class='btn btn-primary' id='create-domain-trigger' data-toggle='modal' data-target='#new-domain-modal' >
        <span class="glyphicon glyphicon-plus new-role-icon" aria-hidden="true" ></span> Create Domain
    </button>
</div>

<div class='modal fade' id='new-domain-modal' tabindex='-1' role='dialog' aria-labelledby='#new-role-label' aria-hidden='true' >
<div class='modal-dialog'>
<div class='modal-content'>

    <div class='modal-header' >
        <button type='button' class='close' data-dismiss='modal'>
            <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
        </button>
        <h4 class='modal-title' id='new-role-label' ><span class='glyphicon glyphicon-tower' ></span> Create Domain</h4>
    </div>

    <div class='modal-body' >
        <form:form modelAttribute='newDomain' action='${ appRoot }${ createDomainAction }' method='POST' id='new-domain-form' class='form-vretical' >
        <fieldset>
            <div class='from-group'>
            </div>
            
            <div class='form-group'>
                <label class='control-label col-sm-2 domain-name-label' for='domain-name' >
                    <spring:message code='domain.form.name.label' ></spring:message>
                </label>
                <div class='col-sm-5'>
                    <c:choose >
                        <c:when test='${ not empty validationErrors and not empty validationErrors.name }' >
                                
                            <span class='has-error' title='${ validationErrors.name }' data-toggle='tooltip' >
                               <form:input id='domain-name' path='name' type='text' class='form-control new-domain-input' ></form:input>
                            </span>
                                
                        </c:when>
                        <c:otherwise>
                            <form:input id='domain-name' path='name' type='text' class='form-control new-domain-input' ></form:input>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class='col-sm-2'>
                    <button id='new-domain-submit' type='submit' class='btn btn-success submit-row-btn new-domain-input' onclick='submitNewDomainForm(event)' >Create</button>
                </div>
                <div class='col-sm-2'>
                    <button type='button' class='btn btn-default submit-row-btn new-domain-input' data-dismiss='modal' >Cancel</button>
                </div>
            </div>
        </fieldset>
        </form:form>
    </div>

    <div class='modal-footer' >
    </div>

</div>
</div>
</div>


<script>
    
    function submitNewDomainForm(event) {
    	$('#new-domain-form').submit();
    }
    
    $('.collapse').collapse();
</script>

<c:if test='${ not empty validationErrors }' >
<script>
    $('#create-domain-trigger').trigger('click');
    $('.has-error').tooltip({
        placement: 'top'
    });
</script>
</c:if>