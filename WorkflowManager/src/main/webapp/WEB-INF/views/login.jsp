<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <c:set var='appRoot' value='${ pageContext.request.contextPath }' />
    <jsp:include page='fragments/html_head.jsp' >
        <jsp:param name='appRoot' value='${ appRoot }' />
        <jsp:param name='title' value='WorkflowManager (R)' />
    </jsp:include>

</head>
<body>
    
    <div id='sign-in-header-wrapper' class='container' >
        <div class='row'>
            <div class='col-md-3'></div>
            <div class='col-md-3' id='app-name-wrapper' ><h3 class='app-name' ><strong>WorkflowManager</strong></h3></div>
            <div class='col-md-4' id='not-part-of-it-wrapper' >
                <span class='app-name' ><strong>Not a part of it yet?</strong></span>
                <button class='btn btn-warning' id='register-btn' onClick='showRegister(event)' >Register</button>
                <button class='btn btn-default' id='sign-in-trigger-btn' onclick='showSignIn(event)' >Sign In</button>
            </div>
            <div class='col-md-2'></div>
        </div>
    </div>
    
    <section id='register-section' class='pos-absol' >
    <div id='register-jumbotron' class='jumbotron' >
	    <div id='register-from-positioner' class='pos-rel'>
	            <jsp:include page='fragments/user_form.jsp'>
	                <jsp:param name='postAction' value='${ appRoot }/register' />
	                <jsp:param name='userRef' value='subject' />
	                <jsp:param name='submitButtonValue' value='Register' />
	            </jsp:include>
	    </div>
	</div>
    </section>
    
    <div class='centraliser pos-absol' >
    <div id='sign-in-form-wrapper' class='pos-rel' >
        <div>
            <button id='close-signin-wrapper' type='button' class='close img-thumbnail pos-rel' onclick='showContent(event)' >
                <span aria-hidden='true' >&times;</span>
                <span class='sr-only'>Close</span>
            </button>
        </div>
        <div>
        <form:form action='/WorkflowManager/login' modelAttribute='subject' method='POST' class='form-horizontal pos-rel' id='sign-in-form' >
            
            <div class='form-row'>
                <div class='error-message-wrapper'>
                    <c:if test='${ not empty loginError }' >
                        <div id='sign-in-error-message' class='alert alert-danger pos-rel'>
                            <spring:message code='login.error.message' />
                        </div>
                    </c:if>
                </div>
            </div>
                                   
            <c:set var='inputClass' value='${ loginError == null ? "" : "has-error"  }' />
                                   
            <div class='form-row ${ inputClass }'>
                <form:input id='username' path='username' type='text' value='Username' class='form:input-large form-control' />
            </div>
            <div class='form-row ${ inputClass }' >
                <form:input id='password' path='password' type='password' class='form:input-large form-control'/>
            </div>
            <div class='form-row' >
                <input type='submit' value='Sign In' id='sign-in-btn' class='btn btn-primary btn-block' />
            </div>
       </form:form>
       </div>
    </div>
    </div>
    
    <script>
        var $_signin = $('#sign-in-form-wrapper');
        var $_register = $('#register-from-positioner').parent();
        var $_page_content = $('#page-content-wrapper');
        var $_dynamics = $([$_signin, $_register, $_page_content]);
        
        function fadeOut($_element) {
        	$_element.fadeOut();
        }
        
        function fadeIn($_element) {
        	$_element.fadeIn();
        }
        
        function fadeAll($_elements) {
        	for(var i = 0; i < $_elements.length; ++i)
        		fadeOut($_elements[i]);
        }
        
        function fadeAllBut($_element, $_elements) {
        	for(var i = 0; i < $_elements.length; ++i) {
        		if($_element === $_elements[i])
        			fadeIn($_elements[i]);
        		else fadeOut($_elements[i]);
        	}
        }
        
        function showSignIn(event) {
        	fadeAllBut($_signin, $_dynamics);
        }
        
        function showRegister(event) {
        	fadeAllBut($_register, $_dynamics);
        }
        
        function showContent(event) {
        	fadeAllBut($_page_content, $_dynamics);
        }
        
        function hideAll($_elements) {
        	for(var i = 0; i < $_elements.length; ++i)
        		$_elements[i].hide();
        }
        
        function hideAllBut($_element, $_elements) {
        	for(var i = 0; i < $_elements.length; ++i) {
                if($_element !== $_elements[i])
                    $_elements[i].hide();
            }
        }
        
        function onUserFormCancel(event) {
        	showContent(event);
        }
    </script>
    
    <c:choose>
        <c:when test='${ not empty loginError }'>
            <script>
                hideAllBut($_signin, $_dynamics);
            </script>
        </c:when>
        <c:when test='${ not empty validationErrors }' >
            <script>
                hideAllBut($_register, $_dynamics);
            </script>
        </c:when>
        <c:otherwise>
            <script>
                hideAll($_dynamics);
            </script>
        </c:otherwise>
    </c:choose>

</body>
</html>