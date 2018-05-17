/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.event;

import java.io.File;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.CheckPage;
import com.whu.tools.DBTools;
import com.whu.tools.JacobWord; // JacobWord not work in linux

import com.whu.tools.SystemConstant;
import com.whu.tools.crypto.AESCrypto;
import com.whu.web.common.SystemShare;
import com.whu.web.eventbean.HandleDecide;
import com.whu.web.eventbean.SurveyReportBean;
import com.whu.web.eventmanage.EventManageForm;
import com.whu.web.log.LogBean;

/** 
 * MyEclipse Struts
 * Creation date: 03-27-2014
 * 
 * XDoclet definition:
 * @struts.action path="/dcbgManageAction" name="dcbgManageForm" parameter="method" scope="request" validate="true"
 */
public class DcbgManageAction extends DispatchAction {
	/*
	 * Generated Methods
	 */
	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		DcbgManageForm dcbgManageForm = (DcbgManageForm) form;
		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// 
		int rowsPerPage = 20;// 
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("queryPageNo") != null && request.getParameter("queryPageNo") != "") {
			queryPageNo = Integer.parseInt(request.getParameter("queryPageNo"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		String sql = "select a.REPORTID, a.REPORTNAME, a.BEREPORTNAME, a.SERIALNUM, b.ID, b.UPDATETIME, b.FILENAME from TB_REPORTINFO a, TB_SURVEYREPORT b where a.REPORTID=b.REPORTID order by b.ID desc";
		String[] params = new String[0];
		request.getSession().setAttribute("queryDcbgSql", sql);
		request.getSession().setAttribute("queryDcbgParams", params);
		pageBean.setQuerySql(sql);
		pageBean.setParams(params);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		String serverPath = SystemConstant.GetServerPath() + "/attachment/";
		ArrayList result = db.queryDcbgList(rs, rowsPerPage, serverPath);
		if(result.size() > 0)
		{
			dcbgManageForm.setRecordNotFind("false");
			dcbgManageForm.setRecordList(result);
			
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			dcbgManageForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		//根据阶段的不同跳转到不同的页面
		return mapping.findForward("init");
	}
	/**
	 * 查询和分页
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryMsg(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");	
		DcbgManageForm dcbgManageForm = (DcbgManageForm) form;
		String operation = request.getParameter("operation");

		CheckPage pageBean = new CheckPage();
		String sql = "";
		String[] params = new String[0];
		int queryPageNo = 1;
		int rowsPerPage = 20;
		pageBean.setRowsPerPage(rowsPerPage);

		if (operation.equalsIgnoreCase("search") || operation.equalsIgnoreCase("select")) {
			String serialNum = dcbgManageForm.getSerialNum();
			String reportName = dcbgManageForm.getReportName();
			String beReportName = dcbgManageForm.getBeReportName();
			String createBeginTime = dcbgManageForm.getCreateBeginTime();
			String createEndTime = dcbgManageForm.getCreateEndTime();
			String temp = "";
			if(!serialNum.equals(""))
			{
			//	temp += " and a.SERIALNUM='" + serialNum + "' ";
				temp += " and a.SERIALNUM=?";
				params = new String[]{serialNum};
			}
			else
			{
				ArrayList<String> paramList = new ArrayList<String>();
				if(!reportName.equals(""))
				{
					AESCrypto aes = new AESCrypto();
					String key = "TB_REPORTINFO";
					temp += " and hex(a.REPORTNAME) = '" + SystemShare.getHexString(aes.createEncryptor(reportName, key)) + "'";
				}
				if(!beReportName.equals(""))
				{
					temp += " and a.REPORTID in (select distinct REPORTID from TB_BEREPORTPE where 1=1 ";
					if(!beReportName.equals(""))
					{
						AESCrypto aes = new AESCrypto();
						String key = "TB_BEREPORTPE";
						temp += " and hex(BEREPORTNAME) = '" + SystemShare.getHexString(aes.createEncryptor(beReportName, key)) + "'";
					}
					temp += ")";
				}
				if(!createBeginTime.equals(""))
				{
					temp += " and b.UPDATETIME >= ? ";
					paramList.add(createBeginTime);
				}
				if(!createEndTime.equals(""))
				{
					temp += " and b.UPDATETIME <= ? ";
					paramList.add(createEndTime);
				}
				params = paramList.toArray(new String[0]);
			}
			sql = "select a.REPORTID, a.REPORTNAME, a.BEREPORTNAME, a.SERIALNUM, b.ID, b.UPDATETIME, b.FILENAME from TB_REPORTINFO a, TB_SURVEYREPORT b where a.REPORTID=b.REPORTID " + temp + " order by b.ID desc";
			request.getSession().setAttribute("queryDcbgSql", sql);
			request.getSession().setAttribute("queryDcbgParams", sql);			
		}
		
		else if(operation.equalsIgnoreCase("changePage")){
			sql = (String)request.getSession().getAttribute("queryDcbgSql");
			params = (String[])request.getSession().getAttribute("queryDcbgParams");
			if (request.getParameter("pageNum") != null && request.getParameter("pageNum") != "") {
				queryPageNo = Integer.parseInt(request.getParameter("pageNum"));
			}
		}
		pageBean.setQuerySql(sql);
		pageBean.setParams(params);
		pageBean.setQueryPageNo(queryPageNo);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		String serverPath = SystemConstant.GetServerPath() + "/attachment/";
		ArrayList result = db.queryDcbgList(rs, rowsPerPage, serverPath);
		if(result.size() > 0)
		{
			dcbgManageForm.setRecordNotFind("false");
			dcbgManageForm.setRecordList(result);
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			dcbgManageForm.setRecordNotFind("true");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("init");
	}
	/**
	 * 编辑调查报告
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String id = request.getParameter("id");

		DBTools dbTools = new DBTools();
		String templatePath = "";
		String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/";
		String docPath = dbTools.querySingleData("TB_SURVEYREPORT", "FILENAME", "REPORTID", id);
		if(docPath != null && !docPath.equals(""))
		{
			String tempFilePath = filePath + docPath;
			if((new File(tempFilePath)).exists())//如果存在，则得到路径
			{
				templatePath = SystemConstant.GetServerPath() + "/attachment/" +  docPath;
				request.setAttribute("IsEdit", "1");
			}
			else//不存在，则继续使用模板，例如：人工删除或系统出错
			{
				request.setAttribute("IsEdit", "0");
				templatePath = SystemConstant.GetServerPath() + "/web/template/dcbg.doc";
			}
		}
		else
		{
			request.setAttribute("IsEdit", "0");
			templatePath = SystemConstant.GetServerPath() + "/web/template/dcbg.doc";
		}
		SurveyReportBean srb = new SurveyReportBean();
		srb.setCheckInfo("");
		srb.setDeptAdvice("");
		srb.setExpertAdvice("");
		srb.setLitigantState("");
		srb.setReportContent("");
		srb.setFacultyAdvice("赞成并尊重监委会意见");
		
		request.setAttribute("ReportID", id);
		request.setAttribute("ServerPath", SystemConstant.GetServerPath());
		request.setAttribute("templatePath", templatePath);
		request.setAttribute("SurveyReportBean", srb);
		return mapping.findForward("edit");
	}
	/**
	 * 合并选定的调查报告
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward combine(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String ids = request.getParameter("ids");
		DBTools dbTool = new DBTools();
		boolean result = true;
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		if(ids == null || ids.equals(""))
		{
			result = false;
		}
		else
		{
			//暂存编号，在后面需要保存到数据库中
			String tempIDs = ids;
			String[] idArray = ids.split(",");
			ids = ids.replaceAll(",", "','");
			ids = "'" + ids + "'";
		//	String sql = "select FILENAME from TB_SURVEYREPORT where REPORTID in (" + ids + ")";
			String sql = "select FILENAME from TB_SURVEYREPORT where REPORTID in (";
			StringBuilder queryBuilder = new StringBuilder(sql);
			for (int i = 0; i < idArray.length; i++) {
				queryBuilder.append(" ?");
				if(i != idArray.length -1) queryBuilder.append(",");
			}
			queryBuilder.append(")");
			ArrayList resultList = new ArrayList();
			resultList = dbTool.queryCombineReport(queryBuilder.toString(), idArray);
			
			String files = "";
			if(resultList.size() > 0) {
				for(int i=0; i<resultList.size(); i++){
					files += resultList.get(i) + ",";
				}
				files = files.substring(0, files.length()-1);
			}
			request.setAttribute("reportFiles", files);
			request.setAttribute("isEdit", "0");
			request.setAttribute("ServerPath", SystemConstant.GetServerPath());
			request.setAttribute("templatePath", SystemConstant.GetServerPath() + "/web/template/empty.doc");
			request.setAttribute("reportIDs",tempIDs);


			/*
			String filePath = "";
			String dirPath = request.getSession().getServletContext().getRealPath("/");
			File docFile;
			
			if(resultList.size() > 0)
			{
				List filePathList = new ArrayList();
				File[] fileList = new File[resultList.size()];
				for(int i = 0; i < resultList.size(); i++)
				{
					filePath = (String)resultList.get(i);
					if(filePath != null && !filePath.equals(""))
					{
						filePath = dirPath + "/attachment/" + filePath;
						filePath = SystemConstant.GetServerPath() + "/attachment/" + filePath;
						
						docFile = new File(filePath);
						if(docFile.exists())
						{
							filePathList.add(filePath);
						}*/
						/*
						docFile = new File(filePath);
						if(docFile.exists())
						{
							fileList[i] = docFile;
						}
						*/
			/*		}
				}
				try
				{
					JacobWord jw = new JacobWord();
					//合并调查报告文档，保存到默认路径中attachment/surveyReport/，
					String fileName = SystemShare.GetNowTime("yyyyMMddmmss");
					String createTime = SystemShare.GetNowTime("yyyy-MM-dd");
					
					String tempName = fileName + ".docx";
					String targetPath = dirPath + "/attachment/surveyReport/" + tempName;
					//result = jw.mergeWord(fileList, targetPath);
					
					result = jw.uniteDoc(filePathList, targetPath); jacobWord do not work on Linux
					
									
					// result = new MergeDocx().mergeDocxs(filePathList, targetPath);
					//向数据库中增加一条合并的记录，用于下次查询
					if(result)
					{
						sql = "insert into TB_COMBINEREPORT(REPORTIDS, FILEPATH, CREATETIME) values('" + tempIDS + "','" + tempName + "','" + createTime + "')";
						result = dbTool.insertItem(sql);
					}
				}
				catch (Exception e) {
					result = false;
				} 
			} */
		}

		
		return mapping.findForward("editCombineReport");
		/* 
		if(result)
		{
			json.put("statusCode", 200);
			json.put("message", "合并调查报告成功!");
		}
		else
		{
			json.put("statusCode", 300);
			json.put("message", "合并调查报告失败！");
		}
		out.write(json.toString());
		out.flush();
		out.close();
		
		return null; */
	}
	/**
	 * 查询已合并的调查报告
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward showCombine(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		DcbgManageForm dcbgManageForm =  (DcbgManageForm) form;
		String reportID = request.getParameter("id");
	/*	String sql = "select * from TB_COMBINEREPORT order by ID desc";
		DBTools dbTools = new DBTools();
		ArrayList result = dbTools.queryCombineReportList(sql); */
		
		

		CheckPage pageBean = new CheckPage();
		int queryPageNo = 1;// 
		int rowsPerPage = 20;// 
		pageBean.setRowsPerPage(rowsPerPage);
		if (request.getParameter("pageNum") != null && request.getParameter("pageNum") != "") {
			queryPageNo = Integer.parseInt(request.getParameter("pageNum"));
		}
		pageBean.setQueryPageNo(queryPageNo);
		String sql = "select * from TB_COMBINEREPORT order by ID desc";
		pageBean.setQuerySql(sql);
		pageBean.setParams(new String[0]);
		DBTools db = new DBTools();
		ResultSet rs = db.queryRs(queryPageNo, pageBean, rowsPerPage);
		String serverPath = SystemConstant.GetServerPath() + "/attachment/";
		ArrayList result = db.queryCombineReportList(rs, rowsPerPage);
		
		
		if(result.size() > 0)
		{
			dcbgManageForm.setRecordNotFind("false");
		//	request.setAttribute("totalRows", result.size());
			dcbgManageForm.setRecordList(result);
			SystemShare.SplitPageFun(request, pageBean, 1);
		}
		else
		{
			dcbgManageForm.setRecordNotFind("true");
		//	request.setAttribute("totalRows", "0");
			SystemShare.SplitPageFun(request, pageBean, 0);
		}
		return mapping.findForward("showCombine");
	}
	/**
	 * 编辑合并后的调查报告
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward editCombineReport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String fileName = request.getParameter("path");

		String templatePath = "";
		String filePath = request.getSession().getServletContext().getRealPath("/")+"/attachment/surveyReport/";
		
		String tempFilePath = filePath + fileName;
		if((new File(tempFilePath)).exists())//如果存在，则得到路径
		{
			templatePath = SystemConstant.GetServerPath() + "/attachment/surveyReport/" +  fileName;
		}
		else
		{
			return null;
		}
		request.setAttribute("filePath", fileName);
		request.setAttribute("ServerPath", SystemConstant.GetServerPath());
		request.setAttribute("templatePath", templatePath);
		return mapping.findForward("editCombineReport");
	}
	/**
	 * 删除合并的调查报告
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward deleteCombine(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		DBTools dbTool = new DBTools();
		
		boolean result = true;
		String id = request.getParameter("id");
		String fileName = request.getParameter("name");
		String dirPath = request.getSession().getServletContext().getRealPath("/");
		String filePath = "";
		if(fileName != null && !fileName.equals(""))
		{
			//删除处理决定文档
			filePath = dirPath + "/attachment/surveyReport/" + fileName;
			result = SystemShare.deleteFileFromDisk(filePath);
		}
		result = dbTool.deleteItemReal(id, "TB_COMBINEREPORT", "ID");

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
}