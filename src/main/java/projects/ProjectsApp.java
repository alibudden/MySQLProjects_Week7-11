package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/**
 * This class is a menu-driven application that accepts user input from the console. It then performs CRUD
 * operations on the project tables.
 * 
 * @author aliso
 *
 */

public class ProjectsApp {
	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();

		//@formatter:off
		private List<String> operations = List.of(
				"1) Add a project"
				);
		//@formatter: on
		
	/**Entry point for Java application.
	 * args Unused.	
	 * @param args
	 */
		
		public static void main(String[] args) {
			new ProjectsApp().processUserSelections();
		}
		
		/**
		 * This method prints the operations, gets a user menu selection, and performs the requested
		 * operation. It repeats until the user requests that the application terminate.
		 * 
		 */
		
		private void processUserSelections() {
			boolean done = false;
			
			while(!done) {
				try {
					int selection = getUserSelection();
					
				switch(selection) {
					case -1:
						done = exitMenu();
						break;
						
					case 1:
						createProject();
						break;
						
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				}
				}
				
				catch(Exception e) {
					System.out.println("\nError: " + e + " Try again.");
				}
			}
		}

		/**
		 * Gather user input for a project row then call the project service to create the row.
		 * 
		 */
		
		private void createProject() {
			String projectName = getStringInput("Enter the project name");
			BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
			BigDecimal actualHours = getDecimalInput("Enter the actual hours");
			Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
			String notes = getStringInput("Enter the project notes");
			
			Project project = new Project();
			
			project.setProjectName(projectName);
			project.setEstimatedHours(estimatedHours);
			project.setActualHours(actualHours);
			project.setDifficulty(difficulty);
			project.setNotes(notes);
			
			Project dbProject = projectService.addProject(project);
			System.out.println("You have successfully created project: " + dbProject);
		}
		
		/**
		 * Gets the user's input from the console and converts it to a BigDecimal.
		 * prompt  The prompt to display on the console.
		 * A BigDecimal value if successful.
		 * DbException Thrown if an error occurs converting the number to a BigDecimal.
		 *
		 */
		
		private BigDecimal getDecimalInput(String prompt) {
			String input = getStringInput(prompt);
			
			if(Objects.isNull(input)) {
				return null;
			}
			try {
				/*Create the BigDecimal object and set it to two decimal places (the scale). */
				return new BigDecimal(input).setScale(2);
			}
			catch(NumberFormatException e) {
				throw new DbException(input + " is not a valid decimal number.");
			}
		}
		
		/**
		 * called when the user wants to exit the application. It prints a message and returns 
		 * {@code true} to terminate the app.
		 * 
		 */
		
		private boolean exitMenu() {
			System.out.println("Exiting the menu.");
			return true;
		}
		
		/**
		 * This method prints the available menu selections. It then gets the user's menu selection from
		 * the console and converts it to an int.
		 * 
		 * The menu selection as an int or -1 if nothing is selected.
		 */
		
		private int getUserSelection() {
			printOperations();
			
			Integer input = getIntInput("Enter a menu selection");
			
			return Objects.isNull(input) ? -1 : input;
		}
		
		/**
		 * Prints a prompt on the console and then gets the user's input from the console. It then
		 * converts the input to an Integer.
		 * 
		 * prompt The prompt to print.
		 * If the user enters nothing, {@code null} is returned. Otherwise, the input is converted
		 * to an Integer.
		 * DbException Thrown if the input is not a valid Integer.
		 */
		
		private Integer getIntInput(String prompt) {
			String input = getStringInput(prompt);
			
			if(Objects.isNull(input)) {
				return null;
				
			}
			
			try {
				return Integer.valueOf(input);	
		}
			catch(NumberFormatException e) {
				throw new DbException(input + " is not a valid number.");
			}
	}
		/**
		 * Prints a prompt on the console and then gets the uesr's input from the console. If the user
		 * enters nothing, {@code null} is returned. Otherwise, the trimmed input is returned.
		 * 
		 * prompt The prompt to print.
		 * The user's input of {@code null}.
		 */
		
		private String getStringInput(String prompt) {
			System.out.println(prompt + ": ");
			String input = scanner.nextLine();
			
			return input.isBlank() ? null : input.trim();
		}
		
		/**
		 * Print the menu selections, one per line.
		 */
		
		private void printOperations() {
			System.out.println("\nThese are the available selections. Press the Enter key to quit:");
			
			operations.forEach(line -> System.out.println("   " + line));
		}
}

