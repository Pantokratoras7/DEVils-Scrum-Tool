package scrumtool.api.resource;

import scrumtool.auth.CustomAuth;
import scrumtool.conf.Configuration;
import scrumtool.data.DataAccess;
import scrumtool.data.Limits;
import scrumtool.data.entities.TeamDB;
import scrumtool.api.representation.JsonMapRepresentation;
import scrumtool.model.*;

import org.restlet.data.Status;
import org.restlet.data.Form;
import org.restlet.data.ClientInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.BufferingRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.resource.Post;
import org.restlet.ext.json.JsonRepresentation;
//import org.restlet.engine.header.Header;
import org.restlet.util.Series;
import org.restlet.Message;
import org.restlet.Context;

import org.json.JSONObject;
import com.google.gson.Gson;

import java.io.Reader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;



public class NotificationsResource extends ServerResource {

    private final DataAccess dataAccess = Configuration.getInstance().getDataAccess();

    @Override
    protected Representation get() throws ResourceException {

        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();

        // Get UserId
        String userIdStr = getRequestAttributes().get("userId").toString();
        if (userIdStr.equals("null")) {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
        int userId = Integer.parseInt(userIdStr);

        // Access the headers of the request!
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        // Get pending notifications
        if (customAuth.checkUserAuthToken(token, userIdStr)) {
            // Get project and its current sprint Information information
            List<Notification> notifsList = dataAccess.getUserNotifications(userId);
            map.put("results", notifsList);
            return new JsonMapRepresentation(map);
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }


    // Insert a new member into project
    @Override
    protected Representation post(Representation entity) throws ResourceException {

        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();

        // Get UserId
        String userIdStr = getRequestAttributes().get("userId").toString();
        if (userIdStr.equals("null")) {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
        int userId = Integer.parseInt(userIdStr);

        // Access the headers of the request!
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if (customAuth.checkUserAuthToken(token, userIdStr)) {
            // Get the whole json body representation
            try {
                String str = entity.getText();
                // Now Create from String the JAVA object
                Gson gson = new Gson();
                Notification invitation = gson.fromJson(str, Notification.class);
                // Check the type of notification (Mail is username in this case)
                if (invitation.getType().equals("Answer-Accept/Decline")) {
                    User receiver = dataAccess.getUserProfile(invitation.getToUserEmail());
                    invitation.setToUserEmail(receiver.getEmail());
                }
                // Insert
                Boolean response = dataAccess.insertNewNotification(invitation);
                // Set the response headers
                map.put("results", response);
                return new JsonMapRepresentation(map);
            }
            catch(IOException e) {
                mapError.put("error", "System Exception");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }



    @Override
    protected Representation put(Representation entity) throws ResourceException {

        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();

        // Get UserId
        String userIdStr = getRequestAttributes().get("userId").toString();
        if (userIdStr.equals("null")) {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
        int userId = Integer.parseInt(userIdStr);

        // Access the headers of the request!
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if (customAuth.checkUserAuthToken(token, userIdStr)) {
            // Delete this notification
            try {
                String str = entity.getText();
                // Now Create from String the JAVA object
                Gson gson = new Gson();
                Integer idNotification = gson.fromJson(str, Integer.class);
                // Delete notification
                int response = dataAccess.deleteUserNotification(idNotification);
                // Set the response headers
                map.put("results", response);
                return new JsonMapRepresentation(map);
            }
            catch(IOException e) {
                mapError.put("error", "System Exception");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }
}
