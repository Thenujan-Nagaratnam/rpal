import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerAnna {
    boolean isOperatorSymbol(String token) {
        Pattern p = Pattern.compile("^[-+*/<>&.@/:=~|$!#%^\\[\\]{}\"'?]$");
        Matcher match = p.matcher(token);
        boolean result = match.find();
        return result;
    }

}