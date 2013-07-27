package xian.visitor;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.Map;

import com.google.common.collect.Maps;

/** 
 * Provide access to Halstead measures targeting at method level.* 
 */
public class HalsteadVisitor extends VoidVisitorAdapter<Void> {

	public static enum ExtraOperator {

		instanceOf, // instanceof

		ternary // :?
	}

	/** The oprAsgn stores unique assign operators and their frequencies. */
	private Map<AssignExpr.Operator, Integer> oprAsgn;

	/** The oprBin stores unique binary operators and their frequencies. */
	private Map<BinaryExpr.Operator, Integer> oprBin;

	/** The oprUnary stores unique unary operators and their frequencies. */
	private Map<UnaryExpr.Operator, Integer> oprUnary;

	/** The extraOpr stores unique extra operators and their frequencies. */
	private Map<ExtraOperator, Integer> extraOpr;

	/** The opd stores unique operands and their frequencies. */
	private Map<String, Integer> opd;

	public HalsteadVisitor() {
		oprAsgn = Maps.newEnumMap(AssignExpr.Operator.class);
		oprBin = Maps.newEnumMap(BinaryExpr.Operator.class);
		oprUnary = Maps.newEnumMap(UnaryExpr.Operator.class);
		extraOpr = Maps.newEnumMap(ExtraOperator.class);
		opd = Maps.newHashMap();
	}

	/**
	 * Visit a variable declaration expression. If there are assignments in it,
	 */
	@Override
	public void visit(final VariableDeclarationExpr n, final Void arg) {
		for (VariableDeclarator vd : n.getVars()) {
			if (vd.getInit() != null) {
				if (oprAsgn.containsKey(AssignExpr.Operator.assign)) {
					oprAsgn.put(AssignExpr.Operator.assign,
							oprAsgn.get(AssignExpr.Operator.assign) + 1);
				} else {
					oprAsgn.put(AssignExpr.Operator.assign, 1);
				}
				if (opd.containsKey(vd.getId().getName())) {
					opd.put(vd.getId().getName(),
							opd.get(vd.getId().getName()) + 1);
				} else {
					opd.put(vd.getId().getName(), 1);
				}
				vd.accept(this, arg);
			}

		}
	}

	/**
	 * Visit an assign expression [=,+=,-=...], collect operators and operands
	 * and then visit the target and value by the current visitor.
	 */
	@Override
	public void visit(final AssignExpr n, final Void arg) {
		if (oprAsgn.containsKey(n.getOperator())) {
			oprAsgn.put(n.getOperator(), oprAsgn.get(n.getOperator()) + 1);
		} else {
			oprAsgn.put(n.getOperator(), 1);
		}
		if (opd.containsKey(n.getTarget().toString())) {
			opd.put(n.getTarget().toString(),
					opd.get(n.getTarget().toString()) + 1);
		} else {
			opd.put(n.getTarget().toString(), 1);
		}
		if (opd.containsKey(n.getValue().toString())) {
			opd.put(n.getValue().toString(),
					opd.get(n.getValue().toString()) + 1);
		} else {
			opd.put(n.getValue().toString(), 1);
		}
		n.getTarget().accept(this, arg);
		n.getValue().accept(this, arg);
	}

	/**
	 * Visit a binary expression [+,-,==,&&...], and then visit left-expr and
	 * right-expr with the current visitor.
	 */
	@Override
	public void visit(final BinaryExpr n, final Void arg) {
		if (oprBin.containsKey(n.getOperator())) {
			oprBin.put(n.getOperator(), oprBin.get(n.getOperator()) + 1);
		} else {
			oprBin.put(n.getOperator(), 1);
		}
		if (opd.containsKey(n.getLeft().toString())) {
			opd.put(n.getLeft().toString(), opd.get(n.getLeft().toString()) + 1);
		} else {
			opd.put(n.getLeft().toString(), 1);
		}
		if (opd.containsKey(n.getRight().toString())) {
			opd.put(n.getRight().toString(),
					opd.get(n.getRight().toString()) + 1);
		} else {
			opd.put(n.getRight().toString(), 1);
		}
		n.getLeft().accept(this, arg);
		n.getRight().accept(this, arg);
	}

	/**
	 * Visit an unary expression [++,--,+,-,!,~] and then visit the
	 * expression-body with the current visitor
	 */
	@Override
	public void visit(final UnaryExpr n, final Void arg) {
		if (oprUnary.containsKey(n.getOperator())) {
			oprUnary.put(n.getOperator(), oprUnary.get(n.getOperator()) + 1);
		} else {
			oprUnary.put(n.getOperator(), 1);
		}
		if (opd.containsKey(n.getExpr().toString())) {
			opd.put(n.getExpr().toString(), opd.get(n.getExpr().toString()) + 1);
		} else {
			opd.put(n.getExpr().toString(), 1);
		}
		n.getExpr().accept(this, arg);
	}

	/**
	 * Visit a ternary expression [boolean?x:y] and then visit its sub-expr.
	 */
	@Override
	public void visit(final ConditionalExpr n, final Void arg) {
		if (extraOpr.containsKey(ExtraOperator.ternary)) {
			extraOpr.put(ExtraOperator.ternary,
					extraOpr.get(ExtraOperator.ternary) + 1);
		} else {
			extraOpr.put(ExtraOperator.ternary, 1);
		}
		n.getCondition().accept(this, arg);
		n.getElseExpr().accept(this, arg);
		n.getThenExpr().accept(this, arg);
	}

	/**
	 * Visit instanceof operation
	 */
	@Override
	public void visit(final InstanceOfExpr n, final Void arg) {
		if (extraOpr.containsKey(ExtraOperator.instanceOf)) {
			extraOpr.put(ExtraOperator.instanceOf,
					extraOpr.get(ExtraOperator.instanceOf) + 1);
		} else {
			extraOpr.put(ExtraOperator.instanceOf, 1);
		}
	}

	/**
	 * Gets the unique operators.
	 * 
	 * @return the unique operators
	 */
	public int getUniqueOperators() {
		return oprAsgn.size() + oprBin.size() + oprUnary.size()
				+ extraOpr.size();
	}

	/**
	 * Gets the total operators.
	 * 
	 * @return the total operators
	 */
	public int getTotalOperators() {
		int total = 0;
		for (Integer i : oprAsgn.values()) {
			total += i;
		}
		for (Integer i : oprBin.values()) {
			total += i;
		}
		for (Integer i : oprUnary.values()) {
			total += i;
		}
		for (Integer i : extraOpr.values()) {
			total += i;
		}
		return total;
	}

	public int getUniqueOperands() {
		return opd.size();
	}

	public int getTotalOperands() {
		int total = 0;
		for (Integer i : opd.values()) {
			total += i;
		}
		return total;
	}

	public int getVocabulary() {
		return getUniqueOperators() + getUniqueOperands();
	}

	public int getProgramLength() {
		return getTotalOperators() + getTotalOperands();
	}

	public double getVolume() {
		if (getVocabulary() == 0)
			return 1;
		return getProgramLength()
				* (Math.log(getVocabulary()) / Math.log(2.0d));
	}

}
