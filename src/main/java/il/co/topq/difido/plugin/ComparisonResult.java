package il.co.topq.difido.plugin;

public class ComparisonResult {

	private final int passToFail;
	private final int passToWarning;
	private final int failToPass;
	private final int failToWarning;
	private final int warningToPass;
	private final int warningToFail;
	private final int newTests;
	private final int deleteTests;
	
	public ComparisonResult(int passToFail, int passToWarning, int failToPass, int failToWarning, int warningToPass,
			int warningToFail, int newTests, int deleteTests) {
		super();
		this.passToFail = passToFail;
		this.passToWarning = passToWarning;
		this.failToPass = failToPass;
		this.failToWarning = failToWarning;
		this.warningToPass = warningToPass;
		this.warningToFail = warningToFail;
		this.newTests = newTests;
		this.deleteTests = deleteTests;
	}
	
	public int getPassToFail() {
		return passToFail;
	}
	public int getPassToWarning() {
		return passToWarning;
	}
	public int getFailToPass() {
		return failToPass;
	}
	public int getFailToWarning() {
		return failToWarning;
	}
	public int getWarningToPass() {
		return warningToPass;
	}
	public int getWarningToFail() {
		return warningToFail;
	}
	public int getNewTests() {
		return newTests;
	}
	public int getDeleteTests() {
		return deleteTests;
	}


}
