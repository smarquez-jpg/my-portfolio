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

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
//import CommentMessage;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    /*TODO move class into own file*/
    public class CommentMessage {
        private String sender; 
        private String message;
        private String imgUrl;
        public CommentMessage(String sndr, String msg, String img){
            sender = sndr;
            message = msg;
            imgUrl = img;
        }
    }
    public List<CommentMessage> messages = new ArrayList<>();
    int numberOfCommentsToDisplay = 0;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            String comment = (String) entity.getProperty("text");
            String sender = (String) entity.getProperty("sender");
            String image = (String) entity.getProperty("imgUrl");
            CommentMessage nComment = new CommentMessage(sender, comment, image);
            messages.add(nComment);
        }
        Gson gson = new Gson();
        response.setContentType("application/json;");
        List<CommentMessage> limitedMessages = new ArrayList<>();
        if(numberOfCommentsToDisplay == 0){
            response.getWriter().println(gson.toJson(limitedMessages));
            return;
        }
        for(int i = 0; i < numberOfCommentsToDisplay; i++){

            limitedMessages.add(messages.get(i));
        }

        response.getWriter().println(gson.toJson(limitedMessages));

    }
    

    /**
   * Converts a ServerStats instance into a JSON string using the Gson library
   */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the input from the form.
        String text = getParameter(request, "text-input", "");
        numberOfCommentsToDisplay = getNumberOfCommentsToDisplay(request);
        if (numberOfCommentsToDisplay < 1 || numberOfCommentsToDisplay > 100) {
            response.setContentType("text/html");
            response.getWriter().println("Please enter an integer between 1 and 100.");
            return;
        }
        // Respond with the result.
        response.setContentType("text/html;");
        response.getWriter().println(text);

        // Add comment to datastore
        /*long timestamp = System.currentTimeMillis();

        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("sender", "Steven");
        commentEntity.setProperty("text", text);
        commentEntity.setProperty("time", timestamp);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        // Redirect back to the HTML page.
        response.sendRedirect("/index.html");*/
    }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value.equals(null)) {
            return defaultValue;
        }
        return value;
    }

    /* Returns number of comments to display */
    private int getNumberOfCommentsToDisplay(HttpServletRequest request) {
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
        return numberOfComments;
    }
}