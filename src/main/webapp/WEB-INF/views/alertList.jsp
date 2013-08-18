<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Известия</title>
<script type="text/javascript">
function deleteAlert(id) {
  $.post("${root}/alerts/delete", {id: id}, function() {
    $("#row-" + id).fadeOut(500, function() {
    	$(this).remove();
    });
  }); 
}
</script>
</c:set>
<%@ include file="header.jsp" %>
<h2>${msg.alertsList}</h2>
<a href="${root}/alerts/new">Ново известие</a>
<table class="table table-bordered table-striped">
<thead>
<tr>
	<td>${msg.name}</td>
    <td>${msg.keywords}</td>
    <td>${msg.email}</td>
    <td>${msg.period}</td>
    <td></td>
</tr>
</thead>
<tbody>
<c:forEach items="${alerts}" var="alert">
<tr id="row-${alert.id}">
	<td>${alert.name}</td>
	<td>${alert.keywords}</td>
	<td>${alert.email}</td>
	<td></td>
	<td><a href="javascript:void(0);" onclick="deleteAlert(${alert.id});">${msg.delete}</a></td>
</tr>
</c:forEach>
</tbody>
</table>
<%@ include file="footer.jsp" %>