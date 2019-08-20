package comon;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.NewCookie;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.homesharingexpenses.comon.dto.GroupIdDTO;
import com.homesharingexpenses.comon.dto.MemberDTO;
import com.homesharingexpenses.comon.dto.OutgoingDTO;
import com.homesharingexpenses.comon.dto.ShoppingListDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
* Here we initialize the necessary to test all services
* This main test will be responsible for:
* 	Member creation
* 	Member Connection 
*/
public class MainServiceTest {
	private static String URL_BASE = "http://localhost:8080/HomeSharingExpenses/HSEServices/";
	private String createMemberOk = "Member has been created";
	private String groupCreated = "Group has been created";
	protected String memberId = "";
	protected String memberName = "";
	protected String memberMail = "";
	protected String memberPass = "";
	
	String _cookieSession = "";
	
	public MainServiceTest(String memberName, String memberMail, String pass) {
		this.memberName = memberName;
		this.memberMail = memberMail;
		this.memberPass = pass;
		//operation needed in every tests scenarios..
		//also tests the member creation  
		login();
	}
	
	// ======================================
    // =       login				   	    =
    // ======================================
	private void login() {
		//First create a member
		MemberDTO memberDTO = new MemberDTO(memberName, "", memberMail, memberPass, "");
		String memberJson = new Gson().toJson(memberDTO);
		String createNewMemberRequest = "MemberService/createMember?member=";
		try {
			String response = this.getResponse(createNewMemberRequest+jsonEncode(memberJson));
			//We try to connect here because the only reason why test would fail is that member already exists
			//If connection is not established we can't delete this member...
			connectToWebService();
			assertEquals("Something went wrong with member creation",createMemberOk, response);
		}catch(RuntimeException re){
			fail("Response cannot be retreive from member creation : " + re.getMessage());
		}
	}
	// ======================================
    // =       createNewGroup		   	    =
    // ======================================
	protected String createNewGroup(String groupName) {
		String createNewGroupRequest = "GroupService/createGroup?groupName=" + groupName; 
		try {
			String response = this.getResponse(createNewGroupRequest);
			assertEquals("Something went wrong with group creation",groupCreated, response);
			return getGroupId(groupName);
		}catch(RuntimeException re){
			  fail("Response cannot be retreive from group creation : " + re.getMessage());
		}
		return "";
	}
	// ======================================
    // =       getGroupId			   	    =
    // ======================================
	private String getGroupId(String groupName) {
		String getGroupsRequest = "MemberService/getGroups"; 
		try {
			String response = this.getResponse(getGroupsRequest);
			Type listOfGroupsIds = new TypeToken<LinkedList<GroupIdDTO>>(){}.getType();
			List<GroupIdDTO> groupsIds = new Gson().fromJson(response, listOfGroupsIds);
			for(GroupIdDTO gid : groupsIds){
				if(gid.getName().equals(groupName))
					return gid.getId();
			}
			
		}catch(RuntimeException re){
			  fail("Response cannot be retreive from group creation : " + re.getMessage());
		}
		fail("Created group cannot be find...");
		return "";
	}
	// ======================================
    // =       createNewList		   	    =
    // ======================================
	protected String createNewList(String listName, String groupId) {
		String createNewListRequest = "GroupService/createList?listName="+listName
																+"&groupId=" + groupId; 
		try {
			String response = this.getResponse(createNewListRequest);
			assertEquals("Something went wrong with list creation","Shopping list testList: has been created", response);
			return getShoppingListId(listName, groupId);
		}catch(RuntimeException re){
			  fail("Response cannot be retreive");
		}
		return "";
	}
	// ======================================
    // =       getShoppingListId	   	    =
    // ======================================
	private String getShoppingListId(String listName, String groupId) {
		String getGroupsRequest = "GroupService/getShoppingLists?groupId="+ groupId; 
		try {
			String response = this.getResponse(getGroupsRequest);
			Type listOfShoppLists = new TypeToken<LinkedList<ShoppingListDTO>>(){}.getType();
			List<ShoppingListDTO> shoppListIds = new Gson().fromJson(response, listOfShoppLists);
			for(ShoppingListDTO shld : shoppListIds){
				if(shld.getName().equals(listName))
					return shld.getId();
			}
		}catch(RuntimeException re){
			  fail("Response cannot be retreive from get shopping list : " + re.getMessage());
		}
		fail("Created List cannot be find...");
		return "";
	}
	// ======================================
    // =       connectToWebService   	    =
    // ======================================
	protected void connectToWebService() {
		Client client =  Client.create();
		WebResource webResource = client.resource(URL_BASE + "MemberService/login?email="+ memberMail +"&pass=" +memberPass);
		//Session attribute
		javax.ws.rs.core.Cookie cookie=new javax.ws.rs.core.Cookie("hellocookie", "Hello Cookie");
		ClientResponse response = webResource.cookie(cookie)
											 .accept("application/json").get(ClientResponse.class);
		List<NewCookie> cookies = response.getCookies();
		//to set memberId
		response.bufferEntity();
		String stringResponse = response.getEntity(String.class);
		this.memberId = stringResponse.replaceAll("\"","");

		for(NewCookie nc : cookies){
			if(nc.getName().equals("JSESSIONID"))
				set_cookieSession((String)nc.getValue());
		}
	}
	// ======================================
    // =       deleteMember				   	=
    // ======================================
	public void deleteMember(String pass) {
		String createNewListRequest = "MemberService/removeAccount?pass=" + "root"; 
		try {
			String response = getResponse(createNewListRequest);
		}catch(RuntimeException re){
			  fail("Response cannot be retreive");
		}
	}
	// ======================================
    // =       getClientResponse   	    	=
    // ======================================
	public String getResponse(String serviceReq){
		Client client =  Client.create();
		WebResource webResource = client.resource(URL_BASE + serviceReq);
		//Session attribute 
		javax.ws.rs.core.Cookie cookie = new javax.ws.rs.core.Cookie("JSESSIONID", _cookieSession);
		ClientResponse response = webResource.cookie(cookie)
											 .accept("application/json")
											 .get(ClientResponse.class);
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		
		response.bufferEntity();
		String stringResponse = response.getEntity(String.class);
		String resp = stringResponse.replaceAll("\"","");
		
		return resp;
	}
	
	public void set_cookieSession(String cookieSession) {
		_cookieSession = cookieSession;
	}
	// ======================================
    // =       jsonEncode		   	    	=
    // ======================================
	public String jsonEncode(String jsonString){
		String encodedMember = "";
		try {
			encodedMember = java.net.URLEncoder.encode(jsonString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedMember;
	}
	
}
