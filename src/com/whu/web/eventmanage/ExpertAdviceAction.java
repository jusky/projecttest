/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.eventmanage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
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
import org.apache.struts.upload.FormFile;

import com.whu.tools.DBTools;
import com.whu.tools.EmailTools;
import com.whu.tools.SystemConstant;
import com.whu.web.common.SystemShare;
import com.whu.web.email.EmailBean;
import com.whu.web.email.EmailInfo;
import com.whu.web.eventbean.DeptSurveyLetter;
import com.whu.web.eventbean.ExpertJDH;
import com.whu.web.eventbean.JDYJSBean;

/** 
 * MyEclipse Struts
 * Creation date: 01-20-2013
 * 
 * XDoclet definition:
 * @struts.action path="/expertAdviceAction" name="expertAdviceForm" parameter="method" scope="request" validate="true"
 */
public class ExpertAdviceAction extends DispatchAction {
	/*
	 * Generated Methods
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		String loginName = (String)request.getSession().getAttribute("LoginName");
		String userName =  (String)request.getSession().getAttribute("UserName");
		String operatorFlag = request.getParameter("operatorFlag");
		String reportID = expertAdviceForm.getReportID();
		boolean result  = false;
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(operatorFlag.equals("sendEmail"))//发送邮件
		{
			String expertID = request.getParameter("org9.expertID");
			String expertName = request.getParameter("org9.expertName");
			String email = request.getParameter("org9.email");
			String content = request.getParameter("content");

			String accessoryPath = request.getSession().getServletContext().getRealPath("/") + "/temp/" + loginName + "/";
			String title = expertAdviceForm.getTitle();
			//String serialNum = expertAdviceForm.getSerialNum();
			String accountName = expertAdviceForm.getLoginName();
			String password = expertAdviceForm.getPassword();
			
			DBTools dbTools = new DBTools();
			String tempsql="select * from SYS_ED_USER where LOGINNAME=?";
			String[] tempparams = new String[]{loginName};
			String sql = "select * from TB_MAILCONFIG where LOGINNAME=? and ISDEFAULT=?";
			String[] params = new String[]{loginName, "1"};
			EmailBean emailBean = dbTools.queryEmailConfig(sql, params);
			
			//所有发送的附件名，以“：”分割
			String attachNames = "";
			
			EmailTools emailTools = new EmailTools();
			if(emailBean != null)
			{
				EmailInfo emailInfo = new EmailInfo();
				emailInfo.setSendName(emailBean.getMailBoxAddress());
				emailInfo.setCsName("");
				emailInfo.setRecvName(email);
				emailInfo.setTitle(title);
				emailInfo.setContent(content);
				emailInfo.setAccessory(accessoryPath);
				result = emailTools.SendEmail(emailInfo, emailBean);
				if(!result)
				{
					//邮件没有发送成功，则将上传的附件删除
					SystemShare.deleteAllFiles(accessoryPath);
					json.put("statusCode", 300);
					json.put("message", "邮件发送失败！请检查您的网络连接是否正常，或者稍后重新发送！");
					//json.put("callbackType", "closeCurrent");
					json.put("navTabId", "addExpertAdvice");
					
					out.write(json.toString());
					out.flush();
					out.close();
					return null;
				}
				else//邮件发送成功后，需要将附件转存到举报文件夹下，用于专家反馈页面查看所有的附件
				{
					String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/expert/";
					String path1 = request.getSession().getServletContext().getRealPath("/") + "/temp/" + loginName + "/";
					String path2 = filePath + reportID;
					attachNames = SystemShare.SaveEmailAttach(path1, path2);
				}
			}
			if(!expertName.equals(""))
			{
					
		    		String time = SystemShare.GetNowTime("yyyy-MM-dd");
		    		//插入到专家鉴定表中
					sql = "insert into TB_EXPERTADVICE(REPORTID,EXPERTNAME,TIME,ISFK,ISEMAIL) values(?, ?, ?, ?, ?)";
					params = new String[]{reportID, expertName, time, "0", "1"};
					result = dbTools.insertItem(sql, params);
					
					//插入到已发送邮件中
					if(result)
					{
						String expertAdviceID = dbTools.queryLastInsertID("TB_EXPERTADVICE");
						sql = "insert into TB_EXPERTEMAIL(EXPERTADVICEID,EXPERTNAME,EMAILADDRESS,TITLE,ATTACHMENT,EMAILCONTENT,LOGINNAME,PASSWORD) values('" + expertAdviceID + "','" + expertName + "','" + email + "','" + title + "','" + attachNames + "','" + content + "','" + accountName + "','" + password + "')";
						params = new String[]{expertAdviceID, expertName, email, title, attachNames, content, accountName, password};
						result = dbTools.insertItem(sql, params);
						if(accountName != null && !accountName.equals(""))
						{
							//自动根据生成的账号和密码创建一个新的账户，分配“鉴定专家”的权限
							//sql = "if not exists(select * from SYS_ED_USER where LOGINNAME='" + loginName + "') insert into SYS_ED_USER(LOGINNAME, PASSWORD, EXPERTID, ROLEIDS, ISUSE) values('" + accountName + "','" + password + "','" + expertID + "','8','1') else update SYS_ED_USER set PASSWORD='" + password + "' where LOGINNAME='" + loginName + "'";
							boolean flag=dbTools.queryISEXIST(tempsql, tempparams);
							if(flag)
							{
								sql="insert into SYS_ED_USER(LOGINNAME, PASSWORD, EXPERTID, ROLEIDS, ISUSE) values(?, ?, ?, ?, ?)";
								params = new String[]{accountName, password, expertID, "4", "1"};
							}
							else
							{
								sql="update SYS_ED_USER set PASSWORD=? where LOGINNAME=?";
								params = new String[]{password, loginName};
							}
							
							result = dbTools.insertItem(sql, params);
							//向专家反馈记录表中 插入一条记录
							sql = "insert into TB_ED_ADVICE(REPORTID,LOGINNAME,EVENTTITLE,FKTIME,ISSUBMIT,ATTACHMENT,ADVICEID) values(?, ?, ?, ?, ?, ?, ?)";
							params = new String[]{reportID, loginName, title, "", "0", attachNames, expertAdviceID};
							result = dbTools.insertItem(sql, params);
						}
						//写入日志文件
						dbTools.insertLogInfo(userName, SystemConstant.LOG_EXPERTADVICE, "向专家发送邮件，事件编号为：" + reportID, request.getRemoteAddr());
					}
			}
		}
		else if(operatorFlag.equals("newAdvice"))//新增意见
		{
			String expertName = expertAdviceForm.getExpertName();
			String time = expertAdviceForm.getTime();
			if(time == null || time.equals(""))
				time = SystemShare.GetNowTime("yyyy-MM-dd");
			String conclusion = expertAdviceForm.getConclusion();
			String advice = expertAdviceForm.getAdvice();
			
			String attachName = (String)request.getSession().getAttribute("EventAttachName");
			String createName = (String)request.getSession().getAttribute("UserName");
			if(attachName != null && !attachName.equals(""))
			{
				attachName = "expert/" + reportID + "/" + attachName;
				request.getSession().setAttribute("EventAttachName", "");
				
				String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/expert/";
				//String path1 = filePath + "temp";
				String path1 = request.getSession().getServletContext().getRealPath("/") + "/temp/" + loginName + "/";
				String path2 = filePath + reportID;
				//获得服务器的IP地址路径，存放在数据库中，便于下载
				String relDirectory = "attachment/expert/" + reportID;
				//将临时文件夹中的附件转存到以警情编号为目录的文件夹下

				result = SystemShare.IOCopy(path1, path2, relDirectory, createName);
			}
			else
			{
				attachName = "";
			}
			
			String id = expertAdviceForm.getId();
			String sql = "";
			String[] params = null;
			//如果不为空，则说明是编辑
			if(id != null && !id.equals(""))
			{
				if(attachName.equals(""))
				{
					sql = "update TB_EXPERTADVICE set EXPERTNAME=?, TIME=?, CONCLUSION=?, ADVICE=?, ISFK=? where ID=?";
					params = new String[]{expertName, time, conclusion, advice, "1", id};
				}
				else
				{
					sql = "update TB_EXPERTADVICE set EXPERTNAME=?, TIME=?, CONCLUSION=?, ADVICE=?, ISFK=?, ATTACHNAME=? where ID=?";
					params = new String[]{expertName, time, conclusion, advice, "1", attachName, id};
				}
				
			}
			else//如果为空，则说明是新增
			{
				sql = "insert into TB_EXPERTADVICE(REPORTID,EXPERTNAME,TIME,CONCLUSION,ADVICE,ISFK,ATTACHNAME,ISEMAIL) values(? , ?, ?, ?, ?, ?, ?, ?)";
				params = new String[]{reportID, expertName, time, conclusion, advice, "1", attachName, "0"};
			}
			
			DBTools dbTools = new DBTools();
			result = dbTools.insertItem(sql, params);
			
			if(result)
			{
				String describe = time + ", " + userName + "   编辑专家鉴定意见";
				//插入处理过程到数据库中
				result = dbTools.InsertHandleProcess(reportID, createName, SystemConstant.HP_EXPERTADVICE, SystemConstant.SS_SURVEYING, SystemConstant.LCT_ZJJD, describe);
				//写入日志文件
				dbTools.insertLogInfo(userName, SystemConstant.LOG_EXPERTADVICE, "编辑专家鉴定意见，事件编号为：" + reportID, request.getRemoteAddr());
				//更新事件的最近一次操作时间
				result = dbTools.UpdateLastTime(reportID);
			}
		}
		
		if(result)
		{
			//防止高级检索功能模块执行
			request.getSession().setAttribute("GjSearch", "false");
			json.put("statusCode", 200);
			json.put("message", "操作成功！");
			//json.put("callbackType", "closeCurrent");
			json.put("navTabId", "addExpertAdvice");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "操作失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		return null;
	}
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String ids = request.getParameter("ids");
		DBTools dbTool = new DBTools();
		boolean result = true;
		if(ids == null || ids == "")
		{
			String id = request.getParameter("id");
			result = dbTool.deleteItemReal(id, "TB_EXPERTADVICE", "ID");
		}
		else
		{
			String[] arrID = ids.split(",");
			result = dbTool.deleteItemsReal(arrID, "TB_EXPERTADVICE", "ID");
		}
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			//防止高级检索功能模块执行
			request.getSession().setAttribute("GjSearch", "false");
			json.put("statusCode", 200);
			json.put("message", "删除专家鉴定意见成功");
			//json.put("rel", "addDeptAdvice");
			//json.put("callbackType", "closeCurrent");
			json.put("navTabId", "addExpertAdvice");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "删除专家鉴定意见失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;
	}
	public ActionForward sendEmail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		String expertName = request.getParameter("org9.expertName");
		String email = request.getParameter("org9.email");
		String content = request.getParameter("content");

		String accessoryPath = request.getSession().getServletContext().getRealPath("/") + "/temp/";
		String title = expertAdviceForm.getTitle();
		
		DBTools dbTools = new DBTools();
		String sql = "select * from TB_MAILCONFIG where ISDEFAULT='1'";
		EmailBean emailBean = dbTools.queryEmailConfig(sql, new String[0]);
		
		EmailTools emailTools = new EmailTools();
		boolean result = false;
		if(emailBean != null)
		{
			EmailInfo emailInfo = new EmailInfo();
			emailInfo.setSendName(emailBean.getMailBoxAddress());
			emailInfo.setCsName("");
			emailInfo.setRecvName(email);
			emailInfo.setTitle(title);
			emailInfo.setContent(content);
			emailInfo.setAccessory(accessoryPath);
			result = emailTools.SendEmail(emailInfo, emailBean);
		}
		
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "邮件发送成功！");
			//json.put("callbackType", "closeCurrent");
			json.put("navTabId", "addExpertAdvice");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "邮件发送失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		return null;
	}
	
	/**
	 * 编辑专家鉴定函内容
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward createExpertJDH(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		String reportID = (String)request.getSession().getAttribute("expertReportID");
		String sql = "select * from TB_EXPERTJDH where REPORTID=?";
		DBTools dbTools = new DBTools();
		ExpertJDH ejdh = dbTools.queryExpertJDH(sql, new String[]{reportID});
		ArrayList result = new ArrayList();
		if(ejdh==null)
		{
			ejdh = new ExpertJDH();
			ejdh.setId("");
			ejdh.setReportID(reportID);
			ejdh.setTitle("关于商请鉴定XXXX的函");
			ejdh.setShortInfo("");
			ejdh.setFkTime("");
			ejdh.setTarget("");
			ejdh.setJdContent("");
		}
		result.add(ejdh);
		expertAdviceForm.setRecordList(result);
		return mapping.findForward("createJDH");
	}
	/**
	 * 保存专家鉴定函信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward saveExpertJDH(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		String reportID = expertAdviceForm.getReportID();
		String title = expertAdviceForm.getTitle();
		String shortInfo = expertAdviceForm.getShortInfo();
		String fkTime = expertAdviceForm.getFkTime();
		String target = expertAdviceForm.getTarget();
		String jdContent = expertAdviceForm.getJdContent();
		DBTools dbTools = new DBTools();
		String tempsql="select * from TB_EXPERTJDH where REPORTID=?";
		String sql="";
		String[] params = null;
		ExpertJDH ejdh = dbTools.queryExpertJDH(tempsql, new String[]{reportID});
		if(ejdh==null)
		{
			sql = "insert into TB_EXPERTJDH(REPORTID,TITLE,SHORTINFO,FKTIME,TARGET,JDCONTENT) values(?, ?, ?, ?, ?, ?)";
			params = new String[]{reportID, title, shortInfo, fkTime, target, jdContent};
		}
		else
		{
			sql = "update TB_EXPERTJDH set TITLE=?, SHORTINFO=?, FKTIME=?, TARGET=?, JDCONTENT=? where REPORTID=?";
			params = new String[]{title, shortInfo, fkTime, target, jdContent, reportID};
		}
		//String sql = "if not exists(select ID from TB_EXPERTJDH where REPORTID='" + reportID + "') insert into TB_EXPERTJDH(REPORTID,TITLE,SHORTINFO,FKTIME,TARGET,JDCONTENT) values('" + reportID + "','" + title + "','" + shortInfo + "','" + fkTime + "','" + target + "','" + jdContent + "') else update TB_EXPERTJDH set TITLE='" + title + "', SHORTINFO='" + shortInfo + "',FKTIME='" + fkTime + "',TARGET='" + target + "',JDCONTENT='" + jdContent + "' where REPORTID='" + reportID + "'";
		boolean result = dbTools.insertItem(sql, params);
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "保存成功！");
			json.put("callbackType", "closeCurrent");
			//json.put("navTabId", "addExpertAdvice");
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
	 * 在线编辑专家鉴定文档
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward makeExpertJDH(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String id = (String)request.getSession().getAttribute("expertReportID");
		if(id.equals(""))
		{
			return null;
		}
		DBTools dbTools = new DBTools();
		String jdhPath = dbTools.querySingleData("TB_EXPERTFILE", "JDHPATH", "REPORTID", id);
		String templatePath = "";
		boolean isEdit = true;
		String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/";
		if(jdhPath != null && !jdhPath.equals(""))//如果是编辑，则查询数据库判断上次编辑过的文件是否存在，若存在，则发送到客户端继续编辑
		{			
			String tempFilePath = filePath + jdhPath;
			if((new File(tempFilePath)).exists())//如果存在，则得到路径
			{
				templatePath = SystemConstant.GetServerPath() + "/attachment/" +  jdhPath;
				request.setAttribute("IsEdit", "1");
			}
			else//不存在，则继续使用模板，例如：人工删除或系统出错
			{
				isEdit = false;
			}
		}
		else//如果是新增，则调出模板发送到客户端
		{
			isEdit = false;
		}
		ExpertJDH ejdh = new ExpertJDH();
		if(!isEdit)
		{
			request.setAttribute("IsEdit", "0");
			templatePath = SystemConstant.GetServerPath() + "/web/template/zjjdh.doc";
			String sql = "select * from TB_EXPERTJDH where REPORTID=?";
			ejdh = dbTools.queryExpertJDH(sql, new String[]{id});
			
			if(ejdh == null)
			{
				ejdh = new ExpertJDH();
				ejdh.setReportID(id);
				ejdh.setTitle("关于商请鉴定XXXX的函");
				ejdh.setTitle("");
				ejdh.setFkTime("");
				ejdh.setShortInfo("");
				ejdh.setFkTime("");
				ejdh.setTarget("");
				ejdh.setJdContent("");
			}
		}
		
		String createTime = SystemShare.GetNowTime("yyyy-MM-dd");
		String year = createTime.substring(0, 4);
		String month = createTime.substring(5, 7);
		String day = createTime.substring(8, 10);
		ejdh.setYear(year);
		ejdh.setMonth(month);
		ejdh.setDay(day);
		
		request.setAttribute("ExpertJDH", ejdh);
		
		request.setAttribute("ReportID", id);
		request.setAttribute("ServerPath", SystemConstant.GetServerPath());
		request.setAttribute("templatePath", templatePath);
		return mapping.findForward("makeJDH");
	}
	/**
	 * 编辑鉴定意见书
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward makeJDYJS(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String id = (String)request.getSession().getAttribute("expertReportID");
		if(id.equals(""))
		{
			return null;
		}
		DBTools dbTools = new DBTools();
		String jdyjsPath = dbTools.querySingleData("TB_EXPERTFILE", "YJSPATH", "REPORTID", id);
		String templatePath = "";
		boolean isEdit = true;
		String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/";
		if(jdyjsPath != null && !jdyjsPath.equals(""))//如果是编辑，则查询数据库判断上次编辑过的文件是否存在，若存在，则发送到客户端继续编辑
		{			
			String tempFilePath = filePath + jdyjsPath;
			if((new File(tempFilePath)).exists())//如果存在，则得到路径
			{
				templatePath = SystemConstant.GetServerPath() + "/attachment/" +  jdyjsPath;
				request.setAttribute("IsEdit", "1");
			}
			else//不存在，则继续使用模板，例如：人工删除或系统出错
			{
				isEdit = false;
				
			}
		}
		else//如果是新增，则调出模板发送到客户端
		{
			isEdit = false;
		}
		if(!isEdit)
		{
			request.setAttribute("IsEdit", "0");
			templatePath = SystemConstant.GetServerPath() + "/web/template/jdyjs.doc";
			String sql = "select * from TB_JDYJSINFO where REPORTID=?";
			JDYJSBean jb = dbTools.queryJDYJS(sql, new String[]{id});
			if(jb == null)
			{
				jb = new JDYJSBean();
				jb.setEventReason("");
				jb.setIdentifyContent("");
				jb.setWtDept("国家自然科学基金委员会监督委员会");
				jb.setJdConclusion("");
			}
			request.setAttribute("JDYJSBean", jb);
		}
		request.setAttribute("ReportID", id);
		request.setAttribute("ServerPath", SystemConstant.GetServerPath());
		request.setAttribute("templatePath", templatePath);
		return mapping.findForward("expertJDYJS");
	}
	/**
	 * 查看邮件信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward showEmail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		String id = request.getParameter("id");
		DBTools dbTools = new DBTools();
		String sql = "select a.*,b.REPORTID from TB_EXPERTEMAIL a,TB_EXPERTADVICE b where a.EXPERTADVICEID=b.ID and a.EXPERTADVICEID=?";
		EmailInfo ei = dbTools.queryExpertEmail(sql, new String[]{id});
		if(ei!=null)
		{
			ArrayList result = new ArrayList();
			result.add(ei);
			expertAdviceForm.setRecordList(result);
			return mapping.findForward("showEmail");
		}
		else
		{
			return mapping.findForward("fail");
		}
	}
	/**
	 * 编辑鉴定意见书内容
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward createJDYJS(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		String reportID = (String)request.getSession().getAttribute("expertReportID");
		String sql = "select * from TB_JDYJSINFO where REPORTID=?";
		DBTools dbTools = new DBTools();
		JDYJSBean jb = dbTools.queryJDYJS(sql, new String[]{reportID});
		ArrayList result = new ArrayList();
		if(jb==null)
		{
			jb = new JDYJSBean();
			jb.setId("");
			jb.setReportID(reportID);
			jb.setEventReason("");
			jb.setIdentifyContent("");
			jb.setWtDept("国家自然科学基金委员会监督委员会");
			jb.setJdConclusion("");
		}
		result.add(jb);
		expertAdviceForm.setRecordList(result);
		return mapping.findForward("createJDYJS");
	}
	/**
	 * 保存鉴定意见书内容
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ActionForward saveJDYJS(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ExpertAdviceForm expertAdviceForm = (ExpertAdviceForm)form;
		//String reportID = (String)request.getSession().getAttribute("expertReportID");
		String reportID = expertAdviceForm.getReportID();
		String eventReason = expertAdviceForm.getEventReason();
		String identifyContent = expertAdviceForm.getIdentifyContent();
		String wtDept = expertAdviceForm.getWtDept();
		String jdConclusion = expertAdviceForm.getJdConclusion();
		DBTools dbTools = new DBTools();
		//String sql = "if not exists(select ID from TB_JDYJSINFO where REPORTID='" + reportID + "') insert into TB_JDYJSINFO(REPORTID,EVENTREASON,IDENTIFYCONTENT,WTDEPT,JDCONCLUSION) values('" + reportID + "','" + eventReason + "','" + identifyContent + "','" + wtDept + "','" + jdConclusion + "') else update TB_JDYJSINFO set EVENTREASON='" + eventReason + "', IDENTIFYCONTENT='" + identifyContent + "',WTDEPT='" + wtDept + "',JDCONCLUSION='" + jdConclusion + "' where REPORTID='" + reportID + "'";
		String tempsql="select * from TB_JDYJSINFO where REPORTID=?";
		String[] tempparams = new String[]{reportID};
		String sql="";
		String[] params = new String[0];
		JDYJSBean jb = dbTools.queryJDYJS(tempsql, tempparams);
		if(jb==null)
		{
			sql = "insert into TB_JDYJSINFO(REPORTID,EVENTREASON,IDENTIFYCONTENT,WTDEPT,JDCONCLUSION) values(?, ?, ?, ?, ?)";
			params = new String[]{reportID, eventReason, identifyContent, wtDept, jdConclusion};
		}
		else
		{
			sql = "update TB_JDYJSINFO set EVENTREASON=?, IDENTIFYCONTENT=?, WTDEPT=?, JDCONCLUSION=? where REPORTID=?";
			params = new String[]{eventReason, identifyContent, wtDept, jdConclusion};
		}
		boolean result = dbTools.insertItem(sql, params);
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "保存成功！");
			json.put("callbackType", "closeCurrent");
			//json.put("navTabId", "addExpertAdvice");
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
}