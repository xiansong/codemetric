package codemetric;

import java.util.HashMap;
import java.util.Map;

public class TestEnum {
	public enum Rule {
		OLD(0), NEW(1), UNKNOWN(-1);

		private final int value;

		private Rule(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		private static final Map<Integer, Rule> intToRuleMap = new HashMap<Integer, Rule>();
		static {
			for (Rule rule : Rule.values()) {
				intToRuleMap.put(rule.value, rule);
			}
		}

		public static Rule fromInt(final int i) {
			Rule rule = intToRuleMap.get(Integer.valueOf(i));
			if (rule == null)
				return Rule.UNKNOWN;
			return rule;
		}
	}

	public static void main(String[] args) {
		Rule r = Rule.fromInt(0);
		System.out.println(r==Rule.OLD);
	}
}
