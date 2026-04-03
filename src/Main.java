// Importing required packages
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;

// Main class
public class Main {

    // Instance variables and objects
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    // registerPatient method (to register a new patient)
    private void registerPatient() {

        // Local variables and objects declaration for registerPatient method
        String name, village, phone, query;
        int age;
        PreparedStatement statement;

        // registerPatient method process
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter patient's name: ");
            name = br.readLine();
            System.out.print("Enter patient's age: ");
            age = Integer.parseInt(br.readLine());
            System.out.print("Enter patient's village: ");
            village = br.readLine();
            System.out.print("Enter patient's phone: ");
            phone = br.readLine();

            query = "INSERT INTO patients (name, age, village, phone) VALUES (?, ?, ?, ?)";

            statement = con.prepareStatement(query);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, village);
            statement.setString(4, phone);

            statement.executeUpdate();
            System.out.print("\nPatient registered.\n");
        } catch (Exception e) {
            System.out.print("\nUnable to register patient.");
            e.printStackTrace();
        }
    }

    // recordVisit method (to record details of the patient's visit)
    private void recordVisit() {

        // Local variables and objects declaration for recordVisit method
        int pat_id;
        String diagnosis, medicine, date, query;
        java.sql.Date followup;
        PreparedStatement statement;

        // recordVisit method process
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter patient's ID: ");
            pat_id = Integer.parseInt(br.readLine());
            System.out.print("Enter patient's diagnosis: ");
            diagnosis = br.readLine();
            System.out.print("Enter patient's medicine: ");
            medicine = br.readLine();
            System.out.print("Enter follow-up date (YYYY-MM-DD) or leave blank: ");
            date = br.readLine();

            query = "INSERT INTO visits (pat_id, diagnosis, medicine, followup) VALUES (?, ?, ?, ?)";

            statement = con.prepareStatement(query);
            statement.setInt(1, pat_id);
            statement.setString(2, diagnosis);
            statement.setString(3, medicine);

            if (date == null || date.trim().isEmpty()) {
                statement.setNull(4, Types.DATE);
            } else {
                followup = java.sql.Date.valueOf(date);
                statement.setDate(4, followup);
            }

            statement.executeUpdate();

            System.out.print("\nVisit recorded.\n");
        } catch (Exception e) {
            System.out.print("\nUnable to record visit.\n");
            e.printStackTrace();
        }
    }

    // viewPatientHistory method (to view a patient's visit history)
    private void viewPatientHistory() {

        // Local variables and objects declaration for viewPatientHistory method
        int pat_id;
        String query;
        PreparedStatement statement;
        ResultSet rs;

        // viewPatientHistory method process
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter patient's ID: ");
            pat_id = Integer.parseInt(br.readLine());

            query = "SELECT * FROM visits WHERE pat_id=? ORDER BY visit_date";

            statement = con.prepareStatement(query);
            statement.setInt(1, pat_id);

            rs = statement.executeQuery();

            if (rs.next()) {
                do {
                    System.out.print("\nVisit date: " + rs.getDate("visit_date") + "\nDiagnosis: " + rs.getString("diagnosis") + "\nMedicine: " + rs.getString("medicine") + "\nFollow-up date: " + rs.getDate("followup") + "\n");
                } while (rs.next());
            } else {
                System.out.print("\nVisit history is not available for the patient.\n");
            }
        } catch (Exception e) {
            System.out.print("\nUnable to view patient history.\n");
            e.printStackTrace();
        }
    }

    // followupDue method (to display the due follow-ups of the patients)
    private void followupDue() {

        // Local variables and objects declaration for followupDue method
        String query;
        PreparedStatement statement, date_st;
        ResultSet rs, date_rs;
        java.sql.Date followup, today;

        // followupDue method process
        try (Connection con = DBConnection.getConnection()) {
            query = "SELECT patients.name, visits.diagnosis, visits.followup FROM patients JOIN visits ON patients.pat_id = visits.pat_id WHERE visits.followup <= CURDATE() AND visits.followup IS NOT NULL AND NOT EXISTS (SELECT 1 FROM visits v WHERE v.pat_id = visits.pat_id AND v.visit_date > visits.followup)";
            
            statement = con.prepareStatement(query);
            date_st = con.prepareStatement("SELECT CURDATE()");

            rs = statement.executeQuery();
            date_rs = date_st.executeQuery();

            if (rs.next()) {
                System.out.print("Follow-up Due:\n");
                date_rs.next();
                today = date_rs.getDate(1);
                do {
                    followup = rs.getDate("followup");
                    if (followup.before(today)) {
                        System.out.printf("  %-10s | %-10s | Due: %s [OVERDUE]\n", rs.getString("name"), rs.getString("diagnosis"), rs.getDate("followup"));
                    } else {
                        System.out.printf("  %-10s | %-10s | Due: %s\n", rs.getString("name"), rs.getString("diagnosis"), rs.getDate("followup"));
                    }
                } while (rs.next());
            } else {
                System.out.print("No follow-ups due today.\n");
            }
        } catch (Exception e) {
            System.out.print("\nUnable to fetch follow-up due data.\n");
            e.printStackTrace();
        }
    }

    // main method (to provide a menu-driven interface (CLI/text-based) for the user to interact with the program)
    public static void main(String[] args) throws IOException {

        // Local variables and objects declaration for the main method
        Main obj;
        int ch;

        obj = new Main();

        // Menu-driven interface
        while (true) {
            System.out.print("===== Village Clinic System =====\n1. Register Patient\n2. Record Visit\n3. View Patient History\n4. Follou-up Due Today\n5. Exit\nEnter choice: ");
            ch = Integer.parseInt(br.readLine());

            switch (ch) {
                case 1:
                    obj.registerPatient();
                    System.out.print("\n");
                    break;

                case 2:
                    obj.recordVisit();
                    System.out.print("\n");
                    break;

                case 3:
                    obj.viewPatientHistory();
                    System.out.print("\n");
                    break;

                case 4:
                    obj.followupDue();
                    System.out.print("\n");
                    break;

                case 5:
                    System.out.print("Exiting...\n");
                    return;

                default:
                    System.out.print("\nInvalid option!\nPlease choose again...\n\n");
            }
        }
    }
}
