/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.expert;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.DBTools;
import com.whu.web.email.ContactBean;
import com.whu.web.eventbean.ExpertInfo;
import com.whu.web.user.ConfigUserForm;
import com.whu.web.user.UserBean;

/** 
 * MyEclipse Struts
 * Creation date: 03-03-2014
 * 
 * XDoclet definition:
 * @struts.action path="/configExpertAction" name="configExpertForm" parameter="method" scope="request" validate="true"
 */
public class ConfigExpertAction extends DispatchAction {
	/*
	 * Generated Methods
	 */

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		ConfigExpertForm configExpertForm = (ConfigExpertForm)form;
		String operation = request.getParameter("operation");
		String addrID = request.getParameter("addrID");
		String expertName = configExpertForm.getName();
		String sex = configExpertForm.getSex();
		String age = configExpertForm.getAge();
		String title = configExpertForm.getTitle();
		String isPHD = configExpertForm.getIsPHD();
		String dept = configExpertForm.getDept();
		String specialty = configExpertForm.getSpecialty();
		String research = configExpertForm.getResearch();
		String faculty = configExpertForm.getFaculty();
		String phone = configExpertForm.getPhone();
		String email = configExpertForm.getEmail();
		String address = configExpertForm.getAddress();		
		DBTools dbTool = new DBTools();
		String mark="expert";
		String sql = "";
		String sqlAddr="";
		if(operation.equals("new"))
		{
			sql = "insert into SYS_EXPERTINFO(NAME,SEX,AGE,TITLE,ISPHD,DEPT,SPECIALTY,RESEARCH,PHONE,EMAIL,ADDRESS,FACULTY) values('" + expertName + "','" + sex + "','" + age + "','" + title + "','" + isPHD + "','" + dept + "','" + specialty + "','" + research + "','" + phone + "','" + email + "','" + address + "','" + faculty + "')";
			sqlAddr="insert into TB_CONTACT(LOGINNAME,CONNAME,CONADDR) values('" + mark + "','" + expertName + "','" + email + "')";
		}
		else if(operation.equals("edit"))
		{
			String id = configExpertForm.getId();
			sql = "update SYS_EXPERTINFO set NAME='" + expertName + "',SEX='" + sex + "',AGE='" + age + "', TITLE='" + title + "', ISPHD='" + isPHD + "',DEPT='" + dept + "',SPECIALTY='" + specialty + "',RESEARCH='" + research + "',PHONE='" + phone + "',EMAIL='" + email + "',ADDRESS='" + address + "',FACULTY='" + faculty + "' where ID=" + id;
			sqlAddr="update TB_CONTACT set LOGINNAME='" + mark + "',CONNAME='" + expertName + "',CONADDR='" + email+ "' where ID='" + addrID + "'";
		}
		boolean result = dbTool.insertItem(sql);
		boolean resuatAddr=dbTool.insertItem(sqlAddr);
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result&&resuatAddr)
		{
			json.put("statusCode", 200);
			json.put("message", "保存成功！");
			json.put("callbackType", "closeCurrent");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "保存失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;
		
	}
	/**
	 * 编辑用户信息，跳转
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		ConfigExpertForm configExpertForm = (ConfigExpertForm)form;
		ContactBean contactBean = new ContactBean();
		String id = request.getParameter("uid");
		DBTools dbTools = new DBTools();
		String sql = "select * from SYS_EXPERTINFO where ID='" + id + "'";
		ExpertInfo expertInfo = dbTools.queryExpertInfo(sql);
		ArrayList result = new ArrayList();
		if(expertInfo!=null)
		{
			result.add(expertInfo);
			configExpertForm.setRecordNotFind("false");
			configExpertForm.setRecordList(result);
			String addrName=expertInfo.getName();
			request.setAttribute("eSex",expertInfo.getSex());
			request.setAttribute("isPHD",expertInfo.getIsPHD());
			String sqlAddr="select * from TB_CONTACT where CONNAME='" + addrName + "'and  LOGINNAME='expert'";
			contactBean=dbTools.queryAddrBean(sqlAddr);
			if(contactBean != null)
			{
				request.setAttribute("addrID", contactBean.getId());
			}
			
			return mapping.findForward("edit");
		}
		else
		{
			configExpertForm.setRecordNotFind("true");
			return mapping.findForward("initError");
		}
	}
}