package pl.abstractsyntax;

/**
 * Objects of classes implementing this interface have concrete correspondence
 * to a position in the source code.
 */
public interface LinkToSource {
    
    /**
     * This can be used as a standard message by implementing classes when no 
     * link was set.
     */
    final static String NO_LINK_PROVIDED = "(no link to source provided)";
    
    /**
     * Acces the link to the source code of this element.
     * 
     * @return A link to the source code in the format "line, column"
     */
    public String getLinkToSource();
    
    /**
     * Idea: Maybe create a class for links if they should ever get more complex
     */
    public static class SourceLink {
        public String link;
    }
}
