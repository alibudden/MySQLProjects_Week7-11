package projects.service;

import projects.entity.Project;
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

}
