<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib tagdir='/WEB-INF/tags/' prefix='wman' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />
<c:set var='formType' value="${ empty formType ? 'create' : formType }" />
<c:set var='formType' value='${ not empty param.formType ? param.formType : formType }' />

<c:choose >
<c:when test="${ formType eq 'create' }" >
    
    <c:set var='formID' value="new-role-form" />
    <c:set var='formTitle' value="Create Role" />
    <c:set var='submitCallback' value='submitNewRoleForm(event)' />
    <c:set var='submitText' value='Create' />
    
</c:when>
<c:otherwise>
    
    <c:set var='formID' value="update-role-form" />
    <c:set var='formTitle' value="Update ${ role.roleName }" />
    <c:set var='submitCallback' value='submitUpdateRoleForm(event)' />
    <c:set var='submitText' value='Update' />

</c:otherwise>
</c:choose>

<!DOCTYPE html>
<form:form modelAttribute='role' action='${ appRoot }${ postRoleAction }' method='POST' id='${ formID }' class='form-horizontal' >
    <div class='modal-dialog' >
    <div class='modal-content' >
        <div class='modal-header' >
            <button type='button' class='close' data-dismiss='modal'>
                <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
            </button>
            <h4 class='modal-title' id='new-role-label' ><span class='glyphicon glyphicon-screenshot' ></span> ${ formTitle }</h4>
        </div>
        
        <div class='modal-body'>
            <div id='new-role-form-fieldset-wrapper' >
            <fieldset>
                
                <div hidden='true'>
                    <form:input type='hidden' path='id' readonly='true'></form:input>
                </div>
                
                <div class='form-group' >
                    <label class='control-label col-sm-2' for='role-name' >
                        <spring:message code='role.form.name.label' ></spring:message>
                    </label>
                    <div class='col-sm-4'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.name }' >
                                
                                <span class='has-error' title='${ validationErrors.name }' data-toggle='tooltip' >
                                    <form:input id='role-name' path='roleName' type='text' class='form-control new-role-input' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='role-name' path='roleName' type='text' class='form-control new-role-input' ></form:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class='form-group' >
                    <label class='control-label col-sm-2' for='role-domain' >
                        <spring:message code='role.form.domain.label' ></spring:message>
                    </label>
                    <div class='col-sm-4' >
                        <div class='input-group' >
                        
                                <c:set var='isReadonly' value="${ formType ne 'create' }" />
                        
                                <span class='input-group-addon' ><span class='glyphicon glyphicon-tower'></span></span>
                                <c:choose >
                                    <c:when test='${ not empty validationErrors and not empty validationErrors.domain }' >
                                        
                                        <span class='has-error' title='${ validationErrors.domain }' data-toggle='tooltip' >
                                            <wman:select selectId='role-domain' 
                                                         selectClass='form-control new-role-input' 
                                                         path='domainName' 
                                                         isReadonly='${ isReadonly }' 
                                                         elements='${ domainNames }' 
                                                         readonlyValue='${ role.domainName }' />
                                        </span>
                                        
                                    </c:when>
                                    <c:otherwise>
                                        <wman:select selectId='role-domain' 
                                                     selectClass='form-control new-role-input' 
                                                     path='domainName' 
                                                     isReadonly='${ isReadonly }' 
                                                     elements='${ domainNames }' 
                                                     readonlyValue='${ role.domainName }' />
                                    </c:otherwise>
                                </c:choose>
                         </div>
                    </div>
                </div>

                <div id='privileges-input-wrapper' class='form-group' >
                    <div class='col-sm-7'>
                        <form:input id='role-privileges' type='hidden' path='privileges' class='form-control new-role-input privileges' ></form:input>
                    </div>
                </div>
                
                <div class='form-group' >
                    <div id='privileges-dnd-target-panel' class='panel panel-default new-role-privileges-input-panel pos-rel' 
                         ondragover='allowDrop(event)' ondrop='onInputDrop(event)' >
                        <div class='panel-heading'>
                            <span class='glyphicon glyphicon-flag' ></span>
                            <strong>
                                <spring:message code='role.form.privileges.label' ></spring:message> <span id='role-name-placeholder'></span>
                            </strong>    
                        </div>
                        
                        <div class='panel-body'>
                            <div id='privileges-dnd-input-wrapper' class='col-sm-6 pos-rel input-content' >
                            </div>
                        </div>
                    </div>
                </div>
                
            </fieldset>
            </div>
            
            <div id='privileges-dnd-source-panel-wrapper' class='pos-rel' >
                <div id='privileges-dnd-source-panel' class='panel panel-default' 
                     ondragover='allowDrop(event)' ondrop='onSourceDrop(event)' >
                    <div class='panel-heading'>
                        <span class='glyphicon glyphicon-flag' ></span>
                        <strong>
                            <spring:message code='privileges.dnd.available.label' ></spring:message>
                        </strong>
                    </div>
                    
                    <div class='panel-body'>
                        <div id='privileges-dnd-source-wrapper' class='col-sm-6 pos-rel source-content' >
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class='modal-footer' >
            <button type='submit' class='btn btn-primary' onclick='${ submitCallback }' >${ submitText }</button>
            <button type='button' class='btn btn-default' data-dismiss='modal' >Cancel</button>
        </div>
    </div>
    </div>
</form:form>

<script >

	var $_priv_src_wrapper = $('#privileges-dnd-source-wrapper');
	var $_priv_input_wrapper = $('#privileges-dnd-input-wrapper');
	var $_privs_in = $('#role-privileges');

	var $_rname_plh = $('#role-name-placeholder');
	var $_rolename_in = $('#role-name');

	$_rname_plh.html( $_rolename_in.val() );
	$_rolename_in.keyup(function() {
	    $_rname_plh.html( $_rolename_in.val() );
	});
</script>