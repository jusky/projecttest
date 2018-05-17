/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.eventmanage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.whu.tools.DBTools;
import com.whu.tools.SystemConstant;
import com.whu.web.event.EventBean;
import com.whu.web.eventbean.ApproveInfo;
import com.whu.web.eventbean.AttachmentBean;
import com.whu.web.eventbean.CheckBean;
import com.whu.web.eventbean.HandleFlow;

/** 
 * MyEclipse Struts
 * Creation date: 01-09-2014
 * 
 * XDoclet definition:
 * @struts.action path="/eventDetailAction" name="eventDetailForm" parameter="method" scope="request" validate="true"
 */
public class EventDetailAction extends DispatchAction {
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
	 * @throws UnsupportedEncodingException 
	 */
	public ActionForward attachment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		EventDetailForm eventDetailForm = (EventDetailForm) form;
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String reportID = (String)request.getSession().getAttribute("reportID");
		if(reportID == null || reportID.equals(""))
		{
			eventDetailForm.setRecordNotFind("true");
			return mapping.findForward("attachment");
		}
		String dirPath = request.getSession().getServletContext().getRealPath("/")+"/attachment/" + reportID;
		//转化为服务器IP地址路径的方式才能进行下载
		String serverPath = SystemConstant.GetServerPath() + "/" + "attachment" + "/" + reportID;
		File files = new File(dirPath);
		if(!files.isDirectory()){
			//System.out.println("目录不存在，或没有上传任何附件！");
			eventDetailForm.setRecordNotFind("true");
			return mapping.findForward("attachment");
		}
		ArrayList attachList = new ArrayList();
		String filePath = "";
		String fileName = "";
		//扩展名
		String extName = "";
		String swfName = "";
		long fileSize = 0L;
		File[] fileList = files.listFiles();  //目录中的所有文件
		int i = 0;
		try
		{
	        for(File file : fileList){
	                  if(!file.isFile())   //判断是不是文件
	                  continue;
	                  //filePath = file.getAbsolutePath();
	                  filePath = serverPath + "/" + file.getName();
	                  fileSize = file.getTotalSpace();
	                  fileName = file.getName();
	                  //String fileName1 = fileName.substring(0, fileName.lastIndexOf(".")); 
	                  extName = filePath.substring(filePath.lastIndexOf(".")+1);  
	                  AttachmentBean ab = new AttachmentBean();
                	  ab.setSerialNum(String.valueOf(++i));
                	  ab.setExtName(extName);
                	  ab.setFileName(fileName);
                	  ab.setFilePath(filePath);
                	  ab.setFileSize(fileSize);
                	  attachList.add(ab);
	                  /*
	                  if(!extName.equals("swf"))//swf文件为预览时的文件
	                  {
	                	  AttachmentBean ab = new AttachmentBean();
	                	  ab.setSerialNum(String.valueOf(++i));
	                	  ab.setExtName(extName);
	                	  ab.setFileName(fileName1);
	                	  ab.setFilePath(filePath);
	                	  if(SystemShare.ISDoc(extName))
			               {
	                		  swfName = dirPath + "/" + fileName1 + ".swf";
	                		  File swfFile = new File(swfName);
	                		  if(!swfFile.exists())//如果不存在，则生成swf文件
	                		  {
					              String converfilename = dirPath +"/"+fileName; 
					              DocConverter d = new DocConverter(converfilename);
					              d.conver();  
	                		  }
	                		  //将路径修改为服务器路径
	                		  swfName = serverPath + "/" + fileName1 + ".swf";
			               }
	                	  ab.setSwfPath(swfName);
	                	  ab.setFileSize(fileSize);
	                	  attachList.add(ab);
	                  }
	                  */
	        }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        if(attachList.size() > 0)
        {
        	eventDetailForm.setRecordNotFind("false");
        	eventDetailForm.setRecordList(attachList);
        }
        else
        {
        	eventDetailForm.setRecordNotFind("true");
        }
		return mapping.findForward("attachment");
	}
	
	
	/**
	 * 处理决定
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public ActionForward handleDecide(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		EventDetailForm eventDetailForm = (EventDetailForm) form;
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String reportID = (String)request.getSession().getAttribute("reportID");
		String sql = "select * from TB_HANDLEDECIDE where REPORTID=?";
		DBTools dbTools = new DBTools();
		ArrayList result = dbTools.queryHandleDecide(sql, "2", new String[]{reportID});
		if(result.size() > 0)
		{
			eventDetailForm.setRecordNotFind("false");
			eventDetailForm.setRecordList(result);
		}
		else
		{
			eventDetailForm.setRecordNotFind("true");
		}
		return mapping.findForward("handleDecide");
	}
	
	/**
	 * 事件详情
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public ActionForward eventDetail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		EventDetailForm eventDetailForm = (EventDetailForm) form;
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		String type = request.getParameter("type");//approveInfo
		String reportID = (String)request.getSession().getAttribute("reportID");
		
		String sql = "";
		DBTools dbTools = new DBTools();
		ArrayList result = new ArrayList();
		if(type.equals("approveInfo"))
		{
			ApproveInfo ai = new ApproveInfo();
			sql = "select * from TB_CHECKINFO where REPORTID=? limit 1";
			ArrayList checkList = dbTools.queryCheckInfoList2(sql, new String[]{reportID});
			if(checkList!=null&& checkList.size() > 0)
			{
				ai.setCheckList(checkList);
			}
			sql = "select * from TB_APPROVEINFO where REPORTID=?";
			// TODO
			// may be multi APPROVEINFO to one REPORTID, to be fixed...
			
			//ApproveBean ab = dbTools.queryApproveInfo(sql, new String[]{reportID});
			ArrayList approveList=dbTools.queryApproveInfo(sql, new String[]{reportID});
			//if(ab != null)
			//{
			//	ai.setHeadAdvice(ab.getLaAdvice());
			//	ai.setHeadName(ab.getApproveName());
			//	ai.setHeadTime(ab.getApproveTime());
			//	ai.setIsXY(ab.getIsXY());
			//}
			if(approveList!=null&& approveList.size() > 0)
			{
				ai.setApproveList(approveList);
			}
			if(ai!=null)
			{
				result.add(ai);
			}
		}
		else if(type.equals("deptAdvice"))
		{
			sql = "select * from TB_DEPTADVICE where REPORTID=?";
			result = dbTools.queryDeptAdvice(sql, "2", new String[]{reportID});
		}
		else if(type.equals("expertAdvice"))
		{
			//sql = "select a.*,b.JDCONCLUSION from TB_EXPERTADVICE a, TB_JDYJSINFO b where a.REPORTID=b.REPORTID and a.REPORTID=?";
			sql = "select * from TB_EXPERTADVICE where REPORTID=?";
			result = dbTools.queryExpertAdvice(sql, "2", new String[]{reportID});
		}
		else if(type.equals("litigantState"))
		{
			sql = "select * from TB_LITIGANTSTATE where REPORTID=?";
			result = dbTools.queryLitigantState(sql, "2", new String[]{reportID});
		}
		else if(type.equals("facultyAdvice"))
		{
			sql = "select a.*,b.ZZNAME as FACULTYNAME from TB_FACULTYADVICE a, SYS_ZZINFO b where a.REPORTID=? and a.FACULTYID=b.ZZID";
			result = dbTools.queryFacultyAdvice(sql, "2", new String[]{reportID});
		}
		else if(type.equals("handleDecide"))
		{
			sql = "select * from TB_HANDLEDECIDE where REPORTID=?";
			result = dbTools.queryHandleDecide(sql, "2", new String[]{reportID});
		}
		else if(type.equals("handleFlow"))
		{
			sql = "select a.*,b.CAPTION from TB_HANDLEPROCESS a,SYS_DATA_DIC b  where a.REPORTID=? and a.STATUS=b.CODE and b.CODENAME='" + SystemConstant.sjzt +"' order by a.ID asc";
			result = dbTools.queryHandleFlow(sql, new String[]{reportID});
		}
		else if(type.equals("analysInve"))
		{
			sql = "select * from TB_ANALYSANDINVE where REPORTID=?";
			result = dbTools.queryanalysisAndInvestigation(sql,"2",new String[]{reportID});
		}
		else if(type.equals("treatSuggest"))
		{
			sql = "select * from TB_TREATANDSUG where REPORTID=?";
			result = dbTools.querytreatmentSuggestion(sql,"2", new String[]{reportID});
		}
		else if(type.equals("conofmeet"))
		{
			sql = "select * from TB_CONOFMEET where REPORTID=?";
			result = dbTools.queryconOfMeet(sql,"2", new String[]{reportID});
		}
		
		request.setAttribute("size", result.size());
		
		if(result.size() > 0)
		{
			eventDetailForm.setRecordNotFind("false");
			eventDetailForm.setRecordList(result);
		}
		else
		{
			eventDetailForm.setRecordNotFind("true");
		}
		return mapping.findForward(type);
	}
	/**
	 * 生成流程图
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public ActionForward flowGraph(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		EventDetailForm eventDetailForm = (EventDetailForm) form;
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		String reportID = (String)request.getSession().getAttribute("reportID");
		//开头，包括“开始节点”
		String jsonStart = "{\"id\":null,\"name\":null,\"count\":85,\"nodes\":[{\"id\":\"node_38\",\"name\":\"node_38\",\"type\":\"start\",\"shape\":\"oval\",\"number\":38,\"left\":12,\"top\":88,\"width\":20,\"height\":20,\"property\":null},";
		String temp = "";
		
		Hashtable ht =  InitFlowGraph();
		
		DBTools dbTools = new DBTools();
		String sql = "select a.*,b.CAPTION from TB_HANDLEPROCESS a,SYS_DATA_DIC b  where a.REPORTID=? and a.STATUS=b.CODE and b.CODENAME='" + SystemConstant.sjzt +"' order by a.ID asc";
		ArrayList result = dbTools.queryHandleFlow(sql, new String[]{reportID});
		HandleFlow hf;
		String flowType = "";
		if(result.size() > 0)
		{
			for(int i = 0; i < result.size(); i++)
			{
				hf = (HandleFlow)result.get(i);
				flowType = hf.getFlowType();
				if(flowType != null && !flowType.equals(""))
				{
					if(ht.containsKey(flowType))
					{
						//覆盖初始值
						ht.put(flowType, hf);
					}
				}
			}
		}
		String key = "";
		for(Iterator itr = ht.keySet().iterator(); itr.hasNext();){ 
			key = (String)itr.next();
			hf = (HandleFlow)ht.get(key);
			temp = GetJsonStr(key, hf.getSel(), hf.getName(), hf.getTime(), hf.getDescribe());
			if(temp != null)
				jsonStart += temp + ",";
		}
		/*
		//节点数据
		temp = GetJsonStr("受理举报", "true", "方处长", "2013-11-11", "测试描述");
		jsonStart += temp + ",";
		temp = GetJsonStr("立案调查", "false", "方处长", "2013-12-11", "测试描述222222222");
		jsonStart += temp + ",";
		*/
		//结束节点
		String jsonEnd = "{\"id\":\"node_39\",\"name\":\"node_39\",\"type\":\"end\",\"shape\":\"oval\",\"number\":39,\"left\":781,\"top\":567,\"width\":20,\"height\":20,\"property\":null}";
		jsonStart += jsonEnd + "],";
		//箭头数据
		// String jsonLine = "\"lines\":[{\"id\":\"line_56\",\"name\":\"line_56\",\"type\":\"line\",\"shape\":\"line\",\"number\":56,\"from\":\"node_38\",\"to\":\"node_46\",\"fromx\":32,\"fromy\":98,\"tox\":87,\"toy\":96.5,\"polydot\":[],\"property\":null},{\"id\":\"line_57\",\"name\":\"line_57\",\"type\":\"line\",\"shape\":\"line\",\"number\":57,\"from\":\"node_46\",\"to\":\"node_40\",\"fromx\":122,\"fromy\":96.5,\"tox\":198,\"toy\":95.5,\"polydot\":[],\"property\":null},{\"id\":\"line_58\",\"name\":\"line_58\",\"type\":\"line\",\"shape\":\"line\",\"number\":58,\"from\":\"node_40\",\"to\":\"node_47\",\"fromx\":233,\"fromy\":95.5,\"tox\":291,\"toy\":95,\"polydot\":[],\"property\":null},{\"id\":\"line_59\",\"name\":\"line_59\",\"type\":\"line\",\"shape\":\"line\",\"number\":59,\"from\":\"node_47\",\"to\":\"node_42\",\"fromx\":391,\"fromy\":95,\"tox\":458,\"toy\":94.5,\"polydot\":[],\"property\":null},{\"id\":\"line_64\",\"name\":\"line_64\",\"type\":\"line\",\"shape\":\"polyline\",\"number\":64,\"from\":\"node_63\",\"to\":\"node_44\",\"fromx\":425,\"fromy\":207,\"tox\":335,\"toy\":269,\"polydot\":[{\"x\":335,\"y\":207}],\"property\":null},{\"id\":\"line_66\",\"name\":\"line_66\",\"type\":\"line\",\"shape\":\"polyline\",\"number\":66,\"from\":\"node_63\",\"to\":\"node_49\",\"fromx\":525,\"fromy\":207,\"tox\":622,\"toy\":267,\"polydot\":[{\"x\":622,\"y\":207}],\"property\":null},{\"id\":\"line_67\",\"name\":\"line_67\",\"type\":\"line\",\"shape\":\"line\",\"number\":67,\"from\":\"node_63\",\"to\":\"node_45\",\"fromx\":475,\"fromy\":227,\"tox\":478,\"toy\":270,\"polydot\":[],\"property\":null},{\"id\":\"line_68\",\"name\":\"line_68\",\"type\":\"line\",\"shape\":\"line\",\"number\":68,\"from\":\"node_42\",\"to\":\"node_63\",\"fromx\":475.5,\"fromy\":147,\"tox\":475,\"toy\":187,\"polydot\":[],\"property\":null},{\"id\":\"line_69\",\"name\":\"line_69\",\"type\":\"line\",\"shape\":\"line\",\"number\":69,\"from\":\"node_45\",\"to\":\"node_50\",\"fromx\":478,\"fromy\":310,\"tox\":479,\"toy\":356,\"polydot\":[],\"property\":null},{\"id\":\"line_72\",\"name\":\"line_72\",\"type\":\"line\",\"shape\":\"line\",\"number\":72,\"from\":\"node_49\",\"to\":\"node_50\",\"fromx\":572,\"fromy\":307,\"tox\":529,\"toy\":356,\"polydot\":[],\"property\":null},{\"id\":\"line_73\",\"name\":\"line_73\",\"type\":\"line\",\"shape\":\"line\",\"number\":73,\"from\":\"node_44\",\"to\":\"node_50\",\"fromx\":385,\"fromy\":309,\"tox\":429,\"toy\":356,\"polydot\":[],\"property\":null},{\"id\":\"line_74\",\"name\":\"line_74\",\"type\":\"line\",\"shape\":\"line\",\"number\":74,\"from\":\"node_50\",\"to\":\"node_51\",\"fromx\":479,\"fromy\":396,\"tox\":478.5,\"toy\":441,\"polydot\":[],\"property\":null},{\"id\":\"line_75\",\"name\":\"line_75\",\"type\":\"line\",\"shape\":\"line\",\"number\":75,\"from\":\"node_51\",\"to\":\"node_52\",\"fromx\":478.5,\"fromy\":511,\"tox\":478,\"toy\":554,\"polydot\":[],\"property\":null},{\"id\":\"line_79\",\"name\":\"line_79\",\"type\":\"line\",\"shape\":\"line\",\"number\":79,\"from\":\"node_52\",\"to\":\"node_53\",\"fromx\":528,\"fromy\":554,\"tox\":648,\"toy\":526.5,\"polydot\":[],\"property\":null},{\"id\":\"line_80\",\"name\":\"line_80\",\"type\":\"line\",\"shape\":\"line\",\"number\":80,\"from\":\"node_52\",\"to\":\"node_54\",\"fromx\":528,\"fromy\":594,\"tox\":648,\"toy\":620.5,\"polydot\":[],\"property\":null},{\"id\":\"line_81\",\"name\":\"line_81\",\"type\":\"line\",\"shape\":\"line\",\"number\":81,\"from\":\"node_53\",\"to\":\"node_39\",\"fromx\":683,\"fromy\":526.5,\"tox\":781,\"toy\":577,\"polydot\":[],\"property\":null},{\"id\":\"line_82\",\"name\":\"line_82\",\"type\":\"line\",\"shape\":\"line\",\"number\":82,\"from\":\"node_54\",\"to\":\"node_39\",\"fromx\":683,\"fromy\":620.5,\"tox\":781,\"toy\":577,\"polydot\":[],\"property\":null},{\"id\":\"line_84\",\"name\":\"line_84\",\"type\":\"line\",\"shape\":\"line\",\"number\":84,\"from\":\"node_43\",\"to\":\"node_39\",\"fromx\":790,\"fromy\":234,\"tox\":791,\"toy\":567,\"polydot\":[],\"property\":null},{\"id\":\"line_85\",\"name\":\"line_85\",\"type\":\"line\",\"shape\":\"polyline\",\"number\":85,\"from\":\"node_42\",\"to\":\"node_43\",\"fromx\":493,\"fromy\":94.5,\"tox\":790,\"toy\":194,\"polydot\":[{\"x\":790,\"y\":94.5}],\"property\":null},{\"id\":\"line_87\",\"name\":\"line_87\",\"type\":\"line\",\"shape\":\"line\",\"number\":87,\"from\":\"node_52\",\"to\":\"node_86\",\"fromx\":428,\"fromy\":574,\"tox\":355,\"toy\":574,\"polydot\":[],\"property\":null},{\"id\":\"line_88\",\"name\":\"line_88\",\"type\":\"line\",\"shape\":\"line\",\"number\":88,\"from\":\"node_86\",\"to\":\"node_52\",\"fromx\":355,\"fromy\":574,\"tox\":428,\"toy\":574,\"polydot\":[],\"property\":null}]}";
		String jsonLine = "\"lines\":[{\"id\":\"line_56\",\"name\":\"line_56\",\"type\":\"line\",\"shape\":\"line\",\"number\":56,\"from\":\"node_38\",\"to\":\"node_46\",\"fromx\":32,\"fromy\":98,\"tox\":87,\"toy\":96.5,\"polydot\":[],\"property\":null},{\"id\":\"line_57\",\"name\":\"line_57\",\"type\":\"line\",\"shape\":\"line\",\"number\":57,\"from\":\"node_46\",\"to\":\"node_40\",\"fromx\":122,\"fromy\":96.5,\"tox\":198,\"toy\":95.5,\"polydot\":[],\"property\":null},{\"id\":\"line_58\",\"name\":\"line_58\",\"type\":\"line\",\"shape\":\"line\",\"number\":58,\"from\":\"node_40\",\"to\":\"node_47\",\"fromx\":233,\"fromy\":95.5,\"tox\":291,\"toy\":95,\"polydot\":[],\"property\":null},{\"id\":\"line_59\",\"name\":\"line_59\",\"type\":\"line\",\"shape\":\"line\",\"number\":59,\"from\":\"node_47\",\"to\":\"node_42\",\"fromx\":391,\"fromy\":95,\"tox\":458,\"toy\":94.5,\"polydot\":[],\"property\":null},{\"id\":\"line_64\",\"name\":\"line_64\",\"type\":\"line\",\"shape\":\"polyline\",\"number\":64,\"from\":\"node_63\",\"to\":\"node_44\",\"fromx\":425,\"fromy\":207,\"tox\":335,\"toy\":269,\"polydot\":[{\"x\":335,\"y\":207}],\"property\":null},{\"id\":\"line_66\",\"name\":\"line_66\",\"type\":\"line\",\"shape\":\"polyline\",\"number\":66,\"from\":\"node_63\",\"to\":\"node_49\",\"fromx\":525,\"fromy\":207,\"tox\":622,\"toy\":267,\"polydot\":[{\"x\":622,\"y\":207}],\"property\":null},{\"id\":\"line_67\",\"name\":\"line_67\",\"type\":\"line\",\"shape\":\"line\",\"number\":67,\"from\":\"node_63\",\"to\":\"node_45\",\"fromx\":475,\"fromy\":227,\"tox\":478,\"toy\":270,\"polydot\":[],\"property\":null},{\"id\":\"line_68\",\"name\":\"line_68\",\"type\":\"line\",\"shape\":\"line\",\"number\":68,\"from\":\"node_42\",\"to\":\"node_63\",\"fromx\":475.5,\"fromy\":147,\"tox\":475,\"toy\":187,\"polydot\":[],\"property\":null},{\"id\":\"line_69\",\"name\":\"line_69\",\"type\":\"line\",\"shape\":\"line\",\"number\":69,\"from\":\"node_45\",\"to\":\"node_50\",\"fromx\":478,\"fromy\":310,\"tox\":479,\"toy\":356,\"polydot\":[],\"property\":null},{\"id\":\"line_72\",\"name\":\"line_72\",\"type\":\"line\",\"shape\":\"line\",\"number\":72,\"from\":\"node_49\",\"to\":\"node_50\",\"fromx\":572,\"fromy\":307,\"tox\":529,\"toy\":356,\"polydot\":[],\"property\":null},{\"id\":\"line_73\",\"name\":\"line_73\",\"type\":\"line\",\"shape\":\"line\",\"number\":73,\"from\":\"node_44\",\"to\":\"node_50\",\"fromx\":385,\"fromy\":309,\"tox\":429,\"toy\":356,\"polydot\":[],\"property\":null},{\"id\":\"line_74\",\"name\":\"line_74\",\"type\":\"line\",\"shape\":\"line\",\"number\":74,\"from\":\"node_50\",\"to\":\"node_51\",\"fromx\":479,\"fromy\":396,\"tox\":478.5,\"toy\":441,\"polydot\":[],\"property\":null},{\"id\":\"line_75\",\"name\":\"line_75\",\"type\":\"line\",\"shape\":\"line\",\"number\":75,\"from\":\"node_51\",\"to\":\"node_52\",\"fromx\":478.5,\"fromy\":511,\"tox\":478,\"toy\":554,\"polydot\":[],\"property\":null},{\"id\":\"line_79\",\"name\":\"line_79\",\"type\":\"line\",\"shape\":\"line\",\"number\":79,\"from\":\"node_52\",\"to\":\"node_53\",\"fromx\":528,\"fromy\":554,\"tox\":648,\"toy\":526.5,\"polydot\":[],\"property\":null},{\"id\":\"line_80\",\"name\":\"line_80\",\"type\":\"line\",\"shape\":\"line\",\"number\":80,\"from\":\"node_52\",\"to\":\"node_54\",\"fromx\":528,\"fromy\":594,\"tox\":648,\"toy\":620.5,\"polydot\":[],\"property\":null},{\"id\":\"line_81\",\"name\":\"line_81\",\"type\":\"line\",\"shape\":\"line\",\"number\":81,\"from\":\"node_53\",\"to\":\"node_39\",\"fromx\":683,\"fromy\":526.5,\"tox\":781,\"toy\":577,\"polydot\":[],\"property\":null},{\"id\":\"line_82\",\"name\":\"line_82\",\"type\":\"line\",\"shape\":\"line\",\"number\":82,\"from\":\"node_54\",\"to\":\"node_39\",\"fromx\":683,\"fromy\":620.5,\"tox\":781,\"toy\":577,\"polydot\":[],\"property\":null},{\"id\":\"line_84\",\"name\":\"line_84\",\"type\":\"line\",\"shape\":\"line\",\"number\":84,\"from\":\"node_43\",\"to\":\"node_39\",\"fromx\":790,\"fromy\":234,\"tox\":791,\"toy\":567,\"polydot\":[],\"property\":null},{\"id\":\"line_85\",\"name\":\"line_85\",\"type\":\"line\",\"shape\":\"polyline\",\"number\":85,\"from\":\"node_42\",\"to\":\"node_43\",\"fromx\":493,\"fromy\":94.5,\"tox\":790,\"toy\":194,\"polydot\":[{\"x\":790,\"y\":94.5}],\"property\":null},{\"id\":\"line_87\",\"name\":\"line_87\",\"type\":\"line\",\"shape\":\"line\",\"number\":87,\"from\":\"node_52\",\"to\":\"node_86\",\"fromx\":428,\"fromy\":574,\"tox\":355,\"toy\":574,\"polydot\":[],\"property\":null},{\"id\":\"line_88\",\"name\":\"line_88\",\"type\":\"line\",\"shape\":\"line\",\"number\":88,\"from\":\"node_86\",\"to\":\"node_52\",\"fromx\":355,\"fromy\":574,\"tox\":428,\"toy\":574,\"polydot\":[],\"property\":null},{\"id\":\"line_70\",\"name\":\"line_70\",\"type\":\"line\",\"shape\":\"line\",\"number\":70,\"from\":\"node_70\",\"to\":\"node_50\",\"fromx\":429,\"fromy\":376,\"tox\":355,\"toy\":376,\"polydot\":[],\"property\":null},{\"id\":\"line_71\",\"name\":\"line_71\",\"type\":\"line\",\"shape\":\"line\",\"number\":71,\"from\":\"node_50\",\"to\":\"node_70\",\"fromx\":355,\"fromy\":376,\"tox\":429,\"toy\":376,\"polydot\":[],\"property\":null}]}";	
		String jsonStr = jsonStart + jsonLine;
		request.setAttribute("jsonData", jsonStr);
		return mapping.findForward("flowGraph");
	}
	/**
	 * 初始化流程图的对象hashTable，最主要的是将节点的执行变量sel设置为false
	 * @return
	 */
	public Hashtable InitFlowGraph()
	{
		Hashtable ht = new Hashtable();
		String[] flowArr = SystemConstant.LCT_ARR;
		HandleFlow hf;
		for(int i = 0;  i < flowArr.length; i++)
		{
			hf = new HandleFlow();
			hf.setFlowType(flowArr[i]);
			hf.setTime("无");
			hf.setName("无");
			hf.setDescribe("无");
			hf.setSel("false");
			ht.put(flowArr[i], hf);
		}
		return ht;
	}
	/**
	 * 生成流程图需要的json字符串
	 * @param name 
	 * @param sel
	 * @param user
	 * @param time
	 * @param desc
	 * @return
	 */
	public String GetJsonStr(String name, String sel,  String user, String time, String desc)
	{
		String result = "";		
//		public static String LCT_SLJB = "监委会受理举报";
//		public static String LCT_LDSP = "领导审批";
//		public static String LCT_BYLA = "不予立案";
//		public static String LCT_DWDC = "委托依托单位调查";
//		public static String LCT_ZJJD = "委托专家鉴定";
//		public static String LCT_JB = "个人/单位举报";
//		public static String LCT_DCHS = "初步调查核实";
//		public static String LCT_CS = "当事人陈述";
//		public static String LCT_DCBG = "调查报告";
//		public static String LCT_TL = "全委会讨论";
//		public static String LCT_CLJD = "处理决定";
//		public static String LCT_XGBM = "委内相关部门";
//		public static String LCT_DWGR = "依托单位/个人";
//		public static String LCT_LADC = "开展调查";
//		public static String LCT_FYSQ = "复议申请";
//		public static String LCT_FACULTY = "学部意见";
		if(name.equals(SystemConstant.LCT_SLJB))
		{
			result = GetSigleNodeJson("node_40", name, sel, "node", "img", "40", "178", "78", "75", "70", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_LDSP))
		{
			result = GetSigleNodeJson("node_42", name, sel, "node", "img", "42", "438", "77", "75", "70", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_BYLA))
		{
			result = GetSigleNodeJson("node_43", name, sel, "node", "rect", "43", "740", "194", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_DWDC))
		{
			result = GetSigleNodeJson("node_44", name, sel, "node", "rect", "44", "285", "269", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_ZJJD))
		{
			result = GetSigleNodeJson("node_45", name, sel, "node", "rect", "45", "428", "270", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_JB))
		{
			result = GetSigleNodeJson("node_46", name, sel, "node", "img", "46", "67", "79", "75", "70", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_DCHS))
		{
			result = GetSigleNodeJson("node_47", name, sel, "node", "rect", "47", "291", "75", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_CS))
		{
			result = GetSigleNodeJson("node_49", name, sel, "node", "rect", "49", "572", "267", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_DCBG))
		{
			result = GetSigleNodeJson("node_50", name, sel, "node", "rect", "50", "429", "356", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_TL))
		{
			result = GetSigleNodeJson("node_51", name, sel, "node", "img", "51", "441", "441", "75", "70", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_CLJD))
		{
			result = GetSigleNodeJson("node_52", name, sel, "node", "rect", "52", "428", "554", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_XGBM))
		{
			result = GetSigleNodeJson("node_53", name, sel, "node", "img", "53", "628", "509", "75", "70", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_DWGR))
		{
			result = GetSigleNodeJson("node_54", name, sel, "node", "img", "54", "628", "603", "75", "70", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_LADC))
		{
			result = GetSigleNodeJson("node_63", name, sel, "node", "rect", "63", "425", "187", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_FYSQ))
		{
			result = GetSigleNodeJson("node_86", name, sel, "node", "rect", "86", "255", "554", "100", "40", user, time, desc);
		}
		else if(name.equals(SystemConstant.LCT_FACULTY))
		{
			result = GetSigleNodeJson("node_70", name, sel, "node", "rect", "70", "255", "356", "100", "40", user, time, desc);
		} else {
			return null;
		}
		return result;
	}
	/**
	 * 生成每一个节点的json数据
	 * @param id
	 * @param name
	 * @param sel
	 * @param type
	 * @param shape
	 * @param number
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param user
	 * @param time
	 * @param desc
	 * @return
	 */
	public String GetSigleNodeJson(String id, String name, String sel, String type, String shape, String number, String left, String top, String width, String height, String user, String time, String desc)
	{
		String result = "";
		result = "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"sel\":\"" + sel + "\",\"type\":\"" + type + "\",\"shape\":\"" + shape + "\",\"number\":" + number + ",\"left\":" + left + ",\"top\":" + top + ",\"width\":" + width + ",\"height\":" + height + ",\"property\":[{\"id\":\"n_p_name\",\"text\":\"input\",\"value\":\"" + name + "\"},{\"id\":\"n_p_user\",\"text\":\"input\",\"value\":\"" + user + "\"},{\"id\":\"n_p_time\",\"text\":\"input\",\"value\":\"" + time + "\"},{\"id\":\"n_p_desc\",\"text\":\"textarea\",\"value\":\"" + desc.trim() + "\"}]}";
		return result;
	}
	/**
	 * 打印事件详情
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public ActionForward print(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		//String id = (String)request.getSession().getAttribute("reportID");
		String id = request.getParameter("id");
		String templatePath = "";
		
		String sql = "select * from TB_REPORTINFO where REPORTID=?";
		DBTools db = new DBTools();
		EventBean eb = db.queryEvent(sql, new String[]{id});
		
		sql = "select * from TB_BEREPORTPE where REPORTID=?";
		ArrayList beReportList = db.queryBeReport(sql, new String[]{id});
		if(beReportList != null && beReportList.size() > 0)
		{
			eb.setBeReportList(beReportList);
		}
		
		//templatePath = SystemConstant.GetServerPath() + "/web/template/sjxq.doc";
		templatePath = "web/template/sjxq" + String.valueOf(beReportList.size()) + ".doc";
		System.out.println("ceshi:" + templatePath);
		request.setAttribute("EventBean", eb);
		
		request.setAttribute("ReportID", id);
		request.setAttribute("ServerPath", SystemConstant.GetServerPath());
		request.setAttribute("templatePath", templatePath);
		
		return mapping.findForward("printEvent");
	}
}