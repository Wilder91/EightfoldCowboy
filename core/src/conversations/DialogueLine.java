package conversations;

public class DialogueLine {
    public final String text;
    public final boolean isDecision;

    public DialogueLine(String text, boolean isDecision) {
        this.text = text;
        this.isDecision = isDecision;
    }
}
