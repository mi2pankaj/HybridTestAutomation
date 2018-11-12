package poc;

import org.testng.annotations.Test;

public class MavenCommandLineParameter {

	/*
add a section in pom.xml

	<configuration>
	<!-- <suiteXmlFiles> <suiteXmlFile>testng.xml</suiteXmlFile> </suiteXmlFiles> -->

	<properties>
		<name>{data}</name>
		<name>{abc}</name>
	</properties>

</configuration>
	 */
	
	
	@Test
	public void test()
	{

		System.out.println("***************** " );
		System.out.println("ABC ---- "+System.getProperty("data"));
		System.out.println("ABC ---- "+System.getProperty("abc"));
	}

}
