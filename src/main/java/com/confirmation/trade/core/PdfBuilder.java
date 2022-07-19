package com.confirmation.trade.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;

public class PdfBuilder {
	private static final String destignationPath = "/demo/src/main/resources/pdf/";
	private PDDocument document = new PDDocument();
	private PDPage page;
	private PDDocumentInformation documentProperties;
	private PDPageContentStream contentStream;
	private TradeData data;
	public PdfBuilder(TradeData tradeData)  {
		this.setData(tradeData);
	}
	public PdfBuilder() {
		// TODO Auto-generated constructor stub
	}
	public PDDocument getDocument() {
		return document;
	}
	public void setDocument(PDDocument document) {
		this.document = document;
	}
	public PDPage getPage() {
		return page;
	}
	public void setPage(PDPage page) {
		this.page = page;
	}
	public PDDocumentInformation getDocumentProperties() {
		return documentProperties;
	}
	public void setDocumentProperties(PDDocumentInformation documentProperties) {
		this.documentProperties = documentProperties;
	}
	public PDPageContentStream getContentStream() {
		return contentStream;
	}
	public void setContentStream(PDPageContentStream contentStream) {
		this.contentStream = contentStream;
	}
	
	public TradeData getData() {
		return data;
	}
	public void setData(TradeData data) {
		this.data = data;
	}
	public void addQRCode(PDPage page, String text, float x, float y) throws IOException, WriterException {
		 
	    this.contentStream = new PDPageContentStream(this.getDocument(), page, PDPageContentStream.AppendMode.APPEND, true);
	 
	   Map<EncodeHintType,Object> hintMap = new HashMap<>();
	   hintMap.put(EncodeHintType.MARGIN, 0);
	   hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	 
	   BitMatrix matrix = new MultiFormatWriter().encode(
	     new String(text.getBytes("UTF-8"), "UTF-8"),
	     BarcodeFormat.QR_CODE, 100, 100, hintMap);
	 
	   MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
	   BufferedImage bImage = MatrixToImageWriter.toBufferedImage(matrix, config);
	   PDImageXObject image = JPEGFactory.createFromImage(document, bImage);
	   this.contentStream.drawImage(image, x, y, 75, 75);
	   this.contentStream.close();

	 }
	public void saveDocument(String fileName, Boolean close) throws IOException  {
		this.document.save(destignationPath+fileName);
		if(close)  {
			this.document.close();
		}
	}
	
}
