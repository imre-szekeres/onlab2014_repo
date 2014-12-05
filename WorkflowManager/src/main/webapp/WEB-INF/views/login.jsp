<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri='http://www.springframework.org/tags' prefix='spring' %>
<%@ taglib uri='http://www.springframework.org/tags/form' prefix='form' %>

<c:set var='loginError' value='${ (not empty loginError or not empty requestScope.error or not empty param.error) ? true : loginError }' />
<!DOCTYPE html>
<html>
<head>

    <c:set var='appRoot' value='${ pageContext.request.contextPath }' />
    <jsp:include page='fragments/html_head.jsp' >
        <jsp:param name='appRoot' value='${ appRoot }' />
        <jsp:param name='title' value='WorkflowManager (R)' />
    </jsp:include>

</head>
<body>
    
    <div id='sign-in-header-wrapper' >
        <div id='sign-in-header-positioner' >
            
            <div id='sign-in-header' >
                <span id='app-name-wrapper'>
                    <strong><span class='glyphicon glyphicon-random' ></span> <span id='app-name' >WorkflowManager</span></strong>
                </span>
                <span id='not-part-of-it-yet' > Not part of it yet?  </span>
                <button class='btn btn-warning' id='register-btn' onClick='showRegister(event)' >Register</button>
                <button class='btn btn-default' id='sign-in-trigger-btn' data-toggle='modal' data-target='#sign-in-modal' >Sign In</button>
            </div>
            
        </div>
    </div>
    
    <section id='register-section' class='pos-absol' >
    <div id='register-jumbotron' class='jumbotron' >
	    <div id='register-from-positioner' class='pos-rel'>
	        <jsp:include page='fragments/user_form.jsp'>
	            <jsp:param name='postAction' value='${ appRoot }/register' />
	            <jsp:param name='userRef' value='user' />
	            <jsp:param name='submitButtonValue' value='Register' />
	        </jsp:include>
	    </div>
	</div>
    </section>
    
    <div class='modal fade' id='sign-in-modal' tabindex='-1' role='dialog' aria-labelledby='#sign-in-label' aria-hidden='true' >
        <div class='modal-dialog' id='sign-in-modal-dialog' >
            <div class='modal-content' >
                <div class='modal-header' >
                    <button type='button' class='close' data-dismiss='modal'>
                        <span aria-hidden='true' >&times;</span><span class='sr-only' >Close</span>
                    </button>
                    <h4 class='modal-title' id='sign-in-label' >Sign In</h4>
                </div>
                <div class='modal-body' id='sign-in-wrapper' >
                
                    <form name='f' action='<c:url value="j_spring_security_check" />' method='POST' class='form-horizontal pos-rel' id='sign-in-form' >
			            <div class='form-row'>
			                <div class='error-message-wrapper'>
			                    <c:choose >
			                        <c:when test='${ not empty loginError }' >
			                        
			                            <div id='sign-in-error-message' class='alert alert-danger pos-rel'>
                                            <spring:message code='login.error.message' />
                                        </div>
			                        
			                        </c:when>
			                        <c:otherwise>
			                        
			                            <div id='sign-in-user-icon-wrapper' >
			                                <span id='sign-in-user-icon' >
			                                    <span class='glyphicon glyphicon-user' ></span>
			                                </span>
			                            </div>
			                        
			                        </c:otherwise>
			                    </c:choose>
			                </div>
			            </div>
			                                   
			            <c:set var='inputClass' value='${ loginError == null ? "" : "has-error"  }' />
			                                   
			            <div class='form-row ${ inputClass }'>
			                <input id='username' name='j_username' type='text' placeholder='Username' class='form:input-large form-control' >
			            </div>
			            <div class='form-row ${ inputClass }' >
			                <input id='password' name='j_password' type='password' class='form:input-large form-control' >
			            </div>
			            
			            <div hidden='true'>
			                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" >
			            </div>
			       </form>
                
                </div>
                <div class='modal-footer' >
                    <button type='button' class='btn btn-primary' onclick='submitSignIn(event)' >Sign In</button>
                    <button type='button' class='btn btn-warning' data-dismiss='modal' >Cancel</button>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        var $_register = $('#register-from-positioner').parent();
        var $_page_content = $('#page-content-wrapper');
        var $_dynamics = $([$_register, $_page_content]);
        
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
        
        function submitSignIn(event) {
        	$('#sign-in-form').submit();
        }
        
    </script>
    
    <c:choose>
        <c:when test='${ not empty loginError }'>
            <script>
                hideAll($_dynamics);
                $('#sign-in-trigger-btn').trigger('click');
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