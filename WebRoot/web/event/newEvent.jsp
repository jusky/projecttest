<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<%  
    String path = request.getContextPath();  
    String basePath = request.getScheme() + "://"  
                + request.getServerName() + ":" + request.getServerPort()  
                + path + "/";  
%> 
<script type="text/javascript">
function setRealName(cb)
{
	if(cb.checked==true)
	{
		document.getElementById("niDiv").style.display="block";
		document.getElementById("jbName").value="";
		document.getElementById("jbName").readOnly=false;
		document.getElementById("reportReason").value = "";
		document.getElementById("reportContent").value = "";	
		deleteDefault();
	}
	
}
function setName(cb)
{
	if(cb.checked==true)
	{
		document.getElementById("niDiv").style.display="none";
		document.getElementById("jbName").value="匿名举报";
		document.getElementById("jbName").readOnly=true;
		document.getElementById("reportReason").value = "";
		document.getElementById("reportContent").value = "";
		//将必填的数据填充为默认值，提交表单后可以不验证这些信息
		setDefault();
	}
	else
	{
		document.getElementById("niDiv").style.display="block";
		document.getElementById("jbName").value="";
		document.getElementById("jbName").readOnly=false;
		document.getElementById("reportReason").value = "";
		document.getElementById("reportContent").value = "";
		deleteDefault();
	}
}
function setSimilar(cb)
{
	if(cb.checked==true)
	{
		document.getElementById("nimingID").readOnly = true;
		document.getElementById("niDiv").style.display="none";
		document.getElementById("jbName").value="信息中心";
		document.getElementById("jbName").readOnly=true;
		document.getElementById("reportReason").value = "申请项目高相似度";
		document.getElementById("reportContent").value = "整体相似度：%，摘要：%，立项依据：%，研究内容：%，研究方案：%，特色创新：%";
		//将必填的数据填充为默认值，提交表单后可以不验证这些信息
		setDefault();
		
	}
	else
	{
		document.getElementById("niDiv").style.display="block";
		document.getElementById("jbName").value="";
		document.getElementById("jbName").readOnly=false;
		
		document.getElementById("reportReason").value = "";
		document.getElementById("reportContent").value = "";
		
		deleteDefault();
	}
}
function setDefault()
{
	document.getElementById("dept").value="无单位";
	document.getElementById("telPhone").value="12345678901";
	document.getElementById("mailAddress").value="no@no.com";
}
function deleteDefault()
{
	document.getElementById("dept").value="";
	document.getElementById("telPhone").value="";
	document.getElementById("mailAddress").value="";
}
function commit()
{
    document.forms[0].action = "<%=path%>/newEventAction.do?method=commit";
    document.forms[0].submit();
}
function showreason()
{
	paramers="dialogWidth:520px; dialogHeight:500px; resizable:yes; overflow:auto; status:no";
	url = "<%=path%>/web/event/reportReason.jsp";
	var value1 = window.showModalDialog(url, "", paramers);
	if(value1.names !== null)
	{	
		document.getElementById("reportReason").value=value1.names;
	}
}
</script>
<script type="text/javascript">
    $(document).ready(function() {
        $("#eventUpload").uploadify({
            'uploader' : '<%=path%>/dwz/uploadify/scripts/uploadify.swf',
            'script' : '<%=path%>/servlet/UploadServlet?type=event',//后台处理的请求
            'cancelImg' : '<%=path%>/dwz/uploadify/img/cancel.png',
            'folder' : 'uploads',//您想将文件保存到的路径
            'queueID' : 'fileQueue',//与下面的id对应
            'fileDataName'   : 'eventUpload', //和以下input的name属性一致 
            'queueSizeLimit' : 1,
            'auto' : false,
            'multi' : true,
            'simUploadLimit' : 2,
            'buttonText' : 'BROWSE'
        });
    });
</script>
<script type="text/javascript" src="<%=path%>/js/uploadAjax.js"></script>
<script type="text/javascript">
	function uploadNewEvent(obj,type)
	{
		var url="<%=path%>/servlet/DeleteUploadServlet?type=" + type;
		sendAjax(url);
		jQuery(obj).uploadifyUpload();
	}
</script>
<div class="pageContent">
	<form method="post" class="pageForm required-validate" onsubmit="return validateCallback(this, navTabAjaxDone);" action="<%=path%>/newEventAction.do?method=save">
		<div class="pageFormContent" layoutH="56">
			<div class="panel">
				<h1>举报人信息（注意：标有<font color="#ff0000">*</font>的必须填写）</h1>
				<div style="background:#ffffff;">
				<dl class="nowrap">
					<dt>举报人姓名：</dt>
					<dd>
						<input id="jbName" name="reportName" minlength="2" maxlength="15" class="required" type="text" size="20" value=""/>
						<input type="radio" name="choose" valude="1" checked onclick="setRealName(this)"/>实名举报
						<input type="radio" id="nimingID" name="choose" value="1" onclick="setName(this);"/>匿名举报
						<input type="radio" name="choose" value="1" onclick="setSimilar(this);"/>高相似度
					</dd>
				</dl>
				<dl class="nowrap">
					<dt>编号：</dt>
					<dd>
						<input id="serialNum" name="serialNum" minlength="5" maxlength="8" class="required" type="text" size="10" value="<%=request.getAttribute("SerialNum") %>"/>
					</dd>
				</dl>
				<div id="niDiv">
				<dl class="nowrap">
					<dt>所属单位：</dt>
					<dd>
						<input id="dept" name="dept" minlength="3" maxlength="30" class="required" type="text" size="70" value=""/>
					</dd>
				</dl>
				<dl class="nowrap">
					<dt>办公电话：</dt>
					<dd>
						<input id="gdPhone" minlength="4" maxlength="15" name="gdPhone" type="text" size="30" value=""/>
					</dd>
				</dl>
				<dl class="nowrap">
					<dt>手机号码：</dt>
					<dd>
						<input id="telPhone" name="telPhone" minlength="5" maxlength="15" class="required phone" class="phone" type="text" size="30" value=""/>
					</dd>
				</dl>
				<dl class="nowrap">
					<dt>邮箱地址：</dt>
					<dd>
						<input id="mailAddress" name="mailAddress" class="required email" type="text" size="50" value=""/>
					</dd>
				</dl>
				</div>
				<dl class="nowrap">
					<dt>举报时间：</dt>
					<dd>
						<input id="reportTime" type="text" name="reportTime" class="required date" size="20" readonly="true"/><a class="inputDateButton" href="javascript:;">选择</a>
					</dd>
				</dl>
				<!--
				<dl class="nowrap">
					<dt>举报方式：</dt>
					<dd>
						<select class="combox" name="reportType">
							<option value="书信举报">书信举报</option>
							<option value="邮件举报">邮件举报</option>
							<option value="网络举报">网络举报</option>
							<option value="电话举报">电话举报</option>
							<option value="其他方式">其他方式</option>
						</select>
					</dd>
				</dl>
				 -->
				</div>
			</div>

			<div class="panel">
				<h1>被举报人信息（注意：标有<font color="#ff0000">*</font>的必须填写，最多输入5个被举报人）</h1>
				<div style="background:#ffffff;">
					<table class="list nowrap itemDetail" addButton="新建被举报人" width="100%">
						<thead>
							<tr>
								<th align="center" type="text" name="items.name[#index#]" size="15" fieldClass="required">姓名</th>
								<th align="center" type="text" name="items.position[#index#]" size="25">职称</th>
								<th align="center" type="text" name="items.phone[#index#]" size="20" fieldClass="digits">联系方式</th>
								<th align="center" type="text" name="items.dept[#index#]" fieldClass="required"  size="60">所属单位</th>
								<th align="center" type="del" width="30">操作</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				
			</div>
			<div class="panel">
				<h1>举报内容（注意：标有<font color="#ff0000">*</font>的必须填写）</h1>
				<div style="background:#ffffff;">
				<dl class="nowrap">
					<dt>举报事由：</dt>
					<dd>
						<textarea class="required" readonly id="reportReason" rows="4" cols="60" name="org4.jbReason" ></textarea>
						<a class="btnLook" href="javascript:showreason()">选择举报事由</a>
						<span class="info"></span>
					</dd>
				</dl>
				<dl class="nowrap">
					<dt>举报详情：</dt>
					<dd>
						<textarea id="reportContent" class="required" rows="15" cols="100" minlength="10" name="reportContent"></textarea>
					</dd>
				</dl>
				<dl class="nowrap">
					<dt>上传附件：</dt>
					<dd>
						<input type="file" name="eventUpload" id="eventUpload" />
        					<a href="javascript:uploadNewEvent('#eventUpload','event')">开始上传</a>&nbsp;
        					<a href="javascript:jQuery('#eventUpload').uploadifyClearQueue()">取消所有上传</a>
    					<div id="fileQueue"></div>
					</dd>
				</dl>
				</div>
			</div>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
				<!-- 
				<li><div class="button"><div class="buttonContent"><button type="button" onclick="commit();">提交</button></div></div></li>
				 -->
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
			</ul>
		</div>
	</form>
</div>