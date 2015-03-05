/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.credit;

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
import com.whu.web.credit.MistypeBean;
import com.whu.web.credit.ConfigMistypeForm;

/** 
 * MyEclipse Struts
 * Creation date: 12-05-2014
 * 
 * XDoclet definition:
 * @struts.action path="/configMistypeAction" name="configMistypeForm" parameter="method" scope="request" validate="true"
 */
public class ConfigMistypeAction extends DispatchAction {
	/*
	 * Generated Methods
	 */


	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ConfigMistypeForm configMistypeForm = (ConfigMistypeForm) form;
		String operation = request.getParameter("operation");
		String rname = configMistypeForm.getRname();
		String prid = configMistypeForm.getPrid();
		String sql = "";
		String rid = "";
		String rsort = "1"; // personal credit factor 
		DBTools dbTool = new DBTools();	
		boolean result = false;
		
		sql = "select RID from SYS_JBREASON where PRID='" + prid + "' order by RID desc limit 1";
		String maxRid = dbTool.queryMistypeRid(sql);

		if(!maxRid.equals(""))
		{
			rid = String.valueOf(Integer.parseInt(maxRid) + 1);
			if(operation.equals("new"))
			{
				sql = "insert into SYS_JBREASON(RID,RNAME,PRID,RSORT,ISJC) values('" + rid + "','" + rname + "','" + prid + "','" + rsort + "','0')";
			}
			else if(operation.equals("edit"))
			{
				rid = configMistypeForm.getRid();
				sql = "update SYS_JBREASON set RID='" + rid + "',RNAME='" + rname + "', PRID='" + prid + "' where rid='" + rid + "'";
			}
			result = dbTool.insertItem(sql);
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			request.getSession().setAttribute("configFlag", "true");
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
		return null;
	}
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		
		ConfigMistypeForm configMistypeForm = (ConfigMistypeForm) form;
		String id = request.getParameter("id");
		DBTools dbTools = new DBTools();
		String sql = "select a.*,b.RNAME as PNAME from SYS_JBREASON a left join SYS_JBREASON b on a.PRID=b.RID where a.ID=" + id;
		MistypeBean mistypeBean = dbTools.queryMistypeBean(sql);
		ArrayList result = new ArrayList();
		if(mistypeBean!=null)
		{
			result.add(mistypeBean);
			configMistypeForm.setRecordNotFind("false");
			configMistypeForm.setRecordList(result);
			return mapping.findForward("edit");
		}
		else
		{
			configMistypeForm.setRecordNotFind("true");
			return mapping.findForward("initError");
		}
	}
	
	/**
	 * weight init
	 * 
	 * 
	 * 
	 */
	public ActionForward weight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		String type = request.getParameter("type");
		String sql = "";
		
		if(type.equals("0")){
			sql = "select * from SYS_JBREASON where RSORT=1 and ISJC=1";
		} else {
			sql = "select * from SYS_JBREASON where RSORT=1 and PRID='" + type + "'";
		}
		
				
		DBTools db = new DBTools();
		ResultSet rs = db.queryRsList(sql);
		
		List<String> lstTree = db.queryMistype(rs);
	        
		
		request.setAttribute("weightJson", JSONArray.fromObject(lstTree).toString());
		
		return mapping.findForward("weight");
	}
	/**
	 * weight save
	 * 
	 * 
	 * 
	 */
	public ActionForward saveWeight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		String weightList = request.getParameter("weightList");
		String sql = "";
		Boolean result = false;
		
		
		DBTools db = new DBTools();
		

		org.json.JSONArray weights = new org.json.JSONArray(weightList);	
			
		for (int i = 0; i < weights.length(); i++)
		{
			  String rid = weights.getJSONObject(i).getString("rid");
			  String weight = weights.getJSONObject(i).getString("weight");
			  sql = "update SYS_JBREASON set WEIGHT ='" + weight + "' where RID='" + rid + "'";
			  result = db.insertItem(sql);
			  if(!result)
				  break;
		}

		
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "更新成功！");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "更新失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;
	}
}