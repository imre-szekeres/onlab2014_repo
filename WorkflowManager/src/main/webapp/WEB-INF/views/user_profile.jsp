<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored='false' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<!DOCTYPE html>
<div id='user-profile-positioner' class='positioner' >
    
    <div id='username-panel' class='panel panel-default' >
        <span id='username-span'>
             <span id='sign-in-user-icon' >
                 <span class='glyphicon glyphicon-user' ></span>
             </span> 
             <span id='username-text' ><c:out value='${ user.username }' /></span>
        </span>
        
        <span class='button-part' id='visible-trigger-wrapper' >
            <c:if test="${ isEditable eq 'true' }" >
                <button type='button' id='edit-profile-trigger' class='btn btn-danger' > 
                    <span id='edit-trigger-icon' class='glyphicon glyphicon-pencil' ></span>  Edit
                </button>
            </c:if>
        </span>
        
        <div hidden='true' id='hidden-trigger-wrapper' >
            <button id='cancel-edit-trigger' type='button' class='btn btn-warning' >Cancel</button>
        </div>
    </div>

    <div id='email-wrapper' class='toggle-on-edit' >
        <span class='email-span' >
            <span class='glyphicon glyphicon-envelope' ></span> 
                <a href='mailto:${ user.email }' class='underlined' >
                    <span class='email-text' ><c:out value='${ user.email  }' /></span>
                </a>
        </span>
    </div>
    
    <div id='forms-wrapper' hidden='true' >
        <c:if test='${ not empty validationErrors }' >
            <jsp:include page='fragments/user_details_form.jsp' />
        </c:if>
    </div>

    <c:set var='descrClass' value='${ empty user.comments ? "descr-lowered" : "" }' />

    <div id='description-panel' class='panel panel-default ${ descrClass } toggle-on-edit' >
        <div class='panel-heading' >
            <span class='glyphicon glyphicon-paperclip' ></span> Description
        </div>
        <div class='panel-body'>
            <div id='description-wrapper' >
                <c:out value='${ user.description }' />
            </div>
        </div>
    </div>
    
    <c:if test='${ not empty user.comments }' >
    <div id="user-comments-panel" class="panel panel-default toggle-on-edit" >
        <div class="panel-heading">
            <h3 class="panel-title">
                <span class='glyphicon glyphicon-comment' ></span> Comments
            </h3>
        </div>
        <div id='user-comments'>
            <c:forEach var="comment" items="${ user.comments }">
                <div class="project-comment-body panel-body">
                    <div class="project-comment-inform">
                        <strong><c:out value='${ comment.user.username }' /></strong> <span style="color:#9A9A9A;">- ${ comment.postDate }<span>
                    </div>
                    <div class='user-comment-project-href-wrapper' >
                        <span style="color:#9A9A9A;">on <span> <a href='${ appRoot }${ viewProjectAction }?id=${ comment.project.id }' ><c:out value='${ comment.project.name }' /></a>
                    </div>
                    <div class="project-comment-message">
                        <c:out value='${ comment.description }' />
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    </c:if>
    
    <div id='domains-n-roles-wrapper' class='toggle-on-edit' >
        <div id='domains-n-roles-trigger-wrapper' >

            <button id='domains-n-roles-trigger' type='button' class='btn btn-success' ><span class='glyphicon glyphicon-tower' ></span> Domains &amp; roles</button>            

        </div>
        <div id='domains-n-roles-table-wrapper' class='toggle-on-request' >
        </div>
    </div>
</div>

<script>
   var $_dnr_wrapper = $('#domains-n-roles-table-wrapper');
   var $_dnr_trigger = $('#domains-n-roles-trigger');
   
   var $_edit_trigger = $('#edit-profile-trigger');
   var $_cancel_trigger = $('#cancel-edit-trigger');
   
   var $_visible_wrapper = $('#visible-trigger-wrapper');
   var $_hidden_wrapper = $('#hidden-trigger-wrapper');
   
   var $_toggles_on_edit = $('.toggle-on-edit');
   var $_forms_wrapper = $('#forms-wrapper');

   var domains_loaded = false;
   var update_form_loaded = false;

   function requestDomainsNRolesTable($_wrapper) {
	   var url_ = "${ appRoot }${ selectDNRTable }?user=${ user.id }";
	   $.ajax({
		   url: url_,
		   dataType: 'html',
		   method: 'GET',
		   success: function(data) {
			   $_wrapper.empty();
			   $(data).appendTo( $_wrapper );
			   domains_loaded = true;   
		   }
	   });
   }
   
   function requestUpdateForms($_wrapper) {
	   var url_ = "${ appRoot }${ selectDetailsForm }?user=${ user.id }";
	   $.ajax({
		   url: url_,
		   dataType: 'html',
		   method: 'GET',
		   success: function(data) {
			   $_wrapper.empty();
			   $(data).appendTo( $_wrapper );
			   toggleForm();
			   update_form_loaded = true;
		   }
	   });
   }

   function toggleForm() {
	   $_toggles_on_edit.toggle();
	   $_forms_wrapper.toggle();
	   
	   var $_target_0 = $_edit_trigger.parent();
	   var $_target_1 = $_cancel_trigger.parent();
	   
	   $_edit_trigger.appendTo( $_target_1 );
	   $_cancel_trigger.appendTo( $_target_0 );
	   console.log('2');
   }
   
   $_dnr_trigger.click(function(evet) {
	   if (domains_loaded) {
		   $_dnr_wrapper.slideToggle();
	   } else {
		   requestDomainsNRolesTable( $_dnr_wrapper );
	   }
   });

   $_edit_trigger.click(function() {
	  if (update_form_loaded)
		  toggleForm();
	  else
		  requestUpdateForms( $_forms_wrapper );
   });
   
   $_cancel_trigger.click( toggleForm );
</script>

<c:if test='${ not empty validationErrors }' >
<script>
	update_form_loaded = true;
    $_edit_trigger.trigger('click');
    $('.has-error').tooltip({
        placement: 'top'
    });
</script>
</c:if>