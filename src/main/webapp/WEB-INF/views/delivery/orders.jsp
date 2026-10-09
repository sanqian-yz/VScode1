<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>配送中心 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>配送中心</h2>

<h3>待配送 / 配送中订单</h3>
<c:choose>
  <c:when test="${empty availableOrders}"><p>暂无可配送订单。</p></c:when>
  <c:otherwise>
    <table class="table">
      <tr><th>订单号</th><th>用户</th><th>金额</th><th>状态</th><th>当前配送员</th><th>操作</th></tr>
      <c:forEach var="o" items="${availableOrders}">
        <tr>
          <td>#${o.id}</td>
          <td>${o.username}</td>
          <td>¥${o.totalAmount}</td>
          <td>${o.deliveryStatus}</td>
          <td><c:out value="${empty o.deliveryStaffName ? '未分配' : o.deliveryStaffName}"/></td>
          <td>
            <c:if test="${empty o.deliveryStaffId}">
              <form method="post" action="${pageContext.request.contextPath}/delivery/orders" class="inline">
                <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                <input type="hidden" name="action" value="claim"/>
                <input type="hidden" name="orderId" value="${o.id}"/>
                <button type="submit">领取</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<h3>我的配送记录</h3>
<c:choose>
  <c:when test="${empty myOrders}"><p>暂无配送记录。</p></c:when>
  <c:otherwise>
    <table class="table">
      <tr><th>订单号</th><th>用户</th><th>金额</th><th>配送状态</th><th>操作</th></tr>
      <c:forEach var="o" items="${myOrders}">
        <tr>
          <td>#${o.id}</td>
          <td>${o.username}</td>
          <td>¥${o.totalAmount}</td>
          <td>${o.deliveryStatus}</td>
          <td>
            <c:if test="${o.deliveryStatus ne 'DONE'}">
              <form method="post" action="${pageContext.request.contextPath}/delivery/orders" class="inline">
                <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                <input type="hidden" name="action" value="delivering"/>
                <input type="hidden" name="orderId" value="${o.id}"/>
                <button type="submit">设为配送中</button>
              </form>
              <form method="post" action="${pageContext.request.contextPath}/delivery/orders" class="inline">
                <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                <input type="hidden" name="action" value="finish"/>
                <input type="hidden" name="orderId" value="${o.id}"/>
                <button type="submit">完成配送</button>
              </form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>
</body>
</html>
