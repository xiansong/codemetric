package xian.visitor;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class HalsteadVisitor.
 * 
 * Check unique operators, operands and total number of operators and total
 * number of operands.
 */
public final class HalsteadVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * These two operators have not been defined as operators in javaparser api
	 */
	public static enum ExtraOperator {

		/** The instance of. */
		instanceOf,

		/** The ternary [:?]. */
		ternary
	}

	/** The oprAsgn stores unique assign operators and their frequencies. */
	private final TObjectIntMap<AssignExpr.Operator> oprAsgn;

	/** The oprBin stores unique binary operators and their frequencies. */
	private final TObjectIntMap<BinaryExpr.Operator> oprBin;

	/** The oprUnary stores unique unary operators and their frequencies. */
	private final TObjectIntMap<UnaryExpr.Operator> oprUnary;

	/** The extraOpr stores unique extra operators and their frequencies. */
	private final TObjectIntMap<ExtraOperator> extraOpr;

	/** The opd stores hashcode of unique operands and their frequencies. */
	private final TObjectIntMap<String> opd;

	public HalsteadVisitor() {
		oprAsgn = new TObjectIntHashMap<AssignExpr.Operator>();
		oprBin = new TObjectIntHashMap<BinaryExpr.Operator>();
		oprUnary = new TObjectIntHashMap<UnaryExpr.Operator>();
		extraOpr = new TObjectIntHashMap<ExtraOperator>();
		opd = new TObjectIntHashMap<String>();
	}

	/**
	 * Visit a variable declaration expression. If there are assignments in it,
	 */
	@Override
	public void visit(final VariableDeclarationExpr n, final Void arg) {
		for (VariableDeclarator vd : n.getVars()) {
			if (vd.getInit() != null) {
				oprAsgn.adjustOrPutValue(AssignExpr.Operator.assign, 1, 1);
				opd.adjustOrPutValue(vd.getId().getName(), 1, 1);
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
		oprAsgn.adjustOrPutValue(n.getOperator(), 1, 1);
		opd.adjustOrPutValue(n.getTarget().toString(), 1, 1);
		opd.adjustOrPutValue(n.getValue().toString(), 1, 1);
		n.getTarget().accept(this, arg);
		n.getValue().accept(this, arg);
	}

	/**
	 * Visit a binary expression [+,-,==,&&...], and then visit left-expr and
	 * right-expr with the current visitor.
	 */
	@Override
	public void visit(final BinaryExpr n, final Void arg) {
		oprBin.adjustOrPutValue(n.getOperator(), 1, 1);
		opd.adjustOrPutValue(n.getLeft().toString(), 1, 1);
		opd.adjustOrPutValue(n.getRight().toString(), 1, 1);
		n.getLeft().accept(this, arg);
		n.getRight().accept(this, arg);
	}

	/**
	 * Visit an unary expression [++,--,+,-,!,~] and then visit the
	 * expression-body with the current visitor
	 */
	@Override
	public void visit(final UnaryExpr n, final Void arg) {
		oprUnary.adjustOrPutValue(n.getOperator(), 1, 1);
		opd.adjustOrPutValue(n.getExpr().toString(), 1, 1);
		n.getExpr().accept(this, arg);
	}

	/**
	 * Visit a ternary expression [boolean?x:y] and then visit its sub-expr.
	 */
	@Override
	public void visit(final ConditionalExpr n, final Void arg) {
		extraOpr.adjustOrPutValue(ExtraOperator.ternary, 1, 1);
		n.getCondition().accept(this, arg);
		n.getElseExpr().accept(this, arg);
		n.getThenExpr().accept(this, arg);
	}

	/**
	 * Visit instanceof operation
	 */
	@Override
	public void visit(final InstanceOfExpr n, final Void arg) {
		extraOpr.adjustOrPutValue(ExtraOperator.instanceOf, 1, 1);
	}

	public int getUniqueOperators() {
		return oprAsgn.size() + oprBin.size() + oprUnary.size()
				+ extraOpr.size();
	}

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
		if (getVocabulary() == 0 || getProgramLength() == 0)
			return 1.0;
		return getProgramLength() * (Math.log(getVocabulary()) / Math.log(2.0));
	}

}
