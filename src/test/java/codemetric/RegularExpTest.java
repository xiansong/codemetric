package codemetric;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpTest {

	public static void main(String[] args) {
	Pattern p = Pattern.compile("[a-zA-Z0-9\\.\\-\\_]+(\\.git)$");
	Matcher m = p.matcher("https://github.com/xetorthio/jedis.git");
	String name = null;
	while(m.find()){
		System.out.println(m.group(0));
		name = m.group(0);
	}
	System.out.println(name.substring(0, name.length()-4));
	}
}
