package il.co.topq.difido.plugin;

public class ExecutionStatus {
	
	private final int id;
	private final String type;
	private final int pass;
	private final int fail;
	private final int warning;
	
	public ExecutionStatus(int id, String type, int pass, int fail, int warning) {
		super();
		this.id = id;
		this.type = type;
		this.pass = pass;
		this.fail = fail;
		this.warning = warning;
	}
	
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("id:");
		sb.append(id);
		sb.append(", type");
		sb.append(type);
		sb.append(", pass");
		sb.append(pass);
		sb.append(", fail");
		sb.append(fail);
		sb.append(", warning");
		sb.append(warning);
		return sb.toString();
		
	}
	
	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public int getPass() {
		return pass;
	}

	public int getFail() {
		return fail;
	}

	public int getWarning() {
		return warning;
	}
	
	
	
	
	
	
}
