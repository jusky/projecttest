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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
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
import com.whu.web.credit.InstituteInfo;
import com.whu.web.credit.InstituteManageForm;


/** 
 * MyEclipse Struts
 * Creation date: 12-02-2014
 * 
 * XDoclet definition:
 * @struts.action path="/miscountManageAction" name="miscountManageAction" input="/web/credit/miscountManage.jsp" parameter="method" scope="request" validate="true"
 * @struts.action-forward name="init" path="/web/credit/miscountManage.jsp"
 */
public class MiscountManageAction extends DispatchAction {
	
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		MiscountManageForm miscountManageForm = (MiscountManageForm)form;
		
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null) {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		String sql = "select a.ID,a.MISCOUNTID,a.TITLE,a.REPORTID,a.TIME,a.DETAIL,b.NAME as INDI,c.NAME as INST, GROUP_CONCAT(d.RID SEPARATOR ',') as MISLIST,GROUP_CONCAT(d.RNAME SEPARATOR ',') as MISNAME, e.CAPTION as PUNISHMENT from VIEW_FULL_MISCOUNT a, SYS_INDIVIDUAL_INFO b, SYS_INST_INFO c, SYS_JBREASON as d, (select CODE,CAPTION from SYS_DATA_DIC where CODENAME='ZDBZ_CLJD')as e where a.INDIID=b.PID and a.INSTID=c.CODE and a.MISTYPE=d.RID and a.PUNISH=e.CODE group by a.MISCOUNTID";
		request.getSession().setAttribute("queryMiscountSql", sql);
		pageBean.setQuerySql(sql);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryMiscountList(rs, rowsPerPage);
				
		if(result.size() > 0)
		{
			miscountManageForm.setRecordNotFind("false");
			miscountManageForm.setRecordList(result);
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			miscountManageForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		MiscountManageForm miscountManageForm = (MiscountManageForm)form;
		String operation = request.getParameter("operation");
			
		CheckPage pageBean = new CheckPage();
		String sql = "";
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		
		// for request from individualManage.jsp to query about individual
			String pid = request.getParameter("pid");
			if(pid != null && pid != "")
			{
				sql = "select a.ID,a.MISCOUNTID,a.TITLE,a.REPORTID,a.TIME,a.DETAIL,b.NAME as INDI,c.NAME as INST, GROUP_CONCAT(d.RID SEPARATOR ',') as MISLIST,GROUP_CONCAT(d.RNAME SEPARATOR ',') as MISNAME, e.CAPTION as PUNISHMENT from (select * from VIEW_FULL_MISCOUNT where INDIID='" + pid + "') as a, SYS_INDIVIDUAL_INFO b, SYS_INST_INFO c, SYS_JBREASON as d, (select CODE,CAPTION from SYS_DATA_DIC where CODENAME='ZDBZ_CLJD')as e where a.INDIID=b.PID and a.INSTID=c.CODE and a.MISTYPE=d.RID and a.PUNISH=e.CODE group by a.MISCOUNTID";
				request.getSession().setAttribute("queryIndiMiscountSql", sql);
				request.setAttribute("instDetail", "1");
			}		
			else if (operation.equalsIgnoreCase("search")) {
				String individual = miscountManageForm.getIndividual();
				String institute = miscountManageForm.getInstitute();
				String punish = miscountManageForm.getPunish();
				String mistype = miscountManageForm.getMistype();
				String indi_sql = "";
				String inst_sql = "";
				String mis_sql = "";
				String punish_sql ="";
				
				indi_sql = "select PID,NAME from SYS_INDIVIDUAL_INFO where NAME like '%" + individual + "%'";
				inst_sql = "select CODE,NAME from SYS_INST_INFO where NAME like '%" + institute + "%'";
				punish_sql = "select CODE,CAPTION from SYS_DATA_DIC where CODENAME='ZDBZ_CLJD' and CAPTION like '%" + punish + "%'";
				mis_sql = "select RID,RNAME from SYS_JBREASON where RNAME like '%"  + mistype + "%'";
				
				sql = "select a.ID,a.MISCOUNTID,a.TIME,a.TITLE,a.REPORTID,a.DETAIL,b.NAME as INDI,c.NAME as INST, GROUP_CONCAT(d.RID SEPARATOR ',') as MISLIST,GROUP_CONCAT(d.RNAME SEPARATOR ',') as MISNAME, e.CAPTION as PUNISHMENT from VIEW_FULL_MISCOUNT a, (" + indi_sql + ") b, (" + inst_sql + ") c, (" + mis_sql + ") d, (" + punish_sql + ") e where a.INDIID=b.PID and a.INSTID=c.CODE and a.MISTYPE=d.RID and a.PUNISH=e.CODE group by a.MISCOUNTID";
				request.getSession().setAttribute("queryMiscountSql", sql);
			}
			else if(operation.equalsIgnoreCase("changePage")){
				sql = (String)request.getSession().getAttribute("queryMiscountSql");
				if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
					queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
				}
			}
		pageBean.setQuerySql(sql);
		pageBean.setQueryPageNo(queryPageNo);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryMiscountList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			miscountManageForm.setRecordNotFind("false");
			miscountManageForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			miscountManageForm.setRecordNotFind("true");
			
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
			result = dbTool.deleteItemReal(uid, "TB_MISCOUNT", "ID");
		}
		else
		{
			String[] arrID = ids.split(",");
			result = dbTool.deleteItemsReal(arrID, "TB_MISCOUNT", "ID");
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
	
	public ActionForward export(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		DBTools db = new DBTools();
		String sql = "";
		String indi = request.getParameter("indi");
		if( indi !=null && indi.equals("true")){
			sql = (String)request.getSession().getAttribute("queryIndiMiscountSql");}
		else sql = (String)request.getSession().getAttribute("queryMiscountSql");
		String num = request.getParameter("exportnum");
		if( num != null && Integer.parseInt(num) > 0){
			sql += " limit " + num;
		}
		try
		{
			String fname = "miscountList";
			OutputStream os = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment;filename=" + fname + ".xls");
			response.setContentType("application/msexcel");
			ResultSet rs = db.queryRsList(sql);
			rs.last();
			int length = rs.getRow();
			rs.beforeFirst();
			ArrayList result = db.queryMiscountList(rs, length);
			ExcelTools et = new ExcelTools();
			//et.createSheet(rs, os);
			String sheetName = "不端行为记录表";
			ArrayList titleList = new ArrayList();
			titleList.add("编号");
			titleList.add("标题");
			titleList.add("当事人");
			titleList.add("当事单位");
			titleList.add("不端类型");
			titleList.add("处罚措施");
			titleList.add("生效时间");
			titleList.add("相关举报编号");
			et.createEventSheet(result, os, sheetName,9, titleList);
			rs.close();
		}
		catch(Exception e)
		{
			DebugLog.WriteDebug(e);
		}
		return null;
	}
	
	
	public void initTree(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		MiscountManageForm miscountManageForm = (MiscountManageForm) form;
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		
		String sql = "select CODE,CAPTION from SYS_DATA_DIC where CODENAME='ZDBZ_CLJD'";

		DBTools dbTools = new DBTools();
		
      List<String> lstTree = dbTools.queryPunishTree(sql);
      response.getWriter().print(JSONArray.fromObject(lstTree).toString()); 
	}
}