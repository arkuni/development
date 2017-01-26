package arkuni.http.urlconnection;

import java.util.HashMap;
import java.util.Map;



public class XMLObject{
	
	/*
	<element name="Object" type="ds:ObjectType"/> 
	<complexType name="ObjectType" mixed="true">
		<sequence minOccurs="0" maxOccurs="unbounded">
			<any namespace="##any" processContents="lax"/>
		</sequence>
		<attribute name="Id" type="ID" use="optional"/> 
		<attribute name="MimeType" type="string" use="optional"/>
		<attribute name="Encoding" type="anyURI" use="optional"/> 
	</complexType>
	 */
	
    /**
     * The maximum number of keys in the key pool.
     */
    private static final int keyPoolSize = 100;
    private static HashMap keyPool = new HashMap(keyPoolSize);
    private final Map map;
	
	private String xmlStr;
	public XMLObject() {
		this.map = new HashMap();
	}
	public XMLObject(String xmlStr) {
		this.map = new HashMap();
		
	}
	public String getTagName() {
		return null;
	}
	public String getAttributeName() {
		return null;
	}
	public String getAttributeValue(String tag, String name) {
		return null;
	}
	public String getContents(String tag) {
		return null;
	}
	
	/*
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMimeType() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
}
