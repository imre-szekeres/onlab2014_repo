<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<div id='side-menu-bar'>
	<div id='home-href-wrapper'>
		<a id='home-href-side-menu' class='decor-less' href='${ param.appRoot }/'>Workflow Manager</a>
    </div>
	
	<div id='side-menu-buttons'>
		<a href='${ param.appRoot }/projects?active=true'>
			<div id='projects-button' class='side-menu-button'>
				<span class="glyphicon glyphicon-tasks side-menu-icon"/>
				<div class='side-menu-title'>Projects</div>
			</div>
		</a>
		
		<a href='${ param.appRoot }/workflows'>
			<div id='workflows-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-random side-menu-icon'/>
				<div class='side-menu-title'>Workflows</div>
			</div>
		</a>
		
		<a href='${ param.appRoot }/users/profile?user=${ sessionScope.subject.userID }'>
			<div id='profile-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-user side-menu-icon'/>
				<div class='side-menu-title'>Profile</div>
			</div>
		</a>
		
		<a href='${ param.appRoot }/admin'>
			<div id='admin-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-cog side-menu-icon'/>
				<div class='side-menu-title'>Administration</div>
			</div>
		</a>
		
		<a href='${ param.appRoot }/logout'>
			<div id='log-out-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-off side-menu-icon'/>
				<div class='side-menu-title'>Logout</div>
			</div>
		</a>
	</div>
</div>