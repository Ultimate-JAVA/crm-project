<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

%>
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">

    <!--  PAGINATION plugin -->
    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">
    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.min.js"></script>

    <script type="text/javascript">
        $(function () {
            $("#createActivityBtn").click(function () {
                $("#createActivityForm")[0].reset();

                $("#createActivityModal").modal("show")
            });
            //点击创建市场活动
            $("#saveCreatActivityBtn").click(function () {
                var owner = $("#create-marketActivityOwner").val()
                var name = $.trim($("#create-marketActivityName").val())
                var startDate = $("#create-startDate").val()
                var endDate = $("#create-endDate").val()
                var cost = $.trim($("#create-cost").val())
                var description = $.trim($("#create-description").val())
                res = formVerify(owner,name, startDate,endDate,cost)
                if (res){
                    $.ajax({
                        url: "workbench/activity/saveCreateActivity.do",
                        data: {
                            owner: owner,
                            name: name,
                            startDate: startDate,
                            endDate: endDate,
                            cost: cost,
                            description: description
                        },
                        type: "post",
                        dataType: "json",
                        success: function (data) {
                            if (data.code == "1") {
                                selectActivity(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));

                                $("#createActivityModal").modal("hide")

                            } else {
                                alert(data.message)
                                $("#createActivityModal").modal("show")
                            }
                        }
                    })
                }
            })
            //日历插件
            $(".myDate").datetimepicker({
                language: "en",
                format: "yyyy-mm-dd",
                minView: "month",
                todayBtn: true,
                clearBtn: true,
                initialDate: new Date(),
                autoclose: true
            });
            //条件查询
            $("#queryBtn").click(function () {
                selectActivity(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
            })
            //一进来就查询全部数据
            selectActivity(1, 10);

            //全选删除功能
            $("#checkedAll").click(function () {
                $("#tbody input[type='checkbox']").prop("checked",$("#checkedAll").prop("checked"))
            })
            $("#tbody").on('click','input',function () {
                if ($("#tbody input[type='checkbox']:checked").size() != $("#tbody input[type='checkbox']").size()){
                    $("#checkedAll").prop("checked",false)
                }else {
                    $("#checkedAll").prop("checked", true)
                }
            })
            //点击删除按钮
            $("#deleteActivityBtn").click(function () {
                var checkedIds = $("#tbody input[type='checkbox']:checked")
                if (checkedIds.size()==0){
                    alert("请选择需要删除的市场活动");
                    return
                }
                res = confirm("确认要删除吗？");
                if (res){
                    var ids = "";
                    $.each(checkedIds,function () {
                        ids += "id="+this.value+"&"
                    })
                    ids = ids.substr(0,ids.length-1)
                    $.ajax({
                        url:"workbench/activity/deleteActivityById.do",
                        data:ids,
                        dataType:"json",
                        type:"post",
                        success:function (data) {
                            if (data.code == "1"){
                                selectActivity(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
                            }else {
                                alert(data.message)
                            }
                        }
                    })
                }
            })
            //修改市场活动
            $("#editActivityBtn").click(function () {
                var checkedIds = $("#tbody input[type='checkbox']:checked")
                if (checkedIds.size()==0){
                    alert("请选择需要修改的市场活动")
                }
                if (checkedIds.size()>1){
                    alert("每次只能修改一条市场活动")
                }
                // var id = checkedIds[0].value
                var id = checkedIds.val()
                // var id = checkedIds.get(0).value
                $.ajax({
                    url:"workbench/activity/queryActivityById.do",
                    data:{
                        id:id
                    },
                    dataType:"json",
                    type: "post",
                    success: function (data) {
                        $("#edit-activityId").val(data.id)
                        //会自动根据data.owner这个id自动去select标签下面的option的value属性去自动比对，跟谁相等自动选择显示，浏览器会帮我们处理
                        $("#edit-marketActivityOwner").val(data.owner)
                        $("#edit-marketActivityName").val(data.name)
                        $("#edit-startDate").val(data.startDate)
                        $("#edit-endDate").val(data.endDate)
                        $("#edit-description").val(data.description);
                        //弹出市场模态窗口
                        $("#editActivityModal").modal("show")
                    }
                })
            })
            //点击更新按钮
            $("#updateActivityBtn").click(function () {
                var id = $("#edit-activityId").val();
                var owner = $("#edit-marketActivityOwner").val();
                var name = $.trim($("#edit-marketActivityName").val())
                var startDate =  $("#edit-startDate").val()
                var endDate =  $("#edit-endDate").val()
                var cost =  $.trim($("#edit-cost").val())
                var description = $.trim($("#edit-description").val())
                res = formVerify(owner,name, startDate,endDate,cost)
                if (res){
                    $.ajax({
                        url:"workbench/activity/editActivityById.do",
                        data:{
                            id:id,
                            owner:owner,
                            name:name,
                            startDate:startDate,
                            endDate:endDate,
                            cost:cost,
                            description:description
                        },
                        dataType:"json",
                        type:"post",
                        success:function (data) {
                            if (data.code == "1") {
                                selectActivity($("#demo_pag1").bs_pagination('getOption', 'currentPage'), $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
                                $("#editActivityModal").modal("hide")
                            }else {
                                alert(data.message)
                                $("#editActivityModal").modal("show")
                            }
                        }
                    })
                }
            })
            $("#exportActivityAllBtn").click(function () {
                window.location.href = "workbench/activity/queryAllActivitys.do"

            })
        });

        //按条件查询的方法
        function selectActivity(pageNo, pageSize) {
            var name = $("#query-name").val()
            var owner = $("#query-owner").val()
            var startDate = $("#query-startDate").val()
            var endDate = $("#query-endDate").val()
            var htmlStr = ""
            $.ajax({
                type: "post",
                dataType: "json",
                data: {
                    name: name,
                    owner: owner,
                    startDate: startDate,
                    endDate: endDate,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                url: "workbench/activity/queryActivityByConditionForPage.do",
                success: function (data) {
                    $("#totalRowsB").text(data.totalRows)
                    $.each(data.activityList, function (index, activity) {
                        htmlStr += "<tr class=\"active\">"
                        htmlStr += "<td><input type=\"checkbox\" value=\"" + activity.id + "\"/></td>"
                        htmlStr += "<td><a style=\"text-decoration: none; cursor: pointer;\"onclick=\"window.location.href=\'detail.html\';\">" + activity.name + "</a></td>"
                        htmlStr += "<td>" + activity.owner + "</td>"
                        htmlStr += "<td>" + activity.startDate + "</td>"
                        htmlStr += "<td>" + activity.endDate + "</td>"
                        htmlStr += "</tr>"
                    })
                    $("#tbody").html(htmlStr)
                    var totalPages = 0
                    if (data.totalRows % pageSize == 0) {
                        totalPages = data.totalRows / pageSize
                    } else {
                        totalPages = parseInt(data.totalRows / pageSize + 1)
                    }
                    //分页插件
                    $("#demo_pag1").bs_pagination({
                        rowsPerPage: pageSize,//pageSize
                        currentPage: pageNo,//pageNo
                        totalRows: data.totalRows,
                        totalPages: totalPages,
                        visiblePageLinks: 5,
                        showGoToPage: false,

                        onChangePage: function (event, pageObj) { // returns page_num and rows_per_page after a link has clicked
                            selectActivity(pageObj.currentPage, pageObj.rowsPerPage)
                            $("#checkedAll").prop("checked",false)
                        }
                    });
                }
            })
        }
        function formVerify(owner,name,startDate,endDate,cost) {
            if (owner == "") {
                alert("所有者不能为空");
                return false;
            }
            if (name == "") {
                alert("名称不能为空");
                return false
            }
            if (startDate != "" && endDate != "") {
                if (startDate > endDate) {
                    alert("结束日期不能比开始日期小")
                    return false;
                }
            }

            var regExp = /^(([1-9]\d*)|0)$/
            if (!regExp.test(cost)) {
                alert("成本只能为非负整数");
                return false;
            }
            return true
        }
    </script>
</head>
<body>

<!-- 创建市场活动的模态窗口 -->
<div class="modal fade" id="createActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form" id="createActivityForm">

                    <div class="form-group">
                        <label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-marketActivityOwner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-marketActivityName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control myDate" id="create-startDate" readonly>
                        </div>
                        <label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control myDate" id="create-endDate" readonly>
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="create-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-cost">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-description"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveCreatActivityBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改市场活动的模态窗口 -->
<div class="modal fade" id="editActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form">
                    <input type="hidden" id="edit-activityId">
                    <div class="form-group">
                        <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-marketActivityOwner">
                                <c:forEach items="${userList}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control myDate" id="edit-startDate" readonly >
                        </div>
                        <label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control myDate" id="edit-endDate" readonly>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-cost" value="5,000">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-description">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="updateActivityBtn">更新</button>
            </div>
        </div>
    </div>
</div>

<!-- 导入市场活动的模态窗口 -->
<div class="modal fade" id="importActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
            </div>
            <div class="modal-body" style="height: 350px;">
                <div style="position: relative;top: 20px; left: 50px;">
                    请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                </div>
                <div style="position: relative;top: 40px; left: 50px;">
                    <input type="file" id="activityFile">
                </div>
                <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;">
                    <h3>重要提示</h3>
                    <ul>
                        <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                        <li>给定文件的第一行将视为字段名。</li>
                        <li>请确认您的文件大小不超过5MB。</li>
                        <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                        <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                        <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                        <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                    </ul>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>市场活动列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="query-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="query-owner">
                    </div>
                </div>


                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">开始日期</div>
                        <input class="form-control myDate" type="text" id="query-startDate" readonly/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">结束日期</div>
                        <input class="form-control myDate" type="text" id="query-endDate readonly" readonly>
                    </div>
                </div>

                <button type="button" class="btn btn-default" id="queryBtn">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" data-toggle="modal" id="createActivityBtn">
                    <span class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button type="button" class="btn btn-default" id="editActivityBtn"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button type="button" class="btn btn-danger" id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
            </div>
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal">
                    <span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）
                </button>
                <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）
                </button>
                <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）
                </button>
            </div>
        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="checkedAll"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="tbody">
                <%--<tr class="active">
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           onclick="window.location.href='detail.html';">发传单</a></td>
                    <td>zhangsan</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                </tr>
                <tr class="active">
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           onclick="window.location.href='detail.html';">发传单</a></td>
                    <td>zhangsan</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                </tr>--%>
                </tbody>
            </table>
        </div>

        <div style="height: 50px; position: relative;top: 30px;">

            <div id="demo_pag1"></div>

            <%--<div>
                <button type="button" class="btn btn-default" style="cursor: default;">共<b id="totalRowsB"></b>条记录</button>
            </div>
            <div class="btn-group" style="position: relative;top: -34px; left: 110px;">
                <button type="button" class="btn btn-default" style="cursor: default;">显示</button>
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                        10
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">20</a></li>
                        <li><a href="#">30</a></li>
                    </ul>
                </div>
                <button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
            </div>
            <div style="position: relative;top: -88px; left: 285px;">
                <nav>
                    <ul class="pagination">
                        <li class="disabled"><a href="#">首页</a></li>
                        <li class="disabled"><a href="#">上一页</a></li>
                        <li class="active"><a href="#">1</a></li>
                        <li><a href="#">2</a></li>
                        <li><a href="#">3</a></li>
                        <li><a href="#">4</a></li>
                        <li><a href="#">5</a></li>
                        <li><a href="#">下一页</a></li>
                        <li class="disabled"><a href="#">末页</a></li>
                    </ul>
                </nav>
            </div>--%>
        </div>

    </div>

</div>
</body>
</html>
