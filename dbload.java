
import java.io.*;
import java.util.*;

public class dbload {

	private static void padCurrentPage(int writtenBytes, FileOutputStream out, int pageSize) {
		for (int j=writtenBytes; j < pageSize; j++) {
			try {
				out.write(0);  // pad 0
			} catch (IOException e) {
				System.err.println("IOException occurred\n");
				System.exit(1);
			}
		}
	}

    public static void main(String args[]) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
		File myObj;
		Scanner myReader = null;
		String outfn = "heap." + args[1];
		out = new FileOutputStream(outfn);
		final int recordSize = 102;
		
		boolean types[] = new boolean[] { 
				true, false, true, false, true, false, true, true, false, true};
		int lengths[] = new int[] { 
				4, 22, 4, 9, 4, 9, 4, 4, 38, 4};

        if (args.length == 0) {
            System.err.println("Please supply appropriate arguments\n");
            System.exit(-1);
        }

		int writtenBytes = 0;
		int pageSize = Integer.valueOf(args[1]);
        try {
			myObj = new File(args[2]);
			myReader = new Scanner(myObj);
			byte[] bytes = new byte[pageSize];
			int count = 0;
			System.err.println("Input file " + args[2] + 
				"     Output file" + outfn + "\n");
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				System.out.println(data);

				// do we need a new page?
				int remPageSpace = pageSize - writtenBytes;
				if (remPageSpace < recordSize) {				
					// we need a new page
					// pad the current page to the page sizee 
					//padCurrentPage(writtenBytes, out, pageSize);
					for (; count < pageSize; count++){
						bytes[count] = 0;
					}
					out.write(bytes);
					count = 0;
					writtenBytes = 0;
				}
				
				List<String> fieldList = Arrays.asList(data.split(","));
				for (int i=0; i<fieldList.size(); i++) {  // write the fields
					String fld = fieldList.get(i);
					boolean typ = types[i];
					int len1 = lengths[i];
					//System.out.println(i + "  value=" + fld +
					//	"  type=" + typ +
					//	"  length=" + len1);
					
					if (typ) {
						// writing an integer
						int v = Integer.valueOf(fld);
						for (int j=0; j < 4; j++) {
							int c = v % 256;      
							bytes[count++] = (byte) c;
							v /= 256;
						}
						writtenBytes += 4;
					} else {
						// writing a text field
						for (int j=0; j < len1; j++) {
							if (j < fld.length()) 
								bytes[count++] =(byte) fld.charAt(j);								
							else 
								bytes[count++] = 0;  // pad 0
						}
						writtenBytes += len1;
					}
				}  // end of write the fields
				
			}
			myReader.close();
        } catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
        } finally {
			padCurrentPage(writtenBytes, out, pageSize);
			myReader.close();
			out.close();
		}
        System.out.println("End of program\n");
}
}
