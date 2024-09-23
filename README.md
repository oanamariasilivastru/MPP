# Multi-Platform Project (MPP)

## Overview
The MPP project is a comprehensive client-server system designed to manage and process transportation-related data. It includes server-side components for handling data persistence, networking, and RESTful APIs, alongside a frontend client for user interaction. The project is modular and scalable, leveraging modern development tools and technologies for both backend and frontend development.

## Features
- **Modular architecture**: Separate modules for networking, persistence, REST services, and client interaction.
- **Client-server communication**: Utilizing REST APIs for seamless data exchange.
- **Database integration**: Persistent storage for handling and querying transportation data.
- **Frontend application**: Built with Vite and React for a fast, modern UI.

## Project Structure
- **CursaNetworking**: Handles network communication between client and server.
- **CursaPersistence**: Manages database operations and ensures data is stored and retrieved efficiently.
- **CursaServer**: Backend server responsible for coordinating requests, business logic, and communication with the database.
- **CurseClient**: Frontend client application built with Vite and React for interacting with the system.
- **CurseModel**: Data models representing transportation-related entities.
- **RestServices**: REST API endpoints exposing various system functionalities.

## Technologies

### Backend
- **Java/Groovy**: Main languages used for backend services and scripting.
- **Gradle**: Build automation tool for managing dependencies and compiling the project.
- **SQLite**: Local database for persistent data storage (`curse.db`).
- **REST API**: Provides endpoints for client-server communication.

### Frontend
- **Vite**: Fast build tool for modern web projects.
- **React**: Frontend JavaScript library used for building dynamic user interfaces.

## Tools
- **GitHub Actions**: For continuous integration and deployment (CI/CD).
- **JUnit**: Used for unit testing in the Java-based modules.
- **SQLite**: Database system for storing transportation data.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/oanamariasilivastru/MPP.git
    cd MPP
    ```

2. **Backend setup**:
   - Ensure Java and Gradle are installed.
   - Build the project:
    ```bash
    gradle build
    ```

3. **Frontend setup**:
   - Navigate to the client directory:
    ```bash
    cd CurseClient
    npm install
    npm run dev
    ```

4. **Database configuration**: The system uses an SQLite database (`curse.db`). Make sure the database is correctly configured and accessible.

## Usage
- Run the server:
    ```bash
    gradle run
    ```
- Access the frontend client by opening the development server URL (usually [http://localhost:3000](http://localhost:3000)).

## License
This project is licensed under the MIT License.

---

Feel free to adjust any sections as necessary for your project!
