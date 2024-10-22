# Lunchtime Restaurant Discovery
by: Clare Kinery

## Overview

This project is an Android application designed to help users discover restaurants nearby. The app utilizes the Google Places API for data retrieval and includes the following core features:

- Accesses the user's current location (with permission).
- Displays nearby restaurants upon launch.
- Provides a search feature to find restaurants by name or type.
- Users can view search results as a list or as pins on a map.
- Selecting a restaurant marker displays basic information about the venue.

## Instructions to Run the Project

1. Open the repository on your local machine.
2. Open the project in Android Studio Koala Feature Drop | 2024.1.2 Patch 1.
3. Ensure that your environment is configured to target:
    - minSdk: 24
    - targetSdk: 34
4. Obtain your Google Places API secret from your Google Cloud Console.
5. Create a `secrets.properties` file:
    - In the root of your project, create a file named `secrets.properties`.
    - Add the following line to the file:
      ```
      PLACES_API_KEY=your_api_key_here
      ```
    - Replace `your_api_key_here` with your actual API key.
6. Build and run the project directly on an emulator or a physical device (no additional configurations required).

## Focus Areas

- **Architecture**: Employed a clean MVVM architecture to manage separation of concerns and data flow.
- **Data Management**: Focused on proper state handling and organizing data efficiently using ViewModel and Flow.

## Technologies Used

- Jetpack Compose for UI
- Hilt for dependency injection
- Coroutines and Flows for background tasks and reactive programming
- Retrofit and Moshi for networking and JSON parsing
- Google Places API for restaurant data

## Trade-offs and Design Choices

- The UI design was kept simple to focus on core functionality and scalability.
- Accessibility (a11y) and testing were not prioritized due to time constraints.
- No tests (unit, integration, or UI) were implemented due to the project's scope and deadline.

## Areas Showcasing Expertise

- **Jetpack Compose**: Developed a modern, scalable UI using Compose.
- **State Management**: Managed app state effectively with Coroutines and Flow.
- **Data Integration**: Integrated Google Places API for location-based restaurant discovery.

## Future Improvements

Given more time, I would focus on:

- Implementing thorough accessibility (a11y) testing.
- Adding light/dark mode to enhance the UI.
- Refining the UI for a cleaner and more robust experience.
- Adding unit, integration, and UI testing for better coverage.
- Enabling live location updates so that the app can fetch the user's location continuously and update restaurant suggestions in real-time.