package beleg.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Resources {

	public static String loadFileToString(String file_path) {
		
		StringBuilder content = new StringBuilder("");
		File file = new File(file_path);

		try (BufferedReader reader = new BufferedReader(new FileReader(file));) {

			String line;

			while((line = reader.readLine()) != null) {
			
				content.append(line);
				content.append('\n');
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}		
		
		return content.toString();
	}
	
}