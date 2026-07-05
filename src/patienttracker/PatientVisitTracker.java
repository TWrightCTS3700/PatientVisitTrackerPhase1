package patienttracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Stores patient visits and performs the Phase 1 operations.
 */
public final class PatientVisitTracker {
    private final ArrayList<PatientVisit> visits;

    public PatientVisitTracker() {
        visits = new ArrayList<>();
    }

    /**
     * Loads patient visits from a pipe-delimited text file.
     * fileName location entered by the user
     * number of records loaded
     * FileNotFoundException if the file cannot be opened
     */
    public int loadFromFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        ArrayList<PatientVisit> loadedVisits = new ArrayList<>();
        int lineNumber = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                lineNumber++;

                if (line.isBlank() || line.startsWith("#") || isHeader(line)) {
                    continue;
                }

                PatientVisit visit = parseRecord(line, lineNumber);
                if (containsId(loadedVisits, visit.getVisitId())) {
                    throw new IllegalArgumentException(
                            "Duplicate Visit ID " + visit.getVisitId()
                                    + " on line " + lineNumber + ".");
                }
                loadedVisits.add(visit);
            }
        }

        if (loadedVisits.isEmpty()) {
            throw new IllegalArgumentException(
                    "The text file does not contain any patient visits.");
        }

        visits.clear();
        visits.addAll(loadedVisits);
        return visits.size();
    }

    /**
     * Adds a visit when its ID is not already stored.
     * true when the visit was added
     */
    public boolean addVisit(PatientVisit visit) {
        if (visit == null || findVisit(visit.getVisitId()) != null) {
            return false;
        }

        visits.add(visit);
        return true;
    }

    /**
     * Returns a copy of all visits for display.
     * return copy of the patient visit list
     */
    public ArrayList<PatientVisit> getAllVisits() {
        return new ArrayList<>(visits);
    }

    /**
     * Finds a visit by ID.
     * return matching visit, or null when the ID does not exist
     */
    public PatientVisit findVisit(int visitId) {
        for (PatientVisit visit : visits) {
            if (visit.getVisitId() == visitId) {
                return visit;
            }
        }
        return null;
    }

    /**
     * Replaces an existing visit with updated values.
     * originalVisitId ID of the record being changed
     * updatedVisit replacement record
     * return true when the update was completed
     */
    public boolean updateVisit(int originalVisitId, PatientVisit updatedVisit) {
        if (updatedVisit == null) {
            return false;
        }

        for (int index = 0; index < visits.size(); index++) {
            PatientVisit currentVisit = visits.get(index);

            if (currentVisit.getVisitId() == originalVisitId) {
                PatientVisit duplicate = findVisit(updatedVisit.getVisitId());
                boolean idChanged = updatedVisit.getVisitId() != originalVisitId;

                if (idChanged && duplicate != null) {
                    return false;
                }

                visits.set(index, updatedVisit);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a patient visit by ID.
     * return true when a record was removed
     */
    public boolean deleteVisit(int visitId) {
        for (int index = 0; index < visits.size(); index++) {
            if (visits.get(index).getVisitId() == visitId) {
                visits.remove(index);
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the visits whose Follow-Up Needed field is Yes.
     * return number of visits requiring follow-up
     */
    public int countFollowUps() {
        int total = 0;

        for (PatientVisit visit : visits) {
            if (visit.isFollowUpNeeded()) {
                total++;
            }
        }
        return total;
    }

    private boolean containsId(ArrayList<PatientVisit> list, int visitId) {
        for (PatientVisit visit : list) {
            if (visit.getVisitId() == visitId) {
                return true;
            }
        }
        return false;
    }

    private boolean isHeader(String line) {
        return line.toLowerCase().startsWith("visitid|");
    }

    private PatientVisit parseRecord(String line, int lineNumber) {
        String[] fields = line.split("\\|", -1);

        if (fields.length != 6) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + " must contain exactly six fields.");
        }

        try {
            int visitId = Integer.parseInt(fields[0].trim());
            LocalDate visitDate = LocalDate.parse(fields[2].trim());
            boolean followUp = parseFollowUp(fields[5], lineNumber);

            return new PatientVisit(
                    visitId,
                    fields[1],
                    visitDate,
                    fields[3],
                    fields[4],
                    followUp);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Visit ID on line " + lineNumber + " must be a positive integer.");
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Visit date on line " + lineNumber + " must use YYYY-MM-DD.");
        }
    }

    private boolean parseFollowUp(String value, int lineNumber) {
        String answer = value.trim();

        if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("true")) {
            return true;
        }
        if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("false")) {
            return false;
        }

        throw new IllegalArgumentException(
                "Follow-Up Needed on line " + lineNumber + " must be Yes or No.");
    }
}
