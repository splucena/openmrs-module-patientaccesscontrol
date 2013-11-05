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
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.programaccesscontrol.PatientProgramModel;
import org.openmrs.module.programaccesscontrol.RoleProgram;
import org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService;
import org.openmrs.module.programaccesscontrol.api.db.RoleProgramDAO;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.RoleConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link ProgramAccessControlService}.
 */
public class ProgramAccessControlServiceImpl extends BaseOpenmrsService implements ProgramAccessControlService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RoleProgramDAO dao;
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(RoleProgramDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the dao
	 */
	public RoleProgramDAO getDao() {
		return dao;
	}
	
	@Override
	public List<Role> getRoles(Program program) {
		return dao.getRoles(program);
	}
	
	@Override
	public void saveRoleProgram(RoleProgram programPatientAccessControl) {
		dao.saveRoleProgram(programPatientAccessControl);
	}
	
	@Override
	public RoleProgram getRoleProgram(Program program, Role role) {
		return dao.getRoleProgram(program, role);
	}
	
	@Override
	public void deleteRoleProgram(Role role, Program program) {
		dao.deleteRoleProgram(role, program);
	}
	
	@Override
	public void deleteRolePrograms(Role role) {
		dao.deleteRolePrograms(role);
	}
	
	@Override
	public void deleteRolePrograms(Program program) {
		dao.deleteRolePrograms(program);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPrivilege(Patient patient) {
		try {
			if (patient == null) {
				return true;
			}
			
			Set<Role> roles = Context.getUserContext().getAllRoles();
			
			for (Role role : roles) {
				if (hasPrivilege(role, patient)) {
					return true;
				}
			}
			
			return false;
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	private boolean hasPrivilege(Role role, Patient patient) {
		if (role.getRole().equals(RoleConstants.SUPERUSER)) {
			return true;
		}
		boolean enrolled = false;
		for (PatientProgram patientProgram : Context.getProgramWorkflowService().getPatientPrograms(patient, null, null,
		    null, null, null, false)) {
			if (!patientProgram.getActive()) {
				continue;
			}
			enrolled = true;
			if (hasPrivilege(role, patientProgram.getProgram())) {
				return true;
			}
		}
		// not in any programs
		if (!enrolled) {
			return dao.getDefaultRoleProgram(role) != null;
		} else {
			return false;
		}
	}
	
	private boolean hasPrivilege(Role role, Program program) {
		if (role.getRole().equals(RoleConstants.SUPERUSER)) {
			return true;
		}
		RoleProgram fac = getRoleProgram(program, role);
		return fac != null;
	}
	
	@Override
	public List<Program> getPrograms() {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return Context.getProgramWorkflowService().getAllPrograms();
				}
			}
			
			return dao.getPrograms(roles);
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	private boolean canViewPatientsNotInPrograms() {
		try {
			Set<Role> roles = Context.getUserContext().getAllRoles();
			for (Role role : roles) {
				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
					return true;
				}
			}
			
			return !dao.getDefaultRolePrograms(roles).isEmpty();
		}
		catch (Exception e) {
			throw new APIException(e);
		}
	}
	
	/**
	 * @see ProgramAccessControlService#getCountOfPatients(String)
	 */
	@Override
	public Integer getCountOfPatients(String query) {
		List<PatientIdentifierType> emptyList = new Vector<PatientIdentifierType>();
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatients(null, null, emptyList, false, false, getPrograms(),
			    excludePatientNotInPrograms));
		} else {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatients(null, query, emptyList, false, true, getPrograms(),
			    excludePatientNotInPrograms));
		}
	}
	
	/**
	 * @see ProgramAccessControlService#getPatients(String, Integer, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException {
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return dao.getPatients(null, null, Collections.EMPTY_LIST, false, start, length, false, getPrograms(),
			    excludePatientNotInPrograms);
		} else {
			return dao.getPatients(query, null, Collections.EMPTY_LIST, false, start, length, true, getPrograms(),
			    excludePatientNotInPrograms);
		}
	}
	
	/**
	 * @see ProgramAccessControlService#getPatients(String, Integer, Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PatientProgramModel> getPatientPrograms(String query, Integer start, Integer length) throws APIException {
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return dao.getPatientPrograms(null, null, Collections.EMPTY_LIST, false, start, length, false, getPrograms(),
			    excludePatientNotInPrograms);
		} else {
			return dao.getPatientPrograms(query, null, Collections.EMPTY_LIST, false, start, length, true, getPrograms(),
			    excludePatientNotInPrograms);
		}
	}
	
	/**
	 * @see ProgramAccessControlService#getCountOfPatients(String)
	 */
	@Override
	public Integer getCountOfPatientPrograms(String query) {
		List<PatientIdentifierType> emptyList = new Vector<PatientIdentifierType>();
		boolean excludePatientNotInPrograms = !canViewPatientsNotInPrograms();
		if (StringUtils.isEmpty(query)) {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatientPrograms(null, null, emptyList, false, false,
			    getPrograms(), excludePatientNotInPrograms));
		} else {
			return OpenmrsUtil.convertToInteger(dao.getCountOfPatientPrograms(null, query, emptyList, false, true,
			    getPrograms(), excludePatientNotInPrograms));
		}
	}
	
	@Override
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length) throws APIException {
		return dao.getPatients(name, identifier, identifierTypes, matchIdentifierExactly, start, length, true,
		    getPrograms(), !canViewPatientsNotInPrograms());
	}
	
}
