package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Task management.
 * Contains business logic and coordinates between controller and repository layers.
 * 
 * This class demonstrates:
 * - Business logic separation
 * - Transaction management
 * - Input validation
 * - Error handling
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Creates a new task.
     * 
     * @param task the task to create
     * @return the created task with generated ID
     * @throws IllegalArgumentException if task is null
     */
    public Task createTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        return taskRepository.save(task);
    }

    /**
     * Creates a new task with title and description.
     * 
     * @param title the task title
     * @param description the task description
     * @return the created task
     * @throws IllegalArgumentException if title is null or empty
     */
    public Task createTask(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        Task task = new Task(title.trim(), description != null ? description.trim() : null);
        return taskRepository.save(task);
    }

    /**
     * Retrieves all tasks ordered by creation date (newest first).
     * 
     * @return list of all tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Retrieves a task by its ID.
     * 
     * @param id the task ID
     * @return optional containing the task if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Updates an existing task.
     * 
     * @param id the ID of the task to update
     * @param updatedTask the task with updated information
     * @return optional containing the updated task if found, empty otherwise
     */
    public Optional<Task> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
            .map(existingTask -> {
                // Update fields
                if (updatedTask.getTitle() != null && !updatedTask.getTitle().trim().isEmpty()) {
                    existingTask.setTitle(updatedTask.getTitle().trim());
                }
                if (updatedTask.getDescription() != null) {
                    existingTask.setDescription(updatedTask.getDescription().trim());
                }
                existingTask.setCompleted(updatedTask.isCompleted());
                
                return taskRepository.save(existingTask);
            });
    }

    /**
     * Marks a task as completed.
     * 
     * @param id the task ID
     * @return optional containing the updated task if found, empty otherwise
     */
    public Optional<Task> markTaskAsCompleted(Long id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.markAsCompleted();
                return taskRepository.save(task);
            });
    }

    /**
     * Marks a task as not completed.
     * 
     * @param id the task ID
     * @return optional containing the updated task if found, empty otherwise
     */
    public Optional<Task> markTaskAsNotCompleted(Long id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.markAsNotCompleted();
                return taskRepository.save(task);
            });
    }

    /**
     * Deletes a task by ID.
     * 
     * @param id the task ID
     * @return true if task was deleted, false if task was not found
     */
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Retrieves tasks by completion status.
     * 
     * @param completed the completion status to filter by
     * @return list of tasks with the specified completion status
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByCompletedStatus(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }

    /**
     * Searches tasks by keyword in title or description.
     * 
     * @param keyword the search keyword
     * @return list of matching tasks
     */
    @Transactional(readOnly = true)
    public List<Task> searchTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return taskRepository.findByTitleOrDescriptionContaining(keyword.trim());
    }

    /**
     * Gets task statistics.
     * 
     * @return task statistics including total, completed, and incomplete counts
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByCompleted(true);
        long incompleteTasks = taskRepository.countByCompleted(false);
        
        return new TaskStatistics(totalTasks, completedTasks, incompleteTasks);
    }

    /**
     * Statistics class for task counts.
     */
    public static class TaskStatistics {
        private final long totalTasks;
        private final long completedTasks;
        private final long incompleteTasks;

        public TaskStatistics(long totalTasks, long completedTasks, long incompleteTasks) {
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.incompleteTasks = incompleteTasks;
        }

        public long getTotalTasks() {
            return totalTasks;
        }

        public long getCompletedTasks() {
            return completedTasks;
        }

        public long getIncompleteTasks() {
            return incompleteTasks;
        }

        public double getCompletionPercentage() {
            return totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        }

        @Override
        public String toString() {
            return String.format("TaskStatistics{total=%d, completed=%d, incomplete=%d, completion=%.1f%%}",
                    totalTasks, completedTasks, incompleteTasks, getCompletionPercentage());
        }
    }
}