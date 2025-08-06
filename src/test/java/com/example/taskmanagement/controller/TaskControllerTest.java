package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TaskController.
 * Uses @WebMvcTest for focused web layer testing with mocked service layer.
 * Tests REST API endpoints and HTTP status codes.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;
    private Task completedTask;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Learn Spring Boot", "Complete Spring Boot tutorial");
        sampleTask.setId(1L);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());

        completedTask = new Task("Setup Project", "Initialize Maven project");
        completedTask.setId(2L);
        completedTask.markAsCompleted();
        completedTask.setCreatedAt(LocalDateTime.now().minusHours(1));
        completedTask.setUpdatedAt(LocalDateTime.now());

        taskList = Arrays.asList(sampleTask, completedTask);
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        // Given
        when(taskService.getAllTasks()).thenReturn(taskList);

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Learn Spring Boot")))
                .andExpect(jsonPath("$[0].description", is("Complete Spring Boot tutorial")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Setup Project")))
                .andExpect(jsonPath("$[1].completed", is(true)));

        verify(taskService).getAllTasks();
    }

    @Test
    void shouldGetEmptyListWhenNoTasks() throws Exception {
        // Given
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService).getAllTasks();
    }

    @Test
    void shouldGetTaskById() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));

        // When & Then
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Learn Spring Boot")))
                .andExpect(jsonPath("$.description", is("Complete Spring Boot tutorial")))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(taskService).getTaskById(1L);
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        // Given
        when(taskService.getTaskById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(999L);
    }

    @Test
    void shouldCreateTask() throws Exception {
        // Given
        Task newTask = new Task("New Task", "New Description");
        when(taskService.createTask(any(Task.class))).thenReturn(sampleTask);

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Learn Spring Boot")));

        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void shouldReturn400WhenCreatingTaskWithInvalidData() throws Exception {
        // Given
        Task invalidTask = new Task("", "Description"); // Empty title

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(Task.class));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        // Given
        Task updatedTask = new Task("Updated Title", "Updated Description");
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(Optional.of(sampleTask));

        // When & Then
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));

        verify(taskService).updateTask(eq(1L), any(Task.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
        // Given
        Task updatedTask = new Task("Updated Title", "Updated Description");
        when(taskService.updateTask(anyLong(), any(Task.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());

        verify(taskService).updateTask(eq(999L), any(Task.class));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        // Given
        when(taskService.deleteTask(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
        // Given
        when(taskService.deleteTask(anyLong())).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService).deleteTask(999L);
    }

    @Test
    void shouldMarkTaskAsCompleted() throws Exception {
        // Given
        when(taskService.markTaskAsCompleted(1L)).thenReturn(Optional.of(completedTask));

        // When & Then
        mockMvc.perform(patch("/api/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(taskService).markTaskAsCompleted(1L);
    }

    @Test
    void shouldReturn404WhenMarkingNonExistentTaskAsCompleted() throws Exception {
        // Given
        when(taskService.markTaskAsCompleted(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/tasks/999/complete"))
                .andExpect(status().isNotFound());

        verify(taskService).markTaskAsCompleted(999L);
    }

    @Test
    void shouldMarkTaskAsNotCompleted() throws Exception {
        // Given
        when(taskService.markTaskAsNotCompleted(2L)).thenReturn(Optional.of(sampleTask));

        // When & Then
        mockMvc.perform(patch("/api/tasks/2/incomplete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(taskService).markTaskAsNotCompleted(2L);
    }

    @Test
    void shouldGetTasksByCompletedStatus() throws Exception {
        // Given
        List<Task> completedTasks = Arrays.asList(completedTask);
        when(taskService.getTasksByCompletedStatus(true)).thenReturn(completedTasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/completed/true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completed", is(true)));

        verify(taskService).getTasksByCompletedStatus(true);
    }

    @Test
    void shouldSearchTasks() throws Exception {
        // Given
        String keyword = "spring";
        List<Task> searchResults = Arrays.asList(sampleTask);
        when(taskService.searchTasks(keyword)).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/tasks/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", containsString("Spring")));

        verify(taskService).searchTasks(keyword);
    }

    @Test
    void shouldReturn400WhenSearchingWithoutKeyword() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/search"))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).searchTasks(anyString());
    }

    @Test
    void shouldGetTaskStatistics() throws Exception {
        // Given
        TaskService.TaskStatistics stats = new TaskService.TaskStatistics(10L, 6L, 4L);
        when(taskService.getTaskStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalTasks", is(10)))
                .andExpect(jsonPath("$.completedTasks", is(6)))
                .andExpect(jsonPath("$.incompleteTasks", is(4)))
                .andExpect(jsonPath("$.completionPercentage", is(60.0)));

        verify(taskService).getTaskStatistics();
    }
}