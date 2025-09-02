package application;

import java.util.*;
import java.io.*;

public class UserManager {

    private List<User> users = new ArrayList<>();

    public UserManager() {
    	read();
    }
    
    
    protected void read() {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("userlist.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3)
                    users.add(User.fromFile(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.out.println("Error - Reading the database failed");
        }
    }

    protected void write() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("userlist.csv"))) {
            for (User u : users) {
                bw.write(u.getUsername() + ";" + u.getPassword() + ";" + u.getSalt());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error - Writing in the database failed");
        }
    }

    protected boolean usernameExists(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username))
                return true;
        }
        return false;
    }

    protected boolean addUser(String username, String password) {
        if (usernameExists(username)) {
            System.out.println("Username already exists");
            return false;
        }
        User newUser = new User(username, password);
        users.add(newUser);

        write();

        System.out.println("User added successfully");
        return true;
    }

    
    protected boolean strongPassword(String password) {
    	if(password==null)
    		return false;
    	if(password.length()<6)
			return false;
    	boolean upper=false, digit=false, special=false;
    	
    	for(char c: password.toCharArray()) {
    		if(Character.isUpperCase(c))
    			upper=true;
    		else
    			if(Character.isDigit(c))
    				digit=true;
    			else
    				if(!Character.isLetterOrDigit(c))
    					special=true;
    	}
    	
    	return upper && digit && special;
    	
    }

    protected User loginUser(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.verifyPassword(password)) {
                return u;
            }
        }
        return null;
    }
}
