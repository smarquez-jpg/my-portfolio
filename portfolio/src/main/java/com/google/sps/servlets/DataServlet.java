// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.io.*;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    public class Message{
        String sender, message, recipient;
        public Message(String sndr, String msg){
            sender = sndr;
            message = msg;
        }
    }
    List<Message> messages = new ArrayList<>();
    int numberOfComments = 0;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        
        Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            String comment = (String) entity.getProperty("text");
            String sender = (String) entity.getProperty("sender");
            Message nComment = new Message(sender, comment);
            messages.add(nComment);
            
            
        }
        Gson gson = new Gson();
        response.setContentType("application/json;");
        List<Message> limitedMessages = new ArrayList<>();
        if(numberOfComments == 0){
            response.getWriter().println(gson.toJson(limitedMessages));

        }
        else {
            for(int i = 0; i < numberOfComments; i++){
                limitedMessages.add(messages.get(i));
            }
            response.getWriter().println(gson.toJson(limitedMessages));

        }
        
    }
    

    /**
   * Converts a ServerStats instance into a JSON string using the Gson library
   */
    private String convertToJsonUsingGson(Message messages) {
        Gson gson = new Gson();
        String json = gson.toJson(messages);
        return json;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    
    
    // Get the input from the form.
        String text = getParameter(request, "text-input", "");
        numberOfComments = getNumberOfComments(request);
        if (numberOfComments == -1) {
            response.setContentType("text/html");
            response.getWriter().println("Please enter an integer between 1 and 100.");
            return;
        }
    // Respond with the result.
        response.setContentType("text/html;");
        response.getWriter().println(text);

    // Add comment to datastore
        long timestamp = System.currentTimeMillis();

        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("sender", "Steven");
        commentEntity.setProperty("text", text);
        commentEntity.setProperty("time", timestamp);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

    // Redirect back to the HTML page.
        response.sendRedirect("/index.html");
    }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /* Returns number of comments to display */
    private int getNumberOfComments(HttpServletRequest request) {
    // Get the input from the form.
        String numberOfCommentsString = request.getParameter("comments-choice");
    // Convert the input to an int.
        int numberOfComments;
        try {
        numberOfComments = Integer.parseInt(numberOfCommentsString);
        } catch (NumberFormatException e) {
        System.err.println("Could not convert to int: " + numberOfCommentsString);
        return -1;
        }
        /*
    // Check that the input is between 1 and size of messages.
        if (numberOfComments < 1 || numberOfComments > messages.size()) {
            System.err.println("Number choice is out of range: " + numberOfCommentsString);
            return -1;
        }*/

        return numberOfComments;
    }
}