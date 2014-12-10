<%@ tag isELIgnored='false' body-content='empty' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib tagdir='/WEB-INF/tags/' prefix='wman' %>

<%@ attribute name='parentId' required='true' %>
<%@ attribute name='editPath' %>
<%@ attribute name='detailsPath' %>
<%@ attribute name='deletePath' %>

<%@ attribute name='editIconClass' %>
<%@ attribute name='detailsIconClass' %>
<%@ attribute name='deleteIconClass' %>

<%@ attribute name='iconRowClass' %>


<div class='icon-row ${ iconRowClass }' parentId='${ parentId }' >
    <c:if test='${ not empty editPath }' >
            
        <div class='icon-row-icon edit-icon ${ editIconClass }' >
            <a href='${ editPath }' class='icon-row-icon-href edit-icon-href' >
               <span class='glyphicon glyphicon-pencil'></span>
            </a>
        </div>
    </c:if>
    
    <c:if test='${ not empty detailsPath }' >
            
        <div class='icon-row-icon details-icon ${ detailsIconClass }' >
            <a href='${ detailsPath }' class='icon-row-icon-href details-icon-href' >
               <span class='glyphicon glyphicon-share-alt'></span>
            </a>
        </div>
    </c:if>
    
    <c:if test='${ not empty deletePath }' >
            
        <div class='icon-row-icon delete-icon ${ deleteIconClass }' >
            <wman:deleteRef href="${ deletePath }" refClasses='icon-row-icon-href delete-icon-href' >
                <span class='glyphicon glyphicon-trash'></span>
            </wman:deleteRef>
        </div>
    </c:if>
</div>


<script>

    $.each($('.icon-row'), function(index, row) {
    	var $_row = $(row);
    	var parent_id = $_row.attr('parentId');
    	var $_parent = $('#' + parent_id);
    	
    	$_parent.mouseover(function() {
    		$_row.css('display', 'block');
    		$_row.appendTo( $_parent );
    	}).mouseleave(function() {
    		$_row.css('display', 'none')
    	});
    	
    	$_row.mouseover(function() {
    		$_parent.trigger('mouseenter');
    	}).mouseleave(function() {
    		$_parent.trigger('mouseleave');
    	});
    });
</script>