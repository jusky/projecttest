/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.eventmanage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.sql.ResultSet;

import com.whu.tools.DBTools;
import com.whu.tools.CheckPage;
import com.whu.web.user.UserBean;
import com.whu.web.common.SystemShare;
import com.whu.tools.SystemConstant;

/** 
 * MyEclipse Struts
 * Creation date: 01-14-2015
 * 
 * XDoclet definition:
 * @struts.action path="/dispatchEventAction" name="dispatchManageForm" input="/web/eventManage/dispatchEvent.jsp" parameter="method" scope="request" validate="true"
 * @struts.action-forward name="init" path="/web/eventManage/dispatchEvent.jsp"
 */
public class DispatchEventAction extends DispatchAction {
	/*
	 * Generated Methods
	 */

	
	public ActionForward init( ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {		
		DispatchEventForm dispatchEventForm = (DispatchEventForm)form;
	
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null) {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		String sql = "select ID,LOGINNAME,USERNAME from SYS_USER where POSIDS=9";
		request.getSession().setAttribute("queryOfficerSql", sql);
		pageBean.setQuerySql(sql);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryOfficerList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			dispatchEventForm.setRecordNotFind("false");
			dispatchEventForm.setRecordList(result);
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			dispatchEventForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		DispatchEventForm dispatchEventForm = (DispatchEventForm)form;
		String operation = request.getParameter("operation");
		CheckPage pageBean = new CheckPage();
		String sql = "";
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (operation.equalsIgnoreCase("search")) {
			String officer = dispatchEventForm.getOfficer();
			String temp = "";
			if(officer != null && !officer.equals(""))
			{
				temp += " and USERNAME like '%" + officer + "%'";
			}
			sql = "select ID,USERNAME,LOGINNAME from SYS_USER where POSIDS=9 " + temp;
			request.getSession().setAttribute("queryOfficerSql", sql);	
		}
		else if(operation.equalsIgnoreCase("changePage")){
			sql = (String)request.getSession().getAttribute("queryOfficerSql");
			if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
			}
		}
		pageBean.setQuerySql(sql);
		pageBean.setQueryPageNo(queryPageNo);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryOfficerList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			dispatchEventForm.setRecordNotFind("false");
			dispatchEventForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			dispatchEventForm.setRecordNotFind("true");
			
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	
	public ActionForward dispatch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");

		String serialNum = request.getParameter("serialNum");
		String userId = request.getParameter("officer");
		
		DBTools dbTool = new DBTools();
		// get officer before and now associated to this serialNum;
		String oldOfficerName = dbTool.querySingleDate("SYS_USER", "USERNAME", "LOGINNAME", dbTool.querySingleDate("TB_REPORTINFO", "OFFICER", "SERIALNUM", serialNum));
		String newOfficerName = dbTool.querySingleDate("SYS_USER", "USERNAME", "ID", userId);
		String newOfficer = dbTool.querySingleDate("SYS_USER", "LOGINNAME", "ID", userId);
		// get status, if like 4% prevent dispatch
		String status = dbTool.querySingleDate("TB_REPORTINFO", "STATUS", "SERIALNUM", serialNum);
				
		boolean result = true;
		if (status.charAt(0) == '4') {
			result = false;
		}
		
		String sql = "update TB_REPORTINFO set OFFICER='" + newOfficer + "' where SERIALNUM='" + serialNum + "'";
		
		if (result) {
			result = dbTool.insertItem(sql);
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			String userName = (String)request.getSession().getAttribute("UserName"); // operator name
			if (oldOfficerName != null && !oldOfficerName.equals("")) {
				dbTool.insertLogInfo(userName, SystemConstant.LOG_DISPATCH, "从 " + oldOfficerName + " 分派案件 " + serialNum + " 给： " + newOfficerName, request.getRemoteAddr());
			} else {
				dbTool.insertLogInfo(userName, SystemConstant.LOG_DISPATCH, "分派案件 " + serialNum + " 给： " + newOfficerName, request.getRemoteAddr());
			}
			
			json.put("statusCode", 200);
			json.put("message", "更新成功！");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "更新失败！");
			if (status.charAt(0) == '4') {
				json.put("message", "事件已结束! 无法指派");
			}
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;

	}
	
}