/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.zuzhi;

import java.io.IOException;
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
import com.whu.web.common.SystemShare;
import com.whu.web.user.UserBean;
import com.whu.web.user.UserManageForm;

/** 
 * MyEclipse Struts
 * Creation date: 08-05-2013
 * 
 * XDoclet definition:
 * @struts.action path="/zzManageAction" name="zzManageForm" input="/web/zuzhi/zzManage.jsp" parameter="method" scope="request" validate="true"
 */
public class ZzManageAction extends DispatchAction {
	/*
	 * Generated Methods
	 */

	/** 
	 * Method execute
	 * 初始化组织机构树
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws IOException 
	 */
	public void initTree(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		ZzManageForm zzManageForm = (ZzManageForm) form;// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		//type=1 表示正常显示菜单，type=2表示展开所有菜单项，用于选择上级组织时的下拉菜单
		String type = request.getParameter("type");
		
		String sql = "select * from SYS_ZZINFO";
		//添加专家时，选择的学部，仅仅查询出学部下的组织即可
		if(type.equals("3"))
		{
			sql = "select * from SYS_ZZINFO where PZZID='3000'";
			type="2";
		}
		DBTools dbTools = new DBTools();
		
        List<String> lstTree = dbTools.queryAllZZ(sql, new String[0], type);
        //利用Json插件将Array转换成Json格式
        response.getWriter().print(JSONArray.fromObject(lstTree).toString()); 
	}
	/**
	 * 选定一个组织名称后，显示
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		ZzManageForm zzManageForm = (ZzManageForm) form;// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// 
		int rowsPerPage = 20;// 
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null && request.getParameter("queryPageNo") != "") {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		
		String sql = "select a.*,b.ZZNAME as PZZNAME from SYS_ZZINFO a, SYS_ZZINFO b where a.PZZID=b.ZZID order by ZZID asc";
		String[] params = new String[0];
		request.getSession().setAttribute("queryZZSql", sql);
		request.getSession().setAttribute("queryZZParams", params);
		pageBean.setQuerySql(sql);
		pageBean.setParams(params);
		
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryZZList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			zzManageForm.setRecordNotFind("false");
			zzManageForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			zzManageForm.setRecordNotFind("true");
			
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		ZzManageForm zzManageForm = (ZzManageForm) form;
		String operation = request.getParameter("operation");

		CheckPage pageBean = new CheckPage();
		String sql = "";
		DBTools db = new DBTools();
		String[] params = new String[0];
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		
		if (operation.equalsIgnoreCase("search") || operation.equalsIgnoreCase("select")) {
			String id = request.getParameter("id");
			String temp = "";
			//点击树形菜单，根据选中的编号查询
			if(id!=null && !id.equals(""))
			{
				if(db.querySingleData("SYS_ZZINFO", "ISJC", "ZZID", id).equals("1"))//顶级单位，可能有下级单位
				{
					temp += " and a.PZZID=?";
				}
				else
				{
					temp += " and a.ZZID=?";
				}
				params = new String[]{id};
			}
			else
			{
				String zzName = zzManageForm.getZzName();
				if(zzName != null && !zzName.equals(""))
				{
					temp += " and a.ZZNAME like ?";
					params  = new String[]{"%" + zzName + "%"};
				}
			}
			sql = "select a.*,b.ZZNAME as PZZNAME from SYS_ZZINFO a, SYS_ZZINFO b where a.PZZID=b.ZZID " + temp + " order by ZZID asc";
			request.getSession().setAttribute("queryZZSql", sql);
			request.getSession().setAttribute("queryZZParams", params);
		}
		else if(operation.equalsIgnoreCase("changePage")){
			sql = (String)request.getSession().getAttribute("queryZZSql");
			params = (String[])request.getSession().getAttribute("queryZZParams");
			if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
			}
		}
		pageBean.setQuerySql(sql);
		pageBean.setParams(params);
		pageBean.setQueryPageNo(queryPageNo);
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryZZList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			zzManageForm.setRecordNotFind("false");
			zzManageForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			zzManageForm.setRecordNotFind("true");
			
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		//添加完组织之后，会自动刷新页面；如果直接跳转到dicList页面，则左侧的组织机构树将会不显示，出现问题！设置该变量用于控制页面的跳转
		String type =(String) request.getSession().getAttribute("configFlag");
		if(type == null || type.equals(""))
		{
			//跳转到zzList.jsp
			return mapping.findForward("initZZ");
		}
		else if(type.equals("true"))
		{
			//跳转到zzManage.jsp
			request.getSession().setAttribute("configFlag", "");
			return mapping.findForward("init");
		}
		return null;
	}
	/**
	 * 删除组织信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		String type = request.getParameter("type");
		
		String ids = request.getParameter("ids");
		DBTools dbTool = new DBTools();
		boolean result = true;
		if(ids == null || ids == "")
		{
			String id = request.getParameter("id");
			result = dbTool.deleteItemReal(id, "SYS_ZZINFO", "ID");
		}
		else
		{
			String[] arrID = ids.split(",");
			result = dbTool.deleteItemsReal(arrID, "SYS_ZZINFO", "ID");
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			request.getSession().setAttribute("configFlag", "true");
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
	public ActionForward detail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		ZzManageForm zzManageForm = (ZzManageForm)form;
		
		String id = request.getParameter("id");
		DBTools dbTools = new DBTools();
		String sql = "select a.*,b.ZZNAME as PZZNAME from SYS_ZZINFO a, SYS_ZZINFO b where a.PZZID=b.ZZID and a.ID=?";
		ZZBean zzBean = dbTools.queryZZBean(sql, new String[]{id});
		ArrayList result = new ArrayList();
		if(zzBean!=null)
		{
			result.add(zzBean);
			zzManageForm.setRecordNotFind("false");
			zzManageForm.setRecordList(result);
			return mapping.findForward("detail");
		}
		else
		{
			zzManageForm.setRecordNotFind("true");
			return mapping.findForward("initError");
		}
	}
}