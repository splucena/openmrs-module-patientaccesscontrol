<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/program") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/programaccesscontrol/program.list"><spring:message
				code="programaccesscontrol.manage" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
<h2>
	<spring:message code="programaccesscontrol.title" />
</h2>
