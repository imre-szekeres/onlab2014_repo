<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions'   prefix='fn' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<c:set var='labelColClass' value='col-sm-2' />
<c:set var='inputColClass' value='col-sm-4' />
<c:set var='inputAreaColClass' value='col-sm-6' />

<c:if test="${ fn:indexOf(header['User-Agent'], 'Firefox/') > -1 }" >
    <c:set var='labelColClass' value='col-sm-2' />
    <c:set var='inputColClass' value='col-sm-4' />
    <c:set var='inputAreaColClass' value='col-sm-6' />
</c:if>

<!DOCTYPE html>
<div class='details-forms-wrapper' >
    
    <div id='user-details-panel' class='info-panel update-form-panel' >
        <form:form action='${ appRoot }${ updateDetailsAction }' method='POST'  
                   modelAttribute='updated' class='form-horizontal' id='update-details-form' >
            <fieldset>
 
                <div hidden='true' >
                    <form:input path='id' type='hidden' class='form-control new-user-input' readonly='true' ></form:input>
                </div>
            
                <div class='form-group row new-user-row'>
                    <label class='control-label ${ labelColClass }' for='username' >
                        <spring:message code='user.form.username.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.username }' >
                                
                                <span class='has-error' title='${ validationErrors.username }' data-toggle='tooltip' >
                                    <form:input id='username' path='username' type='text' class='form-control new-user-input' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='username' path='username' type='text' class='form-control new-user-input' ></form:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class='form-group row new-user-row'>
                    <label class='control-label ${ labelColClass }' for='email' >
                        <spring:message code='user.form.email.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.email }' >
                                
                                <span class='has-error' title='${ validationErrors.email }' data-toggle='tooltip' >
                                    <form:input id='email' path='email' type='text' class='form-control new-user-input' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='email' path='email' type='text' class='form-control new-user-input' ></form:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class='form-group row new-user-row'>
                    <label class='control-label ${ inputAreaColClass }' for='description' >
                        <span class='description-label' >
                            <spring:message code='user.form.description.label' ></spring:message>
                        </span>
                    </label>
                </div>
                <div class='form-group row new-user-row' >
                    <div class='${ inputAreaColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.description }' >
                                
                                <span class='has-error' title='${ validationErrors.description }' data-toggle='tooltip' >
                                    <form:textarea id='description' path='description' type='text' class='form-control new-user-input' 
                                                   rows='5' cols='31' />
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:textarea id='description' path='description' type='text' class='form-control new-user-input' 
                                               rows='5' cols='31' placeholder='A brief introduction.' />
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </fieldset>
            
            <div id='submit-details-trigger-wrapper' class='submit-trigger-wrapper' >
                <button id='details-submit-trigger' onclick='submitDetailsForm(event)' type='button' class='btn btn-success' >Update</button>
            </div>
        </form:form>
    </div>
    
    <div id='user-password-panel' class='warning-panel update-form-panel' >
        <form:form action='${ appRoot }${ updatePasswordAction }' method='POST'  
                   modelAttribute='updated' class='form-horizontal' id='update-passwords-form' >
            <fieldset>
            
                <div hidden='true' >
                    <form:input path='id' type='hidden' class='form-control new-user-input' readonly='true' ></form:input>
                </div>
                
                <div class='form-group row new-user-row'>
                    <label class='control-label ${ labelColClass }' for='old-password' >
                        <spring:message code='user.form.oldPassword.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.oldPassword }' >
                                
                                <span class='has-error' title='${ validationErrors.oldPassword }' data-toggle='tooltip' >
                                    <form:input id='old-password' path='oldPassword' type='password' class='form-control new-user-input' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='old-password' path='oldPassword' type='password' class='form-control new-user-input' ></form:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            
                <div class='form-group row new-user-row'>
                    <label class='control-label ${ labelColClass }' for='password' >
                        <spring:message code='user.form.password.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.password }' >
                                
                                <span class='has-error' title='${ validationErrors.password }' data-toggle='tooltip' >
                                    <form:input id='password' path='password' type='password' class='form-control new-user-input' value='' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='password' path='password' type='password' class='form-control new-user-input' value='' ></form:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            
                <div class='form-group row new-user-row'>
                    <label class='control-label ${ labelColClass }' for='confirm' >
                        <spring:message code='user.form.passwordConfirm.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.confirmPassword }' >
                                
                                <span class='has-error' title='${ validationErrors.confirmPassword }' data-toggle='tooltip' >
                                    <form:input id='confirm' path='confirmPassword' type='password' class='form-control new-user-input' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='confirm' path='confirmPassword' type='password' class='form-control new-user-input' ></form:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            
            </fieldset>
            <div id='submit-passwords-trigger-wrapper' class='submit-trigger-wrapper' >
                <button id='details-submit-trigger' onclick='submitPasswordsForm(event)' type='button' class='btn btn-success' >Update</button>
            </div>
        </form:form>
    </div>
</div>

<script>
    function submitPasswordsForm(event) {
    	$('#update-passwords-form').submit();
    }
    
    function submitDetailsForm(event) {
        $('#update-details-form').submit();
    }
</script>