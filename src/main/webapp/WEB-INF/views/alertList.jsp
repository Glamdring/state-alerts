<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Известия</title>
</c:set>
<%@ include file="header.jsp" %>

<a href="${root}/alerts/new">Ново известие</a>
<table>
<thead>
<tr>
	<td>Име</td>
    <td>Ключови думи</td>
    <td>Email</td>
    <td>Период</td>
    <td></td>
</tr>
</thead>
<tbody>
<c:forEach items="${alerts}" var="alert">
<tr>
	<td>${alert.name}</td>
	<td>${alert.keywords}</td>
	<td>${alert.email}</td>
	<td></td>
	<td><a href="javascript:void(0);" onclick="delete(${alert.id})" /></td>
</tr>
</c:forEach>
</tbody>
</table>
<%@ include file="footer.jsp" %>