/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.event;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * MyEclipse Struts
 * Creation date: 03-12-2014
 * 
 * XDoclet definition:
 * @struts.form name="cljdManageForm"
 */
public class CljdManageForm extends ActionForm {
	/*
	 * Generated Methods
	 */
	private String handleName;
	private String serialNum;
	private String conference;
	private String handleBeginTime;
	private String handleEndTime;
	private String recordNotFind = "false";
	private List recordList = null;
	public String getConference() {
		return conference;
	}

	public void setConference(String conference) {
		this.conference = conference;
	}

	public String getHandleName() {
		return handleName;
	}

	public void setHandleName(String handleName) {
		this.handleName = handleName;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getHandleBeginTime() {
		return handleBeginTime;
	}

	public void setHandleBeginTime(String handleBeginTime) {
		this.handleBeginTime = handleBeginTime;
	}

	public String getHandleEndTime() {
		return handleEndTime;
	}

	public void setHandleEndTime(String handleEndTime) {
		this.handleEndTime = handleEndTime;
	}

	public String getRecordNotFind() {
		return recordNotFind;
	}

	public void setRecordNotFind(String recordNotFind) {
		this.recordNotFind = recordNotFind;
	}

	public List getRecordList() {
		return recordList;
	}

	public void setRecordList(List recordList) {
		this.recordList = recordList;
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