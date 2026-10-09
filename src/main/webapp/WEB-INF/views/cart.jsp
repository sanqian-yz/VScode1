<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>购物车 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>我的购物车</h2>
<c:choose>
  <c:when test="${empty items}"><p>购物车为空，快去选购吧。</p></c:when>
  <c:otherwise>
    <table class="table">
      <tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th><th>操作</th></tr>
      <c:forEach var="item" items="${items}">
        <tr>
          <td>${item.product.name}</td>
          <td>¥${item.product.price}</td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/cart" class="inline">
              <input type="hidden" name="csrfToken" value="${csrfToken}"/>
              <input type="hidden" name="action" value="update"/>
              <input type="hidden" name="productId" value="${item.productId}"/>
              <input type="number" min="1" name="quantity" value="${item.quantity}" style="width:70px"/>
              <button type="submit">更新</button>
            </form>
          </td>
          <td>¥${item.subtotal}</td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/cart" class="inline">
              <input type="hidden" name="csrfToken" value="${csrfToken}"/>
              <input type="hidden" name="action" value="delete"/>
              <input type="hidden" name="productId" value="${item.productId}"/>
              <button type="submit">删除</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
    <form method="post" action="${pageContext.request.contextPath}/checkout">
      <input type="hidden" name="csrfToken" value="${csrfToken}"/>
      <button type="submit">模拟支付并下单</button>
    </form>
  </c:otherwise>
</c:choose>
</body>
</html>
