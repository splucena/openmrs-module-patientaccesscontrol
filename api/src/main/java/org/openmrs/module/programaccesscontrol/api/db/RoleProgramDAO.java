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
package org.openmrs.module.programaccesscontrol.api.db;

import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.Role;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.programaccesscontrol.PatientProgramModel;
import org.openmrs.module.programaccesscontrol.RoleProgram;
import org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService;

/**
 * Database methods for {@link ProgramAccessControlService}.
 */
public interface RoleProgramDAO {
	
	/**
	 * @see ProgramAccessControlService#saveRoleProgram(RoleProgram)
	 */
	public RoleProgram saveRoleProgram(RoleProgram roleProgram) throws DAOException;
	
	/**
	 * Get role program by internal program patient identifier
	 * 
	 * @param roleProgramId <code>Integer</code> internal identifier for requested Role Program *
	 * @return requested <code>RoleProgram</code>
	 * @throws DAOException
	 */
	public RoleProgram getRoleProgram(Integer roleProgramId) throws DAOException;
	
	/**
	 * Get all role programs
	 * 
	 * @return the list of all role programs
	 * @throws DAOException
	 */
	public List<RoleProgram> getAllRolePrograms() throws DAOException;
	
	/**
	 * Get the roles for the given program
	 * 
	 * @param program the program to get
	 * @return the list of role with the given program
	 * @throws DAOException
	 */
	public List<Role> getRoles(Program program) throws DAOException;
	
	/**
	 * Get role program by program and role
	 * 
	 * @param program the program to get
	 * @param role the role to get
	 * @return the role program with the program and role given
	 * @throws DAOException
	 */
	public RoleProgram getRoleProgram(Program program, Role role) throws DAOException;
	
	/**
	 * Gets default role program by role
	 * 
	 * @param role the role to get
	 * @return the default role program for the role given
	 * @throws DAOException
	 */
	public RoleProgram getDefaultRoleProgram(Role role) throws DAOException;
	
	/**
	 * Gets default role programs by role
	 * 
	 * @param role the role to get
	 * @return the default role programs for the role given
	 * @throws DAOException
	 */
	public List<RoleProgram> getDefaultRolePrograms(Set<Role> role) throws DAOException;
	
	/**
	 * Delete role programs from database. This is included for troubleshooting and low-level system
	 * administration. This should only be called when the Program is deleted, which ideally should
	 * never happen.
	 * 
	 * @param roleProgram RoleProgram to delete
	 * @throws DAOException
	 */
	public void deleteRolePrograms(Program program) throws DAOException;
	
	/**
	 * @see org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService#deleteRolePrograms(Role)
	 */
	public void deleteRolePrograms(Role role) throws DAOException;
	
	/**
	 * @see org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService#deleteRolePrograms(Role,Program)
	 */
	public void deleteRoleProgram(Role role, Program program);
	
	/**
	 * Retrieves the list of programs the roles have access to
	 * 
	 * @param roles set of roles
	 * @throws DAOException
	 */
	public List<Program> getPrograms(Set<Role> roles) throws DAOException;
	
	/**
	 * @param searchOnNamesOrIdentifiers specifies if the logic should find patients that match the
	 *            name or identifier otherwise find patients that match both the name and identifier
	 * @see ProgramAccessControlService#getCountOfPatients(String)
	 */
	public Long getCountOfPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                               boolean matchIdentifierExactly, boolean searchOnNamesOrIdentifiers,
	                               List<Program> includePrograms, boolean excludePatientNotInPrograms);
	
	/**
	 * @param searchOnNamesOrIdentifiers specifies if the logic should find patients that match the
	 *            name or identifier otherwise find patients that match both the name and identifier
	 * @should escape percentage character in name phrase
	 * @should escape underscore character in name phrase
	 * @should escape an asterix character in name phrase
	 * @should escape percentage character in identifier phrase
	 * @should escape underscore character in identifier phrase
	 * @should escape an asterix character in identifier phrase
	 * @should get patients with a matching identifier and type
	 */
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                 boolean matchIdentifierExactly, Integer start, Integer length,
	                                 boolean searchOnNamesOrIdentifiers, List<Program> includePrograms,
	                                 boolean excludePatientNotInPrograms) throws DAOException;
	
	/**
	 * @see org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService#getPatientPrograms(String,
	 *      Integer, Integer)
	 */
	public List<PatientProgramModel> getPatientPrograms(String name, String identifier,
	                                             List<PatientIdentifierType> identifierTypes,
	                                             boolean matchIdentifierExactly, Integer start, Integer length,
	                                             boolean searchOnNamesOrIdentifiers, List<Program> includePrograms,
	                                             boolean excludePatientNotInPrograms) throws DAOException;
	
	/**
	 * @see org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService#getCountOfPatientPrograms(String,
	 *      Integer, Integer)
	 */
	public Long getCountOfPatientPrograms(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	                                      boolean matchIdentifierExactly, boolean searchOnNamesOrIdentifiers,
	                                      List<Program> includePrograms, boolean excludePatientNotInPrograms);
	
}
