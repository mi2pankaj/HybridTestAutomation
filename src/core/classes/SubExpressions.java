package core.classes;

public class SubExpressions {

	public boolean contains(String superset, String subset)
	{
		if(superset.contains(subset)) {
			return true;
		}else {
			return false;
		}
	}


	public boolean equals(String superset, String subset)
	{
		if(superset.equals(subset)) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean startwith(String superset, String subset)
	{
		if(superset.startsWith(subset)) {
			return true;
		}else {
			return false;
		}
		
	}
	public boolean lessthan(String str1,String str2) {
		Integer actual=Integer.valueOf(str1);
		Integer expected=Integer.valueOf(str2);
		if(actual<expected) {
			return true;
		}else {
			return false;
		}
		
	}
}
