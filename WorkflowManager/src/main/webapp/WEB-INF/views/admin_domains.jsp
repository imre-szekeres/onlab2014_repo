<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri='http://java.sun.com/jsp/jstl/core'        prefix='c' %>

<c:set var='appRoot' value='${ pageContext.request.contextPath }' />

<div id='roles-list-panel' class='panel panel-default admin-panel domain-panel'>
<div id='admin-roles-content-wrapper' class='panel-group' role='tablist' aria-multiselectable='false' >
    <jsp:include page='fragments/domains_list.jsp' />
</div>
</div>

<div id='admin-domains-create-wrapper' >
    <button class='btn btn-primary' id='create-domain-trigger' data-toggle='modal' data-target='#new-domain-modal' >
        <span class="glyphicon glyphicon-plus new-role-icon" aria-hidden="true" ></span> Create Domain
    </button>
</div>

<div class='modal fade' id='new-domain-modal' tabindex='-1' role='dialog' aria-labelledby='#new-role-label' aria-hidden='true' >
    <script>
       var form_included = false;
    </script>

    <c:if test='${ not empty validationErrors }' >
        
        <jsp:include page='fragments/domain_form_modal.jsp'>
            <jsp:param name='formType' value='${ formType }' />
        </jsp:include>
        <script>
            var form_included = true;
        </script>
        
    </c:if>
</div>


<script>

    var cform_url = "${ appRoot }${ selectCreateFormUrl }";
    
    var $_ndomain_modal = $('#new-domain-modal');
    var $_cdomain_trigger = $('#create-domain-trigger');
    var current_form = 'None';
    
    function submitNewDomainForm(event) {
    	$('#new-domain-form').submit();
    }
    
    function requestCreateForm($_modal) {
    	$.ajax({
    		url: cform_url,
    		dataType: 'html',
    		method: 'GET',
    		success: function(data) {
    			$_modal.empty();
    			$(data).appendTo( $_modal );
    			current_form = 'create';
    		}
    	});
    }
    
    $('.collapse').collapse();
    $_cdomain_trigger.click(function() {
    	if (current_form != 'create' && !form_included)
    		requestCreateForm($_ndomain_modal);
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
                	$_ndomain_modal.empty();
                    $(data).appendTo( $_ndomain_modal );
                    form_included = true;
                    current_form = 'update';
                    $_cdomain_trigger.trigger('click');
                    form_included = false;
                }
            });
        });
    });
</script>

<c:if test='${ not empty validationErrors }' >
<script>
    $_cdomain_trigger.trigger('click');
    $('.has-error').tooltip({
        placement: 'top'
    });
    current_form = '${ formType }';
    form_included = false;
</script>
</c:if>