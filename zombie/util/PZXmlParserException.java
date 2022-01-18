// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public final class PZXmlParserException extends Exception
{
    public PZXmlParserException() {
    }
    
    public PZXmlParserException(final String message) {
        super(message);
    }
    
    public PZXmlParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public PZXmlParserException(final Throwable cause) {
        super(cause);
    }
    
    @Override
    public String toString() {
        String string = super.toString();
        final Throwable cause = this.getCause();
        if (cause != null) {
            string = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, string, System.lineSeparator(), System.lineSeparator(), cause.toString());
        }
        return string;
    }
}
