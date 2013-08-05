package xian.visitor.model;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Stores information about a user-defined class.
 */
public final class UserClass {

	private PackageDeclaration packageDeclaration;
	private String name;
	private Map<String, String> fields;
	private List<UserMethod> definedMethods;
	private List<ImportDeclaration> imports;
	
	private int cyclo;
	private double volume;
	private double ratio;

	public UserClass(final PackageDeclaration packageDeclaration,
			final String name, List<ImportDeclaration> imports) {
		this.packageDeclaration = packageDeclaration;
		this.name = name;
		this.imports = imports;
	}

	public String getCanonicalName() {
		if (packageDeclaration == null) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(packageDeclaration.getName()).append(".").append(name);
		return sb.toString();
	}

	public List<ImportDeclaration> getImports() {
		if (imports == null)
			return Collections.emptyList();
		return imports;
	}

	public String getPackage() {
		return packageDeclaration.getName().toString();
	}

	public String getName() {
		return name;
	}

	public List<UserMethod> getDefinedMethods() {
		return definedMethods;
	}

	public void setDefinedMethods(final List<UserMethod> definedMethods) {
		this.definedMethods = definedMethods;
	}

	public UserMethod getUserMethod(final String name) {
		if (definedMethods == null)
			return null;
		for (UserMethod um : definedMethods) {
			if (um.getName().equals(name)) {
				return um;
			}
		}
		return null;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(final Map<String, String> fields) {

		this.fields = fields;
	}

	public int getCyclomatic() {
		if (cyclo != 0)
			return cyclo;
		for (UserMethod um : definedMethods) {
			cyclo += um.getCylomatic();
			volume += um.getVolume();
			ratio += um.getRatio();
		}
		return cyclo;
	}

	public double getVolume() {
		if (volume != 0.0)
			return volume;
		for (UserMethod um : definedMethods) {
			cyclo += um.getCylomatic();
			volume += um.getVolume();
			ratio += um.getRatio();
		}
		return volume;
	}

	public double getRatio() {
		if (ratio != 0.0)
			return ratio;
		for (UserMethod um : definedMethods) {
			cyclo += um.getCylomatic();
			volume += um.getVolume();
			ratio += um.getRatio();
		}
		return ratio;
	}

}
