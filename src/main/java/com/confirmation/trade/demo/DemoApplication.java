package com.confirmation.trade.demo;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
		PDDocument document = new PDDocument();
		builder.setDocument(document);
	    PDPage page = new PDPage(PDRectangle.LETTER);
	    PDPageContentStream contentStream = new PDPageContentStream(document, page);
	    contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
	    contentStream.beginText();
	    contentStream.newLineAtOffset(25, 500);
	    contentStream.showText("Please check the below document and sign it. Thank you.");
	    contentStream.endText();
	    contentStream.close();
	    document.addPage(page);
	    // add QRCode 
	    builder.addQRCode(page, "1234567890", 30,300);
	    builder.saveDocument("Sample_QR.pdf",true);
	}

}
