<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>注册 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>新用户注册</h2>
<c:if test="${not empty error}"><p class="error">${error}</p></c:if>
<form method="post" action="${pageContext.request.contextPath}/register">
  <input type="hidden" name="csrfToken" value="${csrfToken}"/>
  <p><label>用户名：<input name="username" required minlength="3" maxlength="30"></label></p>
  <p><label>手机号：<input name="phone" pattern="1[0-9]{10}" placeholder="选填"></label></p>
  <p><label>密码：<input type="password" name="password" required minlength="6"></label></p>
  <p><label>确认密码：<input type="password" name="confirmPassword" required minlength="6"></label></p>
  <p><button type="submit">注册</button></p>
</form>
</body>
</html>
