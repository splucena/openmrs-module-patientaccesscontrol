/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.programaccesscontrol.api.impl;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.programaccesscontrol.PatientProgramModel;
import org.openmrs.module.programaccesscontrol.api.RoleAccessControlService;
import org.openmrs.module.programaccesscontrol.api.RolePatientService;
import org.openmrs.module.programaccesscontrol.api.RoleProgramService;
import org.openmrs.module.programaccesscontrol.api.db.RoleAccessControlDAO;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link RoleAccessControlService}.
 */
public class RoleAccessControlServiceImpl extends BaseOpenmrsService implements RoleAccessControlService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RoleAccessControlDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(RoleAccessControlDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public RoleAccessControlDAO getDao() {
		return dao;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient) {
		return Context.getService(RoleProgramService.class).hasPrivilege(patient)
		        || Context.getService(RolePatientService.class).hasPrivilege(patient);
	}
	
	public List<Program> getPrograms() {
		return Context.getService(RoleProgramService.class).getPrograms();
	}
	
	public List<Integer> getExcludePatients() {
		return Context.getService(RolePatientService.class).getExcludedPatients();
	}
	
	private boolean canViewPatientsNotInPrograms() {
		return Context.getService(RoleProgramService.class).canViewPatientsNotInPrograms();
	}
	
	/**
	 * @see RoleAccessControlService#getCountOfPatients(String)
	 */
	@Override
	public Integer getCountOfPatients(String query) {
		List<PatientIdentifierType> emptyList = new Vector<PatientIdentifierType>();
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatients(null, null, emptyList, false, false, getPrograms(),
			    excludePatientNotInPrograms, getExcludePatients()));
		} else {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatients(null, query, emptyList, false, true, getPrograms(),
			    excludePatientNotInPrograms, getExcludePatients()));
		}
	}
	
	/**
	 * @see RoleAccessControlService#getPatients(String, Integer, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException {
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return dao.getPatients(null, null, Collections.EMPTY_LIST, false, start, length, false, getPrograms(),
			    excludePatientNotInPrograms, getExcludePatients());
		} else {
			return dao.getPatients(query, null, Collections.EMPTY_LIST, false, start, length, true, getPrograms(),
			    excludePatientNotInPrograms, getExcludePatients());
		}
	}
	
	/**
	 * @see RoleAccessControlService#getPatients(String, Integer, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PatientProgramModel> getPatientPrograms(String query, Integer start, Integer length) throws APIException {
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return dao.getPatientPrograms(null, null, Collections.EMPTY_LIST, false, start, length, false, getPrograms(),
			    excludePatientNotInPrograms, getExcludePatients());
		} else {
			return dao.getPatientPrograms(query, null, Collections.EMPTY_LIST, false, start, length, true, getPrograms(),
			    excludePatientNotInPrograms, getExcludePatients());
		}
	}
	
	/**
	 * @see RoleAccessControlService#getCountOfPatients(String)
	 */
	@Override
	public Integer getCountOfPatientPrograms(String query) {
		List<PatientIdentifierType> emptyList = new Vector<PatientIdentifierType>();
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatientPrograms(null, null, emptyList, false, false,
			    getPrograms(), excludePatientNotInPrograms, getExcludePatients()));
		} else {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatientPrograms(null, query, emptyList, false, true,
			    getPrograms(), excludePatientNotInPrograms, getExcludePatients()));
		}
	}
	
	@Override
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length) throws APIException {
		return dao.getPatients(name, identifier, identifierTypes, matchIdentifierExactly, start, length, true,
		    getPrograms(), !canViewPatientsNotInPrograms(), getExcludePatients());
	}
	
}
