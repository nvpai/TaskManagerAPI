package com.example.taskmanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Task entity.
 * Following TDD principles - tests are written first.
 */
class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
    }

    @Test
    void shouldCreateTaskWithDefaultValues() {
        // When
        Task newTask = new Task();
        
        // Then
        assertThat(newTask.getId()).isNull();
        assertThat(newTask.getTitle()).isNull();
        assertThat(newTask.getDescription()).isNull();
        assertThat(newTask.isCompleted()).isFalse();
        assertThat(newTask.getCreatedAt()).isNull();
        assertThat(newTask.getUpdatedAt()).isNull();
    }

    @Test
    void shouldCreateTaskWithConstructor() {
        // When
        Task newTask = new Task("Test Title", "Test Description");
        
        // Then
        assertThat(newTask.getTitle()).isEqualTo("Test Title");
        assertThat(newTask.getDescription()).isEqualTo("Test Description");
        assertThat(newTask.isCompleted()).isFalse();
    }

    @Test
    void shouldSetAndGetId() {
        // When
        task.setId(1L);
        
        // Then
        assertThat(task.getId()).isEqualTo(1L);
    }

    @Test
    void shouldSetAndGetTitle() {
        // Given
        String title = "Learn Spring Boot";
        
        // When
        task.setTitle(title);
        
        // Then
        assertThat(task.getTitle()).isEqualTo(title);
    }

    @Test
    void shouldSetAndGetDescription() {
        // Given
        String description = "Complete the Spring Boot tutorial";
        
        // When
        task.setDescription(description);
        
        // Then
        assertThat(task.getDescription()).isEqualTo(description);
    }

    @Test
    void shouldSetAndGetCompletedStatus() {
        // When
        task.setCompleted(true);
        
        // Then
        assertThat(task.isCompleted()).isTrue();
    }

    @Test
    void shouldMarkTaskAsCompleted() {
        // When
        task.markAsCompleted();
        
        // Then
        assertThat(task.isCompleted()).isTrue();
    }

    @Test
    void shouldMarkTaskAsNotCompleted() {
        // Given
        task.setCompleted(true);
        
        // When
        task.markAsNotCompleted();
        
        // Then
        assertThat(task.isCompleted()).isFalse();
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCompleted(true);
        
        // When
        String result = task.toString();
        
        // Then
        assertThat(result).contains("Test Task");
        assertThat(result).contains("Test Description");
        assertThat(result).contains("true");
    }

    @Test
    void shouldHaveValidEqualsAndHashCode() {
        // Given
        Task task1 = new Task("Same Title", "Same Description");
        task1.setId(1L);
        
        Task task2 = new Task("Same Title", "Same Description");
        task2.setId(1L);
        
        Task task3 = new Task("Different Title", "Different Description");
        task3.setId(2L);
        
        // Then
        assertThat(task1).isEqualTo(task2);
        assertThat(task1).isNotEqualTo(task3);
        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());
    }
}