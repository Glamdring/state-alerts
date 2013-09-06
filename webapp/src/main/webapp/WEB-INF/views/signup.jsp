<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="signup" />
<c:set var="head">
<title>${msg.signup}</title>
</c:set>
<%@ include file="header.jsp" %>

<div class="main">
   <div class="lead">${msg.signupPageText}</div>
   <%@ include file="signin.jsp" %>
</div>
<%@ include file="footer.jsp" %>