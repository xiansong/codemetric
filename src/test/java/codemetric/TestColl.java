package codemetric;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import japa.parser.ast.expr.AssignExpr;

import java.util.EnumMap;
import java.util.Map;

public class TestColl {

	public static void main(String[] args) {

		TObjectIntMap<AssignExpr.Operator> tMap = new TObjectIntHashMap<AssignExpr.Operator>();
		Map<AssignExpr.Operator, Integer> eMap = new EnumMap<AssignExpr.Operator, Integer>(
				AssignExpr.Operator.class);

		tMap.put(AssignExpr.Operator.and, 10);
		eMap.put(AssignExpr.Operator.and, 10);

		long t1 = System.currentTimeMillis();

		for (int i = 0; i < 10000; i++) {
			tMap.adjustOrPutValue(AssignExpr.Operator.and, 1, 1);
		}
		System.out.println("TMap: " + (System.currentTimeMillis() - t1));

		long t2 = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			if (eMap.containsKey(AssignExpr.Operator.and))
				// eMap.get(AssignExpr.Operator.and);
				eMap.put(AssignExpr.Operator.and,
						eMap.get(AssignExpr.Operator.and) + 1);

		}
		System.out.println("eMap: " + (System.currentTimeMillis() - t2));
		System.out.println();



	}

}
