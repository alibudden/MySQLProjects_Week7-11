package projects.service;

import projects.entity.Project;
import projects.exception.DbException;

import java.util.List;
import java.util.NoSuchElementException;
//import java.util.Optional;

import projects.dao.ProjectDao;

//the service layer is responsible for calling the DAO to update the project details and to return those details
//to the caller. if the project cannot be found, the service throws an exception. The service method is called by
//the menu application class, and results are returned to that class.

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();
	
	
	
	
	// this method calls the DAO class to insert a project row
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
		
	}
 // this method calls the DAO class and returns the list of project records
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
		
	}
	
//this method calls the project DAO to get all project details, including materials, steps, and categories.
	//if the project ID is invalid, it throws an exception
	public Project fetchProjectById(Integer projectId) {
//	Optional<Project> op = projectDao.fetchProjectById(projectId);
	
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID =" + projectId + " does not exist."));
	}
	 //this method calls the DAO and passes the project object as a parameter. 
	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}
		}
	
	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
			
			
		}
	}
}





