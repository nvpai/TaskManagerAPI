#!/bin/bash

# Task Management API Demo Script
# This script demonstrates all the available endpoints

BASE_URL="http://localhost:8080/api/tasks"

echo "🚀 Task Management API Demo"
echo "============================="
echo

# 1. Create some tasks
echo "1. Creating sample tasks..."
echo "----------------------------"

echo "Creating task 1..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn Spring Boot", "description": "Complete the Spring Boot tutorial and build a REST API", "completed": false}' \
  -w "\n"

echo "Creating task 2..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"title": "Write Unit Tests", "description": "Create comprehensive unit tests following TDD principles", "completed": false}' \
  -w "\n"

echo "Creating task 3..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"title": "Setup Database", "description": "Configure H2 database for development", "completed": true}' \
  -w "\n"

echo
echo "2. Retrieving all tasks..."
echo "----------------------------"
curl -X GET $BASE_URL -H "Content-Type: application/json" | jq '.'
echo

echo "3. Getting a specific task (ID: 1)..."
echo "--------------------------------------"
curl -X GET $BASE_URL/1 -H "Content-Type: application/json" | jq '.'
echo

echo "4. Marking task as completed (ID: 1)..."
echo "----------------------------------------"
curl -X PATCH $BASE_URL/1/complete -H "Content-Type: application/json" | jq '.'
echo

echo "5. Getting completed tasks..."
echo "-----------------------------"
curl -X GET "$BASE_URL/completed/true" -H "Content-Type: application/json" | jq '.'
echo

echo "6. Searching for tasks..."
echo "-------------------------"
curl -X GET "$BASE_URL/search?query=Spring" -H "Content-Type: application/json" | jq '.'
echo

echo "7. Getting task statistics..."
echo "-----------------------------"
curl -X GET $BASE_URL/statistics -H "Content-Type: application/json" | jq '.'
echo

echo "8. Updating a task (ID: 2)..."
echo "------------------------------"
curl -X PUT $BASE_URL/2 \
  -H "Content-Type: application/json" \
  -d '{"title": "Write Comprehensive Tests", "description": "Create unit, integration, and end-to-end tests", "completed": false}' | jq '.'
echo

echo "9. Final task list..."
echo "---------------------"
curl -X GET $BASE_URL -H "Content-Type: application/json" | jq '.'
echo

echo "✅ Demo completed!"
echo "=================="
echo
echo "Available endpoints:"
echo "- GET    /api/tasks                 - Get all tasks"
echo "- POST   /api/tasks                 - Create a new task"
echo "- GET    /api/tasks/{id}            - Get task by ID"
echo "- PUT    /api/tasks/{id}            - Update task"
echo "- DELETE /api/tasks/{id}            - Delete task"
echo "- PATCH  /api/tasks/{id}/complete   - Mark task as completed"
echo "- PATCH  /api/tasks/{id}/incomplete - Mark task as incomplete"
echo "- GET    /api/tasks/completed/{status} - Get tasks by completion status"
echo "- GET    /api/tasks/search?query={term} - Search tasks"
echo "- GET    /api/tasks/statistics      - Get task statistics"