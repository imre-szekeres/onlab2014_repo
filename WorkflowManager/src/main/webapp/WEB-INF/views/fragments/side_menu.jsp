<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<div id='side-menu-bar'>
	<div id='home-href-wrapper'>
		<a id='home-href' class='decor-less' href='${ param.appRoot }/'>Workflow Manager</a>
    </div>
	
	<div id='side-menu-buttons'>
		<a href=''>
			<div id='projects-button' class='side-menu-button'>
				<span class="glyphicon glyphicon-tasks side-menu-icon"/>
				<div class='side-menu-title'>Projects</div>
			</div>
		</a>
		
		<a href=''>
			<div id='workflows-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-random side-menu-icon'/>
				<div class='side-menu-title'>Workflows</div>
			</div>
		</a>
		
		<a href=''>
			<div id='profile-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-user side-menu-icon'/>
				<div class='side-menu-title'>Profile</div>
			</div>
		</a>
		
		<a href=''>
			<div id='admin-button' class='side-menu-button'>
				<span class='glyphicon glyphicon-cog side-menu-icon'/>
				<div class='side-menu-title'>Administration</div>
			</div>
		</a>
	</div>
</div>