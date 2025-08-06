package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService.
 * Uses Mockito to mock dependencies and test business logic in isolation.
 * Demonstrates TDD approach with comprehensive test coverage.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private Task completedTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Learn Spring Boot", "Complete Spring Boot tutorial");
        sampleTask.setId(1L);
        
        completedTask = new Task("Setup Project", "Initialize Maven project");
        completedTask.setId(2L);
        completedTask.markAsCompleted();
    }

    @Test
    void shouldCreateTask() {
        // Given
        Task newTask = new Task("New Task", "Description");
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        Task result = taskService.createTask(newTask);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(taskRepository).save(newTask);
    }

    @Test
    void shouldCreateTaskWithTitleAndDescription() {
        // Given
        String title = "New Task";
        String description = "Task description";
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        Task result = taskService.createTask(title, description);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingTaskWithNullTitle() {
        // When & Then
        assertThatThrownBy(() -> taskService.createTask(null, "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Task title cannot be null or empty");
        
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingTaskWithEmptyTitle() {
        // When & Then
        assertThatThrownBy(() -> taskService.createTask("", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Task title cannot be null or empty");
        
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldGetAllTasks() {
        // Given
        List<Task> expectedTasks = Arrays.asList(sampleTask, completedTask);
        when(taskRepository.findAllByOrderByCreatedAtDesc()).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(sampleTask, completedTask);
        verify(taskRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldGetTaskById() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // When
        Optional<Task> result = taskService.getTaskById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sampleTask);
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {
        // Given
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.getTaskById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository).findById(999L);
    }

    @Test
    void shouldUpdateTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        Task updatedTask = new Task("Updated Title", "Updated Description");
        Optional<Task> result = taskService.updateTask(1L, updatedTask);

        // Then
        assertThat(result).isPresent();
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentTask() {
        // Given
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Task updatedTask = new Task("Updated Title", "Updated Description");
        Optional<Task> result = taskService.updateTask(999L, updatedTask);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldMarkTaskAsCompleted() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        Optional<Task> result = taskService.markTaskAsCompleted(1L);

        // Then
        assertThat(result).isPresent();
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(sampleTask);
    }

    @Test
    void shouldReturnEmptyWhenMarkingNonExistentTaskAsCompleted() {
        // Given
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.markTaskAsCompleted(999L);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldMarkTaskAsNotCompleted() {
        // Given
        when(taskRepository.findById(2L)).thenReturn(Optional.of(completedTask));
        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        // When
        Optional<Task> result = taskService.markTaskAsNotCompleted(2L);

        // Then
        assertThat(result).isPresent();
        verify(taskRepository).findById(2L);
        verify(taskRepository).save(completedTask);
    }

    @Test
    void shouldDeleteTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = taskService.deleteTask(1L);

        // Then
        assertThat(result).isTrue();
        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTask() {
        // Given
        when(taskRepository.existsById(anyLong())).thenReturn(false);

        // When
        boolean result = taskService.deleteTask(999L);

        // Then
        assertThat(result).isFalse();
        verify(taskRepository).existsById(999L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetTasksByCompletedStatus() {
        // Given
        List<Task> completedTasks = Arrays.asList(completedTask);
        when(taskRepository.findByCompleted(true)).thenReturn(completedTasks);

        // When
        List<Task> result = taskService.getTasksByCompletedStatus(true);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(completedTask);
        verify(taskRepository).findByCompleted(true);
    }

    @Test
    void shouldSearchTasks() {
        // Given
        String keyword = "spring";
        List<Task> searchResults = Arrays.asList(sampleTask);
        when(taskRepository.findByTitleOrDescriptionContaining(keyword)).thenReturn(searchResults);

        // When
        List<Task> result = taskService.searchTasks(keyword);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(sampleTask);
        verify(taskRepository).findByTitleOrDescriptionContaining(keyword);
    }

    @Test
    void shouldReturnEmptyListWhenSearchingWithEmptyKeyword() {
        // When
        List<Task> result = taskService.searchTasks("");

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository, never()).findByTitleOrDescriptionContaining(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenSearchingWithNullKeyword() {
        // When
        List<Task> result = taskService.searchTasks(null);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository, never()).findByTitleOrDescriptionContaining(anyString());
    }

    @Test
    void shouldGetTaskStatistics() {
        // Given
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countByCompleted(true)).thenReturn(6L);
        when(taskRepository.countByCompleted(false)).thenReturn(4L);

        // When
        TaskService.TaskStatistics stats = taskService.getTaskStatistics();

        // Then
        assertThat(stats.getTotalTasks()).isEqualTo(10L);
        assertThat(stats.getCompletedTasks()).isEqualTo(6L);
        assertThat(stats.getIncompleteTasks()).isEqualTo(4L);
        verify(taskRepository).count();
        verify(taskRepository).countByCompleted(true);
        verify(taskRepository).countByCompleted(false);
    }
}