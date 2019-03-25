package com.node.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.node.DAOService.DAO;
import com.node.DAOService.MemberService;
import com.node.beans.Members;
import com.node.beans.Node;
import com.node.beans.WriteDataToCSV;

@Controller
public class DbaController {
	
	private final String BUSY="BUSY";
	private final String FREE="FREE";
	
		@Autowired
		private DAO service;
		
		@Autowired
		private MemberService ser;
		
		@RequestMapping("/secured")
		public String Shome(Model m,@RequestParam(name="message", required=false) String message,@RequestParam(name="errorMessage", required=false) String errorMessage)
		{
			if(errorMessage!=null)
			{
				m.addAttribute("errorMessage",errorMessage);
			}
			if(message!=null)
			{
				m.addAttribute("message",message);
			}
			List<Node> list=service.getAll();
			Collections.sort(list,(x,y)-> x.getName().toLowerCase().compareTo(y.getName().toLowerCase()));
			if(list.isEmpty() || list==null)
			{
				m.addAttribute("data",null);
			}
			else
				m.addAttribute("data",list);
			
			m.addAttribute("node",new Node());
			List<Members> members =  ser.getList();
			Collections.sort(members,(x,y)-> x.getName().toLowerCase().compareTo(y.getName().toLowerCase()));
			m.addAttribute("members", members);
			
			return "DbaHome";
		}
		
		@PostMapping("/secured/addForm")
		public ModelAndView addPage(@ModelAttribute("node") Node node, ModelMap m)
		{
			if(node!=null && !node.getName().matches("\\s*"))
			{
				Node matchedNode=service.getNodeByName(node.getName().toLowerCase());
				if(matchedNode !=null)
				{
					m.addAttribute("errorMessage",String.format("Node '%s' is already added to the list.",node.getName()));
				}
				else
				{
					node.setState(FREE);
					node.setName(node.getName().toLowerCase());
					service.add(node);
					m.addAttribute("message",String.format("Node '%s' has been added to the list successfully.",node.getName()));
				}
				m.addAttribute("data",service.getAll());
			}
			return new ModelAndView("redirect:/secured",m);
		}
		
		@PostMapping("/secured/node")
		public ModelAndView addNode(@ModelAttribute("node") Node node, ModelMap m, @RequestParam(name="Activity_Type", required=false, defaultValue=" ") String type)
		{
			if(!node.getName().matches("\\s*") || !node.getAssignedActivity().matches("\\s*")  && node!=null)
			{
				Node matchedNode=service.getNodeByName(node.getName().toLowerCase());
			
					if(matchedNode==null )
					{
						m.addAttribute("errorMessage",String.format("Node '%s' is not added in the list. please add and try again.",node.getName()));
					}
					else if(matchedNode.getState().equals(BUSY) && !matchedNode.getAssignedActivity().toLowerCase().equals(node.getAssignedActivity().toLowerCase()) && !matchedNode.getAssignedActivity().matches("\\s*") )
					{
						m.addAttribute("errorMessage",String.format("Node '%s' is already assigned to '%s'. Please unassign and try again!",node.getName(),matchedNode.getAssignedActivity()));
					}
					else
					{
						node.setState(BUSY);
						node.setName(matchedNode.getName().toLowerCase());
						
						if(!node.getAssignedActivity().matches("\\s*") && !type.matches("\\s*") && type!=null)
						{
							node.setAssignedActivity(type+":[ "+node.getAssignedActivity()+" ]");
						}
						
						node.setNodeType(matchedNode.getNodeType());
						service.add(node);
						m.addAttribute("message",String.format("Successfully assigned node '%s' to '%s' .",node.getName(), node.getOwner()));
					}
				
				m.addAttribute("data",service.getAll());
			}
			return new ModelAndView("redirect:/secured",m);
		}
		
		@PostMapping("/secured/note")
		public ModelAndView addNoteSecure(@ModelAttribute("node") Node node, ModelMap m)
		{
			if(!node.getName().matches("\\s*") || !node.getAssignedActivity().matches("\\s*")  && node!=null)
			{
				Node matchedNode=service.getNodeByName(node.getName().toLowerCase());
			
					if(matchedNode==null )
					{
						m.addAttribute("errorMessage",String.format("Node '%s' is not added in the list. please add and try again.",node.getName()));
					}
					else
					{
						matchedNode.setNote((node.getNote()==null || node.getNote().matches("\\s*"))?"":node.getNote());
						service.add(matchedNode);
						m.addAttribute("message",String.format("Successfully added note to '%s'.",node.getName()));
					}
				
				m.addAttribute("data",service.getAll());
			}
			return new ModelAndView("redirect:/secured",m);
		}
		
		
		@RequestMapping("/secured/release/{nodeName}")
		public ModelAndView release(@PathVariable("nodeName") String nodeName, ModelMap m)
		{
			if(nodeName!=null)
			{
			Node matchedNode=service.getNodeByName(nodeName.toLowerCase());
			if(matchedNode!=null)
			{
				matchedNode.setState(FREE);
				matchedNode.setAssignedActivity("");
				matchedNode.setAssignedBy("");
				matchedNode.setInstalledBuild("");
				matchedNode.setOwner("");
				service.add(matchedNode);
				m.addAttribute("message",String.format("Successfully released node '%s'. ",nodeName));
			}
			
			else
			{
				m.addAttribute("errorMessage",String.format("Unable to release Node '%s'. Please try again .",nodeName));
				
			}
			}
			return new ModelAndView("redirect:/secured",m);
			
		}
		
		@RequestMapping("/secured/delete/{nodeName}")
		public ModelAndView delete(@PathVariable(name="nodeName", required=true) String nodeName, ModelMap m)
		{
			if(nodeName!=null)
			{
			Node matchedNode=service.getNodeByName(nodeName.toLowerCase());
			if(matchedNode!=null)
			{
				service.deleteNode(matchedNode);
				m.addAttribute("message",String.format("Successfully deleted node '%s'. ",nodeName));
			}
			else
			{
				m.addAttribute("errorMessage",String.format("Unable to delete Node '%s'. Please try again .",nodeName));
				
			}
			}
			return new ModelAndView("redirect:/secured",m);
			
			
		}
		@SuppressWarnings("finally")
		@RequestMapping("/secured/download/NodeDetails.csv")
		public ModelAndView downloadCSV(HttpServletResponse response, @RequestParam("filter") String data, ModelMap m) {
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; file=NodeDetails.csv");
			try
			{
				List<Node> list = (List<Node>) service.getAll(); 
				List<Node> filteredList=new ArrayList<Node>();  
				if(list!=null)
				{
				
				if(data.equalsIgnoreCase("FREE"))
				{
					filteredList=list.stream().filter(s->(s!=null && s.getState().equals("FREE"))).collect(Collectors.toList());
				}
				else if(data.equalsIgnoreCase("PC"))
				{
					filteredList=list.stream().filter(s->(s!=null && s.getAssignedActivity()!=null && s.getAssignedActivity().startsWith("PC:"))).collect(Collectors.toList());
				}
				else if(data.equalsIgnoreCase("BUC"))
				{
					filteredList=list.stream().filter(s->(s!=null && s.getAssignedActivity()!=null &&  s.getAssignedActivity().startsWith("BUC:"))).collect(Collectors.toList());
				}
				else if(data.equalsIgnoreCase("TR"))
				{
					filteredList=list.stream().filter(s->(s!=null && s.getAssignedActivity()!=null && s.getAssignedActivity().startsWith("TR:"))).collect(Collectors.toList());
				}
				else if(data.equalsIgnoreCase("VM"))
				{
					filteredList=list.stream().filter(s->(s!=null && s.getNodeType().equals("VM"))).collect(Collectors.toList());
				}
				else if(data.equalsIgnoreCase("BM"))
				{
					filteredList=list.stream().filter(s->(s!=null && s.getNodeType().equals("BM"))).collect(Collectors.toList());
				}
				else
				{
					filteredList.addAll(list);
				}
				}
				if(filteredList.isEmpty())
				{
					m.addAttribute("errorMessage","Filtered list is empty! Please try another filter.");
				}
				else
				{	WriteDataToCSV.writeObjectToCSV(response.getWriter(), filteredList);
					m.addAttribute("message","Successfully exported the table!");
				}
			}
			catch(IOException e)
			{
				m.addAttribute("errorMessage","Unable to export the table!");
				
			}
			finally
			{
				return new ModelAndView("redirect:/secured",m);
			}
		}

						
			
		
		
		
	

}
