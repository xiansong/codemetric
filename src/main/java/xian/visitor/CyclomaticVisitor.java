package xian.visitor;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class CyclomaticVisitor.
 * 
 * Checks cyclomatic complexity against a specified limit. The complexity counts
 * the number of if, while, do, for/foreach, ?:, catch, switch, case statements
 * and operators && and || (plus 1) in the method.
 * 
 * Visit a single method declaration.
 */
public final class CyclomaticVisitor extends VoidVisitorAdapter<Void> {

	/** The Cyclomatic complexity for a method, 1 is for entry of the method. */
	private int number = 1;

	/**
	 * Instantiates a new cyclomatic visitor.
	 */
	public CyclomaticVisitor() {
	}

	/**
	 * Visit a do statement - do{body}while(conditon-expr), then visit the
	 * do-body and condition-expr with the current visitor.
	 * 
	 */
	@Override
	public void visit(final DoStmt n, final Void arg) {
		number++;
		n.getBody().accept(this, arg);
		n.getCondition().accept(this, arg);
	}

	/**
	 * Visit a for statement - for(init-expr ; compare-expr; update-expr){body},
	 * then visit the init-expression, compare-expr and update-expr with the
	 * current visitor. Finally visit the body with the current visitor. A
	 * special case is infinite-loop for(;;), which lead to its sub expr or stmt
	 * to be null.
	 */
	@Override
	public void visit(final ForStmt n, final Void arg) {
		number++;
		if (n.getInit() != null)
			for (Expression expr : n.getInit()) {
				expr.accept(this, arg);
			}
		if (n.getCompare() != null)
			n.getCompare().accept(this, arg);
		if (n.getUpdate() != null)
			for (Expression expr : n.getUpdate()) {
				expr.accept(this, arg);
			}
		n.getBody().accept(this, arg);
	}

	/**
	 * Visit a foreach statement - for(T d : Iterable<T>), then visit body.
	 */
	@Override
	public void visit(final ForeachStmt n, final Void arg) {
		number++;
		n.getBody().accept(this, arg);
	}

	@Override
	public void visit(final IfStmt n, final Void arg) {
		visitIfStmt(n);
	}

	/**
	 * Visit an if statement -
	 * if(condition-expr){then-stmt}else-if(){}|else{else-stmt}, Visit the
	 * condition-expr, then-stmt with the current visitor. When there is a
	 * hidden if-stmt, recursively visit the if-stmt, else visit the else-stmt
	 * with the current visitor 
	 */
	private void visitIfStmt(final IfStmt n) {
		number++;
		n.getCondition().accept(this, null);
		n.getThenStmt().accept(this, null);

		Statement elseStmt = n.getElseStmt();
		if (elseStmt != null) {
			if (IfStmt.class.isAssignableFrom(elseStmt.getClass())) {
				visitIfStmt((IfStmt) elseStmt);
			} else {
				n.getElseStmt().accept(this, null);
			}
		}
	}

	/**
	 * Visit a switch statement and only count case statement. Visist each
	 * case-entry including default with the current visitor. A switch statemtn
	 * may have no entries in it.
	 */
	@Override
	public void visit(final SwitchStmt n, final Void arg) {
		if (n.getEntries() != null) {
			number += n.getEntries().size();
			// label of default entry will be null, we don't count default
			if (n.getEntries().get(n.getEntries().size() - 1).getLabel() == null) {
				number -= 1;
			}
			for (SwitchEntryStmt switchEntryStmt : n.getEntries()) {
				switchEntryStmt.accept(this, arg);
			}
		}
	}

	/**
	 * Visit a try statement, counts all catch-blocks. Then visit the the
	 * try-block and each catch-block with the currrent visitor. Finally visit
	 * the finally-block with the current visitor.
	 */
	@Override
	public void visit(final TryStmt n, final Void arg) {
		if (n.getCatchs() != null) {
			number += n.getCatchs().size();
			n.getTryBlock().accept(this, null);
			for (CatchClause catchClause : n.getCatchs()) {
				catchClause.getCatchBlock().accept(this, null);
			}
			if (n.getFinallyBlock() != null) {
				n.getFinallyBlock().accept(this, arg);
			}
		}
	}

	/**
	 * Visit a while statement and then visit the while-body with the current
	 * visitor.
	 */
	@Override
	public void visit(final WhileStmt n, final Void arg) {
		number++;
		n.getBody().accept(this, null);
	}

	/**
	 * Visit a Binary expression with && or || then visit its left and right
	 * expression with the current visitor.
	 */
	@Override
	public void visit(final BinaryExpr n, final Void arg) {
		if (n.getOperator() == BinaryExpr.Operator.and
				|| n.getOperator() == BinaryExpr.Operator.or) {
			number++;
		}
		n.getLeft().accept(this, arg);
		n.getRight().accept(this, arg);
	}

	/**
	 * Visit a conditional expression (condition ? then-expr : else-expr), then
	 * visit its condition, then expression and else expression with the current
	 * visitor.
	 */
	@Override
	public void visit(final ConditionalExpr n, final Void arg) {
		number++;
		n.getCondition().accept(this, arg);
		n.getThenExpr().accept(this, arg);
		n.getElseExpr().accept(this, arg);
	}

	public int getCyclomaticNumber() {
		return number;
	}

}
