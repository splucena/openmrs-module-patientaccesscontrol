<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Program Access Control" otherwise="/login.htm" redirect="/module/programaccesscontrol/programAccessControlEdit.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>

<br/>

<h3><spring:message code="programaccesscontrol.programAccessControl.title"/></h3>

<form:form method="post" modelAttribute="programAccessControlForm">
<table style="border-spacing: 10px;">
	<tr>
		<th align="right"><spring:message code="programaccesscontrol.program"/></th>
		<td>
			${program.name}
		</td>
	</tr>
	<tr>
		<th align="right" style="vertical-align: top;"><spring:message code="programaccesscontrol.programAccessControl"/></th>
		<td>
		
			<table cellpadding="2" cellspacing="0" width="100%">
				<tr>
					<th><spring:message code="programaccesscontrol.role"/></th>
					<th align="center"><spring:message code="programaccesscontrol.programAccessControl.view"/></th>
				</tr>
				<tr class="oddRow">
					<td> ${superuser}</td>
					<td align="center"><input type="checkbox" checked="checked" disabled="disabled" /></td>
				</tr>
				<c:forEach items="${programAccessControlForm.roleViewModels}" var="roleViewModel" varStatus="rowStatus">
					<tr class="<c:choose><c:when test="${rowStatus.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
						<td>${roleViewModel.role.role}</td>
						<td align="center"><form:checkbox path="roleViewModels[${rowStatus.index}].canView" /></td>
					</tr>	
				</c:forEach>
				<c:if test="${empty programAccessControlForm.roleViewModels}">
					<tr>
						<td colspan="4" style="padding: 10px; text-align: center"><spring:message code="programaccesscontrol.noresults"/></td>
					</tr>
				</c:if>
			</table>
		</td>
	</tr>
</table>

<input type="submit" value="<openmrs:message code="programaccesscontrol.save"/>">
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
