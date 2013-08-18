<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Известие</title>
</c:set>
<%@ include file="header.jsp" %>

<form action="${root}/alerts/save" method="POST">
<table>
<tr><td>Име:</td><td><input type="text" name="name" /></td></tr>
<tr><td>Email:</td><td><input type="text" name="email" value="${userContext.user.email}" /></td></tr>
<tr><td>Ключови думи:</td><td><input type="text" name="keywords" /> (${msg.commaSeparated})</td></tr>
<tr><td></td><td><input type="submit" value="Запиши" class="btn" /></td></tr>
</table>
</form>
<%@ include file="footer.jsp" %>