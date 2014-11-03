package permoize;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StreamMemoizeEntryList implements MemoizeEntryList {
	private String filePath;
	private ArrayList<MemoizeEntry> buffer = new ArrayList<MemoizeEntry>();

	public StreamMemoizeEntryList(String filePath) throws IOException, ClassNotFoundException {
		if(!java.nio.file.Files.exists(Paths.get(filePath)))
			java.nio.file.Files.createFile(Paths.get(filePath));
		else {
			FileInputStream fileOutput = new FileInputStream(filePath);
			BufferedInputStream bufferedOutput = new BufferedInputStream(fileOutput);
			
			try {
				while(bufferedOutput.available() != 0) {
					// Should be read in chunks
					ObjectInputStream objectInput = new ObjectInputStream(bufferedOutput);
					MemoizeEntry entry = (MemoizeEntry)objectInput.readObject();
						
					buffer.add(entry);
				}
			} finally {
				bufferedOutput.close();
			}
		}
		
		this.filePath = filePath;
	}

	@Override
	public MemoizeEntry get(int index) {
		return buffer.get(index);
	}

	@Override
	public void append(MemoizeEntry entry) {
		buffer.add(entry);
		
		try {
			FileOutputStream fileOutput = new FileOutputStream(filePath, true);
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
			ObjectOutputStream objectOutput = new ObjectOutputStream(bufferedOutput);
			
			objectOutput.writeObject(entry);
			
			objectOutput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int size() {
		return buffer.size();
	}
}
