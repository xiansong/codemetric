package xian.model;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The Class UserClass.
 * 
 * Stores information about a user-defined class.
 * 
 */
public final class UserClass {

	private PackageDeclaration packageDeclaration;
	private String name;
	private HashMap<String, String> fields;
	private List<UserMethod> definedMethods;
	private List<ImportDeclaration> imports;

	/**
	 * Instantiates a new user class.
	 * 
	 * @param packageDeclaration
	 *            the package declaration parsed from api, may be null
	 * @param name
	 *            the name
	 * @param imports
	 *            the imports parsed from api, may be null.
	 */
	public UserClass(final PackageDeclaration packageDeclaration,
			final String name, List<ImportDeclaration> imports) {
		this.packageDeclaration = packageDeclaration;
		this.name = name;
		this.imports = imports;
	}

	/**
	 * Gets the canonical name.
	 * 
	 * @return the canonical name is package name plus class name
	 */
	public String getCanonicalName() {
		if (packageDeclaration == null) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(packageDeclaration.getName()).append(".").append(name);
		return sb.toString();
	}

	/**
	 * Gets the imports.
	 * 
	 * The imports set from compilation unit may be null. If so return an empty
	 * list.
	 * 
	 * @return the import list
	 */
	public List<ImportDeclaration> getImports() {
		if (imports == null)
			return Collections.emptyList();
		return imports;
	}

	/**
	 * Gets the package name of the class visited.
	 * 
	 * @return the package name
	 */
	public String getPackage() {
		return packageDeclaration.getName().toString();
	}

	/**
	 * Gets the name of the class visited.
	 * 
	 * @return the class name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the defined userMethod list visited of the class visited, can be
	 * empty but not null.
	 * 
	 * @return the defined methods
	 */
	public List<UserMethod> getDefinedMethods() {
		return definedMethods;
	}

	/**
	 * Sets the defined userMehtod list to the userClass.
	 * 
	 * @param definedMethods
	 *            defined userMethod list from method visitor
	 */
	public void setDefinedMethods(final List<UserMethod> definedMethods) {
		this.definedMethods = definedMethods;
	}

	/**
	 * Check if the methods defined in the class has one matching the name.
	 * 
	 * @param name
	 *            the name of the method
	 * @return the userMethod object
	 */
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

	/**
	 * Gets the fields defined in the class, name as key and type as value, can
	 * be empty but not null.
	 * 
	 * @return the fields
	 */
	public HashMap<String, String> getFields() {
		return fields;
	}

	/**
	 * Sets the fields to the user class
	 * 
	 * @param fields
	 *            the fields
	 */
	public void setFields(final HashMap<String, String> fields) {

		this.fields = fields;
	}

	/**
	 * Gets the total Cyclomatic of the class.
	 * 
	 * @return the Cyclomatic number
	 */
	public int getCyclomatic() {
		int sum = 0;
		for (UserMethod um : definedMethods) {
			sum += um.getCylomatic();
		}
		return sum;
	}

	/**
	 * Gets the Halstead volume of the class.
	 * 
	 * @return the Halstead volume
	 */
	public double getVolume() {
		double sum = 0;
		for (UserMethod um : definedMethods) {
			sum += um.getVolume();
		}
		return sum;
	}

}
