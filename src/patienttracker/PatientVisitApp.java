package patienttracker;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the command-line menu for the Patient Visit Tracker.
 */
public final class PatientVisitApp {
    private final PatientVisitTracker tracker;
    private final Scanner keyboard;

    public PatientVisitApp() {
        tracker = new PatientVisitTracker();
        keyboard = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new PatientVisitApp().run();
    }

    /**
     * Displays the menu until the user selects Exit.
     * return zero after a normal exit
     */
    public int run() {
        System.out.println("\nPatient Visit Tracker - Phase 1");

        boolean running = true;

        while (running) {
            System.out.println(getMenu());
            int choice = readMenuChoice();
            String message;

            switch (choice) {
                case 1:
                    message = loadVisits();
                    break;
                case 2:
                    message = displayVisits();
                    break;
                case 3:
                    message = createVisit();
                    break;
                case 4:
                    message = updateVisit();
                    break;
                case 5:
                    message = deleteVisit();
                    break;
                case 6:
                    message = countFollowUps();
                    break;
                case 7:
                    message = "Program closed by user choice.";
                    running = false;
                    break;
                default:
                    message = "Invalid menu option.";
                    break;
            }

            System.out.println("\n" + message + "\n");
        }

        keyboard.close();
        return 0;
    }

    private String getMenu() {
        return "1. Load visits from a text file\n"
                + "2. Display all visits\n"
                + "3. Add a patient visit\n"
                + "4. Update a patient visit\n"
                + "5. Delete a patient visit\n"
                + "6. Count follow-up visits\n"
                + "7. Exit\n";
    }

    private int readMenuChoice() {
        while (true) {
            System.out.print("Choose an option: ");
            String input = keyboard.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);

                if (choice >= 1 && choice <= 7) {
                    return choice;
                }
                System.out.println("Enter a number from 1 to 7.");
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. A whole number is required.");
            }
        }
    }

    private String loadVisits() {
        String fileName = readRequiredText("Enter the text-file location: ");
        fileName = removeQuotes(fileName);

        try {
            int loaded = tracker.loadFromFile(fileName);
            return "Loaded " + loaded + " patient visit records.";
        } catch (FileNotFoundException exception) {
            return "File error: The text file does not exist or cannot be opened.";
        } catch (IllegalArgumentException | SecurityException exception) {
            return "File error: " + exception.getMessage();
        }
    }

    /**
     * Each PatientVisit object formats its own six fields.
     * return all stored records or an empty-list message
     */
    private String displayVisits() {
        ArrayList<PatientVisit> visits = tracker.getAllVisits();

        if (visits.isEmpty()) {
            return "No patient visits are currently stored.";
        }

        String output = "All Patient Visit Records:\n";

        for (PatientVisit visit : visits) {
            output = output + visit + "\n";
        }

        return output;
    }

    private String createVisit() {
        int visitId = readPositiveInteger("Visit ID: ");
        String patientName = readPatientName("Patient first and last name: ");
        LocalDate visitDate = readDate("Visit date (YYYY-MM-DD): ");
        String reason = readRequiredText("Reason for visit: ");
        String provider = readProviderName("Provider name: ");
        boolean followUp = readYesNo("Follow-up needed (Yes/No): ");

        try {
            PatientVisit visit = new PatientVisit(
                    visitId,
                    patientName,
                    visitDate,
                    reason,
                    provider,
                    followUp);

            if (tracker.addVisit(visit)) {
                return "Patient visit " + visitId + " was added.";
            }
            return "Add failed: Visit ID " + visitId + " already exists.";
        } catch (IllegalArgumentException exception) {
            return "Add failed: " + exception.getMessage();
        }
    }

    private String updateVisit() {
        int originalId = readPositiveInteger("Enter the Visit ID to update: ");
        PatientVisit current = tracker.findVisit(originalId);

        if (current == null) {
            return "Update failed: Visit ID " + originalId + " does not exist.";
        }

        System.out.println("Press Enter to keep the current value.");

        int newId = readOptionalPositiveInteger(
                "Visit ID [" + current.getVisitId() + "]: ",
                current.getVisitId());
        String newPatient = readOptionalPatientName(
                "Patient name [" + current.getPatientName() + "]: ",
                current.getPatientName());
        LocalDate newDate = readOptionalDate(
                "Visit date [" + current.getVisitDate() + "]: ",
                current.getVisitDate());
        String newReason = readOptionalText(
                "Reason [" + current.getReasonForVisit() + "]: ",
                current.getReasonForVisit());
        String newProvider = readOptionalProviderName(
                "Provider [" + current.getProviderName() + "]: ",
                current.getProviderName());
        boolean newFollowUp = readOptionalYesNo(
                "Follow-up [" + getYesNo(current.isFollowUpNeeded()) + "]: ",
                current.isFollowUpNeeded());

        try {
            PatientVisit updatedVisit = new PatientVisit(
                    newId,
                    newPatient,
                    newDate,
                    newReason,
                    newProvider,
                    newFollowUp);

            if (tracker.updateVisit(originalId, updatedVisit)) {
                return "Patient visit " + originalId + " was updated.";
            }
            return "Update failed: The updated Visit ID is already in use.";
        } catch (IllegalArgumentException exception) {
            return "Update failed: " + exception.getMessage();
        }
    }

    private String deleteVisit() {
        int visitId = readPositiveInteger("Enter the Visit ID to delete: ");

        if (tracker.deleteVisit(visitId)) {
            return "Patient visit " + visitId + " was deleted.";
        }
        return "Delete failed: Visit ID " + visitId + " does not exist.";
    }

    private String countFollowUps() {
        int count = tracker.countFollowUps();

        if (count == 1) {
            return "Follow-up result: 1 patient visit requires follow-up.";
        }
        return "Follow-up result: " + count + " patient visits require follow-up.";
    }

    private int readPositiveInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            try {
                int number = Integer.parseInt(input);

                if (number > 0 && number <= 999999) {
                    return number;
                }
                System.out.println("Enter a whole number from 1 to 999999.");
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. Enter a whole number from 1 to 999999.");
            }
        }
    }

    private int readOptionalPositiveInteger(String prompt, int currentValue) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            if (input.isBlank()) {
                return currentValue;
            }

            try {
                int number = Integer.parseInt(input);

                if (number > 0 && number <= 999999) {
                    return number;
                }
                System.out.println("Enter a whole number from 1 to 999999 or press Enter.");
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. Enter a whole number or press Enter.");
            }
        }
    }

    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            if (!input.isBlank() && !input.contains("|")) {
                return input;
            }
            System.out.println("A value is required and it cannot contain |.");
        }
    }

    private String readPatientName(String prompt) {
        while (true) {
            String name = readRequiredText(prompt);

            if (name.length() > 50) {
                System.out.println("Patient name cannot be longer than 50 characters.");
            } else if (name.split("\\s+").length < 2) {
                System.out.println("Enter both a first and last name.");
            } else if (!hasValidNameCharacters(name)) {
                System.out.println("Patient name contains an invalid character.");
            } else {
                return name;
            }
        }
    }

    private String readOptionalPatientName(String prompt, String currentValue) {
        while (true) {
            System.out.print(prompt);
            String name = keyboard.nextLine().trim();

            if (name.isBlank()) {
                return currentValue;
            }
            if (name.length() > 50) {
                System.out.println("Patient name cannot be longer than 50 characters.");
            } else if (name.split("\\s+").length < 2) {
                System.out.println("Enter both a first and last name or press Enter.");
            } else if (!hasValidNameCharacters(name)) {
                System.out.println("Patient name contains an invalid character.");
            } else {
                return name;
            }
        }
    }

    private boolean hasValidNameCharacters(String name) {
        for (int index = 0; index < name.length(); index++) {
            char character = name.charAt(index);
            boolean allowed = Character.isLetter(character)
                    || character == ' '
                    || character == '-'
                    || character == '\''
                    || character == '.';

            if (!allowed) {
                return false;
            }
        }
        return true;
    }

    private String readProviderName(String prompt) {
        while (true) {
            String providerName = readRequiredText(prompt);

            if (providerName.length() <= 50) {
                return providerName;
            }
            System.out.println("Provider name cannot be longer than 50 characters.");
        }
    }

    private String readOptionalProviderName(String prompt, String currentValue) {
        while (true) {
            System.out.print(prompt);
            String providerName = keyboard.nextLine().trim();

            if (providerName.isBlank()) {
                return currentValue;
            }
            if (providerName.contains("|")) {
                System.out.println("Provider name cannot contain |.");
            } else if (providerName.length() > 50) {
                System.out.println("Provider name cannot be longer than 50 characters.");
            } else {
                return providerName;
            }
        }
    }

    private String readOptionalText(String prompt, String currentValue) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            if (input.isBlank()) {
                return currentValue;
            }
            if (!input.contains("|")) {
                return input;
            }
            System.out.println("The value cannot contain |.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            try {
                LocalDate date = LocalDate.parse(input);

                if (date.isAfter(LocalDate.now())) {
                    return date;
                }
                System.out.println("Visit date must be in the future.");
            } catch (DateTimeParseException exception) {
                System.out.println("Invalid date. Use YYYY-MM-DD, such as 2027-01-01.");
            }
        }
    }

    private LocalDate readOptionalDate(String prompt, LocalDate currentValue) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            if (input.isBlank()) {
                return currentValue;
            }

            try {
                LocalDate date = LocalDate.parse(input);

                if (date.isAfter(LocalDate.now())) {
                    return date;
                }
                System.out.println("Visit date must be in the future.");
            } catch (DateTimeParseException exception) {
                System.out.println("Invalid date. Use YYYY-MM-DD or press Enter.");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
                return true;
            }
            if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Enter Yes or No.");
        }
    }

    private boolean readOptionalYesNo(String prompt, boolean currentValue) {
        while (true) {
            System.out.print(prompt);
            String input = keyboard.nextLine().trim();

            if (input.isBlank()) {
                return currentValue;
            }
            if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
                return true;
            }
            if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Enter Yes, No, or press Enter.");
        }
    }

    private String getYesNo(boolean value) {
        if (value) {
            return "Yes";
        }
        return "No";
    }

    private String removeQuotes(String value) {
        String cleaned = value.trim();

        if (cleaned.length() >= 2
                && cleaned.startsWith("\"")
                && cleaned.endsWith("\"")) {
            return cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned;
    }
}
