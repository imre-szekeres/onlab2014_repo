<%@ tag body-content='scriptless' isELIgnored='false' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ attribute name='href' required='true' %>
<%@ attribute name='refID' %>
<%@ attribute name='refClasses' %>
<%@ attribute name='method' %>

<a href='#' id='${ refID }' class='${ refClasses }' onclick='submitChildForm(event)' >
    <form action='${ href }' method='POST' name='delete-form' >
        
        <c:if test='${ empty method }' >
            <input type='hidden' name='_method' value='DELETE' >
        </c:if>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
        <jsp:doBody />
 
    </form>
</a>

<script>
   function submitChildForm(event) {
	   event.preventDefault();
	   $(event.target).parent().parent().find('form[name="delete-form"]').submit();
   }
</script>