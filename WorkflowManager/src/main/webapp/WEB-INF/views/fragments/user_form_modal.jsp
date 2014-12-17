<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions'   prefix='fn' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>
<%@ taglib tagdir='/WEB-INF/tags/' prefix='wman' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />
<c:set var='formType' value="${ empty formType ? 'create' : formType }" />
<c:set var='formType' value='${ not empty param.formType ? param.formType : formType }' />

<c:choose >
<c:when test="${ formType eq 'create' }" >
    
    <c:set var='formTitle' value="Create User" />
    <c:set var='submitText' value='Create' />
    
</c:when>
<c:otherwise>
    
    <c:set var='formTitle' value="Assign Domains and Roles to ${ user.username }" />
    <c:set var='submitText' value='Update' />
    <c:set var='inputPanelClass' value='update-dnr-input-panel' />
    <c:set var='sourcePanelClass' value='update-dnr-source-panel' />

</c:otherwise>
</c:choose>

<c:set var='labelColClass' value='col-sm-3' />
<c:set var='inputColClass' value='col-sm-9' />
<c:set var='inputAreaColClass' value='col-sm-12' />

<c:if test="${ fn:indexOf(header['User-Agent'], 'Firefox/') > -1 }" >
    <c:set var='labelColClass' value='col-sm-2' />
    <c:set var='inputColClass' value='col-sm-4' />
    <c:set var='inputAreaColClass' value='col-sm-6' />
</c:if>

<!DOCTYPE html>
<div class='modal-dialog' >
<div class='modal-content' >
    <form:form action='${ appRoot }${ postUserAction }' id='new-user-form' modelAttribute='user' method='POST' class='form-vertical' >
        <div class='modal-header'>
            <button type='button' class='close' data-dismiss='modal'>
                <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
            </button>
            <h4 class='modal-title' id='new-user-label' ><span class='glyphicon glyphicon-user' ></span> <c:out value='${ formTitle }' /></h4>
        </div>
        
        <div class='modal-body'>
            <div id='new-user-form-fieldset-wrapper' class='container new-user-container pos-rel' >
            <fieldset>

                <div hidden='true' >
                    <form:input path='id' type='hidden' readonly='true' ></form:input>
                </div>

<c:choose>
<c:when test='${ formType eq "create" }'>
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
                    <label class='control-label ${ labelColClass }' for='password' >
                        <spring:message code='user.form.password.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <c:choose >
                            <c:when test='${ not empty validationErrors and not empty validationErrors.password }' >
                                
                                <span class='has-error' title='${ validationErrors.password }' data-toggle='tooltip' >
                                    <form:input id='password' path='password' type='password' class='form-control new-user-input' ></form:input>
                                </span>
                                
                            </c:when>
                            <c:otherwise>
                                <form:input id='password' path='password' type='password' class='form-control new-user-input' ></form:input>
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
                        <spring:message code='user.form.description.label' ></spring:message>
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
</c:when>
<c:otherwise>

<link rel='stylesheet' type='text/css' href='${ appRoot }/resources/css/user-dnr-style.css' ></link>
<c:set var='domainSelectRowClass' value='domain-name-select-row' />
<c:set var='domainSelectWrapperClass' value='domain-name-select-wrapper' />

</c:otherwise>
</c:choose>

                <div class='form-group row new-user-row ${ domainSelectRowClass } '>
                    <label class='control-label ${ labelColClass }' for='domain-name-select' >
                        <spring:message code='role.form.domain.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass } ${ domainSelectWrapperClass }'>
                        <div class='input-group' >
                            <span class='input-group-addon' ><span class='glyphicon glyphicon-tower' ></span></span>
                            <select id='domain-name-select' path='domainName' class='form-control new-user-input' >
                                <wman:optionList options='${ domains }' />
                            </select>
                        </div>

                        <c:if test='${ formType ne "create" }' >
                        <span id='remove-domain-icon' data-toggle='tooltip' data-placement='right' title='Deassign <c:out value="${ user.username }" /> from domain' >
	                        <span class='glyphicon glyphicon-remove' onclick='removeDomain(event)' ></span>
	                    </span>
	                    </c:if>
                    </div>
                </div>

                <div class='form-group row new-user-row hidden-row' hidden='true' >
                    <div class='${ inputColClass }'>
                        <form:input id='user-roles' path='userRoles' type='hidden' class='form-control new-user-input user-roles' ></form:input>
                    </div>
                </div>
                
                <div class='form-group row new-user-row' >
                    <div id='privileges-dnd-target-panel' class='panel panel-default new-role-privileges-input-panel pos-rel ${ inputPanelClass }' 
                         ondragover='allowDrop(event)' ondrop='onInputDrop(event)' >
                        <div class='panel-heading'>
                            <span class='glyphicon glyphicon-screenshot' ></span>
                            <strong>
                                <spring:message code='user.form.roles.label' ></spring:message> <span id='username-placeholder'><c:out value='${ user.username }' /></span>
                            </strong>    
                        </div>
                        
                        <div class='panel-body'>
                            <div id='privileges-dnd-input-wrapper' class='col-sm-6 pos-rel' >
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            </div>
            
            <div id='privileges-dnd-source-panel-wrapper' class='pos-rel ${ sourcePanelClass }' >
                <div id='privileges-dnd-source-panel' class='panel panel-default' 
                     ondragover='allowDrop(event)' ondrop='onSourceDrop(event)' >
                    <div class='panel-heading'>
                        <span class='glyphicon glyphicon-screenshot' ></span>
                        <strong>
                            <spring:message code='roles.dnd.available.label' ></spring:message> <span id='domain-name-placeholder' ></span>
                        </strong>
                    </div>
                    
                    <div class='panel-body'>
                        <div id='privileges-dnd-source-wrapper' class='col-sm-6 pos-rel' >
                        </div>
                    </div>
                </div>
            </div>
            
            <div hidden='true'>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
            </div>
            
        </div>
        
        <div class='modal-footer'>
            <button type='submit' class='btn btn-primary' onclick='submitNewUserForm(event)' >${ submitText }</button>
            <button type='button' class='btn btn-default' data-dismiss='modal' >Cancel</button>
        </div>
    </form:form>
</div>
</div>

<script>

	var $_domains_select = $('#domain-name-select');
	var $_remove_d_icon = $('#remove-domain-icon');
	var $_dname_plh = $('#domain-name-placeholder');
	var $_username_in = $('#username');
	var $_uname_plh = $('#username-placeholder');

	var $_avail_roles = 'None';
	var $_roles_src_wrapper = $('#privileges-dnd-source-wrapper');
	var $_roles_input_wrapper = $('#privileges-dnd-input-wrapper');
	var $_roles_in = $('#user-roles');
	var $_nuser_from = $('#new-user-form');

	var domains_n_roles = JSON.parse('${ user.userRoles }');
	
	console.log(domains_n_roles);

	function submitNewUserForm(event) {
	    var json_str = JSON.stringify( domains_n_roles );
	    $_roles_in.val( json_str );
	    $_nuser_form.submit();
	}

	function appendValue(value, key, dict) {
		if (!dict[ key ])
			dict[ key ] = JSON.parse('[]');
		dict[ key ].push( value );
		return true;
	}
	
	function removeValue(value, key, dict) {
		if (!dict[ key ])
			return true;

		$.each(dict[ key ], function(index, val) {
			if (val == value)
				dict[ key ].splice(index, 1);
		});

		if (dict[ key ].length <= 0)
			delete dict[ key ];
		return true;
	}

	function removeDomain(event) {
		var domain = $_domains_select.val();
		delete domains_n_roles[domain];
		$_roles_input_wrapper.find('[owner="' + domain + '"]').remove();
	}

	function hasDuplicate(selector, $_to, owner) {
	    return $_to.find( selector ).length > 0;
	}
	
	function onInputDrop(event) {
	    var sourceID = event.dataTransfer.getData("elementID");
	    var selector = '[id="' + sourceID  + '"]';
	    var $_element = $_roles_src_wrapper.find( selector );
	    var current_owner = $_domains_select.val();

	    if (($_element.attr('owner') == current_owner) && 
	    		hasDuplicate(selector, $_roles_input_wrapper)) {
	    	$_roles_input_wrapper.find( selector ).remove();

	    } else {
	    	appendValue(sourceID, current_owner, domains_n_roles);
	    }

	    $_element.appendTo( $_roles_input_wrapper );
	    applyTooltip($_element, current_owner);
	}
	
	function appendToSource($_element, $_source, current_owner, dict) {
	    if ($_element.attr('owner') == current_owner) {
	    	$_element.appendTo( $_source );
	    	$_element.tooltip('destroy');
	    	removeValue($_element.attr('id').trim(), current_owner, dict);
	    }
	}
	   
	function onSourceDrop(event) {
	    var sourceID = event.dataTransfer.getData("elementID");
	    var selector = '[id="' + sourceID  + '"]';
	    var $_element = $_roles_input_wrapper.find( selector );
	    var current_owner = $_domains_select.val();

	    if (($_element.attr('owner') == current_owner) && 
	    		hasDuplicate(selector, $_roles_src_wrapper)) {
	    	$_roles_input_wrapper.find( selector ).remove();
	    	removeValue($_element.attr('id'), $_element.attr('owner'), domains_n_roles);

	    } else {
	    	appendToSource($_element, $_roles_src_wrapper, current_owner, domains_n_roles);
	    }
	    
	    console.log('id: ' + $_element.attr('id'));
        console.log(domains_n_roles);
	}

	function applyTooltip($_dnd, text) {
		$_dnd.attr('data-toggle', 'tooltip');
        $_dnd.attr('title', text);
        $_dnd.attr('data-placement', 'left');
        $_dnd.tooltip();
	}
	
	function appendRoles(roles, domain, $_container) {
		for(var index in roles) {
			var $_dnd = $(dndElementOf(roles[index], roles[index], domain, "", "role-body"));
			applyTooltip($_dnd, domain);
			$_dnd.appendTo( $_container );
		}
	}

	function buildUserRoles(dnr_dict, $_container) {
		for(var domain in dnr_dict) {
			var roles = dnr_dict[domain];
			appendRoles(roles, domain, $_container);
		}
	}

	$_domains_select.change(function(event) {
	    requestRolesFor( $(this).val() );
	    var domain = $_domains_select.val();
	    $_dname_plh.text( domain );
	});
	
	$_domains_select.trigger('change');
	   
	$_uname_plh.text( $_username_in.val() );
	$_dname_plh.text( $_domains_select.val() );
	   
	$_username_in.keyup(function() {
	     $_uname_plh.text( $_username_in.val() );       
	});
	
	buildUserRoles(domains_n_roles, $_roles_input_wrapper);
</script>