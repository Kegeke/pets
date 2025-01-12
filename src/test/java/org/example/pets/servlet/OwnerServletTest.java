package org.example.pets.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pets.service.OwnerService;
import org.example.pets.servlet.dto.OwnerInDto;
import org.example.pets.servlet.dto.OwnerOutDto;
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

public class OwnerServletTest {
    private OwnerService service;
    private OwnerServlet servlet;
    private StringWriter responseStringWriter;
    private PrintWriter responsePrintWriter;
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpServletResponse response;
    private AtomicInteger responseStatus;
    private HttpServletRequest request;

    @BeforeEach
    void init() throws IOException {
        servlet = new OwnerServlet();
        service = Mockito.mock(OwnerService.class);

        Field serviceField;
        try {
            serviceField = OwnerServlet.class.getDeclaredField("ownerService");
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
    void shouldReturnAllOwners() throws ServletException, IOException {
        List<OwnerOutDto> ownersOutgoingDtos = List.of(new OwnerOutDto(),
                new OwnerOutDto());
        Mockito.when(service.getOwners()).thenReturn(ownersOutgoingDtos);

        Mockito.when(request.getParameter("id")).thenReturn(null);
        servlet.doGet(request, response);
        List<OwnerOutDto> ownersDtos = objectMapper.readValue(
                responseStringWriter.toString(), new TypeReference<List<OwnerOutDto>>() {
                });
        assertEquals(ownersOutgoingDtos.size(), ownersDtos.size());
    }

    @Test
    void shouldReturnOwnerById() throws ServletException, IOException {
        int ownerIdToGet = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(ownerIdToGet));

        Mockito.when(service.getOwnerById(ownerIdToGet))
                .thenReturn(Optional.of(new OwnerOutDto()));

        servlet.doGet(request, response);
        OwnerOutDto ownerDto = objectMapper.readValue(responseStringWriter.toString(),
                OwnerOutDto.class);
        assertNotNull(ownerDto);
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
        int ownerIdToGet = 4;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(ownerIdToGet));
        Mockito.when(service.getOwnerById(ownerIdToGet)).thenReturn(Optional.empty());
        servlet.doGet(request, response);
        assertEquals(404, responseStatus.get());
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
        int ownerIdToDelete = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(ownerIdToDelete));
        Mockito.when(service.deleteOwner(ownerIdToDelete)).thenReturn(false);
        servlet.doDelete(request, response);
        assertEquals(400, responseStatus.get());
    }

    @Test
    void shouldReturnCorrectStatusWhenDelete() throws ServletException, IOException {
        int ownerIdToDelete = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(ownerIdToDelete));
        Mockito.when(service.deleteOwner(ownerIdToDelete)).thenReturn(true);
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
        String json = objectMapper.writeValueAsString(new OwnerInDto());
        Mockito.when(service.createOwner(Mockito.any(OwnerInDto.class)))
                .thenReturn(new OwnerOutDto());

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
        String json = objectMapper.writeValueAsString(new OwnerInDto());
        Mockito.when(service.createOwner(Mockito.any(OwnerInDto.class)))
                .thenThrow(new RuntimeException(""));

        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
        servlet.doPost(request, response);
        assertEquals(500, responseStatus.get());
    }

    @Test
    void shouldCorrectlyUpdateOwnerById() throws ServletException, IOException {
        String json = objectMapper.writeValueAsString(new OwnerInDto());
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new StringReader(json)));

        int ownerIdToUpdate = 2;
        Mockito.when(request.getParameter("id")).thenReturn(Integer.toString(ownerIdToUpdate));
        Mockito.when(service.updateOwner(Mockito.any(OwnerInDto.class)))
                .thenReturn(new OwnerOutDto());
        servlet.doPut(request, response);
        OwnerOutDto ownerDto = objectMapper.readValue(responseStringWriter.toString(),
                OwnerOutDto.class);
        assertNotNull(ownerDto);
    }

    @Test
    void shouldReturnCorrectStatusWhenUpdateWithIncompleateDataInput()
            throws IOException, ServletException {
        String json = objectMapper.writeValueAsString(new OwnerInDto());
        Mockito.when(service.updateOwner(Mockito.any(OwnerInDto.class)))
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
}
