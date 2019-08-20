package webservice;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

import comon.MainServiceTest;

public class ShoppingListServiceTest extends MainServiceTest{

	String groupId = "";
	String shoppingListId = "";

	public ShoppingListServiceTest() {
	     super("memberTest", "test@mail.fr", "root");
	}
	// ======================================
    // =           initialization  		    =
    // ======================================
	@Before
	public void CreateAGroup ()  {
		//if we need to test a shopping list service we first need a group
		this.groupId = createNewGroup("GrouTest");
		//and a shopping list
		this.shoppingListId = createNewList("testList", this.groupId);
	}
	// ======================================
    // =    createNewShoppingListTest       =
    // ======================================
    /**
     * Test : create and delete an item in a list
     */	
	@Test
    public void createDeleteItemInList() throws Exception {
		String itemName = "itemTest";
		String itemId = createItem(itemName);
		deleteItem(itemId, itemName);
    }
	// ======================================
    // =    updateItemInList		        =
    // ======================================
    /**
     * Test : update an item in a list
     */	
	@Test
    public void updateItemInList() throws Exception {
		String itemName = "itemTest2";
		float price = 10;
		String itemId = createItem(itemName);
		updateItem(itemId, price);
		deleteItem(itemId, itemName);
    }
	// ======================================
    // =    buyItemInList			        =
    // ======================================
    /**
     * Test : buy an item in List
     */	
	@Test
    public void buyItemInList() throws Exception {
		String itemName = "itemTest2";
		float price = 10;
		String itemId = createItem(itemName);
		buyItem(itemId, price, itemName);
		deleteItem(itemId, itemName);
    }
	// ======================================
    // =    	buyItem			        =
    // ======================================
    /**
     * Will try to buy an existing item
     */	
	private void buyItem(String itemId, float price, String itName) {
		String buyItemReq = "ShoppingListService/buyItem?itemId="+ itemId
														   +"&itemPrice="+price
														   +"&shoppingListId="+this.shoppingListId; 
		try {
			String response = this.getResponse(buyItemReq);
			assertEquals("Item has not been updated...","Item "+ itName + " has been buyed" , response);
		}catch(RuntimeException re){
			fail("Response cannot be retreive");
		}
	}
	// ======================================
    // =    	updateItem			        =
    // ======================================
    /**
     * Will try to update an existing item
     * @param itemId 
     */	
	private void updateItem(String itemId, float price) {
		LinkedList<String> concernedMembers = new LinkedList<String>();
		concernedMembers.add(memberId);
		String concernedMemberJsonStr = new Gson().toJson(concernedMembers);
		String encodeJsonConernedMembers = jsonEncode(concernedMemberJsonStr);
		String updateItemReq = "ShoppingListService/updateItem?itemId="+ itemId
														   +"&ConcernedMembersIds="+encodeJsonConernedMembers
														   +"&price="+price
														   +"&shoppingListId="+this.shoppingListId; 
		try {
			String response = this.getResponse(updateItemReq);
			assertEquals("Item has not been updated...","Item updated" , response);
		}catch(RuntimeException re){
			fail("Response cannot be retreive");
		}
	}
	// ======================================
    // =    	createItem			        =
    // ======================================
    /**
     * Will try to create an item
     * @param itemName 
     */	
	private String createItem(String itemName){
		String createItem = "ShoppingListService/addItem?itemName="+ itemName
																   +"&description=testItem" 
																   +"&shoppingListId="+this.shoppingListId; 
		try {
			String response = this.getResponse(createItem);
			assertNotNull("Item has not been created...", response);
			return response;
		}catch(RuntimeException re){
			fail("Response cannot be retreive");
		}
		return "";
	}
	// ======================================
    // =   		 deleteItem			       =
    // ======================================
    /**
     * Will try to delete an existing item
     */	
	private void deleteItem(String itemId, String itemName) {
		String expectedResponse = "Item "+ itemName +" has been removed";
		String deleteItem = "ShoppingListService/removeItem?itemId="+ itemId
				   										  +"&shoppingListId="+this.shoppingListId; 
		try {
			String response = this.getResponse(deleteItem);
			assertEquals("Something went wrong with delete Item",expectedResponse, response);
		}catch(RuntimeException re){
			fail("Response cannot be retreive");
		}
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
		deleteMember("root");
	}
}
