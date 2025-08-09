package com.outworkit.outworkit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outworkit.outworkit.entity.Equipment;
import com.outworkit.outworkit.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipmentController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EquipmentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EquipmentService equipmentService;

        @Autowired
        private ObjectMapper objectMapper;

        private Equipment equipment1;
        private List<Equipment> equipmentList;

        @BeforeEach
        void setUp() {
                equipment1 = new Equipment();
                equipment1.setId(1L);
                equipment1.setName("Treadmill");
                equipment1.setDescription("Professional treadmill for cardio exercises");

                Equipment equipment2 = new Equipment();
                equipment2.setId(2L);
                equipment2.setName("Dumbbells");
                equipment2.setDescription("Set of adjustable dumbbells");

                equipmentList = Arrays.asList(equipment1, equipment2);
        }

        @Test
        void getAllEquipments_ShouldReturnListOfEquipments() throws Exception {
                // Given
                when(equipmentService.getEquipments()).thenReturn(equipmentList);

                // When & Then
                mockMvc.perform(get("/api/v1/equipment"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Treadmill"))
                                .andExpect(jsonPath("$[0].description")
                                                .value("Professional treadmill for cardio exercises"))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].name").value("Dumbbells"))
                                .andExpect(jsonPath("$[1].description").value("Set of adjustable dumbbells"));

                verify(equipmentService, times(1)).getEquipments();
        }

        @Test
        void getAllEquipments_WhenNoEquipments_ShouldReturnEmptyList() throws Exception {
                // Given
                when(equipmentService.getEquipments()).thenReturn(Arrays.asList());

                // When & Then
                mockMvc.perform(get("/api/v1/equipment"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(0));

                verify(equipmentService, times(1)).getEquipments();
        }

        @Test
        void getEquipmentById_WithValidId_ShouldReturnEquipment() throws Exception {
                // Given
                Long equipmentId = 1L;
                when(equipmentService.getEquipment(equipmentId)).thenReturn(equipment1);

                // When & Then
                mockMvc.perform(get("/api/v1/equipment/{equipmentId}", equipmentId))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Treadmill"))
                                .andExpect(jsonPath("$.description")
                                                .value("Professional treadmill for cardio exercises"));

                verify(equipmentService, times(1)).getEquipment(equipmentId);
        }

        @Test
        void getEquipmentById_WithInvalidId_ShouldHandleException() throws Exception {
                // Given
                Long equipmentId = 999L;
                when(equipmentService.getEquipment(equipmentId))
                                .thenThrow(new RuntimeException("Equipment not found"));

                // When & Then
                mockMvc.perform(get("/api/v1/equipment/{equipmentId}", equipmentId))
                                .andExpect(status().is5xxServerError());

                verify(equipmentService, times(1)).getEquipment(equipmentId);
        }

        @Test
        void createEquipment_WithValidEquipment_ShouldReturnCreatedEquipment() throws Exception {
                // Given
                Equipment newEquipment = new Equipment();
                newEquipment.setName("Rowing Machine");
                newEquipment.setDescription("Professional rowing machine for full body workout");

                Equipment savedEquipment = new Equipment();
                savedEquipment.setId(3L);
                savedEquipment.setName("Rowing Machine");
                savedEquipment.setDescription("Professional rowing machine for full body workout");

                when(equipmentService.saveOrUpdate(any(Equipment.class))).thenReturn(savedEquipment);

                // When & Then
                mockMvc.perform(post("/api/v1/equipment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newEquipment)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(3))
                                .andExpect(jsonPath("$.name").value("Rowing Machine"))
                                .andExpect(jsonPath("$.description")
                                                .value("Professional rowing machine for full body workout"));

                verify(equipmentService, times(1)).saveOrUpdate(any(Equipment.class));
        }

        @Test
        void createEquipment_WithInvalidEquipment_ShouldReturnBadRequest() throws Exception {
                // Given - Equipment without required fields (assuming name is required)
                Equipment invalidEquipment = new Equipment();
                invalidEquipment.setDescription("Description without name");

                // When & Then
                mockMvc.perform(post("/api/v1/equipment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidEquipment)))
                                .andExpect(status().isBadRequest());

                verify(equipmentService, never()).saveOrUpdate(any(Equipment.class));
        }

        @Test
        void updateEquipment_WithValidIdAndEquipment_ShouldReturnUpdatedEquipment() throws Exception {
                // Given
                Long equipmentId = 1L;
                Equipment updatedEquipment = new Equipment();
                updatedEquipment.setId(equipmentId);
                updatedEquipment.setName("Updated Treadmill");
                updatedEquipment.setDescription("Updated professional treadmill");

                when(equipmentService.saveOrUpdate(any(Equipment.class))).thenReturn(updatedEquipment);

                Equipment requestEquipment = new Equipment();
                requestEquipment.setName("Updated Treadmill");
                requestEquipment.setDescription("Updated professional treadmill");

                // When & Then
                mockMvc.perform(put("/api/v1/equipment/{equipmentId}", equipmentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestEquipment)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Updated Treadmill"))
                                .andExpect(jsonPath("$.description").value("Updated professional treadmill"));

                verify(equipmentService, times(1))
                                .saveOrUpdate(argThat(equipment -> equipment.getId().equals(equipmentId) &&
                                                equipment.getName().equals("Updated Treadmill")));
        }

        @Test
        void updateEquipment_WithInvalidEquipment_ShouldReturnBadRequest() throws Exception {
                // Given
                Long equipmentId = 1L;
                Equipment invalidEquipment = new Equipment();
                invalidEquipment.setDescription("Description without name");

                // When & Then
                mockMvc.perform(put("/api/v1/equipment/{equipmentId}", equipmentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidEquipment)))
                                .andExpect(status().isBadRequest());

                verify(equipmentService, never()).saveOrUpdate(any(Equipment.class));
        }

        @Test
        void deleteEquipment_WithValidId_ShouldReturnNoContent() throws Exception {
                // Given
                Long equipmentId = 1L;
                doNothing().when(equipmentService).delete(equipmentId);

                // When & Then
                mockMvc.perform(delete("/api/v1/equipment/{equipmentId}", equipmentId))
                                .andExpect(status().isNoContent());

                verify(equipmentService, times(1)).delete(equipmentId);
        }

        @Test
        void deleteEquipment_WithNonExistentId_ShouldHandleException() throws Exception {
                // Given
                Long equipmentId = 999L;
                doThrow(new ResourceNotFoundException(String.format("Equipment not found with ID: %d", equipmentId)))
                                .when(equipmentService).delete(equipmentId);

                // When & Then
                mockMvc.perform(delete("/api/v1/equipment/{equipmentId}", equipmentId))
                                .andExpect(status().isNotFound());

                verify(equipmentService, times(1)).delete(equipmentId);
        }

        @Test
        void updateEquipment_WithBadRequestException_ShouldReturnCustomBadRequest() throws Exception {
                // Given
                Long equipmentId = 1L;
                doThrow(new com.outworkit.outworkit.controller.exception.BadRequestException("Custom bad request"))
                                .when(equipmentService).saveOrUpdate(any(Equipment.class));

                // When & Then
                mockMvc.perform(put("/api/v1/equipment/{equipmentId}", equipmentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"\"}")) // Simulate invalid input
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Solicitud incorrecta"))
                                .andExpect(jsonPath("$.message").value("Custom bad request"));

                verify(equipmentService, times(1)).saveOrUpdate(any(Equipment.class));
        }

        @Test
        void updateEquipment_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
                // Given
                Long equipmentId = 1L;

                // When & Then
                mockMvc.perform(put("/api/v1/equipment/{equipmentId}", equipmentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""))
                                .andExpect(status().isBadRequest());

                verify(equipmentService, never()).saveOrUpdate(any(Equipment.class));
        }

        @Test
        void getAllEndpoints_ShouldHaveCorrectContentType() throws Exception {
                // Given
                when(equipmentService.getEquipments()).thenReturn(equipmentList);
                when(equipmentService.getEquipment(1L)).thenReturn(equipment1);
                when(equipmentService.saveOrUpdate(any(Equipment.class))).thenReturn(equipment1);

                // Test GET all
                mockMvc.perform(get("/api/v1/equipment"))
                                .andExpect(header().string("Content-Type", "application/json"));

                // Test GET by ID
                mockMvc.perform(get("/api/v1/equipment/1"))
                                .andExpect(header().string("Content-Type", "application/json"));

                // Test POST
                mockMvc.perform(post("/api/v1/equipment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(equipment1)))
                                .andExpect(header().string("Content-Type", "application/json"));

                // Test PUT
                mockMvc.perform(put("/api/v1/equipment/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(equipment1)))
                                .andExpect(header().string("Content-Type", "application/json"));
        }
}