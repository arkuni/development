package common.util;

import java.io.FileReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXPaserHandler extends DefaultHandler {
    public SAXPaserHandler () {
        super();
    }
    
	public static void main (String args[]) throws Exception {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		SAXPaserHandler handler = new SAXPaserHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		
		// Parse each file provided on the command line.
		for (int i = 0; i < args.length; i++) {
			FileReader r = new FileReader(args[i]);
			xr.parse(new InputSource(r));
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
	}
}
