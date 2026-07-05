# Patient Visit Tracker - Phase 1

## Project Overview

The Patient Visit Tracker is a command-line Java program that stores and manages basic patient visit records.

Phase 1 focuses on program logic, object-oriented programming, text-file loading, and data validation. This version does not use a graphical user interface or database.

## Patient Visit Data

Each patient visit contains six fields:

- Visit ID
- Patient first and last name
- Visit date
- Reason for visit
- Provider name
- Follow-up needed

## Program Features

The user can:

1. Load patient visits from a text file.
2. Display all stored visits.
3. Add a new patient visit.
4. Update any field of an existing visit.
5. Delete a visit using its Visit ID.
6. Count how many visits require follow-up.
7. Exit the program.

The program continues running until the user selects Exit.

## Validation Rules

The program validates all user input to prevent errors and crashes.

- Visit IDs must be whole numbers from 1 through 999999.
- Duplicate Visit IDs cannot be added.
- Patient names must include a first and last name.
- Patient names cannot exceed 50 characters.
- Visit dates must be valid future dates using `YYYY-MM-DD`.
- Provider names cannot exceed 50 characters.
- Required text fields cannot be blank.
- Follow-up values must be Yes or No.
- Invalid files and missing records display clear error messages.

## Custom Action

The custom action counts every patient visit whose Follow-Up Needed field is set to Yes. The program then displays the total number of visits requiring follow-up.

## Text File Format

The included text file contains 20 patient visit samples.

Each line uses the following format:

`VisitID|PatientName|YYYY-MM-DD|Reason|Provider|Yes/No`

Example:

`101|A. Smith|2027-01-01|Annual checkup|Dr. Lee|Yes`

## Project Classes

- `PatientVisit` stores and validates one patient visit.
- `PatientVisitTracker` manages the list and performs CRUD operations.
- `PatientVisitApp` displays the menu and handles user interaction.

## Running the Program

Open the project in IntelliJ, open `PatientVisitApp.java`, and run its `main` method.

To load the included sample data, select option 1 and enter:

`data\patient_visits.txt`
