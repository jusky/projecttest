/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package com.whu.web.credit;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * MyEclipse Struts
 * Creation date: 12-05-2014
 * 
 * XDoclet definition:
 * @struts.form caption="punishManageForm"
 */
public class PunishManageForm extends ActionForm {
	/*
	 * Generated fields
	 */

	/** id property */
	private String id;

	/** caption property */
	private String caption;

	
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

	/** 
	 * Returns the caption.
	 * @return String
	 */
	public String getCaption() {
		return caption;
	}

	/** 
	 * Set the caption.
	 * @param caption The Caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
}