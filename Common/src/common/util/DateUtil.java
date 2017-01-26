package common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class DateUtil {
	public enum DateFormatCD {
		YYMM,
		YYYYMM,
		YYMMDD,
		YYYYMMDD,
		YYMMDDHH,
		YYMMDDHHMI,
		YYYYMMDDHH,
		YYYYMMDDHHMI,
		YYMMDDHHMISS,
		YYYYMMDDHHMISS;
	}
	private static final String[] DATE_FORMAT = {"yyMM","yyyyMM","yyMMdd","yyyyMMdd","yyMMddHH","yyMMddHHmm","yyyyMMddHH","yyyyMMddHHmm","yyMMddHHmmss","yyyyMMddHHmmss"};
	private static final String MONTH_REG = "((0[1-9]{1})|10|11|12)";
	private static final String DAY_REG = "((0[1-9]{1})|(([1-2])[0-9]{1})|30|31)";
	private static final String HOUR_REG = "((0[1-9]{1})|(1[0-9]{1})|20|21|22|23)";
	private static final String MIN_SEC_REG = "((0[1-9]{1})|([1-5]{1}[0-9]{1}))";
	private static Calendar calendar;
	private Date today;

	
	public DateUtil() {
		calendar = Calendar.getInstance();
		today = new Date();
		calendar.setTime(today);
	}
	
	public String getDateStrFromInt(DateFormatCD cd, int year, int mon, int day, int hour, int min, int sec) {
		String yearStr = HMTrans.padLeftStr(2, HMTrans.trim(year), '0');
		String monStr = HMTrans.padLeftStr(2, HMTrans.trim(mon), '0');
		String dayStr = HMTrans.padLeftStr(2, HMTrans.trim(day), '0');
		String hourStr = HMTrans.padLeftStr(2, HMTrans.trim(hour), '0');
		String minStr = HMTrans.padLeftStr(2, HMTrans.trim(min), '0');
		String secStr = HMTrans.padLeftStr(2, HMTrans.trim(sec), '0');
		
		String rslt = "";
		
		StringBuilder sb = new StringBuilder();
		switch (cd) {
		case YYMM: 
			sb.append(yearStr).append(monStr);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+"$", rslt)) return "";
			break;
		case YYYYMM: 
			sb.append(yearStr).append(monStr);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+"$", rslt)) return "";
			break;
		case YYMMDD: 
			sb.append(yearStr).append(monStr).append(dayStr);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+DAY_REG+"$", rslt)) return "";
			break;
		case YYYYMMDD: 
			sb.append(yearStr).append(monStr).append(dayStr);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+"$", rslt)) return "";
			break;
		case YYMMDDHH: 
			sb.append(yearStr).append(monStr).append(dayStr).append(hourStr);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+"$", rslt)) return "";
			break;
		case YYMMDDHHMI: 
			sb.append(yearStr).append(monStr).append(dayStr).append(hourStr).append(minStr);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		case YYYYMMDDHH: 
			sb.append(yearStr).append(monStr).append(dayStr).append(hourStr);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+"$", rslt)) return "";
			break;
		case YYYYMMDDHHMI: 
			sb.append(yearStr).append(monStr).append(dayStr).append(hourStr).append(minStr);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		case YYMMDDHHMISS: 
			sb.append(yearStr).append(monStr).append(dayStr).append(hourStr).append(minStr).append(secStr);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		case YYYYMMDDHHMISS: 
			sb.append(yearStr).append(monStr).append(dayStr).append(hourStr).append(minStr).append(secStr);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		default:
			break;
		}
		return sb.toString();
	}
	
	public String getDateStrFromArr(DateFormatCD cd, String[] dateArr) {
		String rslt = "";
		

		
		StringBuilder sb = new StringBuilder();
		switch (cd) {
		case YYMM: 
			if (dateArr.length < 2) return "";
			if (dateArr[0].length() > 2 ) dateArr[0] = dateArr[0].substring(2);
			sb.append(dateArr[0]).append(dateArr[1]);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+"$", rslt)) return "";
			break;
		case YYYYMM: 
			if (dateArr.length < 2) return "";
			sb.append(dateArr[0]).append(dateArr[1]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+"$", rslt)) return "";
			break;
		case YYMMDD: 
			if (dateArr.length < 3) return "";
			if (dateArr[0].length() > 2 ) dateArr[0] = dateArr[0].substring(2);
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]);
			rslt = sb.toString();
			if (!Pattern.matches("^[0-9]{2}"+MONTH_REG+DAY_REG+"$", rslt)) return "";
			break;
		case YYYYMMDD: 
			if (dateArr.length < 3) return "";
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+"$", rslt)) return "";
			break;
		case YYMMDDHH: 
			if (dateArr.length < 4) return "";
			if (dateArr[0].length() > 2 ) dateArr[0] = dateArr[0].substring(2);
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]).append(dateArr[3]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+"$", rslt)) return "";
			break;
		case YYMMDDHHMI: 
			if (dateArr.length < 5) return "";
			if (dateArr[0].length() > 2 ) dateArr[0] = dateArr[0].substring(2);
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]).append(dateArr[3]).append(dateArr[4]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		case YYYYMMDDHH: 
			if (dateArr.length < 4) return "";
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]).append(dateArr[3]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+"$", rslt)) return "";
			break;
		case YYYYMMDDHHMI: 
			if (dateArr.length < 5) return "";
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]).append(dateArr[3]).append(dateArr[4]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		case YYMMDDHHMISS: 
			if (dateArr.length < 6) return "";
			if (dateArr[0].length() > 2 ) dateArr[0] = dateArr[0].substring(2);
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]).append(dateArr[3]).append(dateArr[4]).append(dateArr[5]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		case YYYYMMDDHHMISS: 
			if (dateArr.length < 6) return "";
			sb.append(dateArr[0]).append(dateArr[1]).append(dateArr[2]).append(dateArr[3]).append(dateArr[4]).append(dateArr[5]);
			rslt = sb.toString();
			if (!Pattern.matches("^(20|19)[0-9]{2}"+MONTH_REG+DAY_REG+HOUR_REG+MIN_SEC_REG+MIN_SEC_REG+"$", rslt)) return "";
			break;
		default:
			break;
		}
		return rslt;
	}
	
	public String nowDateStr(DateFormatCD cd) {
		initCalendar();
		return dateData(calendar, cd, "", "", false);
	}
	 
	public String nowDateStr(DateFormatCD cd, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		initCalendar();
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String[] nowDateArr(DateFormatCD cd) {
		initCalendar();
		return makeDateDataCore(cd, calendar);
	}
	
	public String getDateStr(DateFormatCD cd, int year, int mon, int day) {
		String date = getDateStrFromInt(cd, year, mon, day, 0, 0, 0);
		calendar = parseStringCore(cd, date);
		return dateData(calendar, cd, "", "", false);
	} 
	
	public String getDateStr(DateFormatCD cd, String date) {
		calendar = parseStringCore(cd, date);
		return dateData(calendar, cd, "", "", false);
	}
	
	public String getDateStr(DateFormatCD cd, int year, int mon, int day, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		String date = getDateStrFromInt(cd, year, mon, day, 0, 0, 0);
		calendar = parseStringCore(cd, date);
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String getDateStr(DateFormatCD cd, int year, int mon, int day, int hour, int min, int seq, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		String date = getDateStrFromInt(cd, year, mon, day, hour, min, seq);
		calendar = parseStringCore(cd, date);
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String getDateStr(DateFormatCD cd, String date, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		calendar = parseStringCore(cd, date);
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String getDateStr(DateFormatCD cd, String[] dateArr, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		String date = getDateStrFromArr(cd, dateArr);
		calendar = parseStringCore(cd, date);
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String[] getDateArr(DateFormatCD cd, String date) {
		calendar = parseStringCore(cd, date);
		return makeDateDataCore(cd, calendar);
	}
	
	public String addMonthStr(DateFormatCD cd, String date, int amount) {
		calendar = parseStringCore(cd, date);
		calendar = addMonthCore(amount);
		return dateData(calendar, cd, "", "", false);
	}
	
	public String addMonthStr(DateFormatCD cd, String date, int amount, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		calendar = parseStringCore(cd, date);
		calendar = addMonthCore(amount);
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String[] addMonthArr(DateFormatCD cd, String date, int amount) {
		calendar = parseStringCore(cd, date);
		calendar = addMonthCore(amount);
		return makeDateDataCore(cd, calendar);
	}

	public String addDayStr(DateFormatCD cd, String date, int amount, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		calendar = parseStringCore(cd, date);
		calendar = addDayCore(amount);
		return dateData(calendar, cd, dateDelimeter, timeDelimeter, isBlankOfDateAndTime);
	}
	
	public String addDayStr(DateFormatCD cd, String date, int amount) {
		calendar = parseStringCore(cd, date);
		calendar = addDayCore(amount);
		return dateData(calendar, cd, "", "", false);
	}
	
	public String[] addDayArr(DateFormatCD cd, String date, int amount) {
		calendar = parseStringCore(cd, date);
		calendar = addDayCore(amount);
		return makeDateDataCore(cd, calendar);
	}
	
	private Calendar parseStringCore(DateFormatCD cd, String date) {
		Calendar cal = calendar;
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT[cd.ordinal()]);
			calendar.setTime(formatter.parse(date));
		} catch(Exception e){
		}
		return cal;
	}
	
	private Calendar addMonthCore(int amount) {
		Calendar cal = calendar;
		cal.add(Calendar.MONTH, amount);
		return cal;
	}
	
	private Calendar addDayCore(int amount) {
		Calendar cal = calendar;
		cal.add(Calendar.DAY_OF_MONTH, amount);
		return cal;
	}

	
	private String dateData(Calendar calendar, DateFormatCD cd, String dateDelimeter, String timeDelimeter, boolean isBlankOfDateAndTime) {
		
		String tmpBlank = isBlankOfDateAndTime ? " " : "";
		
		StringBuilder sb = new StringBuilder();
		String[] dateData = makeDateDataCore(cd, calendar);
		
		switch (cd) {
		case YYMM: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]);
			break;
		case YYYYMM: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]);
			break;
		case YYMMDD: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]);
			break;
		case YYYYMMDD: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]);
			break;
		case YYMMDDHH: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]).append(tmpBlank)
			  .append(dateData[3]);
			break;
		case YYMMDDHHMI: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]).append(tmpBlank)
			  .append(dateData[3]).append(timeDelimeter).append(dateData[4]);
			break;
		case YYYYMMDDHH: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]).append(tmpBlank)
			  .append(dateData[3]);
			break;
		case YYYYMMDDHHMI: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]).append(tmpBlank)
			  .append(dateData[3]).append(timeDelimeter).append(dateData[4]);
			break;
		case YYMMDDHHMISS: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]).append(tmpBlank)
			  .append(dateData[3]).append(timeDelimeter).append(dateData[4]).append(timeDelimeter).append(dateData[5]);
			break;
		case YYYYMMDDHHMISS: 
			sb.append(dateData[0]).append(dateDelimeter).append(dateData[1]).append(dateDelimeter).append(dateData[2]).append(tmpBlank)
			  .append(dateData[3]).append(timeDelimeter).append(dateData[4]).append(timeDelimeter).append(dateData[5]);
			break;

		default:
			break;
		}
		return sb.toString();
	}
	
	private String[] makeDateDataCore(DateFormatCD cd, Calendar cal) {
		String[] dateDataArr = null;
		String yearStr = "";
		String monStr = "";
		String dayStr = "";
		String hourStr = "";
		String minStr = "";
		String secStr = "";

		switch (cd) {
		case YYMM: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR)).substring(2);
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dateDataArr = new String[2];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			break;
		case YYYYMM: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR));
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dateDataArr = new String[2];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			break;
		case YYMMDD: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR)).substring(2);
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			dateDataArr = new String[3];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			break;
		case YYYYMMDD: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR));
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			dateDataArr = new String[3];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			break;
		case YYMMDDHH: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR)).substring(2);
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			hourStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.HOUR_OF_DAY)), '0');
			dateDataArr = new String[4];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			dateDataArr[3] = hourStr;
			break;
		case YYMMDDHHMI: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR)).substring(2);
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			hourStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.HOUR_OF_DAY)), '0');
			minStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MINUTE)), '0');
			
			dateDataArr = new String[5];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			dateDataArr[3] = hourStr;
			dateDataArr[4] = minStr;
			break;
		case YYYYMMDDHH: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR));
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			hourStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.HOUR_OF_DAY)), '0');
			dateDataArr = new String[4];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			dateDataArr[3] = hourStr;
			break;
		case YYYYMMDDHHMI: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR));
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			hourStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.HOUR_OF_DAY)), '0');
			minStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MINUTE)), '0');
			
			dateDataArr = new String[5];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			dateDataArr[3] = hourStr;
			dateDataArr[4] = minStr;
			break;
		case YYMMDDHHMISS: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR)).substring(2);
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			hourStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.HOUR_OF_DAY)), '0');
			minStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MINUTE)), '0');
			secStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.SECOND)), '0');
			
			dateDataArr = new String[6];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			dateDataArr[3] = hourStr;
			dateDataArr[4] = minStr;
			dateDataArr[5] = secStr;
			break;
		case YYYYMMDDHHMISS: 
			yearStr = HMTrans.trim(cal.get(Calendar.YEAR));
			monStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MONTH)+1), '0');
			dayStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.DAY_OF_MONTH)), '0');
			hourStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.HOUR_OF_DAY)), '0');
			minStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.MINUTE)), '0');
			secStr = HMTrans.padLeftStr(2, HMTrans.trim(cal.get(Calendar.SECOND)), '0');
			
			dateDataArr = new String[6];
			dateDataArr[0] = yearStr;
			dateDataArr[1] = monStr;
			dateDataArr[2] = dayStr;
			dateDataArr[3] = hourStr;
			dateDataArr[4] = minStr;
			dateDataArr[5] = secStr;
			break;

		default:
			break;
		}
		return dateDataArr;
		
	}
	private void initCalendar() {
		calendar.setTime(today);
	}
}
