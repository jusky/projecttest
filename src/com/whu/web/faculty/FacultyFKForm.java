/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.faculty;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;


import java.util.List;
/** 
 * MyEclipse Struts
 * Creation date: 01-20-2015
 * 
 * XDoclet definition:
 * @struts.form name="facultyFKForm"
 */
public class FacultyFKForm extends ValidatorForm {
	/*
	 * Generated fields
	 */

	/** id property */
	private String id;
	private List recordList;
	private String recordNotFind = "false";
	private String serialNum;
	private String beReportName;
	private String advice;
	
	public List getRecordList() {
		return recordList;
	}
	public void  setRecordList(List recordList) {
		this.recordList = recordList;
	}
	public String getRecordNotFind() {
		return recordNotFind;
	}
	public void setRecordNotFind( String recordNotFind) {
		this.recordNotFind = recordNotFind;
	}
	/*
	 * Generated Methods
	 */


	/** 
	 * Method reset
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
	}

	/** 
	 * Returns the id.
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/** 
	 * Set the id.
	 * @param id The id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getBeReportName() {
		return beReportName;
	}
	public void setBeReportName(String beReportName) {
		this.beReportName = beReportName;
	}
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
}