package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for TaskRepository.
 * Uses @DataJpaTest for focused repository testing with H2 in-memory database.
 */
@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Task sampleTask1;
    private Task sampleTask2;
    private Task completedTask;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        taskRepository.deleteAll();
        
        // Create sample tasks
        sampleTask1 = new Task("Learn Spring Boot", "Complete Spring Boot tutorial");
        sampleTask2 = new Task("Write Tests", "Write unit and integration tests");
        completedTask = new Task("Setup Project", "Initialize Maven project");
        completedTask.markAsCompleted();
    }

    @Test
    void shouldSaveAndFindTask() {
        // When
        Task savedTask = taskRepository.save(sampleTask1);
        
        // Then
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Learn Spring Boot");
        assertThat(savedTask.getDescription()).isEqualTo("Complete Spring Boot tutorial");
        assertThat(savedTask.isCompleted()).isFalse();
        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindTaskById() {
        // Given
        Task savedTask = entityManager.persistAndFlush(sampleTask1);
        
        // When
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        
        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Learn Spring Boot");
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {
        // When
        Optional<Task> foundTask = taskRepository.findById(999L);
        
        // Then
        assertThat(foundTask).isEmpty();
    }

    @Test
    void shouldFindAllTasks() {
        // Given
        entityManager.persistAndFlush(sampleTask1);
        entityManager.persistAndFlush(sampleTask2);
        entityManager.persistAndFlush(completedTask);
        
        // When
        List<Task> allTasks = taskRepository.findAll();
        
        // Then
        assertThat(allTasks).hasSize(3);
        assertThat(allTasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Learn Spring Boot", "Write Tests", "Setup Project");
    }

    @Test
    void shouldFindTasksByCompletedStatus() {
        // Given
        entityManager.persistAndFlush(sampleTask1);
        entityManager.persistAndFlush(sampleTask2);
        entityManager.persistAndFlush(completedTask);
        
        // When
        List<Task> completedTasks = taskRepository.findByCompleted(true);
        List<Task> incompleteTasks = taskRepository.findByCompleted(false);
        
        // Then
        assertThat(completedTasks).hasSize(1);
        assertThat(completedTasks.get(0).getTitle()).isEqualTo("Setup Project");
        
        assertThat(incompleteTasks).hasSize(2);
        assertThat(incompleteTasks).extracting(Task::getTitle)
            .containsExactlyInAnyOrder("Learn Spring Boot", "Write Tests");
    }

    @Test
    void shouldFindTasksByTitleContaining() {
        // Given
        entityManager.persistAndFlush(sampleTask1);
        entityManager.persistAndFlush(sampleTask2);
        entityManager.persistAndFlush(completedTask);
        
        // When
        List<Task> tasksWithSpring = taskRepository.findByTitleContainingIgnoreCase("spring");
        List<Task> tasksWithTest = taskRepository.findByTitleContainingIgnoreCase("test");
        
        // Then
        assertThat(tasksWithSpring).hasSize(1);
        assertThat(tasksWithSpring.get(0).getTitle()).isEqualTo("Learn Spring Boot");
        
        assertThat(tasksWithTest).hasSize(1);
        assertThat(tasksWithTest.get(0).getTitle()).isEqualTo("Write Tests");
    }

    @Test
    void shouldUpdateTask() {
        // Given
        Task savedTask = entityManager.persistAndFlush(sampleTask1);
        entityManager.clear(); // Clear persistence context
        
        // When
        savedTask.setTitle("Updated Title");
        savedTask.markAsCompleted();
        Task updatedTask = taskRepository.save(savedTask);
        
        // Then
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.isCompleted()).isTrue();
        assertThat(updatedTask.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteTask() {
        // Given
        Task savedTask = entityManager.persistAndFlush(sampleTask1);
        Long taskId = savedTask.getId();
        
        // When
        taskRepository.deleteById(taskId);
        
        // Then
        Optional<Task> deletedTask = taskRepository.findById(taskId);
        assertThat(deletedTask).isEmpty();
    }

    @Test
    void shouldCountTasks() {
        // Given
        entityManager.persistAndFlush(sampleTask1);
        entityManager.persistAndFlush(sampleTask2);
        
        // When
        long count = taskRepository.count();
        
        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldCheckIfTaskExists() {
        // Given
        Task savedTask = entityManager.persistAndFlush(sampleTask1);
        
        // When & Then
        assertThat(taskRepository.existsById(savedTask.getId())).isTrue();
        assertThat(taskRepository.existsById(999L)).isFalse();
    }
}