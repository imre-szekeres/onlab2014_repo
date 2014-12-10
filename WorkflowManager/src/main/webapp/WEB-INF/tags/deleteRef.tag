<%@ tag body-content='scriptless' isELIgnored='false' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ attribute name='href' required='true' %>
<%@ attribute name='refID' %>
<%@ attribute name='refClasses' %>

<a href='#' id='${ refID }' class='${ refClasses }' onclick='submitChildForm(event)' >
    <form action='${ href }' method='POST' name='delete-form' >
        <input type='hidden' name='_method' value='DELETE' >
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
        <jsp:doBody />
 
    </form>
</a>

<script>
   function submitChildForm(event) {
	   console.log($(event.target));
	   $(event.target).parent().parent().find('form[name="delete-form"]').submit();
   }
</script>