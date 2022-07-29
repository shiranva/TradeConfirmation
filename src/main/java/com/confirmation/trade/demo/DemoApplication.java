package com.confirmation.trade.demo;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.confirmation.trade.core.PdfBuilder;
import com.google.zxing.WriterException;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws IOException, WriterException {
		SpringApplication.run(DemoApplication.class, args);
		System.out.print("Hello World!");
		PdfBuilder builder = new PdfBuilder();
		PDDocument document =  PDDocument.load(new File("file.pdf"));
		builder.searchReplace("7870647212", "6202308629","ISO-8859-1",true,document);
		document.save("output.pdf");
		document.close();
	}

}
