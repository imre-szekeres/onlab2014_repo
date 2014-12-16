<%@ tag body-content='scriptless' isELIgnored='false' %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<%@ attribute name='href' required='true' %>
<%@ attribute name='refID' required='true' %>
<%@ attribute name='refClasses' %>

<a href='#' id='${ refID }' class='${ refClasses }' onclick='submitPostChildForm(event)' >
    <form action='${ href }' method='POST' name='post-form' >

        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
        <jsp:doBody />
 
    </form>
</a>

<script>
   function submitPostChildForm(event) {
       event.preventDefault();
       $('#' + '${ refID }').find('form[name="post-form"]').submit();
   }
</script>