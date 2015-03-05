/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.eventmanage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/** 
 * MyEclipse Struts
 * Creation date: 01-20-2013
 * 
 * XDoclet definition:
 * @struts.form name="expertAdviceForm"
 */
public class ExpertAdviceForm extends ActionForm {
	/*
	 * Generated Methods
	 */

	private String id;
	private String expertName;
	private String time;
	private String conclusion;
	private String advice;
	private String reportID;
	private String content;
	private String title;
	private String serialNum;
	private String dept;
	private String position;
	private String fileName;
	private FormFile file;
	private String fileExtFlag;
	private String loginName;
	private String password;
	
	//专家鉴定函
	//简述
	private String shortInfo;
	//反馈日期
	private String fkTime;
	//鉴定目标
	private String target;
	//鉴定内容
	private String jdContent;
	
	//案由
	private String eventReason;
	//鉴定内容与目的
	private String identifyContent;
	//委托单位
	private String wtDept;
	//鉴定结论
	private String jdConclusion;
	
	private String recordNotFind = "false";
	public String getShortInfo() {
		return shortInfo;
	}

	public void setShortInfo(String shortInfo) {
		this.shortInfo = shortInfo;
	}

	public String getFkTime() {
		return fkTime;
	}

	public void setFkTime(String fkTime) {
		this.fkTime = fkTime;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getJdContent() {
		return jdContent;
	}

	public void setJdContent(String jdContent) {
		this.jdContent = jdContent;
	}

	public String getRecordNotFind() {
		return recordNotFind;
	}

	public void setRecordNotFind(String recordNotFind) {
		this.recordNotFind = recordNotFind;
	}

	public String getEventReason() {
		return eventReason;
	}

	public void setEventReason(String eventReason) {
		this.eventReason = eventReason;
	}

	public String getIdentifyContent() {
		return identifyContent;
	}

	public void setIdentifyContent(String identifyContent) {
		this.identifyContent = identifyContent;
	}

	public String getWtDept() {
		return wtDept;
	}

	public void setWtDept(String wtDept) {
		this.wtDept = wtDept;
	}

	public String getJdConclusion() {
		return jdConclusion;
	}

	public void setJdConclusion(String jdConclusion) {
		this.jdConclusion = jdConclusion;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private List recordList = null;
	public String getFileExtFlag() {
		return fileExtFlag;
	}

	public void setFileExtFlag(String fileExtFlag) {
		this.fileExtFlag = fileExtFlag;
	}

	public List getRecordList() {
		return recordList;
	}

	public void setRecordList(List recordList) {
		this.recordList = recordList;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpertName() {
		return expertName;
	}

	public void setExpertName(String expertName) {
		this.expertName = expertName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getConclusion() {
		return conclusion;
	}

	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getReportID() {
		return reportID;
	}

	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	/** 
	 * Method validate
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * Method reset
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
	}
}