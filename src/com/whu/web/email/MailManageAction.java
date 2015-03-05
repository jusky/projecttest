/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.email;

import java.io.IOException;
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
import com.whu.tools.EmailTools;
import com.whu.tools.ReciveEmail;
import com.whu.web.common.SystemShare;
import com.whu.web.position.PosBean;
import com.whu.web.position.PosManageForm;
import com.whu.web.role.RoleManageForm;

/** 
 * MyEclipse Struts
 * Creation date: 07-29-2013
 * "�ⲿ�ʼ�"���ܳ�ʼ��Action
 * XDoclet definition:
 * @struts.action path="/mailManageAction" name="mailManageForm" input="/web/email/recvMailManage.jsp" parameter="method" scope="request" validate="true"
 */
public class MailManageAction extends DispatchAction {
	/*
	 * Generated Methods
	 */

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		MailManageForm mailManageForm = (MailManageForm)form;
		String loginName = (String)request.getSession().getAttribute("LoginName");
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null) {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		String sql = "select ID,ACCOUNTNAME,MAILADDRESS,ISDEFAULT from TB_MAILCONFIG where LOGINNAME='" + loginName + "'";
		request.getSession().setAttribute("queryMailSql", sql);
		pageBean.setQuerySql(sql);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryMailList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			mailManageForm.setRecordNotFind("false");
			mailManageForm.setRecordList(result);
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			mailManageForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		String loginName = (String)request.getSession().getAttribute("LoginName");
		MailManageForm mailManageForm = (MailManageForm)form;
		String operation = request.getParameter("operation");
		
		CheckPage pageBean = new CheckPage();
		String sql = "";
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		if (operation.equalsIgnoreCase("search")) {
			String accountName = mailManageForm.getAccountName();
			String mailAddress = mailManageForm.getMailAddress();
			//System.out.println(mailAddress);
			String temp = "";
			if(!accountName.equals(""))
			{
				temp += " and ACCOUNTNAME like '%" + accountName + "%'";
			}
			if(!mailAddress.equals(""))
			{
				temp +=" and MAILADDRESS like '%" + mailAddress +"%'";
			}
			sql = "select ID,ACCOUNTNAME,MAILADDRESS,ISDEFAULT from TB_MAILCONFIG where LOGINNAME='" + loginName + "' " + temp;
			request.getSession().setAttribute("queryMailSql", sql);
		}
		else if(operation.equalsIgnoreCase("changePage")){
			sql = (String)request.getSession().getAttribute("queryMailSql");
			if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
			}
		}
		pageBean.setQuerySql(sql);
		pageBean.setQueryPageNo(queryPageNo);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryMailList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			mailManageForm.setRecordNotFind("false");
			mailManageForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			mailManageForm.setRecordNotFind("true");
			
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
			String uid = request.getParameter("id");
			result = dbTool.deleteItemReal(uid, "TB_MAILCONFIG", "ID");
		}
		else
		{
			String[] arrID = ids.split(",");
			result = dbTool.deleteItemsReal(arrID, "TB_MAILCONFIG", "ID");
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
	/**
	 * 设置默认邮箱
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward setDefault(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String loginName = (String) request.getSession().getAttribute("LoginName");
		String id = request.getParameter("id");
		DBTools dbTools = new DBTools();
		String sql = "update TB_MAILCONFIG set ISDEFAULT='0' where ISDEFAULT='1'";
		
		boolean result = dbTools.insertItem(sql);
		if(result)
		{
			sql = "update TB_MAILCONFIG set ISDEFAULT='1' where ID=" + id;
			result = dbTools.insertItem(sql);
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "设置默认邮箱成功！");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "设置默认邮箱失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;
	}
	/**
	 * 邮箱配置测试
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward test(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String id = request.getParameter("id");
		DBTools dbTools = new DBTools();
		EmailTools emailTool = new EmailTools();
		String sql = "select * from TB_MAILCONFIG where ID=" + id;
		EmailBean emailBean = dbTools.queryEmailConfig(sql);
		if(emailBean!=null)
		{
			boolean result = emailTool.TestConnection(emailBean);
			PrintWriter out = response.getWriter();
			JSONObject json = new JSONObject();
			if(result)
			{
				json.put("statusCode", 200);
				json.put("message", "邮箱配置成功，可以正常使用！");
			}
			else
			{
				json.put("statusCode", 300);
				json.put("message", "邮箱配置失败，请检查信息！");
			}
			out.write(json.toString());
			out.flush();
			out.close();
		}
		return null;
	}
	/**
	 * 查询邮箱配置详情
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward detail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		MailManageForm mailManageForm = (MailManageForm)form;
		String id = request.getParameter("id");
		DBTools dbTools = new DBTools();
		String sql = "select * from TB_MAILCONFIG where ID=" + id;
		EmailBean emailBean = dbTools.queryEmailConfig(sql);
		ArrayList result = new ArrayList();
		if(emailBean!=null)
		{
			result.add(emailBean);
			mailManageForm.setRecordNotFind("false");
			mailManageForm.setRecordList(result);
			return mapping.findForward("detail");
		}
		else
		{
			mailManageForm.setRecordNotFind("true");
			return mapping.findForward("initError");
		}
	}
	/*
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		MailManageForm mailManageForm = (MailManageForm) form;// TODO Auto-generated method stub
		String userName = (String)request.getSession().getAttribute("UserName");
		DBTools dbTools = new DBTools();
		
		String sql = "select ID,ACCOUNTNAME,MAILADDRESS,ISDEFAULT from TB_MAILCONFIG where USERNAME='" + userName + "' order by ISDEFAULT desc";
		ArrayList result = dbTools.queryMailList(sql);
		if(result.size() > 0)
		{
			mailManageForm.setRecordNotFind("false");
			mailManageForm.setRecordList(result);
			return mapping.findForward("init");
		}
		else
		{
			mailManageForm.setRecordNotFind("true");
			return mapping.findForward("initFail");
		}
	}
	

	public ActionForward recvMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		MailManageForm mailManageForm = (MailManageForm) form;
		String id = request.getParameter("id");
		request.getSession().setAttribute("NowMailID", id);
		DBTools dbTools = new DBTools();

		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// �����ҳ��
		int rowsPerPage = 20;// ÿҳ������
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null && request.getParameter("queryPageNo") != "") {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		String sql = "select ID,SENDNAME,TITLE,RECVTIME,ISREAD from TB_RECVMAIL where MAILCONFIGID=" + id + " order by ID desc";
		request.getSession().setAttribute("queryRecvMail", sql);
		pageBean.setQuerySql(sql);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryRecvMailList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			mailManageForm.setRecordNotFind("false");
			mailManageForm.setRecordList(result);
			
			int totalRows = pageBean.getTotalRows();
			int pagecount = pageBean.getTotalPage();// �õ���ҳ��
			int currentPage = pageBean.getQueryPageNo();// �õ���ǰҳ
			request.setAttribute("currentPage",String.valueOf(currentPage));
			request.setAttribute("totalRows",String.valueOf(totalRows));
			request.setAttribute("pageCount",String.valueOf(pagecount));
		}
		else
		{
			mailManageForm.setRecordNotFind("true");
			request.setAttribute("currentPage",String.valueOf(0));
			request.setAttribute("totalRows",String.valueOf(0));
			request.setAttribute("pageCount",String.valueOf(0));
		}
		
		return mapping.findForward("recvMail");
	}
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		MailManageForm mailManageForm = (MailManageForm) form;
		String operation = request.getParameter("operation");
		
		String id = (String)request.getSession().getAttribute("NowMailID");
		
		CheckPage pageBean = new CheckPage();
		String sql = "";
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);
		
		//�������ѯ��
		if (operation.equalsIgnoreCase("search")) {
			String title = mailManageForm.getTitle();
			String recvBeginTime = mailManageForm.getRecvBeginTime();
			String recvEndTime = mailManageForm.getRecvEndTime();
			String isRead = mailManageForm.getIsRead();
			if(title.equals("") && recvBeginTime.equals("")&& recvEndTime.equals("") && isRead.equals("")){
				sql = "select ID,SENDNAME,TITLE,RECVTIME,ISREAD from TB_RECVMAIL where MAILCONFIGID=" + id + " order by ID desc";
			}
			else
			{
				sql = "select ID,SENDNAME,TITLE,RECVTIME,ISREAD from TB_RECVMAIL where MAILCONFIGID=" + id + " and TITLE='" + title + "' order by ID desc";
			}
			request.getSession().setAttribute("queryRecvMail", sql);
		}
		// ����Ƿ�ҳ
		else if(operation.equalsIgnoreCase("changePage")){
			sql = (String)request.getSession().getAttribute("queryRecvMail");
			if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
			}
		}
		pageBean.setQuerySql(sql);
		pageBean.setQueryPageNo(queryPageNo);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		ArrayList result = db.queryRecvMailList(rs, rowsPerPage);
		if(result.size() > 0)
		{
			mailManageForm.setRecordNotFind("false");
			mailManageForm.setRecordList(result);
			
			int totalRows = pageBean.getTotalRows();
			int pagecount = pageBean.getTotalPage();// �õ���ҳ��
			int currentPage = pageBean.getQueryPageNo();// �õ���ǰҳ
			request.setAttribute("currentPage",String.valueOf(currentPage));
			request.setAttribute("totalRows",String.valueOf(totalRows));
			request.setAttribute("pageCount",String.valueOf(pagecount));
		}
		else
		{
			mailManageForm.setRecordNotFind("true");
			
			request.setAttribute("currentPage",String.valueOf(0));
			request.setAttribute("totalRows",String.valueOf(0));
			request.setAttribute("pageCount",String.valueOf(0));
		}
		return mapping.findForward("recvMail");
	}

	public ActionForward syncMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		MailManageForm mailManageForm = (MailManageForm) form;
		String id = (String)request.getSession().getAttribute("NowMailID");
		
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// �����ҳ��
		int rowsPerPage = 20;// ÿҳ������
		pageBean.setRowsPerPage(rowsPerPage);
		pageBean.setQueryPageNo(queryPageNo);
		
		DBTools dbTools = new DBTools();
		EmailBean emailBean = dbTools.queryEmailConfig(id);
		if(emailBean != null)
		{			
			ReciveEmail recvEmail = new ReciveEmail();
			try {
				ArrayList resultList = recvEmail.RecvEmailList(emailBean);
				if(resultList.size() > 0)
				{			
					DBTools db = new DBTools();
					db.insertRecvMail(resultList, id);
				}
				String sql = "select ID,SENDNAME,TITLE,RECVTIME,ISREAD from TB_RECVMAIL where MAILCONFIGID=" + id  + " order by ID desc";
				request.getSession().setAttribute("queryRecvMail", sql);
				pageBean.setQuerySql(sql);
				DBTools db2 = new DBTools();
				ResultSet rs = db2.queryRs(queryPageNo, pageBean, rowsPerPage);
				ArrayList result = db2.queryRecvMailList(rs, rowsPerPage);
				if(result.size() > 0)
				{
					mailManageForm.setRecordNotFind("false");
					mailManageForm.setRecordList(result);
					
					int totalRows = pageBean.getTotalRows();
					int pagecount = pageBean.getTotalPage();// �õ���ҳ��
					int currentPage = pageBean.getQueryPageNo();// �õ���ǰҳ
					request.setAttribute("currentPage",String.valueOf(currentPage));
					request.setAttribute("totalRows",String.valueOf(totalRows));
					request.setAttribute("pageCount",String.valueOf(pagecount));
				}
				else
				{
					mailManageForm.setRecordNotFind("true");
					request.setAttribute("currentPage",String.valueOf(0));
					request.setAttribute("totalRows",String.valueOf(0));
					request.setAttribute("pageCount",String.valueOf(0));
				}
			} catch (Exception e) {
				return mapping.findForward("recvFail");
			}
		}
		
		return mapping.findForward("recvMail");
	}
	*/
}