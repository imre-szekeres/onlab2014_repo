<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
    <c:forEach var='domain' items='${ domains }' >  
        <c:forEach var='role' items='${ domain.roles }' >
            
            <div class='panel panel-default' >
                <div class='panel-heading' role='tab' id='role-${ role.id }-heading' >
                    <h3 class='panel-title'>
                        <a data-toggle='collapse' data-parent='#admin-roles-content-wrapper' href='collapse-${ role.id }' > ${ role.name } </a>
                    </h3>
                </div>
                <div id='collapse-${ role.id }' class='panel-collapse collapse in' role='tabpanel' 
                     aria-labelledby='role-${ role.id }-heading' >
                    <div class='panel-body' ></div>
                </div>
            </div>
            
        </c:forEach>
    </c:forEach>
</div>

<div id='admin-roles-create-wrapper' >
    <button class='btn btn-primary' id='create-role-trigger' data-toggle='modal' data-target='#new-role-modal' >Create Role</button>
</div>

<div class='modal fade' id='new-role-modal' tabindex='-1' role='dialog' aria-labelledby='#new-role-label' aria-hidden='true' >
    
    <form:form modelAttribute='newRole' action='${ appRoot }/roles/create' method='POST' id='new-role-form' class='form-horizontal' >
    <div class='modal-dialog' >
    <div class='modal-content' >
        <div class='modal-header' >
            <button type='button' class='close' data-dismiss='modal'>
                <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
            </button>
            <h4 class='modal-title' id='new-role-label' >Create Role</h4>
        </div>
        
        <div class='modal-body'>
            <div id='new-role-form-fieldset-wrapper' >
            <fieldset>
                
                <div class='form-group' >
                </div>
                
                <div class='form-group' >
                    <label class='control-label col-sm-2' for='role-name' >
                        <spring:message code='role.form.name.label' ></spring:message>
                    </label>
                    <div class='col-sm-4'>
                        <form:input id='role-name' path='roleName' type='text' class='form-control new-role-input' ></form:input>
                    </div>
                </div>
                
                <div class='form-group' >
                    <label class='control-label col-sm-2' for='role-domain' >
                        <spring:message code='role.form.domain.label' ></spring:message>
                    </label>
                    <div class='col-sm-4' >
                        <form:select id='role-domain' path="domainName" class='form-control new-role-input' >
                            <c:forEach var='domain' items='${ domains }' >
                                <form:option value='${ domain.name }' >${ domain.name }</form:option>
                            </c:forEach>
                        </form:select>
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
                            <strong>
                                <spring:message code='role.form.privileges.label' ></spring:message>
                            </strong>    
                        </div>
                        
                        <div class='panel-body'>
                            <div id='privileges-dnd-input-wrapper' class='col-sm-6 pos-rel' >
                                <div id='privileges-dnd-input-content' class='col-sm-6 pos-rel input-content' >
                                </div>
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
                            <spring:message code='privileges.dnd.available.label' ></spring:message>
                        </strong>
                    </div>
                    
                    <div class='panel-body'>
                        <div id='privileges-dnd-source-wrapper' class='col-sm-6 pos-rel' >
                            <div id='privileges-dnd-source-content' class='col-sm-6 pos-rel source-content' >
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class='modal-footer' >
            <button type='submit' class='btn btn-primary' onclick='submitNewRoleForm(event)' >Submit</button>
            <button type='button' class='btn btn-default' data-dismiss='modal' >Cancel</button>
        </div>
    </div>
    </div>
    </form:form>

</div>

<script>
    var url_root = "${ selectPrivilegesUrl }";
    var $_avail_privs = 'None';
    var $_priv_src_wrapper = $('#privileges-dnd-source-wrapper');
    
    var $_privs_in = $('#role-privileges');

    function submitNewRoleForm(event) {
    	$('#new-role-form').submit();
    }
    
    function queryPrivileges($_target) {
    	var url_ = "${ appRoot }" + url_root;
    	$.ajax({
    		url: url_,
    		dataType: 'html',
    		method: "GET",
    		success: function(data) {
    			$_target = data;
    		    $_priv_src_wrapper.find('.source-content').html( data );
    		}
    	});
    }
    
    function onCreateClick(event) {
    	console.log("role create triggered");
    	if ($_avail_privs == 'None') {
    		queryPrivileges($_avail_privs);
    	} else {
    		$_priv_src_wrapper.find('.source-content').html( $_avail_privs );
    	}
    }
    
    function allowDrop(event) {
    	event.preventDefault();
    }
    
    function onInputDrop(event) {
    	var sourceID = event.dataTransfer.getData("elementID");
    	$(document.getElementById( sourceID )).appendTo($(event.target).find('.input-content'));
    	
    	var value = $_privs_in.val();
    	var not_contains = value.indexOf(sourceID) < 0;
    	if (value && not_contains)
    		value += "|" + sourceID;
    	else if (not_contains)
    		value = sourceID;
    	
    	$_privs_in.val( value );
    	console.log( $_privs_in.val() );
    }
    
    function onSourceDrop(event) {
    	var sourceID = event.dataTransfer.getData("elementID");
    	$(document.getElementById( sourceID )).appendTo($_priv_src_wrapper.find('.source-content'));
    	
    	var value = $_privs_in.val();
    	value = value.replace(sourceID, '');
    	value = value.replace('||', '|');
    	
    	if (value.trim() == '|')
            value = '';

        $_privs_in.val( value );
    	console.log( $_privs_in.val() );
    }

    $('#create-role-trigger').click( onCreateClick );
</script>