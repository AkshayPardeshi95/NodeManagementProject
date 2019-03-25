package com.node.controller;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.node.DAOService.DAO;
import com.node.DAOService.MemberService;
import com.node.beans.Members;
import com.node.beans.Node;
import com.node.beans.WriteDataToCSV;

@Controller
public class MainController {


	private static boolean mutex=true;
	private final String BUSY="BUSY";
	private final String FREE="FREE";
	
	
	@Autowired
	private DAO service;
	
	@Autowired
	private MemberService ser;
	
	@RequestMapping("/")
	public ModelAndView home(Principal principal,Model m,@RequestParam(name="message", required=false) String message,@RequestParam(name="errorMessage", required=false) String errorMessage)
	{
	   synchronized(this)
	   {
		List<Node> list=service.getAll();
		Collections.sort(list,(x,y)-> x.getName().toLowerCase().compareTo(y.getName().toLowerCase()));
		if(principal.getName().equalsIgnoreCase("ADMIn"))
		{
			return new ModelAndView("redirect:/securedHome");
		}
		if(principal.getName().equalsIgnoreCase("Akshay"))
		{
			return new ModelAndView("redirect:/secured");
		}
		if(errorMessage!=null)
		{
			m.addAttribute("errorMessage",errorMessage);
		}
		if(message!=null)
		{
			m.addAttribute("message",message);
		}
		
		if(list.isEmpty() || list==null)
		{
			m.addAttribute("data",null);
		}
		else
			m.addAttribute("data",list);
		
		m.addAttribute("node",new Node());
		
		return new ModelAndView("WAHome");
	   }
	}
	
	@RequestMapping("/securedHome")
	public String Shome(Model m,@RequestParam(name="message", required=false) String message,@RequestParam(name="errorMessage", required=false) String errorMessage)
	{
		synchronized(this)
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
		return "home";
		}
	}
	
	@PostMapping("/securedHome/addForm")
	public ModelAndView addPage(@ModelAttribute("node") Node node, ModelMap m)
	{
		synchronized(this)
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
		return new ModelAndView("redirect:/securedHome",m);
		   }
	}
	
	@PostMapping("/securedHome/node")
	public ModelAndView addNode(@ModelAttribute("node") Node node, ModelMap m, @RequestParam(name="Activity_Type", required=false, defaultValue=" ") String type)
	{
		synchronized(this)
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
		return new ModelAndView("redirect:/securedHome",m);
		}
	}
	
	@RequestMapping("/logout")
	public ModelAndView getOut(ModelMap m)
	{
		m.addAttribute("message","Successfully Logged out!");
		return new ModelAndView("redirect:/",m);
	}
	
	@PostMapping("/note")
	public ModelAndView addNote(@ModelAttribute("node") Node node, ModelMap m)
	{
		synchronized(this)
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
		return new ModelAndView("redirect:/",m);
		}
	}
	
	@PostMapping("/securedHome/note")
	public ModelAndView addNoteSecure(@ModelAttribute("node") Node node, ModelMap m)
	{
		synchronized(this)
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
		return new ModelAndView("redirect:/securedHome",m);
		}
	}
	
	
	@RequestMapping("/securedHome/release/{nodeName}")
	public ModelAndView release(@PathVariable("nodeName") String nodeName, ModelMap m)
	{
		synchronized(this)
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
		return new ModelAndView("redirect:/securedHome",m);
		}
		
	}
	
	@RequestMapping("/securedHome/delete/{nodeName}")
	public ModelAndView delete(@PathVariable(name="nodeName", required=true) String nodeName, ModelMap m)
	{
		synchronized(this)
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
		return new ModelAndView("redirect:/securedHome",m);
		}
		
		
	}
	
	@RequestMapping("/download/NodeDetails.pdf")
	public ModelAndView export(HttpServletResponse response, @RequestParam("filter") String data,@RequestParam("path") String path, ModelMap m) 
	{
		synchronized(this)
		{
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; file=NodeDetails.pdf");
		Document document = new Document();
		try
		{
		PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
		PdfPTable table = new PdfPTable(7);
		PdfPCell c1 = new PdfPCell(new Phrase("Name"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Activity Name"));																				
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Owner"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        
        c1 = new PdfPCell(new Phrase("Assigned By"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Build Installed"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Type"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        
        c1 = new PdfPCell(new Phrase("State"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        
       
        Paragraph  subPara = new Paragraph("List of nodes:");
        ArrayList<Node> list=service.getAll();
        List<Node> filteredList=new ArrayList<Node>();  
		
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
		
			Iterator<Node>itr=filteredList.iterator();
			while(itr.hasNext())
			{
				Node n=itr.next();
				table.addCell((n.getName()==null)?" ":n.getName());
				table.addCell((n.getAssignedActivity()==null)?" ":n.getAssignedActivity());
				table.addCell((n.getOwner()==null)?" ":n.getOwner());
				table.addCell((n.getAssignedBy()==null)?" ":n.getAssignedBy());
				table.addCell((n.getInstalledBuild()==null)?" ":n.getInstalledBuild());
				table.addCell((n.getNodeType()==null)?" ":n.getNodeType());
				table.addCell((n.getState()==null)?" ":n.getState());
			}
		subPara.add(table);
		document.add(subPara);
		}
		catch( FileNotFoundException|DocumentException e)
		{
			m.addAttribute("errorMessage","Unable to export the table!");
			return new ModelAndView("redirect:/",m);
		}
		finally
		{
			document.close();
		}
		m.addAttribute("message","Successfully exported the table!");
		return new ModelAndView("redirect:/",m);
		}
	}
	
	
	
	@RequestMapping("/download/NodeDetails.csv")
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
			return new ModelAndView("redirect:/",m);
			
		}
		catch(IOException e)
		{
			m.addAttribute("errorMessage","Unable to export the table!");
			return new ModelAndView("redirect:/",m);
		}
	}

		@RequestMapping("/securedHome/download/NodeDetails.csv")
		public ModelAndView downloadSecureCSV(HttpServletResponse response, @RequestParam("filter") String data, ModelMap m) {
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
				return new ModelAndView("redirect:/securedHome",m);
				
			}
			catch(IOException e)
			{
				m.addAttribute("errorMessage","Unable to export the table!");
				return new ModelAndView("redirect:/securedHome",m);
			}

	}
		
		@RequestMapping("/login")
		public String login()
		{
			return "Login";
		}
		
		
	
	
}
