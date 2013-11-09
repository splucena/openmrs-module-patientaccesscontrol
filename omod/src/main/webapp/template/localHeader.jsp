<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/roleProgram") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/programaccesscontrol/roleProgram.list"><spring:message
				code="programaccesscontrol.roleProgram.title" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/rolePatient") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/programaccesscontrol/rolePatientList.htm"><spring:message
				code="programaccesscontrol.rolePatient.title" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
<h2>
	<spring:message code="programaccesscontrol.title" />
</h2>
