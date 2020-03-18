import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreParser {
    
    public static String preParse(String line) {
        if (line == null || "".equals(line)) {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        } else {
            for (int i = 0; i < line.length(); i++) {
                switch (line.charAt(i)) {
                    case '@':
                    case '#':
                    case '^':
                        System.out.println("WRONG FORMAT!");
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        }
        //指数符号**->^
        String l = line.replaceAll("[\\t ]*\\*\\*[\\t ]*", "^");
        
        //去除部分空白项
        l = l.replaceAll("[\\t ]*\\([\\t ]*", "(");
        l = l.replaceAll("[\\t ]*\\)[\\t ]*", ")");
        l = l.replaceAll("[\\t ]*\\*[\\t ]*", "*");
        //指数的加减号
        l = l.replaceAll("(?<=\\^)\\+", "@");
        l = l.replaceAll("(?<=\\^)-", "#");
        //常数因子的加减号
        //项中非首个因子
        l = l.replaceAll("(?<=\\*)\\+", "@");
        l = l.replaceAll("(?<=\\*)-", "#");
        //项中首个因子
        l = l.replaceAll("(?<=[+\\-])[\\t ]*\\+(?=\\d)", "@");
        l = l.replaceAll("(?<=[+\\-])[\\t ]*-(?=\\d)", "#");
        //三角函数中的常数因子
        l = chTriSig(l);
        //项的加减号
        l = l.replaceAll("(?<=[+\\-])[\\t ]*\\+", "@");
        l = l.replaceAll("(?<=[+\\-])[\\t ]*-", "#");
        //逆置
        l = chSigRev(l);
        return l;
    }
    
    private static String chTriSig(String l) {
        char[] llChar = l.toCharArray();
        String reg = "(sin|cos)\\(((?<sig>[+\\-])\\d+)\\)";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(l);
        while (mat.find()) {
            int index = mat.start("sig");
            if (index != -1) {
                if (l.charAt(index) == '+') {
                    llChar[index] = '@';
                } else if (l.charAt(index) == '-') {
                    llChar[index] = '#';
                }
            }
        }
        return new String(llChar);
    }
    
    private static String chSigRev(String l) {
        char[] llChar = l.toCharArray();
        for (int i = 0; i < l.length(); i++) {
            switch (l.charAt(i)) {
                case '+':
                    llChar[i] = '@';
                    break;
                case '-':
                    llChar[i] = '#';
                    break;
                case '@':
                    llChar[i] = '+';
                    break;
                case '#':
                    llChar[i] = '-';
                    break;
                default:
                    break;
            }
        }
        return new String(llChar);
    }
}
