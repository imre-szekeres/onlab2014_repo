<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<div id='top-menu-bar'>
	
		<ul class="nav nav-tabs nav-justified">
			<c:forEach var='tab' items='${navigationTabs}'>
				<!--<c:choose>
					  <c:when test="${activeTab eq tab.key}">
						<li class="active top-menu-tab">
					  </c:when>
					  <c:otherwise>
						<li class="top-menu-tab">
					  </c:otherwise>
				</c:choose>-->
						<li  class="top-menu-tab">
							<a id="top-menu-link" href="${tab.key}">${tab.value}</a>
						</li>
			</c:forEach>
		</ul>
	
</div>

<script language="javascript">
	$(function () {
		activaTab(document.URL);
	});

	function activaTab(url){
		var indexTabUrl = url.indexOf("WorkflowManager/");
		var length = url.length;
		var endOfUrl = "/" + url.substr(indexTabUrl,length);
		$('.nav-tabs a[href="' + endOfUrl + '"]').parent().addClass("active");
	}	
</script>