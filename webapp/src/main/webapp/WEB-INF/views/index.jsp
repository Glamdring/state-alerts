<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Известия за държавни документи</title>
</c:set>
<%@ include file="header.jsp" %>

<div style="text-align: center;">
<h1>Известия за държавни документи</h1>
<p class="lead">Получете известие за всяко срещане на избрана от вас ключова дума в държавни документи</p>

<form action="${root}/search" method="GET">
<input type="text" name="keywords" style="width: 400px; margin-bottom: 0px;" />&nbsp;&nbsp;<input type="submit" class="btn" value="${msg.search}" />
</form>
<%@ include file="signin.jsp" %>

</div>

<%@ include file="footer.jsp" %>