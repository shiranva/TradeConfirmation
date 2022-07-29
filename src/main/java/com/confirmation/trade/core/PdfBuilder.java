package com.confirmation.trade.core;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.springframework.util.StringUtils;

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
	public  PDDocument _ReplaceText(PDDocument document, String searchString, String replacement) throws IOException
    {
        if (StringUtils.isEmpty(searchString) || StringUtils.isEmpty(replacement)) {
            return document;
        }
        
        for ( PDPage page : document.getPages() )
        {
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            List tokens = parser.getTokens();
            
            for (int j = 0; j < tokens.size(); j++) 
            {
                Object next = tokens.get(j);
                if (next instanceof Operator) 
                {
                    Operator op = (Operator) next;
                    
                    String pstring = "";
                    int prej = 0;
                    
                    //Tj and TJ are the two operators that display strings in a PDF
                    if (op.getName().equals("Tj")) 
                    {
                        // Tj takes one operator and that is the string to display so lets update that operator
                        COSString previous = (COSString) tokens.get(j - 1);
                        String string = previous.getString();
                        string = string.replaceFirst(searchString, replacement);
                        previous.setValue(string.getBytes());
                    } else 
                    if (op.getName().equals("TJ")) 
                    {
                        COSArray previous = (COSArray) tokens.get(j - 1);
                        for (int k = 0; k < previous.size(); k++) 
                        {
                            Object arrElement = previous.getObject(k);
                            if (arrElement instanceof COSString) 
                            {
                                COSString cosString = (COSString) arrElement;
                                String string = cosString.getString();
                                
                                if (j == prej) {
                                    pstring += string;
                                } else {
                                    prej = j;
                                    pstring = string;
                                }
                            }                       
                        }                        
                        

                        if (searchString.equals(pstring.trim())) 
                        {                            
                            COSString cosString2 = (COSString) previous.getObject(0);
                            cosString2.setValue(replacement.getBytes());                           

                            int total = previous.size()-1;    
                            for (int k = total; k > 0; k--) {
                                previous.remove(k);
                            }                            
                        }
                    }
                }
            }
            
            // now that the tokens are updated we will replace the page content stream.
            PDStream updatedStream = new PDStream(document);
            OutputStream out = updatedStream.createOutputStream(COSName.FLATE_DECODE);
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens(tokens);            
            out.close();
            page.setContents(updatedStream);
        }

        return document;
    }
	
	public PDDocument replaceTextNew(PDDocument document, String searchString, String replacement) throws IOException {
	    if (Strings.isEmpty(searchString) || Strings.isEmpty(replacement)) {
	        return document;
	    }
	    PDPageTree pages = document.getDocumentCatalog().getPages();
	    for (PDPage page : pages) {
	        PDFStreamParser parser = new PDFStreamParser(page);
	        parser.parse();
	        List tokens = parser.getTokens();
	        for (int j = 0; j < tokens.size(); j++) {
	            Object next = tokens.get(j);
	            if (next instanceof Operator) {
	                Operator op = (Operator) next;
	                //Tj and TJ are the two operators that display strings in a PDF
	                if (op.getName().equals("Tj")) {
	                    // Tj takes one operator and that is the string to display so lets update that operator
	                    COSString previous = (COSString) tokens.get(j - 1);
	                    String string = previous.getString();
	                    string = string.replaceFirst(searchString, replacement);
	                    previous.setValue(string.getBytes());
	                } else if (op.getName().equals("TJ")) {
	                    COSArray previous = (COSArray) tokens.get(j - 1);
	                    for (int k = 0; k < previous.size(); k++) {
	                        Object arrElement = previous.getObject(k);
	                        if (arrElement instanceof COSString) {
	                            COSString cosString = (COSString) arrElement;
	                            String string = cosString.getString();
	                            string = StringUtils.replace(string, searchString, replacement);
	                            cosString.setValue(string.getBytes());
	                        }
	                    }
	                }
	            }
	        }
	        // now that the tokens are updated we will replace the page content stream.
	        PDStream updatedStream = new PDStream(document);
	        OutputStream out = updatedStream.createOutputStream();
	        ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
	        tokenWriter.writeTokens(tokens);
	        page.setContents(updatedStream);
	        out.close();
	    }
	    return document;
	}
	
	public void searchReplace (String search, String replace,
            String encoding, boolean replaceAll, PDDocument doc) throws IOException {
        PDPageTree pages = doc.getDocumentCatalog().getPages();
        for (PDPage page : pages) {
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            List tokens = parser.getTokens();
            for (int j = 0; j < tokens.size(); j++) {
                Object next = tokens.get(j);
                if (next instanceof Operator) {
                    Operator op = (Operator) next;
                    // Tj and TJ are the two operators that display strings in a PDF
                    // Tj takes one operator and that is the string to display so lets update that operator
                    if (op.getName().equals("Tj")) {
                        COSString previous = (COSString) tokens.get(j-1);
                        String string = previous.getString();
                        if (replaceAll)
                            string = string.replaceAll(search, replace);
                        else
                            string = string.replaceFirst(search, replace);
                        previous.setValue(string.getBytes());
                    } else if (op.getName().equals("TJ")) {
                        COSArray previous = (COSArray) tokens.get(j-1);
                        for (int k = 0; k < previous.size(); k++) {
                            Object arrElement = previous.getObject(k);
                            if (arrElement instanceof COSString) {
                                COSString cosString = (COSString) arrElement;
                                String string = cosString.getString();
                                if (replaceAll)
                                    string = string.replaceAll(search, replace);
                                else
                                    string = string.replaceFirst(search, replace);
                                cosString.setValue(string.getBytes());
                            }
                        }
                    }
                }
            }
            // now that the tokens are updated we will replace the page content stream.
            PDStream updatedStream = new PDStream(doc);
            OutputStream out = updatedStream.createOutputStream();
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens(tokens);
            out.close();
            page.setContents(updatedStream);
        }
    }
	
}
