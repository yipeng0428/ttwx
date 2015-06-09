<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/view/common/inc/path.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>关键字回复</title>
	<link href="<%=resourceUrl%>/bootstrap-table/bootstrap-table.min.css" rel="stylesheet" type="text/css"/>
	<link href="<%=resourceUrl%>/css/material.css?v=2014030901" rel="stylesheet" type="text/css"/>
	<jsp:include page="/WEB-INF/view/common/inc/admin.jsp"></jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/view/common/inc/admin-header.jsp"></jsp:include>
<div class="container-fluid">
	<div class="row">
		<jsp:include page="/WEB-INF/view/wechat/admin/inc_menu.jsp">
			<jsp:param name="index" value="5"/>
		</jsp:include>
		<div id="context" class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
			<ol class="breadcrumb">
				<li><a href="<%=domain %>/admin">后台管理</a></li>
				<li><a href="<%=domain %>/admin/wechat">平台管理</a></li>
				<li class="active">关键字回复</li>
			</ol>
			<div id="toolbar">
				<div class="form-inline" role="form" style="width: 1000px;">
					<fieldset>
						<div class="form-group">
							<div class="control-group">
								<label class="control-label">关键字：</label>
								<input name="qry_key_word" class="form-control" type="text" placeholder="用户发送的文字">
								<label class="control-label">发送时间：</label>
								<div class="input-group">
									<input class="form-control" onClick="WdatePicker()" data-options="dateFmt:'yyyy-M-d H:m:s',readOnly:true,skin:'twoer'" name="start_time" type="text" style="width: 120px;" />
									<div class="input-group-addon">
										<i class="glyphicon glyphicon-calendar"></i>
									</div>
								</div>
								<label class="control-label">--------------</label>
								<div class="input-group">
									<input class="form-control" onClick="WdatePicker()" data-options="dateFmt:'yyyy-M-d H:m:s',readOnly:true,skin:'twoer'" name="end_time" type="text" style="width: 120px;" />
									<div class="input-group-addon">
										<i class="glyphicon glyphicon-calendar"></i>
									</div>
								</div>
								<span class="columns-right pull-right">
									<button onclick="searchDatagrid();" type="button" class="btn">
										<i class="glyphicon glyphicon-search"></i>
										查看
									</button>
									<button onclick="clearDatagrid();" type="button" class="btn">
										<i class="glyphicon glyphicon-transfer"></i>
										重置
									</button>
								</span>
							</div>
						</div>
					</fieldset>
					<div>
						<a class="btn btn-default" href="<%=domain%>/admin/wechat/action/keywordAdd">
							<i class="glyphicon glyphicon-plus"></i>
							添加
						</a>
						<a class="btn btn-default" onclick="searchDatagrid();" href="javascript:void(0);">
							<i class="glyphicon glyphicon-refresh"></i>
							刷新
						</a>
						<a class="btn btn-default" onclick="$table.bootstrapTable('uncheckAll');" href="javascript:void(0);">
							<i class="glyphicon glyphicon-check"></i>
							取消选中
						</a>
					</div>
				</div>
			</div>
			<table id="data-table"></table>

		</div>
	</div>
</div>

<script src="<%=resourceUrl%>/bootstrap-table/bootstrap-table.min.js" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl%>/bootstrap-table/bootstrap-table-option.js" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl%>/bootstrap-table/locale/bootstrap-table-zh-CN.min.js" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl%>/My97DatePicker/WdatePicker.js" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl%>/js/jquery.json-2.4.min.js" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl%>/js/jquery.xml2json.js" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl%>/js/jquery.form.js" type="text/javascript"></script>
<script src="<%=resourceUrl%>/script/wechat/admin/material_util.js?v=2014091101" type="text/javascript" charset="UTF-8"></script>
<script src="<%=resourceUrl %>/script/wechat/admin/keyword_action.js?v=2015060701" type="text/javascript" charset="UTF-8"></script>
</body>
</html>

