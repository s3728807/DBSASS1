   
import java.io.*;
import java.util.*;
import java.time.*;

public class dbquery {

	private static void padCurrentPage(int writtenBytes, FileOutputStream out, int pageSize) {
		for (int j=writtenBytes; j < pageSize; j++) {
			try {
				out.write(0);  // pad 0
			} catch (IOException e) {
				System.err.println("IOException occurred\n");
				System.exit(1);
			}
		}
		System.err.println("writtenbytes=" + 
			writtenBytes + " created page padding\n");
	}

    public static void main(String args[]) throws IOException {
        FileInputStream in = null;
		String inputfn = "heap.512";
		final int recordSize = 102;
		int pageSize = 512;
		int finalPage = Integer.parseInt(args[1]);
		int currentPage = 0;
		int byteRead;
		
		boolean types[] = new boolean[] { 
				true, false, true, false, true, false, true, true, false, true};
		int lengths[] = new int[] { 
				4, 22, 4, 9, 4, 9, 4, 4, 38, 4};

        if (args.length == 0) {
            System.err.println("Please supply appropriate arguments\n");
            System.exit(-1);
        }

		
		int pos = 0;
		boolean feof = false;
		in = new FileInputStream(inputfn);
		Instant starts = Instant.now();
		while (currentPage < finalPage) {
			String dateField = null;
			int sensorIDField = 0;
			int recordID = 0;
			int year = 0;
			String month = null;
			int mdate = 0;
			String day = null;
			int time = 0;
			String sensorName = null;
			int hourlyCounts = 0;
			try { // process a record
				
				// do we need advance to a new page?
				int remPageSpace = pageSize - pos;
				if (remPageSpace < recordSize) {				
					// we need to advance to a new page
					// pad the current page to the pageSize
					int x = pageSize % recordSize;
					for (int i = 0; i < x; i++){
						byteRead = in.read();
					}
					pos = 0;
					currentPage += pageSize;
				}
				
				for (int fieldIndex = 0; fieldIndex < types.length; fieldIndex++) { // read a field for each iteration
					boolean typ = types[fieldIndex];
					int len1 = lengths[fieldIndex];
					int c = 0, v = 1;
					String fld = "";
					if (typ) {
						for (int j=0; j < 4; j++) {
							byteRead = in.read();
							if (byteRead == -1) {
								if (fieldIndex == 0) {
									feof = true;
								}
								System.err.println("Unexpected EOF\n");
								System.exit(-1);						
							}
							
							c += byteRead * v;
							v *= 256;
						}
					} else {
						for (int j=0; j < len1; j++) {
							byteRead = in.read();
							if (byteRead == -1) {
								System.err.println("Unexpected EOF 2\n");
								System.exit(-1);						
							}
							if (byteRead != 0) fld += (char)byteRead;
						}
					}
					
					if (fieldIndex==0) recordID = c;
					if (fieldIndex==1) dateField = fld;
					if (fieldIndex==2) year = c;
					if (fieldIndex==3) month = fld;
					if (fieldIndex==4) mdate = c;
					if (fieldIndex==5) day = fld;
					if (fieldIndex==6) time = c;
					if (fieldIndex==7) sensorIDField = c;
					if (fieldIndex==8) sensorName = fld;
					if (fieldIndex==9) hourlyCounts = c;
				} // have read in a record

				pos += recordSize;
				
				// now compare the input argument against the data:
				if (args[0].equals(sensorIDField + dateField)) {
					System.err.println(recordID + ", " + dateField + ", " + year + ", " + month + ", " + mdate + ", " + day + ", " + time + ", " + sensorIDField + ", " + sensorName + ", " + hourlyCounts );				
				}
			
	 
			} catch (IOException ex) {
				ex.printStackTrace();
			} // end of process a record
			//System.err.println("recordID=" + recordID + 
			//"  Search against =" + sensorIDField + dateField);
		
		}
	Instant ends = Instant.now();
	System.out.println("Duration: " + Duration.between(starts,ends));
		
        System.out.println("End of program\n");
   }
}
