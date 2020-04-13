package ys09.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Patch;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import ys09.auth.CustomAuth;
import ys09.conf.Configuration;
import ys09.data.DataAccess;
import ys09.data.SprintDB;
import ys09.exceptions.ErrorMessage;
import ys09.model.PBI;
import ys09.model.Project;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class BacklogResource extends ServerResource {
    private final DataAccess dataAccess = Configuration.getInstance().getDataAccess();

    @Override
    protected Representation get() throws ResourceException {

        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();

        // Unauthorized access if user is not the product owner
        // Get UserId
        String userId = getRequestAttributes().get("userId").toString();
        if (userId.equals("null")) {
            mapError.put("error", "Unauthorized projects");
            return new JsonMapRepresentation(mapError);
        }
        int user = Integer.parseInt(userId);

        // Check if the user is Product Owner

        // Read the query parameters of the url
        String isEpicStr = getQuery().getValues("isEpic");
        Boolean isEpic;
        if (isEpicStr == null) {
            isEpic = false;
        }
        else {
            isEpic = Boolean.parseBoolean(isEpicStr);
        }
        // Convert it to boolean

        // Get projectId
        String projectId = getRequestAttributes().get("projectId").toString();
        int idProject = Integer.parseInt(projectId);
        // Get Epic_id
        String epicIdStr = getQuery().getValues("epicId");
        int epicId;
        if(epicIdStr == null) {
            epicId = 0;
        }
        else {
            epicId = Integer.parseInt(epicIdStr);
        }

        //int epicId = Integer.parseInt(epicIdStr);

        // Access the headers of the request !
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if(customAuth.checkAuthToken(token)) {
            // Get Projects only for the current user
            // Show them in the index page
            if(customAuth.userValidation(token, userId)) {
                // Get all the pbis of this project (either Epics or Stories)
                List<PBI> pbis = dataAccess.getProjectPBIs(idProject, isEpic, epicId);
                map.put("results", pbis);
                // Set the response headers
                return new JsonMapRepresentation(map);
            }
            else {
                mapError.put("error", "Unauthorized projects");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }

    // Update PBI's Sprint_id
    @Patch
    public Representation update(Representation entity) {
        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();
        // Get UserId
        String userId = getRequestAttributes().get("userId").toString();
        if (userId.equals("null")) {
            mapError.put("error", "Unauthorized projects");
            return new JsonMapRepresentation(mapError);
        }
        int user = Integer.parseInt(userId);

        // Access the headers of the request!
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if(customAuth.checkAuthToken(token)) {
            // Insert a project only for the current user (Product Owner)
            if(customAuth.userValidation(token, userId)) {
                // Get the whole json body representation
                try {
                    String str = entity.getText();
                    // Now Create from String the JAVA object
                    System.out.println(str);
                    // Convert it to JSON
                    // Deserialize a JSON array
                    // https://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
                    /*
                    Type listType = new TypeToken<ArrayList<Epic>>(){}.getType();
                    List<Epic> epics = new Gson().fromJson(str, listType);
                    // Check if the user is Product Owner
                    // Returns List of Project Id's

                    List <Integer> projectsList = dataAccess.createAuthProjectList(user, "Product Owner");
                    System.out.println(projectsList);
                    // Use ArrayUtils to find if the project id is in the array
                    if(projectsList.contains(epics.get(0).getProject_id())) {
                        dataAccess.updateSprintId(epics);
                        // Update the
                        // Set the response headers
                        map.put("result", "Updated PBI's");
                        return new JsonMapRepresentation(map);
                    }
                    else {
                        ErrorMessage errorMessage = new ErrorMessage("Unauthorized", Status.CLIENT_ERROR_UNAUTHORIZED);
                        map.put("error", errorMessage);
                        return new JsonMapRepresentation(map);
                    }
                    */
                    Type listType = new TypeToken<ArrayList<PBI>>(){}.getType();
                    List<PBI> pbis = new Gson().fromJson(str, listType);
                    //System.out.println(pbis.get(0).getIdPBI());
                    System.out.println("lolo");
                    SprintDB sprintDB = new SprintDB();
                    sprintDB.updateSprintId(pbis);
                    // Update the
                    // Set the response headers
                    map.put("PBI Update", "result");
                    return new JsonMapRepresentation(map);
                }
                catch(IOException e) {
                    mapError.put("result", "System Exception");
                    return new JsonMapRepresentation(mapError);
                }
            }
            else {
                mapError.put("error", "Unauthorized projects");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }


    // Insert a new PBI into database
    @Override
    protected Representation post(Representation entity) throws ResourceException {

        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();
        //System.out.println("Inside post");

        // Get UserId
        String userIdStr = getRequestAttributes().get("userId").toString();
        if (userIdStr.equals("null")) {
            mapError.put("error", "Unauthorized projects");
            return new JsonMapRepresentation(mapError);
        }
        int userId = Integer.parseInt(userIdStr);

        // Check if is Epic
        String isEpicStr = getQuery().getValues("isEpic");
        Boolean isEpic;
        if (isEpicStr == null)
            isEpic = false;
        else isEpic = Boolean.parseBoolean(isEpicStr);
        // Convert it to boolean

        // Get projectId
        String projectId = getRequestAttributes().get("projectId").toString();
        int idProject = Integer.parseInt(projectId);

        // Access the headers of the request!
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if(customAuth.checkAuthToken(token)) {
            // Insert the pbi given (either epic or story)
            if(customAuth.userValidation(token, userIdStr)) {
                // Get the whole json body representation
                try {
                    String str = entity.getText();
                    // Now Create from String the JAVA object
                    Gson gson = new Gson();
                    PBI pbi = gson.fromJson(str, PBI.class);
                    pbi.setIsEpic(isEpic);
                    // Insert
                    PBI response = dataAccess.insertNewPBI(pbi);
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
                mapError.put("error", "Unauthorized projects");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }


    // Update an existing PBI
    @Override
    protected Representation put(Representation entity) throws ResourceException {

        // New map string (which is the json name) and objects
        Map<String, Object> map = new HashMap<>();
        Map<String, String> mapError = new HashMap<>();
        //System.out.println("Inside post");

        // Get UserId
        String userIdStr = getRequestAttributes().get("userId").toString();
        if (userIdStr.equals("null")) {
            mapError.put("error", "Unauthorized projects");
            return new JsonMapRepresentation(mapError);
        }
        int userId = Integer.parseInt(userIdStr);

        // Check if is Epic
        String isEpicStr = getQuery().getValues("isEpic");
        Boolean isEpic;
        if (isEpicStr == null)
            isEpic = false;
        else isEpic = Boolean.parseBoolean(isEpicStr);
        // Convert it to boolean

        // Access the headers of the request!
        Series requestHeaders = (Series)getRequest().getAttributes().get("org.restlet.http.headers");
        String token = requestHeaders.getFirstValue("auth");

        if (token == null) {
            mapError.put("error", "null");
            return new JsonMapRepresentation(mapError);
        }
        CustomAuth customAuth = new CustomAuth();

        if(customAuth.checkAuthToken(token)) {
            // Insert the pbi given (either epic or story)
            if(customAuth.userValidation(token, userIdStr)) {
                // Get the whole json body representation
                try {
                    String str = entity.getText();
                    // Now Create from String the JAVA object
                    Gson gson = new Gson();
                    PBI pbi = gson.fromJson(str, PBI.class);
                    pbi.setIsEpic(isEpic);
                    // Update
                    PBI response = dataAccess.updatePBI(pbi);
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
                mapError.put("error", "Unauthorized projects");
                return new JsonMapRepresentation(mapError);
            }
        }
        else {
            mapError.put("error", "Unauthorized user");
            return new JsonMapRepresentation(mapError);
        }
    }
}
