package arkuni.util.hostfile;

public class HostAnnotationInfo implements HostInfo{
	private int lineIdx;
	private String annotation;
	public HostAnnotationInfo(String annotation, int lineIdx) {
		this.lineIdx = lineIdx;
		this.annotation = annotation;
	}
	public int getLineIdx() {
		return lineIdx;
	}
	public void setLineIdx(int lineIdx) {
		this.lineIdx = lineIdx;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	@Override
	public int getInfoType() {
		// TODO Auto-generated method stub
		return ANNOTATION;
	}
	
}
