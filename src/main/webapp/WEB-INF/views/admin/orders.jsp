<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>后台订单管理 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>全部订单</h2>
<c:choose>
  <c:when test="${empty orders}"><p>暂无订单。</p></c:when>
  <c:otherwise>
    <table class="table">
      <tr><th>订单号</th><th>用户</th><th>金额</th><th>支付状态</th><th>配送状态</th><th>配送员</th><th>详情</th></tr>
      <c:forEach var="o" items="${orders}">
        <tr>
          <td>#${o.id}</td>
          <td>${o.username}</td>
          <td>¥${o.totalAmount}</td>
          <td>${o.status}</td>
          <td>${o.deliveryStatus}</td>
          <td><c:out value="${empty o.deliveryStaffName ? '未分配' : o.deliveryStaffName}"/></td>
          <td><a href="${pageContext.request.contextPath}/orders?id=${o.id}">查看</a></td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>
</body>
</html>
