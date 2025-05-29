package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/event")
public class EventServlet extends HttpServlet {

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);  // <-- ADD THIS

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventdb", "root", "1234");

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM event").executeQuery();
            List<Map<String, String>> elist = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> event = new HashMap<>();
                event.put("id", resultSet.getString("id"));
                event.put("name", resultSet.getString("name"));
                event.put("description", resultSet.getString("description"));
                event.put("date", resultSet.getString("date"));
                event.put("place", resultSet.getString("place"));
                elist.add(event);
            }

            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getWriter(), elist);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);  // <-- ADD THIS

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventdb", "root", "1234");

            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO event (id, name, description, date, place) VALUES (?, ?, ?, ?, ?)"
            );

            stmt.setString(1, event.get("id"));
            stmt.setString(2, event.get("name"));
            stmt.setString(3, event.get("description"));
            stmt.setString(4, event.get("date"));
            stmt.setString(5, event.get("place"));

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);  // <-- ADD THIS

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventdb", "root", "1234");

            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE event SET name = ?, description = ?, date = ?, place = ? WHERE id = ?"
            );

            stmt.setString(1, event.get("name"));
            stmt.setString(2, event.get("description"));
            stmt.setString(3, event.get("date"));
            stmt.setString(4, event.get("place"));
            stmt.setString(5, event.get("id"));

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);  // Ensure CORS headers are here

        String id = req.getParameter("id");  // Get ID from query string

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventdb", "root", "1234");
            System.out.println("id : "  + id);
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM event WHERE id = ?");
            stmt.setString(1, id);

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getWriter(), rows);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new ObjectMapper().writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
        }
    }


    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);  // <-- ADD THIS
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
