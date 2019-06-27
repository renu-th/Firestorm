
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates Insert Scripts for STE DB - MySQL & Cassandra Edu Validation DB
 * 
 * @author tat13876 (Renuka) 
 * @version 1.0
 * @since   2019-06-26 
 */
public class STEValidation {

    private static final String DELIMITER = ",";
    private static final String SQL_OUTFILE = "\\sqlInsertScripts.sql";
    private static final String CQL_OUTFILE = "\\cqlInsertScripts.txt";
    private static final String SQL_TEMPLATE = "INSERT INTO EduValidation.EDU_VALIDATIONS(ID,USER_TYPE,COUNTRY,AREA_OF_STUDY,GRADUATION_YEAR,GRADUATION_MONTH,STATUS,EMAIL_ID,PERSON_ID) VALUES ('%s','%s','%s','%s',%s,%s,'%s','%s','%s');";
    private static final String CQL_TEMPLATE = "INSERT INTO edu_validations.edu_validations(id,user_type,country,area_of_study,graduation_year,graduation_month,status,email,person_id,externally_verified,documents_uploaded,vendor_approved_status) VALUES ('%s','%s','%s','%s',%s,%s,'%s','%s','%s',%s,%s,%s);";
    private static final String FILE_NAME = "DATA_FILE";
    private static final String BOOL_VALUE = "False";

    private static String jenkinsWorkspace;
    private static String email;
    private static BufferedReader br;

    public static String generateId() {
        String id = UUIDGeneratorUtil.getUUID();
        id = id.concat("-D");
        return id;
    }

    public static void writeToFile(List<String> sqlStatements, String fileName) {
        Path fileP = Paths.get(jenkinsWorkspace + fileName);
        try {
            Files.write(fileP, sqlStatements, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void fetchCommandLineArgs(String[] cmdLinArgs) {
        if (cmdLinArgs.length > 0) {
            email = cmdLinArgs[0];
        } else {
            System.out.println("Email not provided as Command Line Argument.");
        }

    }

    public static void main(String[] args) {
        List<String> sqlStatements = new ArrayList<String>();
        List<String> cqlStatements = new ArrayList<String>();

        fetchCommandLineArgs(args);
        jenkinsWorkspace = System.getenv("WORKSPACE"); //Jenkins Current Workspace

        try {
            FileReader reader = new FileReader(jenkinsWorkspace + "\\" + FILE_NAME);
            br = new BufferedReader(reader);
            String line = br.readLine();
            int noOfLines = 0;
            while ((line = br.readLine()) != null) {
                String id = generateId();
                String[] values = line.split(DELIMITER);
                noOfLines++;
                sqlStatements.add(String.format(SQL_TEMPLATE, id, values[10].toUpperCase(), values[8].substring(0, 2).toUpperCase(),
                        values[11].toUpperCase(), values[12], values[13], values[9].toUpperCase(), email, values[0]));
                cqlStatements.add(String.format(CQL_TEMPLATE, id, values[10].toUpperCase(), values[8].substring(0, 2).toUpperCase(),
                        values[11].toUpperCase(), values[12], values[13], values[9].toUpperCase(), email, values[0], BOOL_VALUE, BOOL_VALUE,
                        BOOL_VALUE));
            }

            writeToFile(sqlStatements, SQL_OUTFILE);
            writeToFile(cqlStatements, CQL_OUTFILE);

            System.out.println("Generated " + noOfLines + " SQL and CQL Insert Scripts.");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
