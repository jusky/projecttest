/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.expertAndDept;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.CheckPage;
import com.whu.tools.DBTools;
import com.whu.tools.SystemConstant;
import com.whu.tools.crypto.AESCrypto;
import com.whu.web.common.SystemShare;
import com.whu.tools.POIHWPFHelper;

/** 
 * MyEclipse Struts
 * Creation date: 01-20-2015
 * 
 * XDoclet definition:
 * @struts.action path="/facultyFKAction" name="facultyFKForm" parameter="method" scope="request" validate="true"
 * @struts.action-forward name="init" path="/web/expertAndDept/facultyFKList.jsp"
 */
public class FacultyFKAction extends DispatchAction {
	/*
	 * Generated Methods
	 */

	
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		FacultyFKForm facultyFKForm = (FacultyFKForm) form;
		DBTools db = new DBTools();
		
		String loginName = (String)request.getSession().getAttribute("LoginName");
		String facultyId = db.querySingleDate("SYS_USER", "ZZID", "LOGINNAME", loginName);
		String sql = "select a.*, b.SERIALNUM,b.REPORTNAME,b.BEREPORTNAME,b.REPORTREASON, c.FILENAME from TB_FACULTYADVICE a, TB_REPORTINFO b, TB_SURVEYREPORT c where a.FACULTYID='" + facultyId + "' and a.REPORTID=b.REPORTID and a.REPORTID=c.REPORTID order by ISFK asc, FKTIME asc";
		
		
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// 
		int rowsPerPage = 20;// 
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null && request.getParameter("queryPageNo") != "") {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		
		request.getSession().setAttribute("queryFacultyAdviceList", sql);
		pageBean.setQuerySql(sql);
		
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		String serverPath = SystemConstant.GetServerPath() + "/attachment/";
		ArrayList result = db.queryFacultyAdviceList(rs, rowsPerPage, serverPath);
		if(result.size() > 0)
		{
			facultyFKForm.setRecordNotFind("false");
			facultyFKForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			facultyFKForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		
		return mapping.findForward("init");
	}
	
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		FacultyFKForm facultyFKForm = (FacultyFKForm) form;
		String serialNum = facultyFKForm.getSerialNum();
		String beReportName = facultyFKForm.getBeReportName();
		
		String sql = "";
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		String isfk = request.getParameter("isfk");
		
		DBTools db = new DBTools();
		String loginName = (String)request.getSession().getAttribute("LoginName");
		String facultyId = db.querySingleDate("SYS_USER", "ZZID", "LOGINNAME", loginName);
		
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// 
		int rowsPerPage = 20;// 
		String operation = request.getParameter("operation");
		if(operation.equals("search")) {
			String temp = "";
			if(isfk != null && isfk.equals("1")) {
				temp += " and a.ISFK='1'";
			}
			if(serialNum != null && !("".equals(serialNum))){
				temp += " and b.SERIALNUM like '%" + serialNum + "%'";
			}
			if(beReportName != null && !("").equals(beReportName)) {
				AESCrypto aes = new AESCrypto();
				String key = "TB_REPORTINFO";
				temp += " and hex(b.BEREPORTNAME) = '" + SystemShare.getHexString(aes.createEncryptor(beReportName, key)) + "'";
			}
			sql =  "select a.*, b.SERIALNUM,b.REPORTNAME,b.BEREPORTNAME,b.REPORTREASON, c.FILENAME from TB_FACULTYADVICE a, TB_REPORTINFO b, TB_SURVEYREPORT c where a.FACULTYID='" + facultyId + "' and a.REPORTID=b.REPORTID and a.REPORTID=c.REPORTID" + temp + " order by ISFK asc, FKTIME asc";
			request.getSession().setAttribute("queryFacultyAdviceList", sql);
		} else if (operation.equals("changePage")) {
			sql = (String)request.getSession().getAttribute("queryFacultyAdviceList");
			if(orderField != null && !orderField.equals(""))
			{
				sql = sql.substring(0, sql.indexOf("order"));
				sql += " order by " + orderField + " " + orderDirection;
			}
			
			if (request.getParameter("currentPage") != null && request.getParameter("currentPage") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("currentPage"));
			}			
		} else {
			return null;
		}


		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null && request.getParameter("queryPageNo") != "") {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		
		request.getSession().setAttribute("queryFacultyAdviceList", sql);
		pageBean.setQuerySql(sql);
		
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		String serverPath = SystemConstant.GetServerPath() + "/attachment/";
		ArrayList result = db.queryFacultyAdviceList(rs, rowsPerPage, serverPath);
		if(result.size() > 0)
		{
			facultyFKForm.setRecordNotFind("false");
			facultyFKForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			facultyFKForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		
		return mapping.findForward("init");
	}
	
	public ActionForward multiDownload(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=\"dcbg.zip\"");
		response.setHeader("Content-Transfer-Encoding", "binary");
		request.setCharacterEncoding("utf-8");
		
		String reportIds = request.getParameter("ids");
		
		if(reportIds == null || reportIds.equals("")) {
			return null;
		} else {		
			reportIds = "('" + reportIds.replace(",", "','") + "')";
			int len = reportIds.split(",").length;
			String sql = "select FILENAME from TB_SURVEYREPORT where REPORTID in " + reportIds;
			DBTools db = new DBTools();
			String dirPath = request.getSession().getServletContext().getRealPath("/");
			String[] files = db.queryDcbgFiles(sql, len, dirPath);
			if (files != null) {
				
		        ZipOutputStream output = null;
		        byte[] buffer = new byte[102400];
				 try {
			            output = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(), 102400));

			            for (String file : files) {
			                InputStream input = null;
			                File fileIn = new File(file);
			                
			                try {
			                    input = new BufferedInputStream(new FileInputStream(fileIn));
			                    output.putNextEntry(new ZipEntry(fileIn.getName()));
			                    for (int length = 0; (length = input.read(buffer)) > 0;) {
			                        output.write(buffer, 0, length);
			                    }
			                    output.closeEntry();
			                } finally {
			                    if (input != null) try { input.close(); } catch (IOException logOrIgnore) { /**/ }
			                }
			            }
			        } finally {
			            if (output != null) try { output.flush();output.close(); } catch (IOException logOrIgnore) { /**/ }
			        }
			}
		} 
		
		return null;
	}
		
	public ActionForward editFacultyAdvice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		FacultyFKForm facultyFKForm = (FacultyFKForm)form;
		
		String sql = "";
		String id = request.getParameter("id");
		String reportId = request.getParameter("reportId");
		if (id != null && !id.equals("")) {
			sql = "select a.*,b.SERIALNUM,b.REPORTNAME,b.BEREPORTNAME,c.FILENAME from TB_FACULTYADVICE a, TB_REPORTINFO b, TB_SURVEYREPORT c where a.ID='" + id +"' and b.REPORTID=a.REPORTID and c.REPORTID=a.REPORTID";
		} else if (reportId != null && !reportId.equals("")) {
			sql = "select a.*,b.SERIALNUM,b.REPORTNAME,b.BEREPORTNAME,c.FILENAME from TB_FACULTYADVICE a, TB_REPORTINFO b, TB_SURVEYREPORT c where a.REPORTID='" + reportId +"' and b.REPORTID=a.REPORTID and c.REPORTID=a.REPORTID"; 
		} else {
			return null;
		}
		DBTools db = new DBTools();
		ArrayList result = db.queryFacultyAdvice(sql, "0");
		if(result.size() > 0) {
			facultyFKForm.setRecordNotFind("false");
			request.setAttribute("totalRows", result.size());
			facultyFKForm.setRecordList(result);
		} else {
			facultyFKForm.setRecordNotFind("true");
			request.setAttribute("totalRows", "0");
		}
		
		return mapping.findForward("editFacultyAdvice");
	}
	
	public ActionForward saveAdvice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		FacultyFKForm facultyFKForm = (FacultyFKForm)form;
		String id = facultyFKForm.getId();
		String advice = facultyFKForm.getAdvice();
		String dirPath = request.getSession().getServletContext().getRealPath("/");

		String fktime = SystemShare.GetNowTime("yyyy-MM-dd");
		
		String sql = "update TB_FACULTYADVICE set ISFK='1',FKTIME='" + fktime + "',ADVICE='" + advice + "' where ID='" + id + "'";
		DBTools db = new DBTools();
		boolean result = db.insertItem(sql);
		
		if (result) {

			String reportId = db.querySingleDate("TB_FACULTYADVICE", "REPORTID", "ID", id);
			String LoginName = (String)request.getSession().getAttribute("LoginName");
			sql = "select ZZNAME from SYS_USER a, SYS_ZZINFO b where a.LOGINNAME='" + LoginName + "' and a.ZZID=b.ZZID";
			String ZZNAME = db.queryZZName(sql);
			// insert facultyAdvice to survey Report doc
			String dcbgPath = dirPath + "/attachment/" + db.querySingleDate("TB_SURVEYREPORT", "FILENAME", "REPORTID", reportId);

			advice = ZZNAME + "：\r\n\t" +  advice + "\r\n";
			new POIHWPFHelper().fillBookmark("facultyAdvice", advice, dcbgPath);
			
			//更新消息提醒状态
			String UserName = (String)request.getSession().getAttribute("UserName");
			sql = "update TB_MSGNOTIFY set ISHANDLE='1' where RECVNAME='" + UserName + "' and REPORTID='" + reportId + "'";
			db.insertItem(sql);		
	
			String time = SystemShare.GetNowTime("yyyy-MM-dd");
			String userName = (String)request.getSession().getAttribute("UserName");
			String describe = time + "," + userName + "在线提交学部意见";
			//插入处理过程到数据库中
			result = db.InsertHandleProcess(reportId, userName, SystemConstant.HP_FACULTYADVICE, SystemConstant.SS_SURVEYING, SystemConstant.LCT_FACULTY, describe);
			
			//写入日志文件
			db.insertLogInfo(UserName, SystemConstant.LOG_FACULTYADVICE, "编辑学部意见，事件编号为：" + reportId, request.getRemoteAddr());
		}
		
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		if (result) {
			json.put("statusCode", 200);
			json.put("message", "保存成功！");
			json.put("callbackType", "closeCurrent");
		} else {
			json.put("statusCode", 300);
			json.put("message", "保存失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null;
	}	
}