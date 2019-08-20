package com.homesharingexpenses.webservices.memberservice;

import com.homesharingexpenses.comon.dto.GroupIdDTO;
import com.homesharingexpenses.comon.dto.MemberDTO;
import com.homesharingexpenses.comon.exception.DuplicateMemberException;
import com.homesharingexpenses.comon.exception.ObjectNotFoundException;
import com.homesharingexpenses.domain.group.Group;
import com.homesharingexpenses.domain.group.member.GroupMember;
import com.homesharingexpenses.domain.member.Member;
import com.homesharingexpenses.domain.member.invitation.Invitation;
import com.homesharingexpenses.persistence.DataAccessAllObjects;
import com.homesharingexpenses.webservices.groupservice.GroupService;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONException;

import com.google.gson.Gson;


/**
 * The service responsible for member's management
 */
@Path("/MemberService")
public class MemberService {
	@Context HttpServletRequest _request;
	
	public MemberService(){
		
	}	
	// ======================================
    // =           createMember    		    =
    // ======================================
	/**
     * Create a new member.
     * @param Member member description (MemberDTO class)
     * @return Json : request status
     */
	@GET
	@Path("/createMember")
	public String createMember(@QueryParam("member") String memberJsonString) throws JSONException {
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 		
		Gson gsonParser = new Gson(); 
		MemberDTO memberDTO = gsonParser.fromJson(memberJsonString, MemberDTO.class);
		try{
			if(checkMemberValues(memberDTO) == ""){
				Member member = new Member(memberDTO.getName(), memberDTO.getForename(),
											memberDTO.getEmailAdress(), memberDTO.getPassWord());
				dao_A.persistMember(member);
				return new Gson().toJson("Member has been created"); 
			}
			else 
				return new Gson().toJson("Account creation failed : "+"Member informations are incorect");
		}catch(DuplicateMemberException DME){
			return new Gson().toJson("Account creation failed : "+DME.getMessage());
		}
	}

	// ======================================
    // =           login    			    =
    // ======================================
	/**
     * Log member in application.
     * @param email : member's email
     * @param pass : member's password
     * @return Json : request status
     */
	@GET
	@Path("/login")
	@Produces("application/json")
	public String login(@QueryParam("email") String email, @QueryParam("pass") String pass) throws JSONException {
       
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 		
		try{
			Member member = dao_A.findMemberByEmailAdress(email);
			if (member.checkPassword(pass)){
				this._request.getSession().setAttribute("memberId", member.getId());
				return new Gson().toJson(member.getId());
			}
			else{
				return new Gson().toJson("Authentification failed : Password incorrect");
			}
			
		}catch(ObjectNotFoundException ONFE){
			return new Gson().toJson("Authentification failed : Wrong email adress");
		}
	}
	// ======================================
    // =           removeAccount		    =
    // ======================================
	/**
     * Remove the current account.
     * @param pass : member's password
     * @return Json : request status
     */
	@GET
	@Path("/removeAccount")
	@Produces("application/json")
	public String removeAccount(@QueryParam("pass") String pass) throws JSONException {
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		try{
			Member member = dao_A.findMemberById(memberId);
			List<Group> groups = member.getGroups();
			for(Group gp : groups){
				this.leaveGroup(gp.getId());
			}
			
			if (member.checkPassword(pass)){
				this._request.getSession().removeAttribute("memberId");
				dao_A.removeMember(member);
				return new Gson().toJson("");
			}
			else{
				return new Gson().toJson("Password incorrect");
			}
			
		}catch(ObjectNotFoundException ONFE){
			return new Gson().toJson("Authentification failed : Wrong email adress");
		}
	}
	
	// ======================================
    // =           logOff    		   	    =
    // ======================================
	/**
     * Log off member.
     * @return Json : request status
     * removes the current account in session
     */
	@GET
	@Path("/logOff")
	public void logOff() throws JSONException {
		this._request.getSession().removeAttribute("memberId");
	}
	
	// ======================================
    // =           getMemberName   		    =
    // ======================================
	/**
     * Get current member name.
     * @param request : session request
     * @return Json : member name or "not connected"...
     */
	@GET
	@Path("/getMemberName")
	@Produces("application/json")
	public String getMemberName(@Context HttpServletRequest request) throws JSONException {
		this._request = request;
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 		
		try{
			Member member = dao_A.findMemberById(memberId);
			return new Gson().toJson(member.getName());
			
		}catch(ObjectNotFoundException ONFE){
			return new Gson().toJson("Not connected");
		}
	}
	// ======================================
    // =           getGroups    		    =
    // ======================================
	/**
     * Get member groups.
     * @return Json : list of group ids
     */
	@GET
	@Path("/getGroups")
	@Produces("application/json")
	public String getGroups() throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		List<GroupIdDTO> groupIS = new LinkedList<GroupIdDTO>();
		try{
			Member member = dao_A.findMemberById(memberId);
			List<Group> groups = member.getGroups();
			
			for(Group gr : groups){
				GroupIdDTO groupId = new GroupIdDTO(gr.getId(), gr.getName());
				groupIS.add(groupId);
			}
	        return new Gson().toJson(groupIS);
		}catch(ObjectNotFoundException ONFE){
			return new Gson().toJson("Not connected");
		}
	}
	// ======================================
    // =      getGroupInvitations   		=
    // ======================================
	/**
     * Get member invitations.
     * @return Json : list of GroupInvitationDTO
     */
	@GET
	@Path("/getGroupInvitations")
	@Produces("application/json")
	public String getGroupInvitations() throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member exists before...
		if (!checkMemberId(memberId))
			return new Gson().toJson("Not connected");
		else{
			try{
				List<GroupInvitationDTO> invitationsDTO = new LinkedList<GroupInvitationDTO>();
				List<Invitation> invitations = dao_A.findAllInvitationForMemberId(memberId);
				
				for(Invitation iv : invitations){
					//We need the origin member name
					Member originMember = dao_A.findMemberById(iv.getMemberOrigID());
					//We need the group Name
					Group group = dao_A.findGroupById(iv.getGourpId());		
					invitationsDTO.add(new GroupInvitationDTO(iv.getId(), originMember.getName(), group.getName(), group.getId()));
				}

		        return new Gson().toJson(invitationsDTO);
			}catch(ObjectNotFoundException ONFE){
				return null;
			}
		}
	}
	// ======================================
    // =      acceptInvitation		   		=
    // ======================================
	/**
     * Accept an invitation in a group.
     * @param invitationId
     * @return Json : request status
     */
	@GET
	@Path("/acceptInvitation")
	@Produces("application/json")
	public String acceptInvitation(@QueryParam("invitationId") String invitationId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member exists before...
		if (!checkMemberId(memberId))
			return new Gson().toJson("Not connected");
		else{
			try{
				Invitation invitation = dao_A.findInvitationById(invitationId);
		
				GroupService groupService = new GroupService();						
				groupService.addMember(invitation.getDestMemberId(), invitation.getGourpId());
				
				dao_A.removeInvitation(invitation);
				//Build Json object and return it
		        Gson gsonMaker = new Gson();
		        return gsonMaker.toJson("");
			}catch(ObjectNotFoundException ONFE){
				return null;
			}
		}
	}
	// ======================================
    // =      declineInvitation		   		=
    // ======================================
	/**
     * Decline an invitation to a group.
     * @param invitationId
     * @return Json : request status
     */
	@GET
	@Path("/declineInvitation")
	@Produces("application/json")
	public String declineInvitation(@QueryParam("invitationId") String invitationId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member exists before...
		if (!checkMemberId(memberId))
			return new Gson().toJson("Not connected");
		else{
			try{
				Invitation invitation = dao_A.findInvitationById(invitationId);
				dao_A.removeInvitation(invitation);
		        return new Gson().toJson("");
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE);
			}
		}
	}
	// ======================================
    // =      leaveGroup			   		=
    // ======================================
	/**
     * Leave a group.
     * @param groupId
     * @return Json : request status
     */
	@GET
	@Path("/leaveGroup")
	@Produces("application/json")
	public String leaveGroup(@QueryParam("groupId") String groupId) throws JSONException {
		//Get Member id from session
		String memberId = (String) this._request.getSession().getAttribute("memberId");
		
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 	
		//check if member exists before...
		if (!checkMemberId(memberId))
			return new Gson().toJson("Not connected");
		else{
			try{
				Member member =  dao_A.findMemberById(memberId);
				Group group = dao_A.findGroupById(groupId);
				GroupMember groupMember = dao_A.findGroupMemberForMemberIdAndGroupId(memberId, groupId);
				float memberBalance = groupMember.getBalance();
				if(member.removeGroup(groupId)){
					if(group.removeMember(memberId)){
						//check if group has no member, if so remove this group
						if(group.getMembers().isEmpty()){
							dao_A.removeGroup(group);
						}
						else{
							//update group modifications
							dao_A.mergeGroup(group);
							//save that member left group with balance value ... (normally always 0)
							GroupService gs = new GroupService();
							gs.saveInGroupHistory(groupId, member.getName() + " left the group, his balance was " + memberBalance + "...");
							//Do not leave accounts not balanced! 
							gs.equilibreAccounts(groupId);
						}
						dao_A.mergeMember(member);
						
						return new Gson().toJson("Group left");
					}
						
					else
						return new Gson().toJson("Member not find in group's members");
				}
				else {
					return new Gson().toJson("group not find in member's groups");
				}
			}catch(ObjectNotFoundException ONFE){
				return new Gson().toJson(ONFE.getMessage());
			}
		}
	}
	// ======================================
    // =           checkMemberValues	    =
    // ======================================
	private String checkMemberValues(MemberDTO member){
		if(member.getName().equals("") || member.getName().equals(null))
			return "Name is empty ! ";
		if(member.getPassWord().equals("") || member.getPassWord().equals(null))
			return "passWord is empty ! ";
		return "";
	}
	// ======================================
    // =           checkMemberId	   	    =
    // ======================================
	private boolean checkMemberId(String memberId){
		DataAccessAllObjects dao_A = new DataAccessAllObjects("homeSharePU"); 
		if(memberId == null || memberId.equals(""))
			return false;
		try{
			Member member =  dao_A.findMemberById(memberId);
		}catch(ObjectNotFoundException ONFE){
			return false;
		}
		
		return true;
	}
	//Simple classes used to serialize object into Json
	// ======================================
	//			Class						=
    // =           GroupInvitationDTO       =
    // ======================================
	class GroupInvitationDTO{
		String id;
		String memberOriginName;
		String groupName;
		String groupId;
		
		GroupInvitationDTO(String id, String memberOriginName,
						String groupName, String groupId){
			this.id = id;
			this.memberOriginName = memberOriginName;
			this.groupName = groupName;
			this.groupId = groupId;
		}
	}
	
}
