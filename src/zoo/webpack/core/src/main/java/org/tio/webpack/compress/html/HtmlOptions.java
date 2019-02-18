package org.tio.webpack.compress.html;

public class HtmlOptions {
	private boolean	minifyHtml					= true;
	private boolean	preventCaching				= true;
	private boolean	removeComments				= true;
	private boolean	removeMutliSpaces			= true;
	private boolean	removeIntertagSpaces		= false;
	private boolean	removeQuotes				= false;
	private boolean	simpleDoctype				= false;
	private boolean	removeScriptAttributes		= false;
	private boolean	removeStyleAttributes		= false;
	private boolean	removeLinkAttributes		= false;
	private boolean	removeFormAttributes		= false;
	private boolean	removeInputAttributes		= false;
	private boolean	simpleBooleanAttributes		= false;
	private boolean	removeJavaScriptProtocol	= false;
	private boolean	removeHttpProtocol			= false;
	private boolean	removeHttpsProtocol			= false;
	private boolean	preserveLineBreaks			= false;

	public boolean isMinifyHtml() {
		return minifyHtml;
	}

	public void setMinifyHtml(boolean minifyHtml) {
		this.minifyHtml = minifyHtml;
	}

	public boolean isRemoveComments() {
		return removeComments;
	}

	public void setRemoveComments(boolean removeComments) {
		this.removeComments = removeComments;
	}

	public boolean isRemoveMutliSpaces() {
		return removeMutliSpaces;
	}

	public void setRemoveMutliSpaces(boolean removeMutliSpaces) {
		this.removeMutliSpaces = removeMutliSpaces;
	}

	public boolean isRemoveIntertagSpaces() {
		return removeIntertagSpaces;
	}

	public void setRemoveIntertagSpaces(boolean removeIntertagSpaces) {
		this.removeIntertagSpaces = removeIntertagSpaces;
	}

	public boolean isRemoveQuotes() {
		return removeQuotes;
	}

	public void setRemoveQuotes(boolean removeQuotes) {
		this.removeQuotes = removeQuotes;
	}

	public boolean isSimpleDoctype() {
		return simpleDoctype;
	}

	public void setSimpleDoctype(boolean simpleDoctype) {
		this.simpleDoctype = simpleDoctype;
	}

	public boolean isRemoveScriptAttributes() {
		return removeScriptAttributes;
	}

	public void setRemoveScriptAttributes(boolean removeScriptAttributes) {
		this.removeScriptAttributes = removeScriptAttributes;
	}

	public boolean isRemoveStyleAttributes() {
		return removeStyleAttributes;
	}

	public void setRemoveStyleAttributes(boolean removeStyleAttributes) {
		this.removeStyleAttributes = removeStyleAttributes;
	}

	public boolean isRemoveLinkAttributes() {
		return removeLinkAttributes;
	}

	public void setRemoveLinkAttributes(boolean removeLinkAttributes) {
		this.removeLinkAttributes = removeLinkAttributes;
	}

	public boolean isRemoveFormAttributes() {
		return removeFormAttributes;
	}

	public void setRemoveFormAttributes(boolean removeFormAttributes) {
		this.removeFormAttributes = removeFormAttributes;
	}

	public boolean isRemoveInputAttributes() {
		return removeInputAttributes;
	}

	public void setRemoveInputAttributes(boolean removeInputAttributes) {
		this.removeInputAttributes = removeInputAttributes;
	}

	public boolean isSimpleBooleanAttributes() {
		return simpleBooleanAttributes;
	}

	public void setSimpleBooleanAttributes(boolean simpleBooleanAttributes) {
		this.simpleBooleanAttributes = simpleBooleanAttributes;
	}

	public boolean isRemoveJavaScriptProtocol() {
		return removeJavaScriptProtocol;
	}

	public void setRemoveJavaScriptProtocol(boolean removeJavaScriptProtocol) {
		this.removeJavaScriptProtocol = removeJavaScriptProtocol;
	}

	public boolean isRemoveHttpProtocol() {
		return removeHttpProtocol;
	}

	public void setRemoveHttpProtocol(boolean removeHttpProtocol) {
		this.removeHttpProtocol = removeHttpProtocol;
	}

	public boolean isRemoveHttpsProtocol() {
		return removeHttpsProtocol;
	}

	public void setRemoveHttpsProtocol(boolean removeHttpsProtocol) {
		this.removeHttpsProtocol = removeHttpsProtocol;
	}

	public boolean isPreserveLineBreaks() {
		return preserveLineBreaks;
	}

	public void setPreserveLineBreaks(boolean preserveLineBreaks) {
		this.preserveLineBreaks = preserveLineBreaks;
	}

	public boolean isPreventCaching() {
		return preventCaching;
	}

	public void setPreventCaching(boolean preventCaching) {
		this.preventCaching = preventCaching;
	}
}
