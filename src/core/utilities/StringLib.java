/**
 * Last Changes Done on Feb 2, 2015 3:07:02 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */
package core.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import tests.TestSuiteClass; 







public class StringLib 
{

	static Logger logger = Logger.getLogger(StringLib.class.getName());

	public static String[] StrSplit(String MainString, String Limiter)
	{

		//String targetingDetail = "A_4.0,A_4.1,A_5.0,I_3.0";
		//String targetingDetail = "A_4.0";
		//String Limiter = ",";


		if (Strexist(MainString, Limiter))
		{
			String[] OSlists;
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Sub String exist");
			OSlists = MainString.split(Limiter);
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : splits.size: " + OSlists.length);
			//for(String OS: OSlists)
			//{
			//Uncomment for debug 
			//logger.info(OS);
			//}
			return OSlists;
		}
		else
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received String: " + MainString);
			//String[] OSlist = new String[1];
			String[] OSlist = { MainString };
			//OSlist[] = targetingDetail.split(Limiter).toString();
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Value Returned: " + OSlist[0]);
			return OSlist;
		}


	}


	public static boolean Strexist(String Str, String subStr)
	{
		boolean c = Str.contains(subStr);
		return c;	
	}


	//************ This Method Will Split The File Name From The Given HTTP URL ***********
	public static String splitFileNameFromURL(String url)
	{	
		String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		return fileName;
	}


	//************ This Method Will Split The File Name From The Given DIRECTORY LOCATION ***********
	public static String splitDirectoryFromFileLocation(String fileNameWithLocation)
	{
		String directory = "";
		if(System.getProperty("os.name").matches("^Windows.*"))
		{
			fileNameWithLocation = fileNameWithLocation.replace("/", "\\");
			directory = fileNameWithLocation.substring(0, fileNameWithLocation.lastIndexOf("\\"));
		}
		else
		{
			directory = fileNameWithLocation.substring(0, fileNameWithLocation.lastIndexOf("/"));
		}

		return directory;
	}


	//*********** This method will be used to get URL out of generated tag. ******************
	public static String GetURLFromChannelTag(String channelTag)
	{
		//String t = "<img src=\"http://serve.qa.vdopia.com/adserver/tracker.php?m=ti;ci=3708;ai=11010;chid=111400;ou=rd;rand=[timestamp]\" style=\"height:1px;width:1px;position:absolute;visibility:hidden;\" /><script language='javascript' src='http://serve.qa.vdopia.com/adserver/html5/inwapads/?sleepAfter=0;adFormat=banner;ak=960e42b72b82ea66b00d2cfea8a16795;version=1.0;cb=[timestamp]'></script><noscript><img src=\"http://serve.qa.vdopia.com/adserver/tracker.php?m=nji;ci=3708;ai=11011;chid=111400;ou=rd;rand=[timestamp]\" style=\"height:1px;width:1px;position:absolute;visibility:hidden;\" /></noscript>";

		String testURL = null;
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received Channel Tag: "+channelTag);

		List<String> list = Arrays.asList(channelTag.split(" "));

		for(int i=0; i<list.size(); i++)
		{
			if(list.get(i).contains("inwapads"))
			{
				List<String> list01 = Arrays.asList(list.get(i).split("'"));

				for(int j=0; j<list01.size(); j++)
				{
					if(list01.get(j).contains("inwapads"))
					{
						testURL = list01.get(j);
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Found Test URL: " +list01.get(j));
					}
				}
			}
		}

		return testURL;
	}


	//*****************************************************************************************
	// Method to get the current Method name
	//*****************************************************************************************
	public static String trace(StackTraceElement e[]) 
	{
		boolean doNext = false;
		String methodName = "";
		for (StackTraceElement s : e) 
		{
			if (doNext) 
			{
				methodName = s.getMethodName(); 
				break;
			}
			doNext = s.getMethodName().equals("getStackTrace");
		}
		return methodName;
	}


	//This method will return the unique parameter (di parameter) from Test URL
	@SuppressWarnings("finally")
	public static String GetUniqueParamFromURL(String testURL) 
	{
		String diParam = null;
		try
		{
			List<String> di = Arrays.asList(testURL.split(";"));

			for(int i=0; i<di.size(); i++)
			{
				if(di.get(i).startsWith("di="))
				{
					diParam = Arrays.asList(di.get(i).split("=")).get(1);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : di parameter in test url is: "+diParam);
				}
			}
		}
		catch(Exception e)
		{	
			diParam = null;
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: GetUniqueParamFromURL. "+e.getMessage());
		}
		finally
		{
			return diParam;
		} 
	}


	public static String compareLists_Generic(List <String> SuperSet, List <String> Subset)
	{

		boolean loopresult = false;
		String loopresultString = "";

		String compareListsResult = "";


		for(int i=0; i<Subset.size(); i++)
		{
			loopresult = false;
			for(int j=0; j<SuperSet.size(); j++)
			{

				if(loopresult == false)
				{
					if(SuperSet.get(j).equalsIgnoreCase(Subset.get(i)))
					{

						loopresult = true;
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : PASS : " + Subset.get(i) + " exist");
						loopresultString = loopresultString + "PASS : " +  Subset.get(i) + " exist \n";
					}	
				}


			}
			if(loopresult == false)
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : FAIL : " + Subset.get(i) + " does not exist");
				loopresultString = loopresultString + "FAIL : " +  Subset.get(i) + " does not exist\n";
			}

		}

		if(loopresultString.contains("FAIL :"))
		{
			compareListsResult = "false," + loopresultString;
		}
		else
		{
			compareListsResult = "true," + loopresultString;
		}

		return compareListsResult;

	}



	public static String compareLists_impression(List <String> SuperSet, List <String> Subset)
	{

		boolean loopresult = false;
		String loopresultString = "";

		String compareListsResult = "";


		for(int i=0; i<Subset.size(); i++)
		{
			loopresult = false;
			for(int j=0; j<SuperSet.size(); j++)
			{

				if(loopresult == false)
				{
					if(SuperSet.get(j).equalsIgnoreCase(Subset.get(i)))
					{

						loopresult = true;
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : PASS : Impression URL : " + Subset.get(i) + " exist in Adserver XML.");
						loopresultString = loopresultString + "PASS : Impression URL : " +  Subset.get(i) + " exist in Adserver XML.\n";
					}	
				}


			}
			if(loopresult == false)
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : FAIL : Impression URL : " + Subset.get(i) + " does not exist in Adserver XML.");
				loopresultString = loopresultString + "FAIL : Impression URL : " +  Subset.get(i) + " does not exist in Adserver XML.\n";
			}

		}

		if(loopresultString.contains("FAIL :"))
		{
			compareListsResult = "false," + loopresultString;
		}
		else
		{
			compareListsResult = "true," + loopresultString;
		}

		return compareListsResult;

	}



	public static String compareLists_trackers(TreeMap<String, ArrayList<String>> SuperSetMap, TreeMap<String, ArrayList<String>> SubsetMap)
	{

		for (Entry<String, ArrayList<String>>  map : SuperSetMap.entrySet())
		{	
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : SuperSetMap : Key: "+map.getKey() + " Value: "+ map.getValue());
		}


		for (Entry<String, ArrayList<String>>  map : SubsetMap.entrySet())
		{	
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : SubSetMap : Key: "+map.getKey() + " Value: "+ map.getValue());
		}


		ArrayList <String > Mainresult = new ArrayList<String>();
		String MainresultString = "";

		for( String SubsetKey : SubsetMap.keySet())
		{
			boolean SubsetKetExist = false;
			for( String SuperSetKey : SuperSetMap.keySet())
			{
				if(SubsetKetExist == false)
				{
					if(SubsetKey.equalsIgnoreCase(SuperSetKey))
					{
						SubsetKetExist = true;
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Key : " + SubsetKey + " exist in main hashmap.");

						logger.info(SuperSetMap.get(SubsetKey) + ", "+  SubsetMap.get(SuperSetKey));


						Mainresult.add(compareLists_Generic(SuperSetMap.get(SubsetKey), SubsetMap.get(SuperSetKey)).toString());
					}
				}


			}
			if( SubsetKetExist == false)
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Key : " + SubsetKey + " does not exist in main hashmap");
				Mainresult.add("false, "  + SubsetKey + " does not exist in adserve vast xml");

			}

		}


		String MainResultstatus = "true";
		for(String resultRow : Mainresult)
		{
			List<String> KeyResultList = Arrays.asList(resultRow.split(",", 2));
			if(KeyResultList.get(0) == "false")
			{
				MainResultstatus = "false";

			}
			MainresultString = MainresultString + KeyResultList.get(1);

		}

		MainresultString = MainResultstatus + "," + MainresultString;


		return MainresultString;

	}


	//Compare Two Lists and Return the unmatched data from Child List
	public static String CompareTwoList_GetUnMatched(List<String> serveList, List<String> expectedList)
	{
		boolean flag = false;

		String unmatchedData = "";
		String finalString = "";

		for(int i=0; i<expectedList.size(); i++)
		{
			for(int j=0; j<serveList.size(); j++)
			{

				//Exit if match found
				if(serveList.get(j).contains(expectedList.get(i)))
				{
					flag = false;

					break;
				}
				else
				{
					//Set a flag in case of unmatch, to be set after matching one item with the whole second list   
					flag = true;
					unmatchedData = expectedList.get(i);

					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Not Found Item: "+unmatchedData);
				}
			}

			//Appending unmatched strings
			if(flag)
			{
				finalString = finalString + unmatchedData + ", ";
			}
		}

		return finalString;

	}


	public static String[][] MergeTwoArrayWithRow(String[][] recordOutput_WithRon,String[][] recordOutput_WithoutRon)
	{
		String[][] recordOutput = new String[recordOutput_WithRon.length + recordOutput_WithoutRon.length -1][recordOutput_WithRon[0].length];
		int x=0;
		try
		{
			for(int i=0; i<recordOutput_WithRon.length; i++,x++)		// For Every Row
			{
				for(int j=0; j<recordOutput_WithRon[0].length; j++)	// For Every Column
				{
					recordOutput[i][j] = recordOutput_WithRon[i][j];
				}
			}
			for(int i=1; i<recordOutput_WithoutRon.length; i++,x++)		// For Every Row
			{
				for(int j=0; j<recordOutput_WithoutRon[0].length; j++)	// For Every Column
				{
					recordOutput[x][j] = recordOutput_WithoutRon[i][j];
				}
			}
		}
		catch(Exception e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: " + e.getMessage());
		} 
		return recordOutput;
	}


	public static String[][] MergeTwoArrayWithColumn(String[][] recordOutputFirst,String[][] recordOutputSecond)
	{
		String[][] recordOutput = new String[recordOutputFirst.length][];
		try
		{
			for (int index = 0; index < recordOutput.length; index++) 
			{
				recordOutput[index] = join(recordOutputFirst[index], recordOutputSecond[index]);
			}
		}
		catch(Exception e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: " + e.getMessage());
		}
		return recordOutput;
	}


	public static String[] join(String[] array1, String[] array2) {
		String[] array1and2 = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}


	//This method will return the unmatched values of list1 after comparing with list2.
	@SuppressWarnings("finally")
	public static List<String> getUnmatchedValuesFromTwoLists(List<String> list1, List<String> list2) 
	{
		List<String> unMatchedValues = new ArrayList<String>();

		try
		{

			//Checking if the there is any unexpected format is found in server.
			for(int i=0; i<list1.size(); i++)
			{
				String str1 = list1.get(i).trim();
				boolean b = false;

				for(int j=0; j<list2.size(); j++)
				{
					String str2 = list2.get(j).trim().trim();

					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : For index : " + j + ": value is :" + str2 + ":");

					if(str1.equalsIgnoreCase(str2))
					{
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Found item: "+list1.get(i));
						break;
					}
					else
					{
						//Checking is list1.get(i) has been compared to till last element of array
						if(j==list2.size()-1)
						{
							b = true;
						}
					}
				}

				//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Loop2 is over. ");

				if(b)
				{
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Adding in unmatched list: "+str1);

					//Collecting result result after comparing the whole list with array
					unMatchedValues.add(str1);
				}
			}
		}
		catch(Exception e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while comparing two lists. "+e);
		}
		finally
		{
			return unMatchedValues;
		}
	}


	/** Copy values of one hashmap to another hashmap, it will replace those keys in source which has 
	 * no value or space in value
	 * 
	 * @param sourceMap
	 * @param destinationMap
	 * @return
	 */
	public static HashMap<String, String> copyOneDistinctHashmapToAnother(HashMap<String, String> sourceMap, HashMap<String, String> destinationMap)
	{
		/** if source key doesn't exist in destination map or key exists will null value,  then add else not.
		 */
		for(Entry<String, String> sourceEntry: sourceMap.entrySet())
		{
			if(destinationMap.get(sourceEntry.getKey()) == null || destinationMap.get(sourceEntry.getKey()).isEmpty())
			{
				destinationMap.put(sourceEntry.getKey(), sourceEntry.getValue());
			}			
		}

		return destinationMap;
	}


	/** This method will return the printable string from the supplied hashmap in a format like:
	 * key: value, key: value etc..
	 * 
	 * @param queryParam
	 * @return
	 */
	public static String getStringFromMap(HashMap<String, String> queryParam)
	{
		String strQueryParam = ""; 

		for(Entry<String, String> entry : queryParam.entrySet())
		{
			strQueryParam = strQueryParam + entry.getKey() + ": " + entry.getValue() + ", ";
		}

		return strQueryParam;
	}


	/** This method converts a comma separated string in to a list after trimming any space.
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> getListFromCommaSeparatedString(String str)
	{
		List<String> list = new ArrayList<>();

		String [] strArray = str.split(",");

		for(int i=0; i<strArray.length; i++)
		{
			list.add(strArray[i].trim());
		}

		return list;
	}


	/** Convert List of 1 d array to 2 d array.
	 * 
	 * @param x
	 * @return
	 */
	public static String[][] get2DArrayFrom1DArrayList(List<String[]> x)
	{
		String [][] array = new String[x.size()][x.get(0).length];

		for (int i=0; i<x.size(); i++)
		{
			String[] xe = x.get(i);

			for(int j=0; j<xe.length; j++)
			{
				array[i][j] = xe[j];
			}
		}

		//		for(int i=0; i<array.length; i++)
		//		{
		//			for(int j=0; j<array[0].length; j++)
		//			{
		//				System.out.print(array[i][j] + "     ");
		//			}
		//		}

		return array;
	}


	/**
	 *  This method will apply the received the regular expression on the received string and 
	 *  return the list of match found.
	 *  
	 * @param str
	 * @param regx
	 * @return
	 */
	public List<String> getRegxMatches(String str, String regx)
	{
		List<String> matches = new ArrayList<>();

		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(str);

		while(matcher.find())
		{
			matches.add(matcher.group());
		}
		
		return matches;
	}


}




