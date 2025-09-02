package application;

public class Task {

	private String name;
	private String deadline;
	private boolean completed;
	public Task(String name, String deadline, boolean completed) {
		this.name=name;
		this.deadline=deadline;
		this.completed=completed;
	}
	
	
	protected String getName() {
		return name;
		
	}
	
	protected void setName(String name) {
		this.name=name;
	}
	
	
	protected String getDeadline() {
		return deadline;
	}
	
	protected void setDeadline(String deadline) {
		this.deadline=deadline;
	}
	
	
	protected boolean isCompleted() {
		return completed;
	}
	
	protected void setCompleted(boolean completed) {
		this.completed=completed;
	}
	
	
	@Override
	public String toString() {
	    return (completed ? "✔" : "") + name + (deadline.isEmpty() ? "" : " (due: " + deadline + ")");
	}

	
}
