<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<!--<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>-->
    
<div id='top-menu-bar'>
	
		<ul class="nav nav-tabs nav-justified">
			<c:forEach var='tabs' items='${param.navigationTabs}'>
				<li class="active">
					<a href="${tabs.key}">${tabs.value}</a>
				</li>
			</c:forEach>
		</ul>
	
</div>