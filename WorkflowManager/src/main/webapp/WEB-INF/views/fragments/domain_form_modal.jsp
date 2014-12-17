<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />
<c:set var='formType' value="${ empty formType ? 'create' : formType }" />
<c:set var='formType' value='${ not empty param.formType ? param.formType : formType }' />

<c:choose >
<c:when test="${ formType eq 'create' }" >
    
    <c:set var='formID' value="new-domain-form" />
    <c:set var='formTitle' value="Create Domain" />
    <c:set var='submitCallback' value='submitNewDomainForm(event)' />
    <c:set var='submitText' value='Create' />
    
</c:when>
<c:otherwise>
    
    <c:set var='formID' value="new-domain-form" />
    <c:set var='formTitle' value="Update ${ domain.name }" />
    <c:set var='submitCallback' value='submitNewDomainForm(event)' />
    <c:set var='submitText' value='Update' />

</c:otherwise>
</c:choose>

<!DOCTYPE html>
<div class='modal-dialog'>
<div class='modal-content'>

    <div class='modal-header' >
        <button type='button' class='close' data-dismiss='modal'>
            <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
        </button>
        <h4 class='modal-title' id='new-role-label' ><span class='glyphicon glyphicon-tower' ></span> <c:out value='${ formTitle }' /></h4>
    </div>

    <div class='modal-body' >
        <form:form modelAttribute='domain' action='${ appRoot }${ postDomainAction }?oldId=${ oldId }' method='POST' id='${ formID }' class='form-vretical' >
        <fieldset>
            
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
                    <button id='new-domain-submit' type='submit' class='btn btn-success submit-row-btn new-domain-input' 
                            onclick='${ submitCallback }' > ${ submitText } </button>
                </div>
                <div class='col-sm-2'>
                    <button type='button' class='btn btn-default submit-row-btn new-domain-input' data-dismiss='modal' >Cancel</button>
                </div>
                
                <div hidden='true'>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
                </div>
            </div>
        </fieldset>
        </form:form>
    </div>

    <div class='modal-footer' >
    </div>

</div>
</div>