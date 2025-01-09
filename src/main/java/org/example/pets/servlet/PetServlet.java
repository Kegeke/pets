package org.example.pets.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pets.service.PetService;
import org.example.pets.servlet.dto.PetInDto;
import org.example.pets.servlet.dto.PetOutDto;

import java.io.IOException;
import java.io.Serial;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/pets")
public class PetServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String JSON_MIME = "application/json";

    private final ObjectMapper objectMapper;
    private final PetService petService;

    public PetServlet() {
        super();
        this.objectMapper = new ObjectMapper();
        this.petService = new PetService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("id") != null) {
            doGetById(req, resp);
            return;
        }

        resp.setContentType(JSON_MIME);

        try {
            resp.getWriter().print(objectMapper.writeValueAsString(petService.getPets()));
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = req.getReader().lines().collect(Collectors.joining("\n"));

        try {
            PetInDto petInDto = objectMapper.readValue(json, PetInDto.class);
            PetOutDto petOutDto = petService.createPet(petInDto);

            resp.setContentType(JSON_MIME);
            resp.getWriter().print(objectMapper.writeValueAsString(petOutDto));
            resp.setStatus(201);
        } catch (JsonProcessingException e) {
            resp.setStatus(400);
            resp.getWriter().print("Неверный файл JSON." + e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String json = req.getReader().lines().collect(Collectors.joining("\n"));
            PetInDto petInDto = objectMapper.readValue(json, PetInDto.class);
            PetOutDto petOutDto = petService.updatePet(petInDto);

            resp.setContentType(JSON_MIME);
            resp.getWriter().print(objectMapper.writeValueAsString(petOutDto));
        } catch (JsonProcessingException e) {
            resp.setStatus(400);
            resp.getWriter().print("Неверный файл JSON" + e.getMessage());
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer petId = getIdFromPathVariableOrSetErrorInResponse(req, resp);

        if (petId == null) {
            return;
        }

        try {
            boolean resultStatus = petService.deletePet(petId);

            if (resultStatus) {
                resp.setStatus(200);
                resp.getWriter().print("Питомец удален с id " + petId);
            } else {
                resp.setStatus(400);
                resp.getWriter().print("Питомец с таким id несуществует");
            }
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    private void doGetById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer petId = getIdFromPathVariableOrSetErrorInResponse(req, resp);

        if (petId == null) {
            return;
        }

        try {
            Optional<PetOutDto> optionalPet = petService.getPetById(petId);

            if (optionalPet.isEmpty()) {
                resp.setStatus(400);
                resp.getWriter().print("Питомец с таким id не найден");
                return;
            }

            resp.setContentType(JSON_MIME);
            resp.getWriter().print(objectMapper.writeValueAsString(optionalPet.get()));
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }
    }

    private Integer getIdFromPathVariableOrSetErrorInResponse(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Integer petId = null;

        try {
            String petIdAsStr = req.getParameter("id");
            Objects.requireNonNull(petIdAsStr);
            petId = Integer.parseInt(req.getParameter("id"));
        } catch (NullPointerException e) {
            resp.setStatus(400);
            resp.getWriter().print("Должна быть переменная \"id\"");
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().print("Питомец с таким id не существует");
        } catch (RuntimeException e) {
            resp.setStatus(500);
            resp.getWriter().print(e.getMessage());
        }

        return petId;
    }
}
