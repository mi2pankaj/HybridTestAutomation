package poc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertDate {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		
//		String txt="Sat, Mar 10";
		String txt ="March 10, 2018";
		//March 10, 2018
	    String re1="(?:[A-Z][a-z]{2},\\s[A-Z][a-z]{2}\\s\\d{1,2}$)";	// Day Of Week 1
	    String re2="(?:[A-Z][a-z]{4}\\s\\d{1,2},\\s\\d{2,4}$)";
	    Pattern p1 = Pattern.compile(re1);
	    Matcher m1 = p1.matcher(txt);
	    Pattern p2 = Pattern.compile(re2);
	    Matcher m2 = p2.matcher(txt);
	    if (m1.find())
	    {
	        String str=m1.group();
	        str+=" "+Calendar.getInstance().get(Calendar.YEAR);;
	        System.out.println(str);
	        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM dd yyyy");
	        Date date = sdf1.parse(str);
	        SimpleDateFormat sdf2=new SimpleDateFormat("MMM dd yyyy");
	        System.out.println(sdf2.format(date));
	    }else if(m2.find()){
	    	String str=m2.group();
	    	SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy");
	    	Date date = sdf1.parse(str);
	        SimpleDateFormat sdf2=new SimpleDateFormat("MMM dd yyyy");
	        System.out.println(sdf2.format(date));
	    	
	    }

	}

}
