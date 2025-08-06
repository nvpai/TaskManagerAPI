package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Task management.
 * Provides RESTful endpoints for CRUD operations on tasks.
 * 
 * This controller demonstrates:
 * - RESTful API design principles
 * - Proper HTTP status codes
 * - Input validation
 * - Error handling
 * - Clean separation of concerns
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Allow CORS for frontend integration
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all tasks.
     * 
     * @return list of all tasks
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get a specific task by ID.
     * 
     * @param id the task ID
     * @return the task if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new task.
     * 
     * @param task the task to create
     * @return the created task with 201 status
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        // Basic validation
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * Update an existing task.
     * 
     * @param id the task ID
     * @param task the updated task data
     * @return the updated task if found, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        Optional<Task> updatedTask = taskService.updateTask(id, task);
        return updatedTask.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a task.
     * 
     * @param id the task ID
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build()
                      : ResponseEntity.notFound().build();
    }

    /**
     * Mark a task as completed.
     * 
     * @param id the task ID
     * @return the updated task if found, 404 if not found
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsCompleted(@PathVariable Long id) {
        Optional<Task> updatedTask = taskService.markTaskAsCompleted(id);
        return updatedTask.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mark a task as not completed.
     * 
     * @param id the task ID
     * @return the updated task if found, 404 if not found
     */
    @PatchMapping("/{id}/incomplete")
    public ResponseEntity<Task> markTaskAsNotCompleted(@PathVariable Long id) {
        Optional<Task> updatedTask = taskService.markTaskAsNotCompleted(id);
        return updatedTask.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get tasks by completion status.
     * 
     * @param completed the completion status
     * @return list of tasks with the specified completion status
     */
    @GetMapping("/completed/{completed}")
    public ResponseEntity<List<Task>> getTasksByCompletedStatus(@PathVariable boolean completed) {
        List<Task> tasks = taskService.getTasksByCompletedStatus(completed);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks by keyword.
     * 
     * @param keyword the search keyword
     * @return list of matching tasks
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam(required = false) String keyword) {
        if (keyword == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Task> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task statistics.
     * 
     * @return task statistics including counts and completion percentage
     */
    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getTaskStatistics() {
        TaskService.TaskStatistics statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get completed tasks (convenience endpoint).
     * 
     * @return list of completed tasks
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        List<Task> completedTasks = taskService.getTasksByCompletedStatus(true);
        return ResponseEntity.ok(completedTasks);
    }

    /**
     * Get incomplete tasks (convenience endpoint).
     * 
     * @return list of incomplete tasks
     */
    @GetMapping("/incomplete")
    public ResponseEntity<List<Task>> getIncompleteTasks() {
        List<Task> incompleteTasks = taskService.getTasksByCompletedStatus(false);
        return ResponseEntity.ok(incompleteTasks);
    }

    /**
     * Exception handler for validation errors.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * Exception handler for method argument validation errors.
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body("Validation failed: " + e.getMessage());
    }

    /**
     * Global exception handler for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body("An unexpected error occurred: " + e.getMessage());
    }
}