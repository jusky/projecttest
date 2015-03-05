/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.DBTools;
import com.whu.web.user.UserBean;

/** 
 * MyEclipse Struts
 * Creation date: 09-02-2013
 * 
 * XDoclet definition:
 * @struts.action path="/changePwdAction" name="changePwdForm" input="/web/changePwd.jsp" parameter="method" scope="request" validate="true"
 */
public class ChangePwdAction extends DispatchAction {
	/*
	 * Generated Methods
	 */

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
		ChangePwdForm changePwdForm = (ChangePwdForm) form;// TODO Auto-generated method stub
		String loginName = (String)request.getSession().getAttribute("LoginName");
		String oldPwd = changePwdForm.getOldPwd();
		String newPwd = changePwdForm.getNewPwd();
		String ConPwd = changePwdForm.getConNewPwd();
	
		DBTools db = new DBTools();
		String sql = "";
		String identity = (String)request.getSession().getAttribute("Identity");
		String tableName = "";
		if(identity.equals("1"))
		{
			tableName = "SYS_USER";
		}
		else
		{
			tableName = "SYS_ED_USER";
		}
		sql = "select * from " + tableName + " where LOGINNAME='" + loginName + "' and PASSWORD='" + oldPwd + "'";
		boolean result = db.isHave(sql);
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if (result) {
			sql = "update " + tableName  + " set PASSWORD='" + newPwd + "' where LOGINNAME='" + loginName + "'";
			result = db.updateItem(sql);
			
			if(result)
			{
				
				json.put("statusCode", 200);
				json.put("message", "密码修改成功！");
				json.put("callbackType", "closeCurrent");
			}
			else
			{
				json.put("statusCode", 200);
				json.put("message", "密码修改失败！");
			}		
		}
		else
		{
			json.put("statusCode", 200);
			json.put("message", "旧密码不正确，请重新输入！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		return null;
	}
}