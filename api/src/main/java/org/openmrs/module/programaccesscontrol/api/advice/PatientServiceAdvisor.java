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

import java.util.Iterator;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.programaccesscontrol.Constants;
import org.openmrs.module.programaccesscontrol.api.RoleAccessControlService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.aop.Advisor;

/**
 * AOP class used to intercept and log calls to FormService methods
 */
public class PatientServiceAdvisor implements Advisor {
	
	protected static final Log log = LogFactory.getLog(PatientServiceAdvisor.class);
	
	@Override
	public Advice getAdvice() {
		return new PatientServiceAroundAdvice();
	}
	
	@Override
	public boolean isPerInstance() {
		return false;
	}
	
	private class PatientServiceAroundAdvice implements MethodInterceptor {
		
		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String methodName = invocation.getMethod().getName();
			if (methodName.equals("getCountOfPatients")) {
				return Context.getService(RoleAccessControlService.class).getCountOfPatients(
				    (String) invocation.getArguments()[0]);
			} else if (methodName.equals("getPatients")
			        && (invocation.getArguments().length == 3 || invocation.getArguments().length == 6)) {
				int length = invocation.getArguments().length;
				Object startArg = invocation.getArguments()[length - 2];
				Object lengthArg = invocation.getArguments()[length - 1];
				if ((startArg != null && startArg instanceof Integer) || (lengthArg != null && lengthArg instanceof Integer)) {
					if (length == 3) {
						return Context.getService(RoleAccessControlService.class).getPatients(
						    (String) invocation.getArguments()[0], (Integer) invocation.getArguments()[1],
						    (Integer) invocation.getArguments()[2]);
					} else {
						return Context.getService(RoleAccessControlService.class).getPatients(
						    (String) invocation.getArguments()[0], (String) invocation.getArguments()[1],
						    (List<PatientIdentifierType>) invocation.getArguments()[2],
						    (Boolean) invocation.getArguments()[3], (Integer) invocation.getArguments()[4],
						    (Integer) invocation.getArguments()[5]);
					}
				}
			}
			
			Object o = invocation.proceed();
			
			if (methodName.startsWith("getPatients") || methodName.equals("findPatients")) {
				List<Patient> patients = (List<Patient>) o;
				Iterator<Patient> i = patients.iterator();
				RoleAccessControlService svc = Context.getService(RoleAccessControlService.class);
				while (i.hasNext()) {
					Patient patient = i.next();
					if (!svc.hasPrivilege(patient)) {
						i.remove();
					}
				}
			} else if (methodName.startsWith("getPatient") && o instanceof Patient) {
				RoleAccessControlService svc = Context.getService(RoleAccessControlService.class);
				Patient patient = (Patient) o;
				if (!svc.hasPrivilege(patient)) {
					throw new APIAuthenticationException(OpenmrsUtil.getMessage(Constants.MODULE_ID + ".privilegeRequired",
					    "View"));
				}
			}
			
			return o;
		}
	}
	
}
