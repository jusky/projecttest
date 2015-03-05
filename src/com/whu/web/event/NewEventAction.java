/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.event;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.DBTools;
import com.whu.tools.SystemConstant;
import com.whu.web.common.SystemShare;
import com.whu.web.log.LogBean;

/** 
 * MyEclipse Struts
 * Creation date: 07-24-2013
 * 
 * XDoclet definition:
 * @struts.action path="/newEventAction" name="newEventForm" input="/web/event/newEvent.jsp" parameter="method" scope="request" validate="true"
 */
public class NewEventAction extends DispatchAction {
	/*
	 * Generated Methods
	 */
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String sql = "select SERIALNUM from TB_REPORTINFO order by ID desc limit 1";
		String serialNum = SystemShare.GetSerialNum(sql);
		request.setAttribute("SerialNum", serialNum);
		return mapping.findForward("init");
	}
	/** 
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws IOException 
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		NewEventForm newEventForm = (NewEventForm) form;
		boolean result = false;
		
		try {
			String isNi = request.getParameter("niming");
			if(isNi == null)//如果为null，说明没有勾选，不是匿名
			{
				isNi = "0";
			}
			else//否则，勾选，是匿名
			{
				isNi = "1";
			}
			String reportName = newEventForm.getReportName();
			String reportType = "网络举报";
			//高相似度项目也认为是匿名举报
			if(reportName.equals("信息中心"))
			{
				isNi = "1";
				reportType = "相似度检测";
			}
			String serialNum = newEventForm.getSerialNum();
			String gdPhone = newEventForm.getGdPhone();
			String mailAddress = newEventForm.getMailAddress();
			String telPhone = newEventForm.getTelPhone();
			String dept = newEventForm.getDept();
			String reportTime = newEventForm.getReportTime();
			//String reportType = newEventForm.getReportType();
			//目前暂不区分举报方式，全部默认为：网络举报
			
			//String jbReason = newEventForm.getReportReason();
			String jbReason = request.getParameter("org4.jbReason");
			String reasonID = request.getParameter("org4.reasonID");
			String jbContent = newEventForm.getReportContent();
			String createName = (String)request.getSession().getAttribute("UserName");
			
			//根据时间生成查询码
			Calendar calendar = Calendar.getInstance();
			String searchID = String.valueOf(calendar.getTimeInMillis());
			
			String createTime = SystemShare.GetNowTime("yyyy-MM-dd");
			String reportID = SystemShare.GetNowTime("yyyyMMddHHmmss");
			
			String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/";
			//String path1 = filePath + "temp";
			String loginName = (String)request.getSession().getAttribute("LoginName");
			String path1 = request.getSession().getServletContext().getRealPath("/") + "/temp/" + loginName + "/";
			String path2 = filePath + reportID;
			//获得服务器的IP地址路径，存放在数据库中，便于下载
			String relDirectory = "attachment" + "/" + reportID;
			//将临时文件夹中的附件转存到以警情编号为目录的文件夹下
			SystemShare.IOCopy(path1, path2, relDirectory, createName);
			
			EventBean eb = new EventBean();
			eb.setReportID(reportID);
			eb.setReportName(reportName);
			eb.setSerialNum(serialNum);
			eb.setDept(dept);
			eb.setGdPhone(gdPhone);
			eb.setTelPhone(telPhone);
			eb.setMailAddress(mailAddress);
			eb.setReportTime(reportTime);
			eb.setReportType(reportType);
			eb.setCreateTime(createTime);
			eb.setCreateName(createName);
			eb.setReportReason(jbReason);
			eb.setReportContent(jbContent);
			eb.setStatus(SystemConstant.SS_RECVEVENT);
			eb.setIsNI(isNi);
			eb.setIsDelete("0");
			eb.setSearchID(searchID);
			
			String attachName = (String)request.getSession().getAttribute("EventAttachName");
			if(attachName != null && !attachName.equals(""))
			{
				attachName = reportID + "/" + attachName;
				request.getSession().setAttribute("EventAttachName", "");
			}
			else
			{
				attachName = "";
			}
			eb.setAccessory(attachName);
			
			String beName = "";
			String bePosition = "";
			String beTelPhone = "";
			String beDept = "";
			ArrayList list = new ArrayList();
			BeReportBean brb = null;
			String temp = "";
			for(int i = 0; i < SystemConstant.beReportNum; i++)
			{
				beName = request.getParameter("items.name[" + i + "]");
				bePosition = request.getParameter("items.position[" + i + "]");
				beTelPhone = request.getParameter("items.phone[" + i + "]");
				beDept = request.getParameter("items.dept[" + i + "]");
				if(beName != null && beName != "")
				{
					brb = new BeReportBean();
					brb.setReportID(reportID);
					brb.setBeName(beName);
					temp += beName + ",";
					brb.setBePosition(bePosition);
					brb.setBeTelPhone(beTelPhone);
					brb.setBeDept(beDept);
					list.add(brb);
				}
			}
			if(temp != "")//移除最后的“，”
			{
				temp = temp.substring(0, temp.length()-1);
			}
			else
			{
				temp = "无";
			}
			eb.setBeReportName(temp);
			DBTools db = new DBTools();
			try {
				//插入被举报人信息
				db.insertBeReport(list);
			} catch (SQLException e) {
				e.printStackTrace();
				result = false;
			}
			//插入举报信息
			result = db.InsertReportInfo(eb);
			
			String describe = createTime + "," + createName + "在管理系统后台人工录入事件,举报人为：" +reportName + ",被举报人为：" + temp + ",举报事由为：" + jbReason;
			result = db.InsertHandleProcess(reportID, createName, SystemConstant.HP_INPUT, SystemConstant.SS_RECVEVENT, SystemConstant.LCT_JB, describe);
			
			describe = createTime + "," + createName + "受理举报";
			//插入处理过程到数据库中
			db.InsertHandleProcess(reportID, createName, SystemConstant.HP_RECVEVENT, SystemConstant.SS_RECVEVENT, SystemConstant.LCT_SLJB, describe);
			
			//写入日志文件
			db.insertLogInfo(createName, SystemConstant.LOG_NEW, "新录入事件，事件编号为：" + reportID, request.getRemoteAddr());
			
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "事件录入成功！");
			json.put("callbackType", "closeCurrent");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "事件录入失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		return null;
	}

}