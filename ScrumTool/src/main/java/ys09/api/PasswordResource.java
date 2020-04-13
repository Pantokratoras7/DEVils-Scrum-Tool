package ys09.api;

import com.google.gson.Gson;
import org.restlet.representation.Representation;
import org.restlet.resource.Patch;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import ys09.auth.CustomAuth;
import ys09.conf.Configuration;
import ys09.data.DataAccess;
import ys09.model.LocalStorage;
import ys09.model.Password;
import ys09.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class PasswordResource extends ServerResource {
    private final DataAccess dataAccess = Configuration.getInstance().getDataAccess();

    // Post for Sign Up
    protected Representation post(Representation entity) throws ResourceException {
        // Representation object contains the body of the request
        // Handle the exception for the getText
        System.out.println("hhhhhhh");

        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();

        String userId = getRequestAttributes().get("userId").toString();
        if (userId.equals("null")) {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
        int user = Integer.parseInt(userId);

        Series requestHeaders = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");
        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if (customAuth.checkAuthToken(token)) {
            if (customAuth.userValidation(token, userId)) {
                // Get the whole json body representation

                try {

                    // Get the whole json body representation
                    String str = entity.getText();
                    // Now Create from String the JAVA object
                    Gson gson = new Gson();
                    Password password = gson.fromJson(str, Password.class);
                    boolean response = dataAccess.passwordMatches(user,password.getPassword());

                    if (response) {
                        map.put("exists", 1);
                        return new JsonMapRepresentation(map);
                    } else {
                        map.put("exists", 0);
                        return new JsonMapRepresentation(map);
                    }
                } catch (IOException e) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("Message", "System Exception");
                    return new JsonMapRepresentation(map1);
                }
            }
            else {
                mapError.put("error", "Unauthorized userId");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }

    // Update the password

    @Patch
    public Representation update(Representation entity) throws ResourceException {
        // Representation object contains the body of the request
        // Handle the exception for the getText
        Map map = new HashMap<String, String>();
        Map mapError = new HashMap<String, String>();

        String userId = getRequestAttributes().get("userId").toString();
        if (userId.equals("null")) {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
        int user = Integer.parseInt(userId);

        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if(customAuth.checkAuthToken(token)) {
            // Update user's infoprmation
            if(customAuth.userValidation(token, userId)) {
                // Get the whole json body representation
                try {
                    String str = entity.getText();
                    Password password = new Gson().fromJson(str, Password.class);
                    boolean checkpass = dataAccess.updatePassword(password.getPassword(), user);
                    map.put("result", checkpass);
                    return new JsonMapRepresentation(map);
                }
                catch(IOException e) {
                    mapError.put("result", "System Exception");
                    return new JsonMapRepresentation(mapError);
                }
            }
            else {
                mapError.put("error", "Unauthorized userId");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }
}
