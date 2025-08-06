package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Task entity.
 * Extends JpaRepository to provide basic CRUD operations and custom query methods.
 * 
 * This interface demonstrates Spring Data JPA features:
 * - Automatic CRUD operations
 * - Query methods by convention
 * - Custom JPQL queries
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks by completion status.
     * Spring Data JPA automatically generates the implementation based on method name.
     * 
     * @param completed the completion status to filter by
     * @return list of tasks with the specified completion status
     */
    List<Task> findByCompleted(boolean completed);

    /**
     * Find tasks by title containing a specific string (case-insensitive).
     * Useful for search functionality.
     * 
     * @param title the title substring to search for
     * @return list of tasks whose title contains the given string
     */
    List<Task> findByTitleContainingIgnoreCase(String title);

    /**
     * Find tasks by description containing a specific string (case-insensitive).
     * Another search method for filtering by description.
     * 
     * @param description the description substring to search for
     * @return list of tasks whose description contains the given string
     */
    List<Task> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Custom JPQL query to find tasks by title or description containing a keyword.
     * Demonstrates custom query writing in Spring Data JPA.
     * 
     * @param keyword the keyword to search for in title or description
     * @return list of tasks matching the search criteria
     */
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    /**
     * Count tasks by completion status.
     * Useful for dashboard statistics.
     * 
     * @param completed the completion status to count
     * @return number of tasks with the specified completion status
     */
    long countByCompleted(boolean completed);

    /**
     * Find all tasks ordered by creation date (newest first).
     * Demonstrates ordering in query methods.
     * 
     * @return list of all tasks ordered by creation date descending
     */
    List<Task> findAllByOrderByCreatedAtDesc();

    /**
     * Find all tasks ordered by completion status and creation date.
     * Shows incomplete tasks first, then completed tasks, both ordered by creation date.
     * 
     * @return list of all tasks ordered by completion status and creation date
     */
    List<Task> findAllByOrderByCompletedAscCreatedAtDesc();
}