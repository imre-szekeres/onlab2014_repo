<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<div id='top-menu-bar'>
	
		<ul class="nav nav-tabs nav-justified">
			<c:forEach var='tab' items='${navigationTabs}'>
				<c:choose>
					  <c:when test="${activeTab eq tab.key}">
						<li class="active">
					  </c:when>
					  <c:otherwise>
						<li>
					  </c:otherwise>
				</c:choose>
							<a href='${tab.key}'>${tab.value}</a>
						</li>
			</c:forEach>
		</ul>
	
</div>