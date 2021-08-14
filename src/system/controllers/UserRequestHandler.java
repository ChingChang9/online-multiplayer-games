package system.controllers;

import com.sun.net.httpserver.HttpExchange;
import shared.DTOs.Requests.*;
import shared.DTOs.Responses.FriendResponseBody;
import shared.DTOs.Responses.GeneralTemplateDataResponseBody;
import shared.DTOs.Responses.LoginResponseBody;
import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import system.entities.template.QuizTemplate;
import system.use_cases.managers.UserManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

public class UserRequestHandler extends RequestHandler {

    /**
     * a user manager that can manipulate all user entities
     */
    private final UserManager userManager;

    /**
     * Constructor for UserRequestHandler class.
     * @param um user manager that contains all user entities and able to make change to them
     */
    public UserRequestHandler(UserManager um) {
        this.userManager = um;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().getPath().split("/")[2];
        switch (specification) {
            case "username":
                handleGetUsername(exchange);
                break;
            case "userid":
                handleGetUserID(exchange);
                break;
            case "friends":
                handleGetFriends(exchange);
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String specification = exchange.getRequestURI().getPath().split("/")[2];
        switch (specification) {
            case "login":
                handleLogin(exchange);
                break;
            case "logout":
                handleLogout(exchange);
                break;
            case "trial":
                handleTrial(exchange);
                break;
            case "register":
                handleRegister(exchange);
                break;
            case "delete":
                handleDeleteUser(exchange);
                break;
            case "send-pending-friend":
                handleSendPendingFriend(exchange);
                break;
            case "decline-pending-friend":
                //handleDeclineFriend(exchange);
                break;
            case "accept-pending-friend":
                break;
            case "remove-friend":
                break;
            default:
                sendResponse(exchange, 404, "Unidentified Request.");
        }
    }

    private void handleSendPendingFriend(HttpExchange exchange) throws IOException {
        SendPendingFriendBody body = gson.fromJson(getRequestBody(exchange), SendPendingFriendBody.class);
        try {
            userManager.getUser(body.ownerID).addPendingFriend(body.senderID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleDeleteUser(HttpExchange exchange) throws IOException {
        DeleteUserRequestBody body = gson.fromJson(getRequestBody(exchange), DeleteUserRequestBody.class);
        try {
            userManager.deleteUser(body.userID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 400, "Invalid user ID.");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        RegisterRequestBody body = gson.fromJson(getRequestBody(exchange), RegisterRequestBody.class);
        try {
            userManager.createUser(body.username, body.password, body.role);
            sendResponse(exchange, 204, null);
        } catch (DuplicateUsernameException e) {
            sendResponse(exchange, 403, "Duplicate username.");
        }
    }

    private void handleTrial(HttpExchange exchange) throws IOException {
        String trialID = userManager.createTrialUser();
        sendResponse(exchange, 200, "{\"userID\":\"" + trialID+"\"}");
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        LoginRequestBody body = gson.fromJson(getRequestBody(exchange), LoginRequestBody.class);
        try {
            String userID = userManager.login(body.username, body.password);
            LoginResponseBody resBody = new LoginResponseBody();
            resBody.userID = userID;
            resBody.role = userManager.getUserRole(userID);
            sendResponse(exchange, 200, gson.toJson(resBody));
        } catch (InvalidUsernameException | IncorrectPasswordException | ExpiredUserException e) {
            sendResponse(exchange, 400, "User doesn't exist, is expired, or the password is incorrect.");
        } catch (InvalidUserIDException e) {
            throw new RuntimeException("Invalid user ID. This should never happen.");
        }
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        LogoutRequestBody body = gson.fromJson(getRequestBody(exchange), LogoutRequestBody.class);
        try {
            userManager.logout(body.userID);
            sendResponse(exchange, 204, null);
        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 404, "Invalid user ID.");
        }
    }

    private void handleGetFriends(HttpExchange exchange) throws IOException {
        //handle get ownerID
        String ownerID;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            ownerID = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        //handle load friend list as response body
        Set<FriendResponseBody> dataSet = new HashSet<>();

        try{
            Set<String> allfriends = userManager.getFriendList(ownerID);
            for (String id : allfriends){
                FriendResponseBody frb = new FriendResponseBody();
                frb.userID = id;
                frb.userName = userManager.getUsername(id);
                dataSet.add(frb);
            }

        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 404, "Invalid user ID.");
        }
        //send response
        sendResponse(exchange, 200, gson.toJson(dataSet));
    }

    private void handleGetPendingFriends(HttpExchange exchange) throws IOException {
        //handle get ownerID
        String ownerID;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            ownerID = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        //handle load friend list as response body
        Set<FriendResponseBody> dataSet = new HashSet<>();

        try{
            Set<String> allfriends = userManager.getPendingFriendList(ownerID);
            for (String id : allfriends){
                FriendResponseBody frb = new FriendResponseBody();
                frb.userID = id;
                frb.userName = userManager.getUsername(id);
                dataSet.add(frb);
            }

        } catch (InvalidUserIDException e) {
            sendResponse(exchange, 404, "Invalid user ID.");
        }
        //send response
        sendResponse(exchange, 200, gson.toJson(dataSet));
    }

    private void handleGetUserID(HttpExchange exchange) throws IOException {
        String username;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            username = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        try {
            sendResponse(exchange, 200, "{\"userID\":\"" + userManager.getUserId(username)+"\"}");
        } catch (InvalidUsernameException e) {
            sendResponse(exchange, 400, "Invalid username.");
        }

    }

    private void handleGetUsername(HttpExchange exchange) throws IOException {
        String userID;
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendResponse(exchange, 400, "Missing Query.");
                return;
            }
            userID = query.split("=")[1];
        } catch (MalformedURLException e) {
            sendResponse(exchange, 404, "Malformed URL.");
            return;
        }
        try {
            sendResponse(exchange, 200, "{\"username\":\"" + userManager.getUsername(userID)+"\"}");
        } catch (InvalidUserIDException e) {
            e.printStackTrace();
        }
    }


}
