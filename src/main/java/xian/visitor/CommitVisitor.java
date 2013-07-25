package xian.visitor;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xian.git.RepositoryAccess;
import xian.model.CallModel;
import xian.model.UserClass;
import xian.model.UserMethod;

public class CommitVisitor {

	static List<UserClass> ucs = new ArrayList<UserClass>();

	public void access(String url) throws Exception {
		RepositoryAccess ra = new RepositoryAccess(url, RepositoryAccess.OLD);
		for (Iterator<CompilationUnit> itr = ra.getJavaInputStream(
				ra.getCommits().get(0)).iterator(); itr.hasNext();) {
			visit(itr.next());
		}
	}

	public static void main(String[] args) throws Exception {
		CommitVisitor v = new CommitVisitor();
		v.access("https://github.com/xetorthio/jedis.git");
		System.out.println(ucs.size());

		List<CallModel> cms = new ArrayList<CallModel>();

		for (UserClass uc : ucs) {
			for (UserMethod um : uc.getDefinedMethods()) {
				// System.out.println(um.getName());
				for (MethodCallExpr mc : um.getCalls()) {
					CallModel cm = checkCallModel(mc, um, uc);
					if (cm != null) {
						if (!cms.contains(cm)) {
							cms.add(cm);
						}
					}
				}

			}
		}

		System.out.println(cms.size());

		for (CallModel cm : cms) {
			System.out.println(cm);
		}
	}

	private static CallModel checkCallModel(MethodCallExpr mc, UserMethod um,
			UserClass uc) {
		// check where method call variable is defined
		if (mc.getScope() != null
				&& um.getParameters().containsKey(mc.getScope().toString())) {
			if (uc.getImports() != null) {
				for (ImportDeclaration n : uc.getImports()) {
					if (n.getName()
							.getName()
							.equals(um.getParameters().get(
									mc.getScope().toString()))) {
						for (UserClass ucc : ucs) {
							if (ucc.getCanonicalName().equals(
									n.getName().toString())) {
								UserMethod callee = ucc.getUserMethod(mc
										.getName());
								if (callee == null)
									return null;

								StringBuilder sbCaller = new StringBuilder();
								StringBuilder sbCallee = new StringBuilder();
								sbCaller.append(uc.getCanonicalName())
										.append(".").append(um.getName());
								sbCallee.append(ucc.getCanonicalName())
										.append(".").append(callee.getName());

								CallModel cm = new CallModel.Builder(
										sbCaller.toString(),
										sbCallee.toString())
										.callerCyclo(um.getCylomatic())
										.callerVolume(um.getVolume())
										.calleeCyclo(callee.getCylomatic())
										.calleeVolume(callee.getVolume())
										.build();
								return cm;
							}
						}
						return null;
					}
				}
			} else { // import is null
				for (UserClass ucc : ucs) {
					if (ucc.getName().equals(
							um.getParameters().get(mc.getScope().toString()))) {
						UserMethod callee = ucc.getUserMethod(mc.getName());
						if (callee == null)
							return null;

						StringBuilder sbCaller = new StringBuilder();
						StringBuilder sbCallee = new StringBuilder();
						sbCaller.append(uc.getCanonicalName()).append(".")
								.append(um.getName());
						sbCallee.append(ucc.getCanonicalName()).append(".")
								.append(callee.getName());

						CallModel cm = new CallModel.Builder(
								sbCaller.toString(), sbCallee.toString())
								.callerCyclo(um.getCylomatic())
								.callerVolume(um.getVolume())
								.calleeCyclo(callee.getCylomatic())
								.calleeVolume(callee.getVolume()).build();
						return cm;
					}
				}
			}
			// check local defined variables
		} else if (mc.getScope() != null
				&& um.getVariables().containsKey(mc.getScope().toString())) {
			if (uc.getImports() != null) {
				for (ImportDeclaration n : uc.getImports()) {
					if (n.getName()
							.getName()
							.equals(um.getVariables().get(
									mc.getScope().toString()))) {
						for (UserClass ucc : ucs) {
							if (ucc.getCanonicalName().equals(
									n.getName().toString())) {
								UserMethod callee = uc.getUserMethod(mc
										.getName());
								if (callee == null)
									return null;

								StringBuilder sbCaller = new StringBuilder();
								StringBuilder sbCallee = new StringBuilder();
								sbCaller.append(uc.getCanonicalName())
										.append(".").append(um.getName());
								sbCallee.append(ucc.getCanonicalName())
										.append(".").append(callee.getName());

								CallModel cm = new CallModel.Builder(
										sbCaller.toString(),
										sbCallee.toString())
										.callerCyclo(um.getCylomatic())
										.callerVolume(um.getVolume())
										.calleeCyclo(callee.getCylomatic())
										.calleeVolume(callee.getVolume())
										.build();

								return cm;
							}
						}
						return null;
					}
				}
			} else { // import is null
				for (UserClass ucc : ucs) {
					if (ucc.getName().equals(
							um.getVariables().get(mc.getScope().toString()))) {
						UserMethod callee = ucc.getUserMethod(mc.getName());
						if (callee == null)
							return null;
						StringBuilder sbCaller = new StringBuilder();
						StringBuilder sbCallee = new StringBuilder();
						sbCaller.append(uc.getCanonicalName()).append(".")
								.append(um.getName());
						sbCallee.append(ucc.getCanonicalName()).append(".")
								.append(callee.getName());
						CallModel cm = new CallModel.Builder(
								sbCaller.toString(), sbCallee.toString())
								.callerCyclo(um.getCylomatic())
								.callerVolume(um.getVolume())
								.calleeCyclo(callee.getCylomatic())
								.calleeVolume(callee.getVolume()).build();
						return cm;
					}
				}
			}

		} else if (mc.getScope() != null
				&& uc.getFields().containsKey(mc.getScope().toString())) {
			if (uc.getImports() != null) {
				for (ImportDeclaration n : uc.getImports()) {
					if (n.getName()
							.getName()
							.equals(uc.getFields()
									.get(mc.getScope().toString()))) {
						for (UserClass ucc : ucs) {
							if (ucc.getCanonicalName().equals(
									n.getName().toString())) {
								UserMethod callee = ucc.getUserMethod(mc
										.getName());
								if (callee == null)
									return null;
								StringBuilder sbCaller = new StringBuilder();
								StringBuilder sbCallee = new StringBuilder();
								sbCaller.append(uc.getCanonicalName())
										.append(".").append(um.getName());
								sbCallee.append(ucc.getCanonicalName())
										.append(".").append(callee.getName());
								CallModel cm = new CallModel.Builder(
										sbCaller.toString(),
										sbCallee.toString())
										.callerCyclo(um.getCylomatic())
										.callerVolume(um.getVolume())
										.calleeCyclo(callee.getCylomatic())
										.calleeVolume(callee.getVolume())
										.build();
								return cm;
							}
						}
						return null;
					}
				}
			} else { // import is null
				for (UserClass ucc : ucs) {
					if (ucc.getName().equals(mc.getName())) {
						UserMethod callee = ucc.getUserMethod(mc.getName());
						if (callee == null)
							return null;
						StringBuilder sbCaller = new StringBuilder();
						StringBuilder sbCallee = new StringBuilder();
						sbCaller.append(uc.getCanonicalName()).append(".")
								.append(um.getName());
						sbCallee.append(ucc.getCanonicalName()).append(".")
								.append(callee.getName());
						CallModel cm = new CallModel.Builder(
								sbCaller.toString(), sbCallee.toString())
								.callerCyclo(um.getCylomatic())
								.callerVolume(um.getVolume())
								.calleeCyclo(callee.getCylomatic())
								.calleeVolume(callee.getVolume()).build();
						return cm;
					}
				}
			}
		}
		return null;
	}

	public void visit(CompilationUnit cu) {

		ClassVisitor visitor = new ClassVisitor();
		visitor.setPkgDeclaration(cu.getPackage());
		visitor.setImports(cu.getImports());

		cu.accept(visitor, null);

		UserClass uc = visitor.getUserClass();
		if (uc != null)
			ucs.add(uc);
	}
}
