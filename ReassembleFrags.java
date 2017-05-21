package fragment.submissions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ReassembleFrags {

	static class KMP {
		/** Failure array **/
	    private int[] failure;
	    
	    public KMP() { }
	    public int comp(String text, String pat) {
	    	/** pre construct failure array for a pattern **/
	        failure = new int[pat.length()];
	        fail(pat);
	        /** find match **/
	        return posMatch(text, pat);
	    }
	    /** Failure function for a pattern **/
	    private void fail(String pat)
	    {
	        int n = pat.length();
	        failure[0] = -1;
	        for (int j = 1; j < n; j++)
	        {
	            int i = failure[j - 1];
	            while ((pat.charAt(j) != pat.charAt(i + 1)) && i >= 0)
	                i = failure[i];
	            if (pat.charAt(j) == pat.charAt(i + 1))
	                failure[j] = i + 1;
	            else
	                failure[j] = -1;
	        }
	    }
	    /** Function to find match for a pattern **/
	    private int posMatch(String text, String pat)
	    {
	        int i = 0, j = 0;
	        int lens = text.length();
	        int lenp = pat.length();
	        while (i < lens && j < lenp)
	        {
	            if (text.charAt(i) == pat.charAt(j))
	            {
	                i++;
	                j++;
	            }
	            else if (j == 0)
	                i++;
	            else
	                j = failure[j - 1] + 1;
	        }
	        return ((j == lenp) ? (i - lenp) : -1);
	    }
	}
    
	/** Main Function **/
    public static void main(String[] args) throws IOException
    {    	
    	ArrayList<String> fragments;
//    	String path = "/home/mustaphar/research/alfa/mangled";
    	String path = args[0];
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(path));
    		String line;
    		while ((line = reader.readLine()) != null) {

    			String[] separated = line.split(";");
				fragments = new ArrayList<String>(Arrays.asList(separated));
		    	
		    	String merged = arrange(fragments);
		    	System.out.println(merged);
    		}
    		reader.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	

    }
    
    public static String arrange(ArrayList<String> frags) {
    	String rearranged = "";
    	int removeAt = -1;
    	int storeAt = -1;
    	while (frags.size() != 1) {
    		int max = 0;
    		for (int i=0; i<frags.size(); i++) {
	    		for (int j=i+1; j<frags.size(); j++) {
	    			MergedData temp = merge(frags.get(i), frags.get(j));
	        		if (temp != null && temp.intersectionSize > max) {
	        			max = temp.intersectionSize;
	        			rearranged = temp.merged;
	        			storeAt = j;
	        			removeAt = i;
	        		}
	    		}	
	    	}
	    	frags.set(storeAt, rearranged);
	    	frags.remove(removeAt);
    	}
    	rearranged = "";
    	for (int i=0; i<frags.size(); i++)
    		rearranged += frags.get(i);
    	return rearranged;
    }
 
//    public static String arrange(ArrayList<String> frags) {
//    	String rearranged = null;
//    	if (frags.size() != 0) {
//    		rearranged = frags.remove(0);
//    		return arrange(rearranged, frags);
//    	}
//    	return null;
//    }
    
//    public static String arrange(String rearranged, ArrayList<String> frags) {
//    	int max = 0;
//    	if (frags.size() == 0)
//    		return rearranged;
//    	
//    	for (int i=0; i < frags.size(); i++) {
//    		MergedData temp = merge(rearranged, frags.get(i));
//    		if (temp != null && temp.intersectionSize > max) {
//    			max = temp.intersectionSize;
//    			rearranged = temp.merged;
//    			frags.remove(i);
//    			i--;
//    		}
//    	}
//       	return arrange(rearranged, frags);
//    }
    
    private static class MergedData {
    	private String merged;
    	private int intersectionSize;
    	
    	MergedData(String merged, int interSectionSize) {
    		this.merged = merged;
    		this.intersectionSize = interSectionSize;
    	}
    }
    
    public static MergedData merge(String text, String pattern) {
    	
    	String merged = null;
    	int intersectionSize = 0;
    	String ans1 = null;
    	String ans2 = null;
        KMP kmp = new KMP();    
        if (kmp.comp(text, pattern) != -1) { 
//        	System.out.println("pattern inside text");
        	ans1 = pattern;
        	merged = text;
        	intersectionSize = ans1.length();
        } else if (kmp.comp(pattern, text) != -1) {
//        	System.out.println("text inside pattern");
        	ans2 = text;
        	merged = pattern;
        	intersectionSize = ans2.length();
        } else {
        	ans1 = intersection(text,pattern,kmp);
	        ans2 = intersection(pattern,text,kmp);
	        
	        if (ans1 != null) {
	        	
	        	int pos = kmp.comp(text, ans1);
	        	// if index start in text : !0 check if it's at the end of string && check if ans is at beg of pattern
	        	if (pos != 0) {
	        		 if (ans1.equals(text.substring(text.length() - ans1.length(), text.length()))
	        				 && ans1.equals(pattern.substring(0, ans1.length()))) {
//	        			 System.out.println("merging pattern at the end the text"); // you have permission to merge
	        			 
	        			 String cutText = text.substring(0, text.length()-ans1.length());
	        			 merged = cutText + pattern;
	        			 intersectionSize = ans1.length();
	        		 }
	        	}
	        } 
	        
	        if (ans2 != null) {
	        	int pos = kmp.comp(pattern, ans2);
	        	
	        	if (pos != 0) {
	        		if (ans2.equals(pattern.substring(pattern.length() - ans2.length()))
	        				&& ans2.equals(text.substring(0, ans2.length()))) {
//	        			System.out.println("merging text at the end of the pattern");
	        			
	        			String cutPattern = pattern.substring(0, pattern.length()-ans2.length());
	        			if (merged != null) {
	        				if (merged.length() < (cutPattern + text).length()) {
	        					merged = cutPattern + text;
	        					intersectionSize = ans2.length();
	        				}
	        			} else {
	        				merged = cutPattern + text;
	        				intersectionSize = ans2.length();
	        			}
	        		}
	        	}
	        }
        }
        if (merged != null) {
        	MergedData mergedData = new MergedData(merged, intersectionSize);
        	return mergedData;
        }
    	return null;
    }
    
    public static String intersection(String text, String pattern, KMP kmp) {
    	int upperbound = pattern.length()-1;
    	int lowerbound = 0;
    	int midpoint = -1;
    	String pat = null;
    	String sx = null;
    	while (lowerbound <= upperbound) {
    		midpoint = lowerbound + (upperbound - lowerbound) / 2;
    		pat = pattern.substring(0, midpoint+1); // (0..midpoint index char)
	    	if (kmp.comp(text, pat) != -1) {
	    		// pattern matched (possibly too little selected)
	    		lowerbound = midpoint + 1;
	    		// storing successful pattern match
	    		sx = pat;
	    	} else {
	    		// pattern didn't match (too much selected)
	   			upperbound = midpoint - 1;  	
	    	}
    	}
    	return sx;
    }
    
}
