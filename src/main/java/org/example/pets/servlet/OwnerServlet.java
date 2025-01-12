package org.example.pets.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pets.service.OwnerService;
import org.example.pets.servlet.dto.OwnerInDto;
import org.example.pets.servlet.dto.OwnerOutDto;

import java.io.IOException;
import java.io.Serial;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/owners")
public class OwnerServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String JSON_MIME = "application/json";

    private ObjectMapper objectMapper;
    private OwnerService ownerService;

    public OwnerServlet() {
        super();
        this.objectMapper = new ObjectMapper();
        this.ownerService = new OwnerService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") != null) {
            doGetById(req, resp);

            return;
        }

        try {
            resp.setContentType(JSON_MIME);
            resp.getWriter().print(objectMapper.writeValueAsString(ownerService.getOwners()));
        } catch (RuntimeException e) {
            resp.setStatus(505);
            resp.getWriter().print(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String json = req.getReader().lines().collect(Collectors.joining("\n"));
            OwnerInDto ownerInDto = objectMapper.readValue(json, OwnerInDto.class);
            OwnerOutDto ownerOutDto = ownerService.createOwner(ownerInDto);

            resp.setContentType(JSON_MIME);
            resp.getWriter().print(objectMapper.writeValueAsString(ownerOutDto));
            resp.setStatus(201);
        } catch (JsonProcessingException e) {
            resp.setStatus(400);
            resp.getWriter().print("Неправильный входной JSON. " + e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String json = req.getReader().lines().collect(Collectors.joining("\n"));
            OwnerInDto ownerInDto = objectMapper.readValue(json, OwnerInDto.class);
            OwnerOutDto ownerOutDto = ownerService.updateOwner(ownerInDto);

            resp.setContentType(JSON_MIME);
            resp.getWriter().print(objectMapper.writeValueAsString(ownerOutDto));
        } catch (JsonProcessingException e) {
            resp.setStatus(400);
            resp.getWriter().print("Неправильный входной JSON " + e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer ownerId = getIdFromPathVariableOrSetErrorInResponse(req, resp);

        if (ownerId == null) {
            return;
        }

        try {
            boolean resultStatus = ownerService.deleteOwner(ownerId);

            if (resultStatus) {
                resp.setStatus(200);
                resp.getWriter().print("Хозяин был удален с id " + ownerId);
            } else {
                resp.setStatus(400);
                resp.getWriter().print("Хозяина с таким id нет");
            }
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    private void doGetById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer ownerId = getIdFromPathVariableOrSetErrorInResponse(req, resp);

        if (ownerId == null) {
            return;
        }

        try {
            Optional<OwnerOutDto> optionalOwner = ownerService.getOwnerById(ownerId);

            if (optionalOwner.isEmpty()) {
                resp.setStatus(404);
                resp.getWriter().print("Хозяина с таким id нет");

                return;
            }

            resp.setContentType(JSON_MIME);
            objectMapper.writeValue(resp.getWriter(), optionalOwner.get());
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    private Integer getIdFromPathVariableOrSetErrorInResponse(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Integer ownerId = null;

        try {
            String ownerIdAsString = req.getParameter("id");
            Objects.requireNonNull(ownerIdAsString);
            ownerId = Integer.parseInt(req.getParameter("id"));
        } catch (NullPointerException e) {
            resp.setStatus(400);
            resp.getWriter().print("Должна быть переменная в path \"id\"");
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().print("Не верный формат id");
        }

        return ownerId;
    }
}
