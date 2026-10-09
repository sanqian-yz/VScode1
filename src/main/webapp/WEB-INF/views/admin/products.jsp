<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>后台商品管理 - 舍购</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"/>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jspf"/>
<h2>后台商品管理</h2>

<h3><c:out value="${empty editProduct ? '新增商品' : '编辑商品'}"/></h3>
<form method="post" action="${pageContext.request.contextPath}/admin/products">
  <input type="hidden" name="csrfToken" value="${csrfToken}"/>
  <input type="hidden" name="action" value="${empty editProduct ? 'create' : 'update'}"/>
  <input type="hidden" name="id" value="${editProduct.id}"/>
  <p><label>名称：<input name="name" required value="${editProduct.name}"/></label></p>
  <p><label>描述：<input name="description" value="${editProduct.description}"/></label></p>
  <p><label>价格：<input name="price" required value="${editProduct.price}"/></label></p>
  <p><label>库存：<input name="stock" type="number" min="0" required value="${empty editProduct ? 0 : editProduct.stock}"/></label></p>
  <p><label>图片 URL：<input name="imageUrl" value="${editProduct.imageUrl}"/></label></p>
  <button type="submit">保存</button>
</form>

<h3>商品列表</h3>
<table class="table">
  <tr><th>ID</th><th>名称</th><th>价格</th><th>库存</th><th>操作</th></tr>
  <c:forEach var="p" items="${products}">
    <tr>
      <td>${p.id}</td>
      <td>${p.name}</td>
      <td>¥${p.price}</td>
      <td>${p.stock}</td>
      <td>
        <a href="${pageContext.request.contextPath}/admin/products?editId=${p.id}">编辑</a>
        <form method="post" action="${pageContext.request.contextPath}/admin/products" class="inline">
          <input type="hidden" name="csrfToken" value="${csrfToken}"/>
          <input type="hidden" name="action" value="delete"/>
          <input type="hidden" name="id" value="${p.id}"/>
          <button type="submit">删除</button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>
</body>
</html>
