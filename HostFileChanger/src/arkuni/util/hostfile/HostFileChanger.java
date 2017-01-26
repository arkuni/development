package arkuni.util.hostfile;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HostFileChanger {
	private Frame frame;
	private Panel [] panel;
	private Checkbox [] isActive;
	private TextField [] ipinfo;
	private TextField [] hostInfo;
	private TextField [] etcInfo;
	private Label [] annotation;
	
	private List<HostInfo> hosts;
	private int annoCnt = 0;
	private int hostCnt = 0;
	private int curLineIdx = 0;
	private final String HOST_LOCATION = "C:\\Windows\\System32\\drivers\\etc\\hosts";
	public HostFileChanger() {
		hosts = new ArrayList<HostInfo>();
		curLineIdx = 0;
	}
	
	private void readHostFile() {
		BufferedReader reader = null;
		String data = "";
		try {
			reader = new BufferedReader(new FileReader(HOST_LOCATION));
			while((data = reader.readLine()) != null) 
            {
				if (curLineIdx < 22) {
					curLineIdx++;
					continue;
				}
                //System.out.println(data);
				HostInfo tmp = parserString(data);
				if (tmp != null) hosts.add(tmp);
				
                curLineIdx++;
            }
 
            reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	private HostInfo parserString(String txt) {
		String info = txt;
		if (txt == null || txt.trim().equalsIgnoreCase("")) return null;
		Pattern r = Pattern.compile("^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
		if (info.charAt(0) == '#') {
			Matcher m = r.matcher(info.substring(1));
			if (m.find()) {
				HostInfoBean rslt = null;
				rslt = setHostInfoBean(info.substring(1), false);
				hostCnt++;
				return rslt;
			} else {
				HostAnnotationInfo rslt = null;
				rslt = setAnnotationBean(info.substring(1));
				annoCnt++;
				return rslt;
			}
			
		} else {
			HostInfoBean rslt = null;
			//정규식 확인
			Matcher m = r.matcher(info);
			
			if (!m.find()) {
				System.out.println("wrong data form");
				return null;
			}
			rslt = setHostInfoBean(info, true);
			hostCnt++;
			return rslt;
		}
		
		
	}
	
	private HostInfoBean setHostInfoBean(String data, boolean isActive) {
		HostInfoBean rslt = null;
		String info = data.replaceAll("\\s+", " ");
		if (info.indexOf(" ") < 0) return null;
		String [] infoArr = info.split(" ");
		if (infoArr.length < 2) return null;
		rslt = new HostInfoBean();
		rslt.setLineNo(curLineIdx);
		rslt.setIpInfo(infoArr[0]);
		rslt.setHostInfo(infoArr[1]);
		if (infoArr.length > 2) {
			if (infoArr[2].charAt(0) == '#') {
				String tmp = "";
				for (int i=2; i<infoArr.length; i++) {
					tmp += infoArr[i] + " ";
				}
				tmp = tmp.substring(0, tmp.length()-1);
				rslt.setEtcInfo(tmp);
			} else {
				rslt.setAlias(infoArr[2]);
				if (infoArr.length > 3) {
					String tmp = "";
					for (int i=3; i<infoArr.length; i++) {
						tmp += infoArr[i] + " ";
					}
					tmp = tmp.substring(0, tmp.length()-1);
					rslt.setEtcInfo(tmp);
				}
			}
		}
		rslt.setActive(isActive);
		
		return rslt;
	}
	
	private HostAnnotationInfo setAnnotationBean(String data) {
		HostAnnotationInfo rslt = null;
		rslt = new HostAnnotationInfo(data, curLineIdx);
		return rslt;
	}
	
	private void createWindow() {
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		frame = new Frame("Host file Change");
		frame.setSize(600,800);
		frame.setVisible(true);
		frame.setLayout(grid);
		//frame.setLayout(null);
		frame.addWindowListener(new WindowAdapter() { // 프레임의 닫기 이벤트
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		
		
		Toolkit tk = Toolkit.getDefaultToolkit(); // 구현된 Toolkit 객체를 얻는다.
        Dimension screenSize = tk.getScreenSize(); // 화면의 크기를 구한다.
        // Scrollbar
        Scrollbar vbar = new Scrollbar(Scrollbar.VERTICAL, 0, 0, 0, 800);
        vbar.setSize(15, 770);
        vbar.setLocation(585, 30);
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.VERTICAL;
        frame.add(vbar, c);
		
		
		isActive = new Checkbox[hostCnt];
		ipinfo = new TextField[hostCnt];
		hostInfo = new TextField[hostCnt];
		etcInfo = new TextField[hostCnt];
		annotation = new Label[annoCnt];
		panel = new Panel[hosts.size()];

		int k=0, m=0;
		for(int i=0;i<hosts.size(); i++) {



			HostInfo tmp = hosts.get(i);
			panel[i] = new Panel();
			if (tmp.getInfoType() == HostInfo.ANNOTATION) {
				annotation[m] = new Label();
				annotation[m].setSize(120, 30);
				//System.out.println("#"+((HostAnnotationInfo)tmp).getAnnotation());
				annotation[m].setText(((HostAnnotationInfo)tmp).getAnnotation());
				annotation[m].setBackground(Color.gray);
				annotation[m].setBounds(0, 0, 120, 30);
				panel[i].add(annotation[m]);	
				c.fill = GridBagConstraints.HORIZONTAL;
				frame.add(panel[i],c);
				m++;
				
				continue;
			} else if (tmp.getInfoType() == HostInfo.HOSTINFO) {
			
				
				
				isActive[k] = new Checkbox();
				ipinfo[k] = new TextField(18);
				hostInfo[k] = new TextField(20);
				etcInfo[k] = new TextField(30);
				HostInfoBean info = (HostInfoBean) tmp;
				if (info.isActive()) isActive[k].setState(true);
				ipinfo[k].setText(info.getIpInfo());
				hostInfo[k].setText(info.getHostInfo());
				etcInfo[k].setText(info.getEtcInfo());
				
				/*String alias = info.getAlias();
				String etc = info.getEtcInfo();
				
				if (alias == null || alias.trim().equalsIgnoreCase("")) alias = "";
				else alias = "\t"+alias;
				
				if (etc == null || etc.trim().equalsIgnoreCase("")) etc = "";
				else etc = "\t"+etc;
				System.out.println((!info.isActive()? "#" : "") +info.getIpInfo()+"\t"+info.getHostInfo()+alias+etc);*/
				
				panel[i].add(isActive[k]);
				panel[i].add(ipinfo[k]);
				panel[i].add(hostInfo[k]);
				panel[i].add(etcInfo[k]);
				k++;
				frame.add(panel[i]);

			}
		}
		Dimension minimumSize = new Dimension(600, 800);
		frame.setMinimumSize(minimumSize);
		frame.setMaximumSize(minimumSize);
		frame.setResizable(false);
		//frame.pack();
	}
	
	public static void main(String[] args) {
		HostFileChanger host = new HostFileChanger();
		host.readHostFile();
		host.createWindow();
		
		

	}
	
}
