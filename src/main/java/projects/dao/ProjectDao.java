package projects.dao;

import java.sql.Connection;
import java.math.BigDecimal;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import provided.util.DaoBase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import projects.exception.DbException;

	
	/** This class uses JDBC to perform CRUD operations on the project tables.
	 * 
	 */
@SuppressWarnings("unused")
	public class ProjectDao extends DaoBase {
		private static final String CATEGORY_TABLE = "category";
		private static final String MATERIAL_TABLE = "material";
		private static final String PROJECT_TABLE = "project";
		private static final String PROJECT_CATEGORY_TABLE = "project_category";
		private static final String STEP_TABLE = "step";
		
		/**
		 * Insert a project row into the project table.
		 * project The project object to insert.
		 * The project object with the primary key.
		 * DbException Thrown if an error occurs inserting the row.
		 */
		
		public Project insertProject(Project project) {
			//@formatter:off
			String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
			//@formatter:on
			
			try(Connection conn = DbConnection.getConnection()) {
				startTransaction(conn);
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, project.getProjectName(), String.class);
					setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
					setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
					setParameter(stmt, 4, project.getDifficulty(), Integer.class);
					setParameter(stmt, 5, project.getNotes(), String.class);
					
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;	
				}
				catch(Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			}
			
			catch(SQLException e) {
				throw new DbException(e);
			}
		}
//this method instructs MySQL to return all project rows without any materials, steps, or categories. orders the reults by project name
		public List<Project> fetchAllProjects() {
			String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
//obtain connection and start a transaction	within the try-with-resource statement	
			try(Connection conn = DbConnection.getConnection()) {
				startTransaction(conn);
//obtain a prepared statement from the connection object
				//you will get a resultset from the prepared statement
				//iterate over the resultset to create a project object for each row returned
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				try(ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

//this loops through the ResultSet from the Prepared Statement. creates and assign each result row to a new
//Project object. Add the project object to the List of Projects. This method does that:
			while(rs.next()) {
				projects.add(extract(rs, Project.class));
			}
			
			return projects;
				}
			}
//catch an exception object. rollback the transaction and throw a new DBException, passing in the Exception
//object as the clause.
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			}
			
			catch(SQLException e) {
				throw new DbException(e);
			}
		}
//this method will list the project details (except materials, category, etc) from the project table
		public Optional<Project> fetchProjectById(Integer projectId) {
			String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
			
			try(Connection conn = DbConnection.getConnection()) {
				startTransaction(conn);
				
			try {
				Project project = null;
				
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					project = extract(rs, Project.class);
				}
			}
			}
			if(Objects.nonNull(project)) {
				project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
				project.getSteps().addAll(fetchStepsForProject(conn, projectId));
				project.getCategories().addAll(fetchCategoriesForProject(conn,projectId));
			}
			
			commitTransaction(conn);
			return Optional.ofNullable(project);
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}	
			}
			catch(SQLException e) {
			throw new DbException(e);
			}
			}
	//this method will return a list of categories. this one has a join with project category table	
private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		//@formatter:off
			String sql = ""
					+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
					+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
					+ "WHERE project_id = ?";
			//@formatter:on
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
				try (ResultSet rs = stmt.executeQuery()) {
						List<Category> categories = new LinkedList<Category>();
						
						while (rs.next()) {
							categories.add(extract(rs, Category.class));
						
					}
					
					return categories;
				}
			}
}

	//this method returns a list of steps	
		private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
			String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
				try(ResultSet rs = stmt.executeQuery()) {
					List<Step> steps = new LinkedList<Step>();
					
					while(rs.next()) {
						steps.add(extract(rs, Step.class));
					}
					
					return steps;
					
				}
			}
		}
	//this method will return a list of materials	
		private List<Material>fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
			String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
				try(ResultSet rs = stmt.executeQuery()){
					List<Material> materials = new LinkedList<Material>();
					
					while(rs.next()) {
						materials.add(extract(rs, Material.class));
					}
					
					return materials;
				}
			}
		}
//this method returns a boolean that indicates whether the UPDATE operation was successful or throws an exception
//?s are parameter placeholders 
	public boolean modifyProjectDetails(Project project) {
		//@formatter: off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?,"
				+ "actual_hours = ?,"
				+ "difficulty = ?,"
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter: on
//obtain a connection and start a transaction:		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
//this method returns the number of rows affected by the UPDATE operation
//since a single row is being acted on (comparing to the primary key in the WHERE clause guarantees this),
//the return value should be 1. If it is 0 it means that no rows were acted on and the primary key value
//(project ID) is not found. So, the method returns true if executeUpdate() returns 1 and false if it returns 0.
				boolean modified = stmt.executeUpdate() ==1;
				commitTransaction(conn);
				
				return modified;
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
			catch(SQLException e) {
				throw new DbException(e);
			}
		}
//this method returns a boolean that indicates whether the DELETE operation was successful
	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
			boolean deleted = stmt.executeUpdate() == 1;
			
			commitTransaction(conn);
			return deleted;
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
			catch(SQLException e) {
				throw new DbException(e);
			}
		}
}




