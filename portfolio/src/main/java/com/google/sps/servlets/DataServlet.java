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

import java.io.*;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    public class Message{
        String sender, message, recipient;
        public Message(String sndr, String msg, String rcpnt){
            sender = sndr;
            message = msg;
            recipient = rcpnt;
        }
    }
    public ArrayList<String> sampleMessage = new ArrayList<String>();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //response.setContentType("text/html;");
        //response.getWriter().println("Hello Steven!");
        //create arraylist with sample inputs
        
        //sampleMessage.add("Steven");
        //sampleMessage.add("Nice day today");
        //sampleMessage.add("You");

        //TODO figure out how to get multiple messages on page

        //for(int i = 0; i < sampleMessage.size(); i++){
            // Convert the message to JSON
            Message nMessage = new Message("Anonymous", sampleMessage.get(sampleMessage.size()-1), "You");
            String json = convertToJsonUsingGson(nMessage);
            
            // Send the JSON as the response
            response.setContentType("application/json;");
            response.getWriter().println(json);
        //}
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

    // Respond with the result.
        response.setContentType("text/html;");
        response.getWriter().println(text);

    // Add comment to arraylist
        sampleMessage.add(text);

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
}