<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>商品列表 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>商品列表</h2>
<form method="get" action="${pageContext.request.contextPath}/products">
  <input name="keyword" value="${param.keyword}" placeholder="输入关键字搜索">
  <button type="submit">搜索</button>
</form>
<c:choose>
  <c:when test="${empty products}"><p>暂无商品。</p></c:when>
  <c:otherwise>
    <div class="grid">
      <c:forEach var="p" items="${products}">
        <article class="card">
          <h3><c:out value="${p.name}"/></h3>
          <p><c:out value="${p.description}"/></p>
          <p class="price">¥${p.price}</p>
          <p>库存：${p.stock}</p>
          <c:if test="${not empty sessionScope.currentUser}">
            <form method="post" action="${pageContext.request.contextPath}/cart">
              <input type="hidden" name="csrfToken" value="${csrfToken}"/>
              <input type="hidden" name="action" value="add"/>
              <input type="hidden" name="productId" value="${p.id}"/>
              <input type="number" name="quantity" min="1" value="1" style="width:65px"/>
              <button type="submit">加入购物车</button>
            </form>
          </c:if>
          <c:if test="${empty sessionScope.currentUser}">
            <p><a href="${pageContext.request.contextPath}/login">登录后购买</a></p>
          </c:if>
        </article>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
</body>
</html>
