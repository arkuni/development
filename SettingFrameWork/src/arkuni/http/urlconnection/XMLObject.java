package arkuni.http.urlconnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



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

    private String tagName;
    private String attributeString;
    private String contentsString;
    private boolean isLastNode;
    private boolean isSingleAttribute;
    private int currentDepth;
    private int parentIdx;
    private int subIdx = -1;
    
	private String xmlStr;
	public XMLObject() {
		this.map = new HashMap();
	}
	public XMLObject(String xmlStr) {
		this.map = new HashMap();
	}
	public XMLObject(XMLTokener xmlTokener) {
		this.map = new HashMap();
	}
	public String getTagName() {
		return tagName;
	}
	public String getAttributeString() {
		return attributeString;
	}
	public String getContentsString() {
		return contentsString;
	}
	public boolean isLastNode() {
		return isLastNode;
	}
	public boolean isSingleAttribute(){
		return isSingleAttribute;
	}
	public int getCurrentDepth() {
		return currentDepth;
	}
	public int getParentIdx() {
		return parentIdx;
	}
	public int getSubIdx(){
		return subIdx;
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
