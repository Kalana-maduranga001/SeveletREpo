package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dbcp")
public class DBCPServlet extends HttpServlet {
    BasicDataSource dataSource;
    @Override
    public void init() throws ServletException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/eventdb");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        dataSource.setInitialSize(50);
        dataSource.setMaxTotal(100);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement("select * from event").executeQuery();
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), elist);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO event (id, name, description, date, place) VALUES (?, ?, ?, ?,?)"
            );
            stmt.setString(1, event.get("id"));
            stmt.setString(2, event.get("name"));
            stmt.setString(3, event.get("description"));
            stmt.setString(4, event.get("date"));
            stmt.setString(5, event.get("place"));

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> event = mapper.readValue(req.getInputStream(), Map.class);

        try {
            Connection connection = dataSource.getConnection();
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
        String id = req.getParameter("id");

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM event WHERE id = ?");
            stmt.setString(1, id);

            int rows = stmt.executeUpdate();
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getWriter(), rows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}