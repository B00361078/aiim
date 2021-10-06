package com.aiim.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.aiim.app.database.DatabaseConnect;
//import org.json.simple.parser.ParseException;
//import com.app.model.AdminData;
import com.aiim.app.resource.ViewNames;
//import com.app.utils.Validation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/* The following class handles the login and authentication of the application. Part of MVC design Pattern as a controller.
 * Neil Campbell 07/05/2021, B00361078
 */
 
public class LoginController {
    private static final Charset UTF_16 = null;
	@FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    private ViewController viewController;
    private String saltString;
    private String hashString;
    private String salt;
    byte[] fullhashbytes;
    private Connection con;
    //private Validation validation;
    private ResourceBundle strBundle;
    PreparedStatement stmt;
   // private AdminData adminD;
    byte[] thesalt;
    byte[] passBytes;
        
    public void initialize() throws IOException {
    	//adminD = new AdminData();
    	strBundle = ResourceBundle.getBundle("com.aiim.app.resource.bundle");
    	viewController = new ViewController();
    	//validation = new Validation();
    }
    
    @FXML protected void dashView(ActionEvent event) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException, DecoderException  {
    	Scene scene = passwordField.getScene();
    	String mystr = usernameField.getText();
    	
   
        	con = DatabaseConnect.getInstance().getConnection();
        	stmt = con.prepareStatement("USE [honsdb] SELECT* FROM tblUser WHERE username = 'neil0310'");
        	ResultSet rs = stmt.executeQuery();
    		//stmt.executeUpdate();
        	while(rs.next()){
        		hashString = rs.getString(2);
                passBytes = rs.getBytes(2);
                thesalt = rs.getBytes(5);
                

//               System.out.println("full hashed bytes in db are  = " +passBytes);
//                System.out.println("full hashed tring in db is  = " +hashString);
//                System.out.println("full salt bytes  = " + thesalt);
//                System.out.println("full salt string  = " + saltString);
//                byteToHexString(thesalt);
//
//                System.out.println(Arrays.toString(stringToByte(saltString)));
//                System.out.println(Arrays.toString(thesalt));
                
                
                

                //saltString = rs.getString(5);
                //System.out.println("salt string is "+ saltString);
                //System.out.println("salt string is this from the bytes  = " +bytetoString(thesalt));
                //System.out.println("salt bytes in db are now2  = " +getBytesCorrectly(saltString));
                //String mysalt = rs.getString(5);

                //System.out.println(hash);
                //System.out.println(passBytes);
                //System.out.println(thesalt);
            }
        	con.close();
        	String str = "AvaArran3934BCF0-8D65-444B-A87C-4B0AE192480B";
        	System.out.println("this was the test" + DigestUtils.sha1Hex(str).toUpperCase());
//        	MessageDigest md = MessageDigest.getInstance("SHA1");
//    
//            byte[] bytes  = md.digest(str.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < bytes.length; i++) {
//                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//            }
//            System.out.println("md = " +sb.toString());
        	//base64 byte array from hex string
//        	byte[] byte1 = stringToByte(hashString);
//        	
//        	String fullstr = (DigestUtils.sha1Hex("AvaArranBFCE7EC0-EE6F-481B-B186-683D19FDFE4D"));
//        	byte[] byte2 = stringToByte(saltString);
//        	og("AvaArran", byte1);
//        	og("AvaArran", byte2);
        	
        	//System.out.println(Arrays.toString(fullhashbytes));
        	//check("AvaArran", "5D7F643D-664E-4587-B3C9-4710F081BF2E");
        	//byte[] saltybytes = getBytesCorrectly(saltString);
        	//String fullstr = (DigestUtils.sha1Hex("AvaArran1EF0580C-0CDF-4A2C-8704-5EAF7F585194"));
        	
        	
        	
        	
        	//getHashWithSalt("AvaArran", thesalt);
        	//og("AvaArran", stringToByte(saltString));
        	//System.out.println("this is the hash string encoded to bytes properly" + bytetoString(stringToByte(hashString)));
        	//String strencoded = bytetoString(stringToByte(saltString));
        	//stringToByte(strencoded);
        	//og("AvaArran", thesalt);
        	
        	//byte[] finalbytes = getHashWithSalt("AvaArran", thesalt);
        	//String finalstr = bytetoString(finalbytes);
        	//System.out.println("final string after encoding properyl is " + finalstr);
        	


        	
        	//og("AvaArran", passBytes);
        	

        	//byte[] digest = DigestUtils.sha1(passBytes);
        	
        	//System.out.print("sha1 string is " + digest);

        	
        	
//        	String password1 = getSecurePassword(passBytes, thesalt);
//        	System.out.println("the generated password is " + password1);
//        	System.out.println(passwordField.getText());
//        	System.out.println("the hash is " + hash);
//        	
//        	
//        	byte[] secondBytes = getBytes(passwordField.getText(), thesalt);
//        	System.out.println("secondbytes are " + secondBytes);
//        	String passwordfromUI = getSecurePassword(secondBytes, thesalt);
//        	System.out.println("the generated UI pass hash is " + passwordfromUI);
  
        	
        	
                
        		
        	
    }
    // AvaArran gets passed here with salt bytes to see if genrated hash is the same
    // salt in db need to be sha1
    
    
    public String bytetoString(byte[] input) {
    	System.out.println("hash string is "  + org.apache.commons.codec.binary.Base64.encodeBase64String(input).replaceAll("\\+", "-"));
        return org.apache.commons.codec.binary.Base64.encodeBase64String(input).replaceAll("\\+", "-");
    }
    
    public void check(String password, String saltstring) throws NoSuchAlgorithmException {
    	
    	String conact = password+saltstring;
    	
    	MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] byteArray = sha1.digest((conact).getBytes());
        System.out.println("the check result is " + Arrays.toString(byteArray));
        
    	
    	
    }
    public String byteToHexString (byte[] mybytearr) {
    StringBuilder sb = new StringBuilder();
    for (byte b : mybytearr) {
        sb.append(String.format("%02X ", b));
    }
    System.out.println(sb.toString());
	return sb.toString();
    }
    
    public byte[] getHashWithSalt(String input, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.reset();
        //digest.update(salt);
        byte[] hashedBytes = digest.digest(stringToByte(input));
        System.out.println("hashed bytes are now +  " + hashedBytes);

        System.out.print(bytetoString(hashedBytes));
        StringBuilder hex = new StringBuilder(hashedBytes.length * 2);
        for (byte b : hashedBytes) {
            if ((b & 0xff) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xff));
        }
        System.out.println("hex to string is " + hex.toString());
        return hashedBytes;
    }
    public byte[] stringToByte(String input) {
        if (Base64.isBase64(input)) {
        	System.out.println(Base64.decodeBase64(input));
        	//2236150B-8B32-4987-BA63-A5E4452955A5
            return Base64.decodeBase64(input);

        } else {
        	System.out.println(Base64.encodeBase64(input.getBytes()));
            return Base64.encodeBase64(input.getBytes());
        }
	}
    public byte[] getBytesCorrectly(String nvarchar) throws NoSuchAlgorithmException {
    	MessageDigest md = MessageDigest.getInstance("SHA1");
    	byte[] bytes  = md.digest(nvarchar.getBytes());
    	return bytes;
    }
    
    public static String og(String password, byte[] salt) {
		
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(salt);
            byte[] bytes  = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("generated pass >>> " + generatedPassword);
        return generatedPassword;
    }
    
        	public static String getSecurePassword(byte[] password, byte[] salt) {
        		
                String generatedPassword = null;
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    md.update(salt);
                    //byte[] bytes  = md.digest(password.getBytes(StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < password.length; i++) {
                        sb.append(Integer.toString((password[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    generatedPassword = sb.toString();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                return generatedPassword;
            }
        	public static byte[] getBytes(String password, byte[] salt) {
        		byte[] bytes = null;
                String generatedPassword = null;
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    md.update(salt);
                    bytes  = md.digest(password.getBytes(StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < bytes.length; i++) {
                        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    generatedPassword = sb.toString();
                    return bytes;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                return bytes;
            }
        	private static byte[] getSalt() throws NoSuchAlgorithmException {
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);
                
                return salt;
            }
        
    }


    
//        }
//			if(validation.stringValidator(passwordField.getText(), adminD.getPassword()) && 
//				//	validation.stringValidator(usernameField.getText(), adminD.getUsername())== true) {
//        	viewController.setCurrentScene(scene);
//        	viewController.switchToView(ViewNames.DASHBOARD);
//			//}
			//else
			//	new Alert(Alert.AlertType.ERROR, strBundle.getString("e10")).showAndWait();
		//} catch (IOException e1) {
			//e1.printStackTrace();
		//}
   // }
//}