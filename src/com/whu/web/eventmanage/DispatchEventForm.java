/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.eventmanage;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import java.util.List;


/** 
 * MyEclipse Struts
 * Creation date: 01-14-2015
 * 
 * XDoclet definition:
 * @struts.form name="dispatchEventForm"
 */
public class DispatchEventForm extends ActionForm {
	/*
	 * Generated fields
	 */

	/** officer property */
	private String officer;

	/** eventId property */
	private String eventId;
	
	private String recordNotFind = "false";
	private String operation = null;
	private List recordList = null;
	public String getRecordNotFind() {
		return recordNotFind;
	}

	public void setRecordNotFind(String recordNotFind) {
		this.recordNotFind = recordNotFind;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public List getRecordList() {
		return recordList;
	}

	public void setRecordList(List recordList) {
		this.recordList = recordList;
	}

	/*
	 * Generated Methods
	 */

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

	/** 
	 * Returns the officer.
	 * @return String
	 */
	public String getOfficer() {
		return officer;
	}

	/** 
	 * Set the officer.
	 * @param officer The officer to set
	 */
	public void setOfficer(String officer) {
		this.officer = officer;
	}

	/** 
	 * Returns the eventId.
	 * @return String
	 */
	public String getEventId() {
		return eventId;
	}

	/** 
	 * Set the eventId.
	 * @param eventId The eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
}