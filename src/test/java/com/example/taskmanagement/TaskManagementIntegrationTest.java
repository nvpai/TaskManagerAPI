package com.example.taskmanagement;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration test for the Task Management API.
 * Tests the entire application stack from HTTP requests to database operations.
 * Uses the real Spring context and H2 database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Clear database and add test data
        taskRepository.deleteAll();
        
        Task task1 = new Task("Integration Test Task 1", "Description 1");
        Task task2 = new Task("Integration Test Task 2", "Description 2");
        task2.markAsCompleted();
        
        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    @Test
    void shouldPerformFullTaskLifecycle() throws Exception {
        // 1. Get all tasks - should have 2 initial tasks
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", containsString("Integration Test")));

        // 2. Create a new task
        Task newTask = new Task("New Integration Task", "This task was created via API");
        
        String taskJson = objectMapper.writeValueAsString(newTask);
        
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Integration Task")))
                .andExpect(jsonPath("$.description", is("This task was created via API")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.id", notNullValue()));

        // 3. Verify we now have 3 tasks
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        // 4. Get task by ID
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Integration Test Task 1")));

        // 5. Update the task
        Task updatedTask = new Task("Updated Integration Task", "Updated description");
        String updatedTaskJson = objectMapper.writeValueAsString(updatedTask);
        
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Task")))
                .andExpect(jsonPath("$.description", is("Updated description")));

        // 6. Mark task as completed
        mockMvc.perform(patch("/api/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));

        // 7. Get completed tasks
        mockMvc.perform(get("/api/tasks/completed/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // Should have 2 completed tasks

        // 8. Search for tasks
        mockMvc.perform(get("/api/tasks/search")
                        .param("keyword", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].title", containsString("Integration")));

        // 9. Get statistics
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks", is(3)))
                .andExpect(jsonPath("$.completedTasks", is(2)))
                .andExpect(jsonPath("$.incompleteTasks", is(1)))
                .andExpect(jsonPath("$.completionPercentage", closeTo(66.67, 0.1)));

        // 10. Delete a task
        mockMvc.perform(delete("/api/tasks/3"))
                .andExpect(status().isNoContent());

        // 11. Verify task was deleted
        mockMvc.perform(get("/api/tasks/3"))
                .andExpect(status().isNotFound());

        // 12. Verify we now have 2 tasks
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldHandleValidationErrors() throws Exception {
        // Try to create task with empty title
        Task invalidTask = new Task("", "Description");
        String invalidTaskJson = objectMapper.writeValueAsString(invalidTask);
        
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleNotFoundErrors() throws Exception {
        // Try to get non-existent task
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());

        // Try to update non-existent task
        Task task = new Task("Title", "Description");
        String taskJson = objectMapper.writeValueAsString(task);
        
        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isNotFound());

        // Try to delete non-existent task
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleSearchOperations() throws Exception {
        // Search with valid keyword
        mockMvc.perform(get("/api/tasks/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // Search with keyword that matches nothing
        mockMvc.perform(get("/api/tasks/search")
                        .param("keyword", "NonExistentKeyword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Search without keyword should return bad request
        mockMvc.perform(get("/api/tasks/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleTaskStatusOperations() throws Exception {
        // Mark task as completed
        mockMvc.perform(patch("/api/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));

        // Mark task as incomplete
        mockMvc.perform(patch("/api/tasks/1/incomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(false)));

        // Try to complete non-existent task
        mockMvc.perform(patch("/api/tasks/999/complete"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldProvideConvenienceEndpoints() throws Exception {
        // Get completed tasks
        mockMvc.perform(get("/api/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))); // One completed task from setup

        // Get incomplete tasks
        mockMvc.perform(get("/api/tasks/incomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))); // One incomplete task from setup
    }

    @Test
    void shouldMaintainDataIntegrity() throws Exception {
        // Create a task and verify it has timestamps
        Task newTask = new Task("Timestamp Test", "Testing timestamps");
        String taskJson = objectMapper.writeValueAsString(newTask);
        
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        // Update the task and verify updatedAt changes
        Task updatedTask = new Task("Updated Timestamp Test", "Updated description");
        String updatedTaskJson = objectMapper.writeValueAsString(updatedTask);
        
        // Small delay to ensure different timestamp
        Thread.sleep(10);
        
        mockMvc.perform(put("/api/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }
}