<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Известие</title>
</c:set>
<%@ include file="header.jsp" %>

<form action="${root}/alerts/save" method="POST">
<table>
<tr><td>${msg.name}:</td><td><input type="text" name="name" /></td></tr>
<tr><td>${msg.email}:</td><td><input type="text" name="email" value="${userContext.user.email}" /></td></tr>
<tr><td>${msg.keywords}:</td><td><input type="text" name="keywords" /> (${msg.commaSeparated})</td></tr>
<tr><td>${msg.receiveAlerts}:</td>
<td>
<input type="radio" name="period" value="Daily" id="daily" />&nbsp;<label for="daily" class="radioLabel">${msg.daily}</label>
<input type="radio" name="period" value="Weekly" id="weekly" checked="checked" />&nbsp;<label for="weekly" class="radioLabel">${msg.weekly}</label>
<input type="radio" name="period" value="Monthly" id="monthly" />&nbsp;<label for="monthly" class="radioLabel" >${msg.monthly}</label>
</td><tr/>
<tr><td>${msg.source}:</td>
<td>
<c:forEach items="${sources}" var="source">
    <input type="checkbox" name="sources" value="${source.descriptor.sourceKey}" id="source_${source.descriptor.sourceKey}" />&nbsp;<label for="source_${source.descriptor.sourceKey}" class="radioLabel">${source.descriptor.sourceDisplayName}</label>
</c:forEach>
</td></tr>
<tr><td></td><td><input type="submit" value="${msg.save}" class="btn" style="margin-top: 10px;" /></td></tr>
</table>
</form>
<%@ include file="footer.jsp" %>