package poc;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class SampleTestB {

	public static void main(String[] args) {
	}

	public static void linkedlist() {
		
		//linked list contains - ref# https://www.geeksforgeeks.org/linked-list-in-java/
		
		LinkedList<Object> list = new LinkedList<>();
		list.add(1);
		list.add("99");
		list.add("22");
		list.add(999);
		
		for(int i=list.size()-1; i>=0; i--) {
			System.out.println(list.get(i));
		}
		
	}
	
	public static void method(Object o) {
		System.out.println("Object impl");
	}

	public static void method(String s) {
		System.out.println("String impl");
	}

	public static void string_reverse() {
		String x ="PankajForPankaj";

		char[] aa = x.toCharArray();

		char[] bb = new char[x.toCharArray().length];

		for(int i=aa.length-1; i>=0; i--) {

			bb[i] = aa[aa.length-1 -i];
		}

		System.out.println(String.valueOf(bb));
	}

	public static void pyramidofnumber() {

		int count = 100;
		for(int i=0; i<=count; i++) {

			for(int j=count; j>i; j--) {
				System.out.print(" ");
			}

			for(int j=0; j<i; j++) {
				System.out.print(i + " ");
			}

			System.out.println();
		}

	}

	public static void removewhitespaces() {
		String x = "aa aa    ii sasa -- a   aaa  ";

		java.util.List<String> ss = Arrays.asList(x.split(" "));

		for(String sp : ss) {

			if(!sp.trim().isEmpty()) {
				System.out.print(sp);
			}
		}
	}

	public static void duplicatechar() {
		String  xx = "Better Butter";

		char [] cc = xx.toCharArray();

		HashMap<Character, Integer> map = new HashMap<>();
		for(int i=0; i<cc.length; i++) {

			if(map.containsKey(cc[i])) {
				map.put(cc[i], map.get(cc[i])+1);
			}else {
				map.put(cc[i], 1);
			}
		}

		for(Entry<Character, Integer> cp : map.entrySet()) {

			System.out.println(cp.getKey() + " - " +cp.getValue());
		}
	}

	public static void armstrong_number() {

		int number = 54748;
		int length = String.valueOf(number).length();	
		int sum =0;

		while(number > 0) {			
			int last = number % 10;
			int  mult = 1;

			for(int i=0; i<length; i++) {
				mult = mult*last;
			}

			sum = sum +mult;
			number = number /10;
		}

		System.err.println("---> "+sum);
	}

	public static int getLLessThanN(int number, int digit)
	{
		char c = Integer.toString(digit).charAt(0);
		System.out.println(c + " -- > "+Integer.toString(digit));

		//Decrementing number & checking whether it contains digit
		for (int i = number; i > 0; --i)
		{
			if(Integer.toString(i).indexOf(c) == -1)
			{
				//If 'i' doesn't contain 'c'
				System.out.println(i);
				return i;
			}
		}

		return -1;
	}

	public static void swap_two_string_variables() {
		String z = "Pankaj";
		String a = "Katiyar";

		int zlen = z.length();
		int alen = a.length();

		z = z+a;
		a=a+z;

		z = z.replace(z.substring(0, zlen), "").trim();
		a = a.replace(a.substring(0, alen), "").trim();

		System.out.println(z + " ==> " +a);

	}

	public static void  find_longest_substring_without_repeating_characters() {
		String zz = "javaconceptoftheday";

		LinkedHashMap<Character, Integer> map = new LinkedHashMap<Character, Integer>();

		for(int i=0; i<zz.toCharArray().length; i++) {

			if(!map.containsKey(zz.toCharArray()[i])) {
				map.put(zz.toCharArray()[i], i);;
			}else {
				i = map.get(zz.toCharArray()[i]);
				map.clear();
			}
		}

		String xx= "";
		for(Entry<Character, Integer > en : map.entrySet()) {

			xx = xx+en.getKey();
		}

		System.out.println(xx);
	}

	public static void separate_zeros_from_non_zeros(){
		int[] a = {14, 0, 5, 2, 0, 3, 0};

		int counter =0;
		for(int i=0; i<a.length; i++) {

			if(a[i] !=0) {
				a[counter] = a[i];
				counter++;
			}
		}

		System.out.println(" --> " +counter);

		while(counter < a.length) {
			a[counter] = 0;
			counter ++;
		}

		for(int i=0; i<a.length; i++) {
			System.out.println(a[i]);
		}
	}

	public static void  factorial() {
		int number = 4;

		int mul=1;
		for(int i=number; i>0; i--) {
			mul = mul*i;
		}

		System.out.println(mul);
	}

	public static int first_non_repeated() {

		int b =0;
		try {
			String xyz = "ankajKatiyar";
			char [] ch = xyz.toCharArray();

			LinkedHashMap<Character, Boolean> map = new LinkedHashMap<>();

			for(int i=0; i<ch.length; i++) {
				if(map.containsKey(ch[i])) {
					map.put(ch[i], false);
				}else {
					map.put(ch[i], true);
				}
			}

			for(Entry<Character, Boolean> en : map.entrySet()) {
				if(en.getValue()) {
					b = en.getKey();
					System.out.println(en.getKey());
					break;
				}
			}

			System.out.println(1/0.0);
			System.out.println(5*0.1);

			return b;
		}catch (Exception e) {
			System.out.println("Ntithing ");
		}
		finally {
			System.out.println("final");
		}
		return b;
	}

	public static void first_two_max_number() {

		int [] n = {1, 3, 99, 44, 22, 33233, 555, 3312312, 99999};

		int m1=0;
		int m2=0;

		for (int i=0; i<n.length; i++) {

			if(n[i] > m1) {
				m2= m1;
				m1 = n[i];
			}
			else if( n[i] > m2){
				m2 = n[i];
			}
		}

		System.out.println(m1 + " ==> " +m2);
	}

	public static void max_sub_array_same_order() {
		int [] aa = {0,1,0,0,1,1,1,0,0};

		int zero=0;
		int one=0;
		int finalCount =0;

		for(int i=0; i<aa.length; i++) {
			if(aa[i] ==0 ) {
				zero++;
			}
			if(aa[i] ==1 ) {
				one++;
			}
		}

		if(one > zero) {
			finalCount = zero;
		}else {
			finalCount = one;
		}

		int [] bb = new int [finalCount*2];

		zero =0; one =0;

		int count=0;
		for(int i=0; i<aa.length; i++) {

			if(aa[i] == 0 && zero < finalCount) {
				bb[count] = aa[i];

				count ++;
				System.out.println(aa[i] + " --> " +count);

				zero++;
			}

			if(aa[i] == 1 && one < finalCount) {
				bb[count] = aa[i];

				count ++;
				System.out.println(aa[i] + " --> " +count);

				one++;
			}
		}

	}

	public static void binary_search() {
		int [] x = {1,4, 6, 9, 10, 333, 900};

		int find = 333;

		int start = 0;
		int end = x.length -1;

		while(start < end) {

			int mid = (start+ end)/2;

			System.out.println(x[mid]+ " start: " +start + " mid: "+mid + " end: "+end);

			if(x[mid] == find) {
				System.out.println("Mil gaya .. ");
				break;
			}
			else if(x[mid] > find) {
				end = mid;
			}
			else if(x[mid] < find) {
				start = mid +1; 
			}

			System.out.println(x[mid]+ " start: " +start + " mid: "+mid + " end: "+end);
		}
	}


	/** bubble sort is to compare each element with another */
	public static void bubble_sort() {

		//int [] aa = {1,33, 444, 22, 10, 0, 1, 7, 110};

		int [] aa = {0, 1, 1, 7, 10, 22, 33, 110, 444};

		int temp =0;
		boolean swap = true;

		int attempt =0;

		while(swap) {

			swap = false;
			
			for(int i=0; i<aa.length; i++) {

				for(int j=0; j<aa.length; j++) {

					if(aa[i] < aa[j]) {

						temp = aa[i];
						aa[i] = aa[j];
						aa[j] = temp;

						attempt ++;
						swap = true;
						System.out.println("Swapped : " +attempt + " ietration: "+i + " swap: "+swap);
					}
				}
			}

		}
		
		for(int j=0; j<aa.length; j++) {
			System.out.println(aa[j]);
		}
	}

	public static void find_missing_number() {
		int n = 8;
		int[] a = {1, 4, 5, 3, 7, 8, 6};

		int sum = (n*(n+1))/2;
		System.out.println(sum);

		int asum = 0;
		for(int i=0; i<a.length; i++) {
			asum = asum + a[i];
		}

		System.out.println("Missing Num: "+ (sum - asum));
	}

	static public void StringPermutation(String input)
    {
        StringPermutation("", input);
    }
     
    private static void StringPermutation(String permutation, String input)
    {    
        if(input.length() == 0)
        {
            System.out.println(permutation);
        }
        else
        {
            for (int i = 0; i < input.length(); i++)
            {    
                StringPermutation(permutation+input.charAt(i), input.substring(0, i)+input.substring(i+1, input.length()));
            }
        }
    }
}

