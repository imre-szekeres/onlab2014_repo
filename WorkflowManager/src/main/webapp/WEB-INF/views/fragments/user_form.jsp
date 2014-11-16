<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id='user-form-wrapper' class='pos-rel'>
    
    <form:form modelAttribute='${ param.userRef }' method='POST' action='${ param.postAction }' class='form-horizontal pos-rel' id='user-form' >
        
        <fieldset>
        <div id='user-form-input-positioner' class='pos-rel' >
	        <div id='username-row' class='user-form-row form-group ${ not empty validationErrors and not empty validationErrors.username ? "has-error" : "" }' >
	            <label class='control-label col-md-1 user-form-label-row' for='username' >
	                <spring:message code='user.form.username.label' />
	            </label>
	            <div class='col-md-3 user-form-input-row'>
	                <form:input id='username' path='username' type='text' class='form-control' />
	            </div> 
	            <c:if test='${ not empty validationErrors and not empty validationErrors.username }' >
                    <div class='col-md-4'>
                        <span class='text-danger error-message-label' >${ validationErrors.username }</span>
                    </div>
                </c:if>
	        </div>
	        
	        <div id='password-row' class='user-form-row form-group ${ not empty validationErrors and not empty validationErrors.password ? "has-error" : "" }' >
	            <label class='control-label col-md-1 user-form-label-row' for='password' >
	                <spring:message code='user.form.password.label' />
	            </label>
	            <div class='col-md-3  user-form-input-row'>
	                <form:input id='password' path='password' type='password' class='form-control' />
	            </div>
                <c:if test='${ not empty validationErrors and not empty validationErrors.password }' >
	                <div class='col-md-4'>
	                     <span class='text-danger error-message-label' >${ validationErrors.password }</span>
	                </div>
	            </c:if>
	        </div>
	        
	        <div id='password-again-row' class='user-form-row form-group ${ not empty validationErrors and not empty validationErrors.confirmPassword ? "has-error" : "" }' >
	            <label class='control-label col-md-1 user-form-label-row' for='passwordAgain' >
	                <spring:message code='user.form.passwordConfirm.label' />
	            </label>
	            <div class='col-md-3 user-form-input-row'>
	                <input id='password-again' name='password-again' type='password' class='form-control' />
	            </div>
	            <c:if test='${ not empty validationErrors and not empty validationErrors.confirmPassword }' >
	                <div class='col-md-4'>
	                    <span class='text-danger error-message-label' >${ validationErrors.confirmPassword }</span>
	                </div>
	            </c:if> 
	        </div>
	        
	        <div id='email-row' class='user-form-row form-group ${ not empty validationErrors and not empty validationErrors.email ? "has-error" : "" }' >
	            <label class='control-label col-md-1 user-form-label-row' for='email' >
	                <spring:message code='user.form.email.label' />
	            </label>
	            <div class='col-md-3 user-form-input-row'>
	                <form:input id='email' path='email' type='text' class='form-control' /> 
	            </div>
	            <c:if test='${ not empty validationErrors and not empty validationErrors.email }' >
	                <div class='col-md-4'>
	                    <span class='text-danger error-message-label' >${ validationErrors.email }</span>
	                </div>
	            </c:if>
	        </div>
	        
	        <div id='description-row' class='user-form-row form-group ${ not empty validationErrors and not empty validationErrors.description ? "has-error" : "" }' >
	            <label class='control-label col-md-1 user-form-label-row' for='description' >
	                <spring:message code='user.form.description.label' />
	            </label>
	            <div class='col-md-3 user-form-input-row'>
	                <form:textarea id='description' path='description' type='text' class='form-control' />
	            </div>
	            <c:if test='${ not empty validationErrors and not empty validationErrors.description }' >
	                <div class='col-md-4'>
	                    <span class='text-danger error-message-label' >${ validationErrors.description }</span>
	                </div>
	            </c:if> 
	        </div>
        </div>
        
        <div class='user-form-row form-row form-group'>
            <div id='user-from-submit-wrapper' class='col-md-8 pos-rel'>
                <input type='submit' id='user-form-submit' class='btn btn-primary pos-rel' value='${ param.submitButtonValue }' />
                <input type='button' id='user-form-cancel'  class='btn btn-warning pos-rel'  value='Cancel' onclick='onUserFormCancel(event)' />
            </div>
        </div>
        </fieldset>
    </form:form>
</div>