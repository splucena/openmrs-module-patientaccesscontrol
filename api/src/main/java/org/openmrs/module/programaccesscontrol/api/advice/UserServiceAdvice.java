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
package org.openmrs.module.programaccesscontrol.api.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.programaccesscontrol.api.ProgramAccessControlService;
import org.springframework.aop.AfterReturningAdvice;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class UserServiceAdvice implements AfterReturningAdvice {
	
	protected static final Log log = LogFactory.getLog(UserServiceAdvice.class);
	
	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(Object, Method, Object[],
	 *      Object)
	 */
	@Override
	public void afterReturning(Object returnVal, Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("purgeRole")) {
			ProgramAccessControlService svc = Context.getService(ProgramAccessControlService.class);
			Role role = (Role) args[0];
			svc.deleteRolePrograms(role);
		}
		//		else if (method.getName().equals("saveRole")) {
		//			Context.addProxyPrivilege("Manage Program Access Control");
		//			ProgramAccessControlService svc = Context.getService(ProgramAccessControlService.class);
		//			Role role = (Role) returnVal;
		//			for (Program program : Context.getProgramWorkflowService().getAllPrograms()) {
		//				if (role.getRole().equals(RoleConstants.SUPERUSER)) {
		//					continue;
		//				}
		//				if (svc.getProgramPatientAccessControl(program, role) == null) {
		//					RoleProgram accessControl = new RoleProgram();
		//					accessControl.setProgram(program);
		//					accessControl.setRole(role);
		//					accessControl.setCanAdd(false);
		//					accessControl.setCanUpdate(false);
		//					accessControl.setCanView(false);
		//					svc.saveProgramPatientAccessControl(accessControl);
		//				}
		//			}
		//		}
	}
}
