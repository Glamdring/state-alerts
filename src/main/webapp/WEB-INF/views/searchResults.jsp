<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="search" />
<c:set var="head">
<title>Резултати от търсене за: ${params.keywords}</title>
</c:set>
<%@ include file="header.jsp" %>

<table class="table table-bordered table-striped">
<thead>
<tr>
	<td style="width: 40px;" >${msg.documentId}</td>
	<td>${msg.title}</td>
    <td>${msg.snippet}</td>
    <td>${msg.source}</td>
    <td>${msg.publishDate}</td>
    <td>${msg.documentLink}</td>
</tr>
</thead>
<tbody>
<c:forEach items="${results}" var="entry">
<tr id="row-${entry.id}">
	<td>${entry.externalId}</td>
	<td>${entry.title}</td>
	<td>${entry.content.substring(0, 100)}</td>
	<td>${entry.sourceName}</td>
	<td><fmt:formatDate value="${entry.publishDate.toDate()}" pattern="dd.MM.yyyy" /></td>
	<td><a href="${entry.url}">${msg.open}</a></td>
</tr>
</c:forEach>
</tbody>
</table>
<%@ include file="footer.jsp" %>