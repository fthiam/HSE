package webservice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import comon.MainServiceTest;

public class GroupServiceTest extends MainServiceTest{

	String groupId = "";
	String shoppingListId = "";

	public GroupServiceTest() {
	     super("memberTest", "test@mail.fr", "root");
	}
	// ======================================
    // =           initialization  		    =
    // ======================================
	@Before
	public void CreateAGroup ()  {
		//if we need to test a shopping list service we first need a group
		this.groupId = createNewGroup("GrouTest");
	}
	// ======================================
    // =    inviteMemberInGroup		       =
    // ======================================
    /**
     * Test : create and delete an item in a list
     */	
	@Test
    public void inviteMemberInGroup() throws Exception {
		MainServiceTest secondLog = new MainServiceTest("memberTest2", "test2@mail.fr", "root");
		//new member has been created, try to invite him...
		String inviteMember = "GroupService/invitMember?emailAdress=test2@mail.fr"
													  +"&groupId="+this.groupId; 
		try {
			String response = this.getResponse(inviteMember);
			assertEquals("Member for mail adress :test2@mail.fr has been invited." , response);
		}catch(RuntimeException re){
			//To clean this user
			secondLog.deleteMember("root");
			fail("Response cannot be retreive");
		}catch(AssertionError ae){
			//To clean this user
			secondLog.deleteMember("root");
			fail("Member test2 couldn't be invited...");
		}
		//To clean this user
		secondLog.deleteMember("root");
    }
	// ======================================
    // =           clean		   		    =
    // ======================================
	/**
     * We simply need to delete user test account,
     * this way the group is deleted because it had just one user
     * shoppingLists, items are deleted too.
     */	
	@After
	public void clean() {
		this.deleteMember("root");
	}
}
