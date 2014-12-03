<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 

<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<div id='roles-list-panel' class='panel panel-default admin-panel role-panel' >
<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
    <jsp:include page='fragments/roles_list.jsp' />
</div>
</div>

<div id='admin-roles-create-wrapper' >
    <button class='btn btn-primary' id='create-role-trigger' data-toggle='modal' data-target='#new-role-modal' >
        <span class="glyphicon glyphicon-plus new-role-icon" aria-hidden="true" ></span> Create Role
    </button>
</div>

<div class='modal fade' id='new-role-modal' tabindex='-1' role='dialog' aria-labelledby='#new-role-label' aria-hidden='true' >
    <script>
       var form_included = false;
    </script>

    <c:if test='${ not empty validationErrors }' >
        
        <jsp:include page='fragments/role_form_modal.jsp'>
            <jsp:param name='formType' value='${ formType }' />
        </jsp:include>
        <script>
            var form_included = true;
        </script>
        
    </c:if>
</div>

<script>
    var privileges_url = "${ appRoot }${ selectPrivilegesUrl }";
    var create_form_url = "${ appRoot }${ selectCreateFormUrl }";
    
    var $_create_role_trigger = $('#create-role-trigger');
    var $_newr_modal = $('#new-role-modal');
    var $_avail_privs = 'None';
    var current_form = 'None';

    function submitNewRoleForm(event) {
    	$('#new-role-form').submit();
    }

    function queryPrivileges($_target) {
    	var url_ = privileges_url;
    	$.ajax({
    		url: url_,
    		dataType: 'html',
    		method: "GET",
    		success: function(data) {
    			$_target = data;
    		    $_priv_src_wrapper.html( data );
    		    rearrangePrivileges( $_newr_modal );
    		}
    	});
    }
   
    function requestPrivileges() {
    	if ($_avail_privs == 'None') {
            queryPrivileges($_avail_privs);
        } else {
            $_priv_src_wrapper.html( $_avail_privs );
        }
    }
    
    function requestCreateForm() {
    	$.ajax({
    		url: create_form_url,
    		dataType: 'html',
    		method: 'GET',
    		success: function(data) {
    			$_newr_modal.empty();
    			$(data).appendTo( $_newr_modal );
    			requestPrivileges();
    			current_form = 'create';
    		}
    	});
    }
    
    function onCreateClick(event) {
    	if (current_form !== 'create')
    		requestCreateForm();
    	else
    		requestPrivileges();
    }
    
    function allowDrop(event) {
    	event.preventDefault();
    }
    
    function onInputDrop(event) {
    	var sourceID = event.dataTransfer.getData("elementID");
    	$(document.getElementById( sourceID )).appendTo( $_priv_input_wrapper );
    	
    	var value = $_privs_in.val();
    	var not_contains = value.indexOf(sourceID) < 0;
    	if (value && not_contains)
    		value += "|" + sourceID;
    	else if (not_contains)
    		value = sourceID;
    	
    	$_privs_in.val( value );
    }
    
    function onSourceDrop(event) {
    	var sourceID = event.dataTransfer.getData("elementID");
    	$(document.getElementById( sourceID )).appendTo( $_priv_src_wrapper );
    	
    	var value = $_privs_in.val();
    	value = value.replace(sourceID, '');
    	value = value.replace('||', '|');
    	
    	if (value.trim() == '|')
            value = '';

        $_privs_in.val( value );
    }
    
    function contains(list, value) {
    	for(var i = 0; i < list.length; ++i)
    		if (list[i] == value)
    			return true;
    	return false;
    }
    
    function rearrangePrivileges($_modal) {
    	var old_privs = $_privs_in.val().split('|');

        for(var i = 0; i < old_privs.length; ++i) {
    		var id = old_privs[i];
    		
    		var $_e = $_priv_src_wrapper.find('[name="' + id + '"]');
    		$_e.appendTo( $_priv_input_wrapper );
    	}
    }

    $_create_role_trigger.click( onCreateClick );
    $('.collapse').collapse();
    $('[data-toggle="tooltip"]').tooltip({
    	placement: 'bottom'
    });
    
    $.each($('.edit-icon-href'), function(index, href) {
    	var $_href = $(href);
        $_href.click(function(event) {
        	
        	event.preventDefault();
        	var url_ = $_href.attr('href');
        	$.ajax({
        		url: url_,
        		dataType: 'html',
        		method: 'GET',
        		success: function(data) {
        			$_newr_modal.empty();
                    $(data).appendTo( $_newr_modal );
                    current_form = 'create';
                    $_create_role_trigger.trigger('click');
                    current_form = 'update';
        		}
        	});
        });
    });
</script>

<c:if test='${ not empty validationErrors }' >
<script>
    current_form = 'create';
    $_create_role_trigger.trigger('click');
    $('.has-error').tooltip({
    	placement: 'top'
    });
    current_form = '${ formType }';
</script>
</c:if>