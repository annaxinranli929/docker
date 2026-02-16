package command.news;

public class HTMLTransformer {
    public static String transform(String text) {
        if (text == null){
            System.out.println("引数がnullです byHTMLTransformer.transform");
            return null;
        }
        String result = undoEscape(removeTag(removeCdata(text)));
        return result;
    }
    public static String removeCdata(String text) {
        if (text == null){
            System.out.println("引数がnullです byHTMLTransformer.removeCdata");
            return null;
        }
        String result = text.replaceAll("<!\\[CDATA\\[(.*?)\\]\\]>", "$1");
        return result;
    }
    public static String removeTag(String text) {
        if (text == null){
            System.out.println("引数がnullです byHTMLTransformer.removeTag");
            return null;
        }
        String result = text.replaceAll("<[^>]+>", "");
        return result;
    }
    public static String undoEscape(String text) {
        if (text == null){
            System.out.println("引数がnullです byHTMLTransformer.undoEscape");
            return null;
        }
        String result = text.replace("&amp;", "&").replace("“", "\"").replace("”", "\"");
        return result;
    }
}
