<%@ page  contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<table width="600" align="center">
<tr height="30">
	<td align="right">
	<input type="button" value="파일 업로드"
		onclick="location.href='<%=cp%>/file.do?method=write';">
	</td>
</tr>
</table>

<table width="600" align="center" border="1" style="font-size: 10pt;">
<tr height="30" align="center">
	<td width="50">번호</td>
	<td width="200">제목</td>
	<td width="300">파일</td>
	<td width="50">삭제</td>
</tr>

<c:forEach var="dto" items="${lists }">

<tr onmouseover="this.style.backgroundColor='#e4e4e4'"
	onmouseout="this.style.backgroundColor=''" bgcolor=#ffffff>

	<td width="50" align="center">${dto.listNum }</td>
	<td width="200" align="left">${dto.subject }</td>
	<td width="300" align="left">
		<a href="${dto.urlFile }">${dto.originalFileName }</a>
	</td>
	<td width="50" align="center">
		<a href="<%=cp %>/file.do?method=delete_ok&num=${dto.num}">삭제</a>
	</td>
</tr>
</c:forEach>

<c:if test="${totalDatacount==0}">
<tr bgcolor="#ffffff">
	<td align="left" colspan="5">등록된 자료가 없습니다.</td>
</tr>	
</c:if>
</table>

<c:if test="${totalDatacount!=0}">
<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
<tr align="center">
	<td align="center" height="30">${pageIndexList }</td>
</tr>
</table>
</c:if>
</body>
</html>