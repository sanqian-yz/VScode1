<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>订单详情 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>订单详情 #${order.id}</h2>
<p>下单用户：${order.username}</p>
<p>支付状态：${order.status}，配送状态：${order.deliveryStatus}</p>
<p>配送员：<c:out value="${empty order.deliveryStaffName ? '未分配' : order.deliveryStaffName}"/></p>
<p>总金额：<strong class="price">¥${order.totalAmount}</strong></p>

<table class="table">
  <tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr>
  <c:forEach var="item" items="${order.items}">
    <tr>
      <td>${item.productName}</td>
      <td>¥${item.unitPrice}</td>
      <td>${item.quantity}</td>
      <td>¥${item.subtotal}</td>
    </tr>
  </c:forEach>
</table>
<p><a href="${pageContext.request.contextPath}/orders">返回订单列表</a></p>
</body>
</html>
