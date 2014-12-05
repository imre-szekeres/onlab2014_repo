<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions'   prefix='fn' %>
<%@ taglib uri='http://www.springframework.org/tags'      prefix='spring' %>   
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<!DOCTYPE html>
<div id='roles-list-panel' class='panel panel-default admin-panel user-panel' >
<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
    <jsp:include page='fragments/users_list.jsp' />
</div>
</div>

<div id='admin-users-create-wrapper' >
    <button class='btn btn-primary' id='create-user-trigger' data-toggle='modal' data-target='#new-user-modal' >
        <span class="glyphicon glyphicon-plus new-user-icon" aria-hidden="true" ></span> Create User
    </button>
</div>

<div class='modal fade' id='new-user-modal' tabindex='-1' role='dialog' aria-labelledby='#new-user-label' aria-hidden='true' >
    <script>
       var form_included = false;
    </script>

    <c:if test='${ not empty validationErrors }' >
        
        <jsp:include page='fragments/user_form_modal.jsp'>
            <jsp:param name='formType' value='${ formType }' />
        </jsp:include>
        <script>
            var form_included = true;
        </script>
        
    </c:if>
</div>

<script>

   var da_root = "${ appRoot }${ selectDomainsForUrl }";
   var roles_url = "${ appRoot }${ selectRolesForUrl }";
   var dnames_root = "${ appRoot }${ selectDomainNamesUrl }";

   var $_create_user_trigger = $('#create-user-trigger');
   var $_nuser_modal = $('#new-user-modal');
   
   var create_form_url = "${ appRoot }${ selectCreateFormUrl }";
   var current_form = 'None';

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
   
   function requestCreateForm($_modal) {
       $.ajax({
           url: create_form_url,
           dataType: 'html',
           method: 'GET',
           success: function(data) {
               $_modal.empty();
               $(data).appendTo( $_modal );
               current_form = 'create';
               
               if ($_domains_select.find('option').length < 1) {
                   requestDomainNames( $_domains_select );
               }
           }
       });
   }
   

   $_create_user_trigger.click(function(event) {
	   if (current_form != 'create' && !form_included)
		   requestCreateForm($_nuser_modal);
	   
	   else if ($_domains_select.find('option').length < 1)
           requestDomainNames( $_domains_select );
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
                   $_nuser_modal.empty();
                   $(data).appendTo( $_nuser_modal );
                   form_included = true;
                   current_form = 'update';
                   $_create_user_trigger.trigger('click');
                   form_included = false;
                   $_nuser_modal.find('[data-toggle="tooltip"]').tooltip();
               }
           });
       });
   });
</script>

<c:if test='${ not empty validationErrors }' >
<script>
    $_create_user_trigger.trigger('click');
    $('.has-error').tooltip({
        placement: 'top'
    });
</script>
</c:if>
