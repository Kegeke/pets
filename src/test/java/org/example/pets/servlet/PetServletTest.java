package org.example.pets.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pets.service.PetService;
import org.example.pets.servlet.dto.PetInDto;
import org.example.pets.servlet.dto.PetOutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PetServletTest {
    private PetService service;
    private PetServlet servlet;
    private StringWriter responseStringWriter;
    private PrintWriter responsePrintWriter;
    private ObjectMapper objectMapper;
    private HttpServletResponse response;
    private AtomicInteger responseStatus;
    private HttpServletRequest request;

    @BeforeEach
    void init() throws IOException {

        objectMapper = new ObjectMapper();

        servlet = new PetServlet();
        service = Mockito.mock(PetService.class);

        Field serviceField;
        try {
            serviceField = PetServlet.class.getDeclaredField("petService");
            serviceField.setAccessible(true);
            serviceField.set(servlet, service);
            serviceField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response = Mockito.mock(HttpServletResponse.class);
        responseStringWriter = new StringWriter();
        responsePrintWriter = new PrintWriter(responseStringWriter);
        Mockito.when(response.getWriter()).thenReturn(responsePrintWriter);

        responseStatus = new AtomicInteger();
        Mockito.doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) {
                return responseStatus.getAndSet((Integer) invocation.getArguments()[0]);
            }
        }).when(response).setStatus(Mockito.anyInt());

        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void shouldReturnAllPets() throws ServletException, IOException {
        List<PetOutDto> petOutDtos = List.of(new PetOutDto(),
                new PetOutDto());
        Mockito.when(service.getPets()).thenReturn(petOutDtos);

        Mockito.when(request.getParameter("id")).thenReturn(null);
        servlet.doGet(request, response);
        List<PetOutDto> pet = objectMapper.readValue(responseStringWriter.toString(),
                new TypeReference<List<PetOutDto>>() {
                });

        assertEquals(petOutDtos.size(), pet.size());
    }

    @Test
    void shouldReturnPetsById() throws ServletException, IOException {
        int petIdToGet = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(petIdToGet));
        Mockito.when(service.getPetById(petIdToGet))
                .thenReturn(Optional.of(new PetOutDto()));

        servlet.doGet(request, response);
        PetOutDto petOutDto = objectMapper.readValue(responseStringWriter.toString(),
                PetOutDto.class);
        assertNotNull(petOutDto);
    }

    @Test
    void shouldReturnCorrectStatusWhenGetWithIncorrrectIdFormat()
            throws ServletException, IOException {
        Mockito.when(request.getParameter("id")).thenReturn("4.5");
        servlet.doGet(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenGetWithInvalidId() throws ServletException, IOException {
        int petIdToGet = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(petIdToGet));
        Mockito.when(service.getPetById(petIdToGet)).thenReturn(Optional.empty());
        servlet.doGet(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenDeleteWithIncorrrectIdFormat()
            throws ServletException, IOException {
        Mockito.when(request.getParameter("id")).thenReturn("4.5");
        servlet.doDelete(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenDeleteWithInvalidValue()
            throws ServletException, IOException {
        int petIdToDelete = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(petIdToDelete));
        Mockito.when(service.deletePet(petIdToDelete)).thenReturn(false);
        servlet.doDelete(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenDelete() throws ServletException, IOException {
        int petIdToDelete = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(petIdToDelete));
        Mockito.when(service.deletePet(petIdToDelete)).thenReturn(true);
        servlet.doDelete(request, response);
        assertEquals(200, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenDeleteWithoutIdPathVariable()
            throws ServletException, IOException {
        Mockito.when(request.getParameter("id")).thenReturn(null);
        servlet.doDelete(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldWorkCorrectlyWhenPost() throws IOException, ServletException {
        String json = objectMapper.writeValueAsString(new PetInDto());
        Mockito.when(service.createPet(Mockito.any(PetInDto.class)))
                .thenReturn(new PetOutDto());

        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        servlet.doPost(request, response);
        assertEquals(201, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenPostWithInvalidJsonRequestBody()
            throws IOException, ServletException {
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader("asd")));
        servlet.doPost(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenPostWithIncompleateDataInput()
            throws IOException, ServletException {
        String json = objectMapper.writeValueAsString(new PetInDto());
        Mockito.when(service.createPet(Mockito.any(PetInDto.class)))
                .thenThrow(new RuntimeException(""));

        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        servlet.doPost(request, response);
        assertEquals(500, responseStatus.get());
    }

    @Test
    void shouldCorrectlyUpdateMovieById() throws ServletException, IOException {
        String json = objectMapper.writeValueAsString(new PetInDto());
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        int petIdToUpdate = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(petIdToUpdate));
        Mockito.when(service.updatePet(Mockito.any(PetInDto.class)))
                .thenReturn(new PetOutDto());
        servlet.doPut(request, response);
        PetOutDto movieDto = objectMapper.readValue(responseStringWriter.toString(),
                PetOutDto.class);
        assertNotNull(movieDto);
    }

    @Test
    void shouldReturnCorrectStatusWhenUpdateWithIncorrectDataInput()
            throws IOException, ServletException {
        String json = objectMapper.writeValueAsString(new PetInDto());
        Mockito.when(service.updatePet(Mockito.any(PetInDto.class)))
                .thenThrow(new RuntimeException(""));

        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        servlet.doPut(request, response);
        assertEquals(500, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenUpdateWithInvalidJsonRequestBody()
            throws IOException, ServletException {
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader("asd")));
        servlet.doPut(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenUpdateWithIncompleateDataInput()
            throws IOException, ServletException {
        String json = objectMapper.writeValueAsString(new PetInDto());
        Mockito.when(service.updatePet(Mockito.any(PetInDto.class)))
                .thenThrow(new RuntimeException(""));

        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        servlet.doPut(request, response);
        assertEquals(500, responseStatus.get());
    }
}
