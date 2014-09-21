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
        <c:if test='${ not empty validationErrors or not empty validationErrors.username}' >
	        <c:set var='usernameClass' value='has-error' />
	        <div class='user-form-row'>
	            <div class='error-message-wrapper'>
	                <span class='error-message' >${ validationErrors.username }</span>
	            </div>
	        </div>
        </c:if>
        <div id='username-row' class='user-form-row form-group ${ usernameClass }' >
            <label class='control-label col-lg-4' for='username' >
                <spring:message code='user.form.username.label' />
            </label>
            <div class='col-lg-8'>
                <form:input id='username' path='username' type='text' class='form-control' />
            </div> 
        </div>
        
        <c:if test='${ not empty validationErrors or not empty validationErrors.password }' >
	        <c:set var='passwordClass' value='has-error' />
	        <div class='user-form-row'>
	            <div class='error-message-wrapper'>
	                <span class='error-message' >${ validationErrors.password }</span>
	            </div>
	        </div>
        </c:if>
        <div id='password-row' class='user-form-row form-group ${ passwordClass }' >
            <label class='control-label col-lg-4' for='password' >
                <spring:message code='user.form.password.label' />
            </label>
            <div class='col-lg-8'>
                <form:input id='password' path='password' type='password' class='form-control' />
            </div> 
        </div>
        
        <c:if test='${ not empty validationErrors or not empty validationErrors.passwordAgain }' >
	        <c:set var='passwordAgainClass' value='has-error' />
	        <div class='user-form-row'>
	            <div class='error-message-wrapper'>
	                <span class='error-message' >${ validationErrors.passwordAgain }</span>
	            </div>
	        </div>
        </c:if>
        <div id='password-again-row' class='user-form-row form-group ${ passwordAgainClass }' >
            <label class='control-label col-lg-4' for='passwordAgain' >
                <spring:message code='user.form.passwordConfirm.label' />
            </label>
            <div class='col-lg-8'>
                <input id='password-again' name='password-again' type='password' class='form-control' />
            </div> 
        </div>
        
        <c:if test='${ not empty validationErrors or not empty validationErrors.email }' >
	        <c:set var='emailClass' value='has-error' />
	        <div class='user-form-row'>
	            <div class='error-message-wrapper'>
	                <span class='error-message' >${ validationErrors.email }</span>
	            </div>
	        </div>
        </c:if>
        <div id='email-row' class='user-form-row form-group ${ emailClass }' >
            <label class='control-label col-lg-4' for='email' >
                <spring:message code='user.form.email.label' />
            </label>
            <div class='col-lg-8'>
                <form:input id='email' path='email' type='text' class='form-control' /> 
            </div>
        </div>
        
        <c:if test='${ not empty validationErrors or not empty validationErrors.description}' >
	        <c:set var='descriptionClass' value='has-error' />
	        <div class='user-form-row'>
	            <div class='error-message-wrapper'>
	                <span class='error-message' >${ validationErrors.description }</span>
	            </div>
	        </div>
        </c:if>
        <div id='description-row' class='user-form-row form-group ${ descriptionClass }' >
            <label class='control-label col-lg-4' for='description' >
                <spring:message code='user.form.description.label' />
            </label>
            <div class='col-lg-8'>
                <form:textarea id='description' path='description' type='text' class='form-control' />
            </div> 
        </div>
        </div>
        
        <div class='user-form-row form-row form-group'>
            <div id='user-from-submit-wrapper' class='col-lg-12 pos-rel'>
                <input type='submit' id='register-submit' class='btn btn-primary btn-block pos-rel' value='${ param.submitButtonValue }' />
            </div>
        </div>
        </fieldset>
    </form:form>
</div>