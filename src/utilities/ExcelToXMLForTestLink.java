package utilities;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import jxl.Sheet;
import jxl.Workbook;

public class ExcelToXMLForTestLink {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try
		{
			/** change values here only -- sample test case file is in doc folder */
			String testcaseFile = "/Users/pankaj.katiyar/Downloads/Revamp.xls";
			String desiredTestlinkXMLFile = "/Users/pankaj.katiyar/Desktop/file.xml";
			/**************************/

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element testcases = doc.createElement("testcases");

			Attr namespace = doc.createAttribute("xmlns:xsi");
			namespace.setValue("http://www.w3.org/2001/XMLSchema-instance");
			testcases.setAttributeNode(namespace);

			doc.appendChild(testcases);

			// form xml from excel file 
			readExcelFile(testcaseFile, doc, testcases);

			// print xml 
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(desiredTestlinkXMLFile));

			transformer.transform(source, result);

			System.out.println("Printed XML");
		}catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static Element createTestCaseNode(Document doc, Element testcases, String name, String summary, String	preconditions, String step_number, String actions, String  expectedresults)
	{
		Element steps = null;
		try
		{
			// testcase element
			Element testcase = doc.createElement("testcase");
			testcases.appendChild(testcase);

			// set attribute to testcase element
			Attr nameAttr = doc.createAttribute("name");
			nameAttr.setValue(name);
			testcase.setAttributeNode(nameAttr);

			// summary element
			Element summaryElement = doc.createElement("summary");
			summaryElement.appendChild(doc.createTextNode(summary));
			testcase.appendChild(summaryElement);

			// preconditions element
			Element preconditionsElement = doc.createElement("preconditions");
			preconditionsElement.appendChild(doc.createTextNode(preconditions));
			testcase.appendChild(preconditionsElement);

			// steps element
			steps = doc.createElement("steps");
			testcase.appendChild(steps);

			System.out.println("Testcase node added");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return steps;
	}


	public static void createStepsNode(Document doc, Element steps, String name, String summary, String	preconditions, String step_number, String actions, String  expectedresults)
	{
		try
		{
			// step element
			Element step = doc.createElement("step");
			steps.appendChild(step);

			// step_number element
			Element step_numberElement = doc.createElement("step_number");
			step_numberElement.appendChild(doc.createTextNode(step_number));
			step.appendChild(step_numberElement);

			// actions element
			Element actionsElement = doc.createElement("actions");
			actionsElement.appendChild(doc.createTextNode(actions));
			step.appendChild(actionsElement);

			// expectedresults element
			Element expectedresultsElement = doc.createElement("expectedresults");
			expectedresultsElement.appendChild(doc.createTextNode(expectedresults));
			step.appendChild(expectedresultsElement);

			System.out.println("Step node added");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void readExcelFile(String file, Document doc, Element testcases)
	{
		try
		{
			Workbook workbook = Workbook.getWorkbook(new File(file));
			Sheet sheet = workbook.getSheet(0);

			String tempName = "";
			Element steps = null;

			//iterate the whole sheet
			for(int i=1; i<sheet.getRows(); i++)
			{
				String name = sheet.getCell(0, i).getContents();
				String summary = sheet.getCell(1, i).getContents();
				String preconditions = sheet.getCell(2, i).getContents();
				String step_number = sheet.getCell(3, i).getContents();
				String actions = sheet.getCell(4, i).getContents();
				String expectedresults = sheet.getCell(4, i).getContents();

				System.out.println(name + " - " + summary +  " - " + preconditions + " - " + step_number + " - " +actions + " - "+expectedresults);

				if(!name.trim().isEmpty() && name != tempName)
				{
					// create testcase node
					steps = createTestCaseNode(doc, testcases, name, summary, preconditions, step_number, actions, expectedresults);
				}

				// create step node
				createStepsNode(doc, steps, name, summary, preconditions, step_number, actions, expectedresults);


				// storing test case name to compare in next iteration
				tempName = name;
			}
			
			workbook.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}


}
