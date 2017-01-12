package com.nowtv;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@SpringBootApplication
public class FilePersistanceApplication  implements CommandLineRunner {

	@Autowired
	GridFsTemplate gridOperations;

	public static void main(String[] args) {
		SpringApplication.run(FilePersistanceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		readFile();
		writeFile();
	}


	private void writeFile() {

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/Users/agu08/file1.csv");
			gridOperations.store(inputStream, "file1.csv");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}

	private void readFile() {
		List<GridFSDBFile> result = gridOperations.find(
				new Query().addCriteria(Criteria.where("filename").is("file1.csv")));

		for (GridFSDBFile file : result) {
			try {
				System.out.println(file.getFilename());
				System.out.println(file.getContentType());

				//save as another image
				InputStream inputStream = file.getInputStream();
				System.out.println("file content size " + inputStream.available());
				byte[] fileContent = new byte[inputStream.available()];
				inputStream.read(fileContent);
				String str = new String(fileContent, "UTF-8");
				System.out.println("fileContent: " + str);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				file.writeTo(baos);
				fileContent= baos.toByteArray();

				System.out.println("file content without a file destination: " + new String(fileContent, "UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Done");

	}

}

