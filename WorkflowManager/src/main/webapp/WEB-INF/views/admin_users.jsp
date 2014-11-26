<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions'   prefix='fn' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<c:set var='labelColClass' value='col-sm-3' />
<c:set var='inputColClass' value='col-sm-9' />
<c:set var='inputAreaColClass' value='col-sm-12' />

<c:if test="${ fn:indexOf(header['User-Agent'], 'Firefox/') > -1 }" >
    <c:set var='labelColClass' value='col-sm-2' />
    <c:set var='inputColClass' value='col-sm-4' />
    <c:set var='inputAreaColClass' value='col-sm-6' />
</c:if>

<div id='roles-list-panel' class='panel panel-default' >
<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
    <c:forEach var='user' items='${ users }' >  
        
        <div class='panel panel-default admin-role-panel admin-user-panel' >
                <div class='panel-heading' role='tab' id='user-${ user.id }-heading' >
                    <h3 class='panel-title'>
                        <a class='collapsed user-collapsed-href' 
                           aria-expanded='false' 
                           aria-controls='collapse-${ user.id }' 
                           data-toggle='collapse' 
                           data-parent='#admin-roles-content-wrapper' 
                           href='#collapse-${ user.id }' 
                           name='${ user.id }' >${ user.username }</a>
                    </h3>
                </div>
                <div id='collapse-${ user.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='user-${ user.id }-heading' >
                     
                     <div class='role-list-wrapper' requestable='true' name='${ user.id }' >
                     </div>
                     
                </div>
        </div>
        
    </c:forEach>
</div>
</div>

<div id='admin-users-create-wrapper' >
    <button class='btn btn-primary' id='create-user-trigger' data-toggle='modal' data-target='#new-user-modal' >
        <span class="glyphicon glyphicon-plus new-user-icon" aria-hidden="true" ></span> Create User
    </button>
</div>

<div class='modal fade' id='new-user-modal' tabindex='-1' role='dialog' aria-labelledby='#new-user-label' aria-hidden='true' >
<div class='modal-dialog'>
<div class='modal-content'>

    <form:form action='${ appRoot }${ createUserAction }' id='new-user-form' modelAttribute='newUser' method='POST' class='form-vertical' >
        <div class='modal-header'>
            <button type='button' class='close' data-dismiss='modal'>
                <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
            </button>
            <h4 class='modal-title' id='new-user-label' ><span class='glyphicon glyphicon-user' ></span> Create User</h4>
        </div>
        
        <div class='modal-body'>
            <div id='new-user-form-fieldset-wrapper' class='container new-user-container pos-rel' >
            <fieldset>

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
                                               rows='5' cols='31' />
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class='form-group row new-user-row'>
                    <label class='control-label ${ labelColClass }' for='domain-name-select' >
                        <spring:message code='role.form.domain.label' ></spring:message>
                    </label>
                    <div class='${ inputColClass }'>
                        <form:select id='domain-name-select' path='domainName' class='form-control new-user-input' >
                        </form:select>
                    </div>
                </div>
                
                <div class='form-group row new-user-row hidden-row' hidden='true' >
                    <div class='${ inputColClass }'>
                        <form:input id='user-roles' path='userRoles' type='hidden' class='form-control new-user-input user-roles' ></form:input>
                    </div>
                </div>
                
                <div class='form-group row new-user-row' >
                    <div id='privileges-dnd-target-panel' class='panel panel-default new-role-privileges-input-panel pos-rel' 
                         ondragover='allowDrop(event)' ondrop='onInputDrop(event)' >
                        <div class='panel-heading'>
                            <strong>
                                <spring:message code='user.form.roles.label' ></spring:message> <span id='username-placeholder'></span>
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
            
            <div id='privileges-dnd-source-panel-wrapper' class='pos-rel' >
                <div id='privileges-dnd-source-panel' class='panel panel-default' 
                     ondragover='allowDrop(event)' ondrop='onSourceDrop(event)' >
                    <div class='panel-heading'>
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
            
        </div>
        
        <div class='modal-footer'>
            <button type='submit' class='btn btn-primary' onclick='submitNewUserForm(event)' >Create</button>
            <button type='button' class='btn btn-default' data-dismiss='modal' >Cancel</button>
        </div>
    </form:form>

</div>
</div>
</div>

<script>

   var da_root = "${ appRoot }${ selectDomainsForUrl }";
   var roles_url = "${ appRoot }${ selectRolesForUrl }";
   var dnames_root = "${ appRoot }${ selectDomainNamesUrl }";
   
   var $_domains_select = $('#domain-name-select');
   var $_dname_plh = $('#domain-name-placeholder');
   
   var $_username_in = $('#username');
   var $_uname_plh = $('#username-placeholder');

   
   var $_avail_roles = 'None';
   var $_roles_src_wrapper = $('#privileges-dnd-source-wrapper');
   var $_roles_input_wrapper = $('#privileges-dnd-input-wrapper');
   
   var $_roles_in = $('#user-roles');

   var $_nuser_from = $('#new-user-form');
   
   function submitNewUserForm(event) {
	   $_nuser_form.submit();
   }
   
   
   function requestUserRolesFor( $_list_wrapper ) {
	   var url_ = da_root + "?userID=" + $_list_wrapper.attr('name');
	   $.ajax({
		   url: url_,
		   dataType: "html",
		   method: "GET",
		   success: function(data) {
			   $_list_wrapper.html( data );
			   $_list_wrapper.attr('requestable', 'false');
			   $_list_wrapper.parent().parent().find('.collapse').collapse('show');
			   $('[data-toggle="tooltip"]').tooltip();
		   }
	   });
   }

   function requestDomainNames($_domains_select) {
	    $.ajax({
	    	url: dnames_root,
	    	dataType: 'html',
	    	method: 'GET',
	    	success: function(data) {
	    		$_domains_select.empty();
	    		$(data).appendTo( $_domains_select );
	    		$_domains_select.trigger('change');
	    	}
	    });
   }
   
   $('.user-collapsed-href').click(function(event) {
	   var $_target = $(event.target);
	   var $_list_wrapper = $_target.parent().parent().parent().find('.role-list-wrapper');

	   if ($_list_wrapper.attr('requestable') == 'true') {
		   requestUserRolesFor( $_list_wrapper );
	   }
   });
   
   function allowDrop(event) {
       event.preventDefault();
   }
   
   function onInputDrop(event) {
       var sourceID = event.dataTransfer.getData("elementID");
       $(document.getElementById( sourceID )).appendTo( $_roles_input_wrapper );
       
       var value = $_roles_in.val();
       var not_contains = value.indexOf(sourceID) < 0;
       if (value && not_contains)
           value += "|" + sourceID;
       else if (not_contains)
           value = sourceID;
       
       $_roles_in.val( value );
       console.log( $_roles_in.val() );
   }
   
   function onSourceDrop(event) {
       var sourceID = event.dataTransfer.getData("elementID");
       $(document.getElementById( sourceID )).appendTo( $_roles_src_wrapper );
       
       var value = $_roles_in.val();
       value = value.replace(sourceID, '');
       value = value.replace('||', '|');
       
       if (value.trim() == '|')
           value = '';

       $_roles_in.val( value );
       console.log( $_roles_in.val() );
   }
   
   function requestRolesFor(domain) {
	   var url_ = roles_url + "?domain=" + domain;
	   $.ajax({
		   url: url_,
		   dataType: 'html',
		   method: 'GET',
		   success: function(data) {
			   $_roles_src_wrapper.empty();
			   $(data).appendTo( $_roles_src_wrapper );
		   }
	   });
   }
   

   $('#create-user-trigger').click(function(event) {
	   if ($_domains_select.find('option').length < 1) {
		   requestDomainNames( $_domains_select );
	   }
   });
   
   $_domains_select.change(function(event) {
	   requestRolesFor( $(this).val() );
	   $_dname_plh.html( $_domains_select.val() );
   });
   
   $_uname_plh.html( $_username_in.val() );
   $_dname_plh.html( $_domains_select.val() );
   
   $_username_in.keyup(function() {
	    $_uname_plh.html( $_username_in.val() );	   
   });
</script>

<c:if test='${ not empty validationErrors }' >
<script>
    $('#create-user-trigger').trigger('click');
    $('.has-error').tooltip({
        placement: 'top'
    });
</script>
</c:if>
