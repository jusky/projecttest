/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.email;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.DBTools;

/** 
 * MyEclipse Struts
 * Creation date: 07-02-2013
 * 
 * XDoclet definition:
 * @struts.action path="/mailConfigAction" name="mailConfigForm" parameter="method" scope="request" validate="true"
 */
public class MailConfigAction extends DispatchAction {
	/*
	 * Generated Methods
	 */
	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		MailConfigForm mailConfigForm = (MailConfigForm) form;
		EmailBean emailBean = new EmailBean();
		String accountName = mailConfigForm.getAccountName();
		String mailBoxType = mailConfigForm.getMailBoxType();
		String mailBoxAddress = mailConfigForm.getMailBoxAddress();
		String mailBoxPwd = mailConfigForm.getMailBoxPwd();
		String smtpPC = mailConfigForm.getSmtpPC();
		String smtpPort = mailConfigForm.getSmtpPort();
		String popPC = mailConfigForm.getPopPC();
		String popPort = mailConfigForm.getPopPort();
		
		String loginName = (String) request.getSession().getAttribute("LoginName");
		String type = request.getParameter("type");
		DBTools dbTools = new DBTools();
		
		String sql = "";
		boolean result = false;
		if(type.equals("new"))
		{
		/*	sql = "Insert into TB_MAILCONFIG(ACCOUNTNAME,MAILBOXTYPE,MAILADDRESS,PWD,SMTPPC,SMTPPORT,POPPC,POPPORT,LOGINNAME,ISDEFAULT) values('"
				+ accountName + "','" + mailBoxType + "','" + mailBoxAddress + "','"
				+ mailBoxPwd + "','" + smtpPC + "'," + smtpPort + ",'"
				+ popPC + "'," + popPort + ",'" + loginName + "','0')"; */
			sql = "Insert into TB_MAILCONFIG(ACCOUNTNAME,MAILBOXTYPE,MAILADDRESS,PWD,SMTPPC,SMTPPORT,POPPC,POPPORT,LOGINNAME,ISDEFAULT) values(?,?,?,?,?,?,?,?,?,'0')";
			String[] params = {accountName, mailBoxType, mailBoxAddress, mailBoxPwd, smtpPC, smtpPort, popPC, popPort, loginName};
			result = dbTools.insertItem(sql, params);
		}
		else if(type.equals("edit"))
		{
			String id = request.getParameter("id");
		/*	sql = "update TB_MAILCONFIG set ACCOUNTNAME='" + accountName + "', MAILBOXTYPE='" 
			+ mailBoxType + "', MAILADDRESS='" + mailBoxAddress +"',PWD='"
			+ mailBoxPwd + "',SMTPPC='" + smtpPC + "',SMTPPORT="
			+ smtpPort + ",POPPC='" + popPC + "',POPPORT="
			+ popPort + " where ID=" + id; */
			
			sql = "update TB_MAILCONFIG set ACCOUNTNAME=?, MAILBOXTYPE=?, MAILADDRESS=?,PWD=?,SMTPPC=?,SMTPPORT=?,POPPC=?,POPPORT=? where ID=?"; 
			String[] params = {accountName, mailBoxType, mailBoxAddress, mailBoxPwd, smtpPC, smtpPort, popPC, popPort, id};
			result = dbTools.updateItem(sql, params);
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
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
	
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		MailConfigForm mailConfigForm = (MailConfigForm) form;
		DBTools dbTools = new DBTools();
		String sql = "select * from TB_MAILCONFIG where ID=?";
		EmailBean emailBean = dbTools.queryEmailConfig(sql, new String[]{id});
		if(emailBean!=null)
		{
			ArrayList result = new ArrayList();
			result.add(emailBean);
			mailConfigForm.setRecordList(result);
			return mapping.findForward("edit");
		}
		else
		{
			return mapping.findForward("fail");
		}
	}
}