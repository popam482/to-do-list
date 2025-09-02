package application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User {

	private String username, password, salt;
	
	protected User(String username, String password) {
		this.username=username;
		this.salt=generateSalt();
		this.password=hashPassword(password, salt);
	}
	
	private User() {}
	
    private String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
	
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String toHash = password + salt;
            byte[] hashedBytes = md.digest(toHash.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    protected static User fromFile(String username, String hash, String salt) {
        User u = new User();
        u.username = username;
        u.password = hash;
        u.salt = salt;
        return u;
    }

    protected boolean verifyPassword(String inputPassword) {
    	String hashPass=hashPassword(inputPassword, this.salt);
    	return hashPass.equals(password);
    }
    
    protected String toCSV() {
        return username + ";" + password + ";" + salt;
    }

    
    protected String getSalt() {
        return salt;
    }

    
    protected String getUsername() {
    	return username;
    }
    
    protected String getPassword() {
    	return password;
    }
    
}
