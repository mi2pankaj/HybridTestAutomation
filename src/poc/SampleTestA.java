package poc;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import framework.utilities.httpClientWrap;


public class SampleTestA {

	final static String x = "";
	int a;

	public SampleTestA() {
		this.a= 10;
		System.out.println("a = "+a);

		fabonaci();
	}

	public SampleTestA(SampleTestA s) {
		this.a = s.a;
	}

	public static void main(){
		System.out.println("main without args");
	}  

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException, ParseException {		

		
		char c = 'c';
		int x1 = c;
		System.out.println("ASCII VALUE OF CHAR - " +x1);
		
		//int it=1.5; // this won't work, float can't be assigned to int

		String abc = "This     is      pankaj";
		List<String> l = Arrays.asList(abc.split(" "));
		String f = "";

		for(int i=0; i<l.size(); i++) {

			if(!l.get(i).isEmpty()) {
				f = f + " " + l.get(i).trim();
			}
		}
		System.out.println(f);

		String x = "when [abcd] verifyelementpresent then clicklink with [abcd]";
		Pattern p = Pattern.compile("\\[.*?\\]");
		Matcher m = p.matcher(x);

		while(m.find()) {
			System.out.println(m.group());
		}
	}

	public static void primenumber() {

		int n = 37;
		boolean flag = false;

		for(int i=2; i<n/2; i++) {

			if(n % i ==0 ) {
				System.out.println("Not a prime ");

				flag = true;
				break;
			}else {

				flag = false;
				continue;
			}
		}

		if(!flag) {
			System.out.println("Prime");
		}
	}

	public static void fabonaci() {

		char[] charArray ={ 'a', 'b', 'c', 'd', 'e' }; 
		System.out.println(String.valueOf(charArray));

		List<Integer> l = new ArrayList<>();
		l.add(0);
		l.add(1);

		//0,1,1,2,3,5 -- fabonicai series
		for(int i=2; i<50; i++) {
			l.add(l.get(i-1) + l.get(i-2));
		}

		System.out.println(l.toString());
	}

	public static void sortmap() 
	{
		HashMap<String, Integer> map = new HashMap<>();
		map.put("aa2", 12);
		map.put("aa1", 121);
		map.put("aa", 11);

		Set<Entry<String, Integer>> set = map.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<>(set);

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				// TODO Auto-generated method stub

				return (o2.getValue()).compareTo( o1.getValue() );
			}
		});

		for(Map.Entry<String, Integer> entry:list){
			System.out.println(entry.getKey()+" ==== "+entry.getValue());
		}
	}

	public static void maxnumber()
	{
		//int num[] = {5,34,78,2,45,1,99,23};
		int num[] = {1,99,23};

		int max1= 0;
		int max2= 0;

		int maxOne = 0;
		int maxTwo = 0;
		for(int n:num){
			if(maxOne < n){
				maxTwo = maxOne;
				maxOne =n;
			} else if(maxTwo < n){
				maxTwo = n;
			}
		}
		System.out.println("First Max Number: "+maxOne);
		System.out.println("Second Max Number: "+maxTwo);

		for(int i=0; i<num.length; i++)
		{
			if(max1 < num[i]) {
				max2 = max1;
				max1 = num[i];				
			}else if ( max2 < num[i]){
				max2 = num[i];
			}

		}

		System.out.println(max1 + " -- " +max2);;
	}

	public static void sortarray(){

		int [] n = {1, 43, 5,300,4};
		int temp;

		for(int i=0; i< n.length; i++) {

			for(int j= i+1; j< n.length; j++) {

				if(n[i] > n[j]) {

					temp = n[j];
					n[j] = n[i];
					n[i] = temp;

					for(int k=0; k< n.length; k++) {System.out.print(n[k]+",");}
					System.out.println();
				}
			}			
		}

		for(int i=0; i< n.length; i++) {System.out.print(n[i]+",");}

	}

	public static void xy()
	{
		String x ="SeeksforGeeks";

		String x1 = x.substring(0, x.indexOf("for"));
		String x2 = x.substring(x.indexOf("for")+3, x.length());
		System.out.println(x1 + " -- " +x2);

		char [] c1 = x1.toCharArray();
		char [] c2 = x2.toCharArray();

		String sx1 = String.valueOf(c2);
		String sx2 = String.valueOf(c1);

		System.out.println(sx1+"for"+sx2);
	}

	public static void xx()
	{
		String x ="SeeksforGeeks";
		char [] c = x.toCharArray();

		for(int i=c.length-1; i>0; i--) {
			System.out.print(c[i]);
		}
	}

	public static void ff()
	{
		String input = " !#$%&'()*+,-./:;<=>?@[]^_`{|}~0123456789";
		System.out.println("Checking message ... ");

		Pattern p = Pattern.compile("[^A-Za-z0-9]");
		Matcher m = p.matcher(input);

		if(m.find())
		{
			input = m.replaceAll("--");
			System.out.println(input);
		}
	}

	public void abc(){
		System.out.println("abc");
	}

	static public void xyz()
	{		

		String x = "abc[~~]/abc/lll/ab{~~}/abc/ab/a(~~)";

		String a = "1, 2, 3 ";
		//String a = "1k ";

		List<String> xxx = Arrays.asList(a.split(","));

		if(xxx.size()==1)
		{
			x = x.replace("~~", xxx.get(0).trim());
		}
		else
		{
			int i=0;
			while(x.contains("~~"))
			{
				try {
					x = x.replaceFirst("~~", xxx.get(i).trim());
				}catch (ArrayIndexOutOfBoundsException e) {
					x = x.replaceFirst("~~", xxx.get(xxx.size()-1).trim());
				}
				i++;
			}	
		}	

		System.out.println(x);

	}

	public static void jsonparse() throws ParseException 
	{
		
		String x = httpClientWrap.sendGetRequest("https://www.lenskart.com/juno/services/v1/redis?keys=PACKAGES_CONFIG");
		
//		String x = "{\n" + 
//				"	\"result\": {\n" + 
//				"		\"PACKAGES_CONFIG\": \"{\\\"checkOfferFieldPrices\\\":true,\\\"showPrescInfoScreen\\\":false,\\\"showAddOnsScreen\\\":true,\\\"offerText\\\":\\\"\\\",\\\"bannerConfig\\\":{\\\"isVisible\\\":false,\\\"primaryText\\\":\\\"Hi %s, You are a GOLD Member!\\\",\\\"secondaryText\\\":\\\"You are eligible for Buy 1 Get 1 offer on this order!\\\"},\\\"isExpandedByDefault\\\":true,\\\"isPreSelected\\\":true,\\\"displayBogoTabs\\\":false,\\\"defaultSelectedTabId\\\":\\\"buy2\\\",\\\"buy2PriceOfferText\\\":\\\"Frame + Lens\\\",\\\"tabConfig\\\":[{\\\"id\\\":\\\"buy1\\\",\\\"title\\\":\\\"Buy 1\\\",\\\"subtitle\\\":\\\"No Offer\\\"},{\\\"id\\\":\\\"buy2\\\",\\\"title\\\":\\\"Buy 2\\\",\\\"subtitle\\\":\\\"Buy 1 Get 1 Free\\\"}]}\"\n" + 
//				"	}\n" + 
//				"}";
	
		JSONParser jsonq = new JSONParser();
		JSONObject xx =  (JSONObject) jsonq.parse(x);
		
		JSONObject xxx = (JSONObject)xx.get("result");
		
		JSONObject x1  = (JSONObject) jsonq.parse(xxx.get("PACKAGES_CONFIG").toString());
		
		System.out.println(x1.get("showPrescInfoScreen").toString());
	}

}


class SampleTest11 extends SampleTestA{

	public SampleTest11() {

		super();
		System.exit(0);
		super.abc();
	}

	public void abc(){
		System.out.println("abc11");

		super.abc();
	}

}
