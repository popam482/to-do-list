package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;

public class TaskFileManager {
		
	protected void loadFile(ObservableList<Task> masterList, String usernameFile) {
		masterList.clear();
		try(BufferedReader fr=new BufferedReader(new FileReader("users/" + usernameFile + ".csv"))) {
			String line;
			while((line=fr.readLine())!=null) {
				String[] content=line.split(";",-1);
				if(content.length < 3) {
				    continue; 
				}
				
				String taskName=content[0];
				String deadline=content[1];
				boolean completed=Boolean.parseBoolean(content[2]);
				
				Task task=new Task(taskName, deadline, completed);
				masterList.add(task);
				
			}
		}
		
		catch (FileNotFoundException e) {
			
		}
		
		catch(IOException e) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Reading from the file");
			alert.setContentText("An error occured while trying to read from the file");
			alert.showAndWait();
		}
	}
	
	
	protected void saveFile(ObservableList<Task> masterList, String usernameFile) {
		
		try(BufferedWriter fw=new BufferedWriter(new FileWriter("users/" +usernameFile + ".csv"))){
			for(Task element:masterList) {
				fw.write(element.getName()+ ";"+ element.getDeadline()+ ";"+ element.isCompleted());
				fw.newLine();
			}
		}catch(IOException e) {
			Alert alert=new Alert(AlertType.ERROR);
			alert.setTitle("Writing in the file");
			alert.setContentText("An error occured while trying to write in the file");
			alert.showAndWait();
		}
	}

}
