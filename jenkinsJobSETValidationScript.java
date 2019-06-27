import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tat13876 (Renuka Thorati) DB Setup:
 *         https://wiki.corp.adobe.com/pages/viewpage.action?pageId=1270022214
 *         GIT Repo: https://git.corp.adobe.com/Ecommerce/OrderCaptureScripts
 *         JIRA Issue: https://jira.corp.adobe.com/browse/ECOMM-73133
 */
public class jenkinsJobSETValidationScript {

	private static final String DELIMITER = ",";
	//public final static String csvFile = "C:\\Adobe\\CacheBack\\DBScripts\\Batch2BadData\\batch2Data.csv";
	private static final String SQL_OUTFILE = "\\DBScripts\\sqlInsertScripts.sql";
	private static final String CQL_OUTFILE = "\\DBScripts\\cqlInsertScripts.txt";
	final static String sqlTemplate = "INSERT INTO EduValidation.EDU_VALIDATIONS(ID,USER_TYPE,COUNTRY,AREA_OF_STUDY,GRADUATION_YEAR,GRADUATION_MONTH,STATUS,EMAIL_ID,PERSON_ID) VALUES ('%s','%s','%s','%s',%s,%s,'%s','%s','%s');";
	final static String cqlTemplate = "INSERT INTO edu_validations.edu_validations(id,user_type,country,area_of_study,graduation_year,graduation_month,status,email,person_id,externally_verified,documents_uploaded,vendor_approved_status) VALUES ('%s','%s','%s','%s',%s,%s,'%s','%s','%s',%s,%s,%s);";

	/**
	 * Code to generate Insert Scripts for STE DB - MySQL & Cassandra Scripts
	 */

	public static int generaterandomID() {
		int randNum = 0;
		randNum = (int) ((Math.random() * 90000000) + 10000000);
		return randNum;
	}

	public static void writeToFile(List<String> sqlStatements, String fileName) {
		Path fileP = Paths.get(fileName);
		try {
			Files.write(fileP, sqlStatements, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void writeStringToFile(StringBuilder stmt, String fileName) {
		File file = new File(fileName);
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(stmt.toString());
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void main(String[] args) {
		List<String> sqlStatements = new ArrayList<>();
		List<String> cqlStatements = new ArrayList<>();
		String csvFile = System.getenv("csvFile"); // Jenkins Build Parameter - Input CSV File
		String email = System.getenv("EMAIL"); // Jenkins Build Parameter
		System.out.println(email);
		int startID = generaterandomID();
		try {
			FileReader reader = new FileReader(csvFile);
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();
			int noOfLines = 0;
			int count = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(DELIMITER);
				startID++;
				noOfLines++;
				  sqlStatements.add(String.format(sqlTemplate,startID+"-D",values[10].toUpperCase(), values[8].substring(0, 2).toUpperCase(), values[11].toUpperCase(),values[12], values[13], values[9].toUpperCase(),email, values[0]));
				  cqlStatements.add(String.format(cqlTemplate,startID+"-D",values[10].toUpperCase(), values[8].substring(0, 2).toUpperCase(),values[11].toUpperCase(),values[12], values[13], values[9].toUpperCase(),email, values[0], false, false, false));
				
			}
			writeToFile(sqlStatements, SQL_OUTFILE); // SQL Scripts
			writeToFile(cqlStatements, CQL_OUTFILE); // Cassandra Scripts
			System.out.print("Generated " + noOfLines + " Insert Scripts.");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
