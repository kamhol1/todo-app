
# Todo App

Todo App is a task management web app that allows you to manage your tasks. It also allows you to create group of tasks to make it easier to classify different tasks. You can also create projects that represent long term Todos in which you can specify different steps to be completed to finish the entire project.

## Features
The Todo App has the following features:

* Task management: Users can create tasks and toggle them as done/undone. Tasks have a description, deadline and done status.
* Task groups management: Users can create groups of tasks, which can be associated with project steps.
* Project management: Users can create projects. Projects have a title and steps to complete. Every step has a description and deadline. Users can specify the whole project's deadline and on this basis create a group (project) of tasks (steps) to do.
* The functionalities described above are secured with user authentication implemented with Spring Security.

## Tech Stack
* Java 17
* Spring Framework
* Hibernate
* Flyway
* H2 Database
* JUnit5
* Thymeleaf

## Installation
To run the Todo App locally, follow these steps:

1. Run a command:
``` git clone https://github.com/kamhol1/todo-app.git ```

2. In the project directory, build a project using:
``` mvn clean install ```

3. Run the application:
``` mvn spring-boot:run ```

4. Access the Todo App in your web browser at http://localhost:8080
<br />
Note: The application uses an H2 Database in a file, so the data will persist as long as the application is running. If you restart the application, the data will be reloaded from the file.
