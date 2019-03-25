<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    

<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="ISO-8859-1">
<title>Node Manager</title>
</head>
<style>
table.td:{
width:100px;


}
</style>
<body>
<H1>Welcome to Node Manager</H1>
<hr>
 List of Nodes:
    <br />
    <table>
     <tr>
    			<td>Name</td>
                <td>Activity Name</td>
                <td>Owner</td>
                <td>Assigned By</td>
                <td>BUild Installed</td>
                <td>Type</td>
         </tr>
            <tr th:each="node : ${data}">
                <td th:text="${node.name}"/>
                <td th:text="${node.assignedActivity}"/>
                <td th:text="${node.owner}"/>
                <td th:text="${node.assignedBy}"/>
                <td th:text="${node.installedBuild}"/>
                <td th:text="${node.nodeType}"/>
                
            </tr>
    </table>
</body>
</html>