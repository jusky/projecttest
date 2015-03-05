/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.credit;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.CheckPage;
import com.whu.tools.DBTools;
import com.whu.tools.DebugLog;
import com.whu.tools.ExcelTools;
import com.whu.web.common.SystemShare;
import com.whu.web.credit.IndividualInfo;
import com.whu.web.credit.IndividualManageForm;

/** 
 * MyEclipse Struts
 * Creation date: 11-19-2014
 * 
 * XDoclet definition:
 * @struts.action path="/individualManage" name="individualManageForm" scope="request" validate="true"
 */
public class IndividualManageAction extends DispatchAction {
	/*
	 * Generated Methods
	 */


	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		IndividualManageForm individualManageForm = (IndividualManageForm)form;
		
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null) {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		//String sql = "select * from SYS_INDIVIDUAL_INFO";
		
		String sql = "select a.*,ROUND(IFNULL(b.CREDIT, 1), 4) as CREDIT from (select m.*,n.NAME as INST_NAME from SYS_INDIVIDUAL_INFO m,SYS_INST_INFO n where m.INSTITUTE=n.CODE) as a left join VIEW_INDIVIDUAL_CREDIT b on a.PID=b.INDIID order by CREDIT asc";
		
		request.getSession().setAttribute("queryIndividualSql", sql);
		pageBean.setQuerySql(sql);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryIndividualList(rs, rowsPerPage);
		
		if(result.size() > 0) {
			individualManageForm.setRecordNotFind("false");
			individualManageForm.setRecordList(result);
			SystemShare.SplitPageFun(request, pageBean, 1);
		} else {
			individualManageForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		
		return mapping.findForward("init");
	}
	
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		IndividualManageForm individualManageForm = (IndividualManageForm)form;
		String operation = request.getParameter("operation");
		CheckPage pageBean = new CheckPage();
		String sql = "";
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (operation.equalsIgnoreCase("search")) {
			String name = individualManageForm.getName();
			String institute = individualManageForm.getInstitute();
			String pid = individualManageForm.getPId();
			String temp = "";
			if(!name.equals(""))
			{
				temp += " and NAME like '%" + name + "%'";
			}
			if(!institute.equals(""))
			{
				temp += " and INSTITUTE in (select CODE from SYS_INST_INFO where NAME like '%" + institute + "%')";
			}
			if(!pid.equals(""))
			{
				temp += " and PID like '%" + pid + "%'";
			}
			sql = "select a.*,ROUND(IFNULL(b.CREDIT, 1), 4) as CREDIT from (select m.*,n.NAME as INST_NAME from SYS_INDIVIDUAL_INFO m,SYS_INST_INFO n where m.INSTITUTE=n.CODE) as a left join VIEW_INDIVIDUAL_CREDIT b on a.PID=b.INDIID where 1=1 " + temp +" order by CREDIT asc";
			request.getSession().setAttribute("queryIndividualSql", sql);
		}
		else if(operation.equalsIgnoreCase("changePage")){
			sql = (String)request.getSession().getAttribute("queryIndividualSql");
			if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
			}
		}
		pageBean.setQuerySql(sql);
		pageBean.setQueryPageNo(queryPageNo);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryIndividualList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			individualManageForm.setRecordNotFind("false");
			individualManageForm.setRecordList(result);			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			individualManageForm.setRecordNotFind("true");
			
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	
	
	
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");

		boolean result = false;
		String ids = request.getParameter("ids");
		DBTools dbTool = new DBTools();
		if(ids == null || ids == "")
		{
			String uid = request.getParameter("uid");
			System.out.println(uid);
			result = dbTool.deleteItemReal(uid, "SYS_INDIVIDUAL_INFO", "ID");
		}
		else
		{
			String[] arrID = ids.split(",");
			result = dbTool.deleteItemsReal(arrID, "SYS_INDIVIDUAL_INFO", "ID");
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "删除成功！");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "删除失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;
	}
	
/* export individual info and credit
 * 
 * 	
 */
	public ActionForward export(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		DBTools db = new DBTools();
		
		String sql = (String)request.getSession().getAttribute("queryIndividualSql");
		String num = request.getParameter("exportnum");
		if( num != null && Integer.parseInt(num) > 0){
			sql += " limit " + num;
		}
		try
		{
			String fname = "individualList";
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment;filename=" + fname + ".xls");
			response.setContentType("application/msexcel");
			ResultSet rs = db.queryRsList(sql);
			rs.last();
			int length = rs.getRow();
			rs.beforeFirst();
			ArrayList result = db.queryIndividualList(rs, length);
			ExcelTools et = new ExcelTools();
			//et.createSheet(rs, os);
			String sheetName = "科研人员表";
			ArrayList titleList = new ArrayList();
			titleList.add("姓名");
			titleList.add("身份证");
			titleList.add("诚信值");
			titleList.add("所在单位");
			titleList.add("联系方式");
			et.createEventSheet(result, os, sheetName,8, titleList);
			rs.close();
		}
		catch(Exception e)
		{
			DebugLog.WriteDebug(e);
		}
		return null;
	}
	
	
	
	
	public ActionForward detail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		String pid = request.getParameter("pid");
		String sql = "";
		if(pid != ""){
			sql = "select * from TB_MISCOUNT where INSTID='" + pid + "'";
		}		
		return mapping.findForward("detail");
	}
}