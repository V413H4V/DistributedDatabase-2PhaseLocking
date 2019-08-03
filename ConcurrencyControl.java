import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import util.Utility;

/*
 *  @author Vaibhav Murkute
 * 	07/20/2019
 * 
 */

public class ConcurrencyControl {

	public static void main(String[] args) {
		String input_file = "";
		if(args.length > 0) {
			input_file = args[0];
		}
		
		parseInput(input_file);

	}
	
	protected static void parseInput(String file_name) {
		File file = new File(file_name);
		if(file.exists()) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while(line != null) {
					Utility.runOperation(line);
					line = reader.readLine();
				}
			} catch (FileNotFoundException e) {
				System.out.println("File Not found. Please check your input argument.");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Had trouble reading the input file.");
				e.printStackTrace();
			}
		}
	}

}
