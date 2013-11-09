package org.openmrs.module.programaccesscontrol.api;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.programaccesscontrol.Constants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RoleAccessControlServiceTest extends BaseModuleContextSensitiveTest {
	
	protected static final String INITIAL_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/initialTestData.xml";
	
	protected static final String PROGRAM_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/RoleProgramServiceTest.xml";
	
	protected static final String PATIENT_DATASET_XML = "org/openmrs/module/" + Constants.MODULE_ID
	        + "/include/RolePatientServiceTest.xml";
	
	public RoleAccessControlServiceTest() {
		super();
		//runtimeProperties.put(Environment.SHOW_SQL, "true");
	}
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(RoleAccessControlService.class));
	}
	
	private RoleAccessControlService getProgramAccessControlService() {
		return Context.getService(RoleAccessControlService.class);
	}
	
	@Before
	public void beforeTest() throws Exception {
		executeDataSet(INITIAL_DATASET_XML);
		executeDataSet(PROGRAM_DATASET_XML);
		executeDataSet(PATIENT_DATASET_XML);
	}
	
	@Override
	@After
	public void deleteAllData() throws Exception {
		Context.addProxyPrivilege("View Users");
		super.deleteAllData();
		Context.removeProxyPrivilege("View Users");
	}
	
	/**
	 * @see RoleAccessControlService#getCountOfPatients(String)
	 * @verifies return the right count when a patient has multiple matching person names
	 */
	@Test
	public void getCountOfPatients_shouldReturnTheRightCountWhenAPatientHasMultipleMatchingPersonNames() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getProgramAccessControlService().getCountOfPatients("").intValue());
		Assert.assertEquals(2, getProgramAccessControlService().getCountOfPatients("Test").intValue());
		
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getProgramAccessControlService().getCountOfPatients("").intValue());
		Assert.assertEquals(2, getProgramAccessControlService().getCountOfPatients("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(4, getProgramAccessControlService().getCountOfPatients("").intValue());
		Assert.assertEquals(4, getProgramAccessControlService().getCountOfPatients("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#getCountOfPatients(String)
	 * @verifies return the right count of patients with a matching name
	 */
	@Test
	public void getCountOfPatients_shouldReturnTheRightCountOfPatientsWithAMatchingName() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(1, getProgramAccessControlService().getCountOfPatients("Horn").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(0, getProgramAccessControlService().getCountOfPatients("Johnny").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(1, getProgramAccessControlService().getCountOfPatients("Johnny").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#getPatients(String,Integer,Integer)
	 * @verifies find a patients with a matching name
	 */
	@Test
	public void getPatients_shouldFindAPatientsWithAMatchingName() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getProgramAccessControlService().getPatients("", null, 10).size());
		Assert.assertEquals(1, getProgramAccessControlService().getPatients("Horn", null, 10).size());
		Assert.assertEquals(0, getProgramAccessControlService().getPatients("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getProgramAccessControlService().getPatients("", null, 10).size());
		Assert.assertEquals(1, getProgramAccessControlService().getPatients("Horn", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(4, getProgramAccessControlService().getPatients("", null, 10).size());
		Assert.assertEquals(1, getProgramAccessControlService().getPatients("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#hasPrivilege(Patient)
	 * @verifies authorize if authenticated user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAuthenticatedUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(2)));
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#hasPrivilege(Patient)
	 * @verifies authorize if anonymous user has view privilege for the specified patient
	 */
	@Test
	public void hasPrivilege_shouldAuthorizeIfAnonymousUserHasViewPrivilegeForTheSpecifiedPatient() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertTrue(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(7)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#hasPrivilege(Patient)
	 * @verifies not authorize if authenticated user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAuthenticatedUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertFalse(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Assert.assertFalse(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(8)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#hasPrivilege(Patient)
	 * @verifies not authorize if anonymous user does not have view privilege for the specified
	 */
	@Test
	public void hasPrivilege_shouldNotAuthorizeIfAnonymousUserDoesNotHaveViewPrivilegeForTheSpecified() throws Exception {
		Context.logout();
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertFalse(getProgramAccessControlService().hasPrivilege(Context.getPatientService().getPatient(6)));
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#getCountOfPatientPrograms(String)
	 * @verifies return the right count when a patient is enrolled in multiple programs
	 */
	@Test
	public void getCountOfPatientPrograms_shouldReturnTheRightCountWhenAPatientIsEnrolledInMultiplePrograms()
	    throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getProgramAccessControlService().getCountOfPatientPrograms("").intValue());
		Assert.assertEquals(2, getProgramAccessControlService().getCountOfPatientPrograms("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(3, getProgramAccessControlService().getCountOfPatientPrograms("").intValue());
		Assert.assertEquals(3, getProgramAccessControlService().getCountOfPatientPrograms("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(5, getProgramAccessControlService().getCountOfPatientPrograms("").intValue());
		Assert.assertEquals(5, getProgramAccessControlService().getCountOfPatientPrograms("Test").intValue());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
	
	/**
	 * @see RoleAccessControlService#getPatientPrograms(String,Integer,Integer)
	 * @verifies find patients with a matching name and the authenticated user has access to
	 */
	@Test
	public void getPatientPrograms_shouldFindPatientsWithAMatchingNameAndTheAuthenticatedUserHasAccessTo() throws Exception {
		Context.logout();
		Context.authenticate("thirdaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(2, getProgramAccessControlService().getPatientPrograms("", null, 10).size());
		Assert.assertEquals(1, getProgramAccessControlService().getPatientPrograms("Horn", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("secondaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(3, getProgramAccessControlService().getPatientPrograms("", null, 10).size());
		Assert.assertEquals(2, getProgramAccessControlService().getPatientPrograms("Horn", null, 10).size());
		Assert.assertEquals(0, getProgramAccessControlService().getPatientPrograms("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
		Context.authenticate("firstaccount", "test");
		Context.addProxyPrivilege("View Patients");
		Context.addProxyPrivilege("View Patient Programs");
		Assert.assertEquals(5, getProgramAccessControlService().getPatientPrograms("", null, 10).size());
		Assert.assertEquals(1, getProgramAccessControlService().getPatientPrograms("Johnny", null, 10).size());
		Context.removeProxyPrivilege("View Patients");
		Context.removeProxyPrivilege("View Patient Programs");
		
		Context.logout();
	}
}
