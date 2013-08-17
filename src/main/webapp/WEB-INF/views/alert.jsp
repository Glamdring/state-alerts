<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Известие</title>
</c:set>
<%@ include file="header.jsp" %>

<form action="${root}/alerts/save" method="POST">
Име: <input type="text" name="name" />
Email: <input type="text" name="email" value="${userContext.user.email}" />
Ключови думи: <input type="text" name="keywords" />
<input type="submit" value="Запиши" />
</form>
<%@ include file="footer.jsp" %>