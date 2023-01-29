package projects.service;

import projects.entity.Project;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;


public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();
	
	/** this method simply calls the DAO class to insert a project row.
	 * 
	 * return the project object with the newly generated primary key value.
	 */
	
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
		
	}

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
		
	}
	
	public Project fetchProjectById(Integer projectId) {
	Optional<Project> op = projectDao.fetchProjectById(projectId);
	
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID =" + projectId + " does not exist."));
	}

}
