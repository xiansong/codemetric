package xian.visitor;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.Map;

import xian.model.UserClass;

import com.google.common.collect.Maps;

/**
 * The Class ClassVisitor.
 */
public final class ClassVisitor extends VoidVisitorAdapter<Void> {

	private PackageDeclaration packageDeclaration;

	private List<ImportDeclaration> imports;

	private UserClass userClass;

	public ClassVisitor() {
	}

	public void setPkgDeclaration(final PackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	public void setImports(final List<ImportDeclaration> imports) {
		this.imports = imports;
	}

	@Override
	public void visit(final ClassOrInterfaceDeclaration n, final Void arg) {
		if (!n.isInterface()) {
			FieldVisitor fv = new FieldVisitor();
			n.accept(fv, arg);
			userClass = new UserClass(packageDeclaration, n.getName(), imports);
			userClass.setFields(fv.fields);
			fv = null;
			MethodVisitor mv = new MethodVisitor();
			n.accept(mv, arg);
			userClass.setDefinedMethods(mv.getMethodList());
		}
	}

	public UserClass getUserClass() {
		return userClass;
	}

	/** The Class FieldVisitor. */
	private static class FieldVisitor extends VoidVisitorAdapter<Void> {

		private Map<String, String> fields;

		public FieldVisitor() {
			fields = Maps.newHashMap();
		}

		@Override
		public void visit(final FieldDeclaration n, final Void arg) {
			for (VariableDeclarator v : n.getVariables()) {
				Boolean isRefType = n.getType().accept(
						new GenericVisitorAdapter<Boolean, Void>() {
							@Override
							public Boolean visit(final ReferenceType n,
									final Void arg) {
								return Boolean.TRUE;
							}
						}, null);
				if (isRefType != null)
					fields.put(v.getId().toString(), n.getType().toString());
			}
		}
		
	}

}
