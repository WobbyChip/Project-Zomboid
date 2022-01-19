// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

import javax.xml.bind.Marshaller;
import java.util.function.Supplier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import zombie.core.logger.ExceptionLogger;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;
import javax.xml.bind.JAXBException;
import java.util.function.Consumer;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Text;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import java.util.HashMap;
import org.w3c.dom.Attr;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.IOException;
import org.xml.sax.SAXException;
import zombie.ZomboidFileSystem;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;

public final class PZXmlUtil
{
    private static boolean s_debugLogging;
    private static final ThreadLocal<DocumentBuilder> documentBuilders;
    
    public static Element parseXml(final String s) throws PZXmlParserException {
        final String resolveFileOrGUID = ZomboidFileSystem.instance.resolveFileOrGUID(s);
        Element xmlInternal;
        try {
            xmlInternal = parseXmlInternal(resolveFileOrGUID);
        }
        catch (SAXException | IOException ex) {
            final Throwable t;
            throw new PZXmlParserException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, resolveFileOrGUID), t);
        }
        final Element includeAnotherFile = includeAnotherFile(xmlInternal, resolveFileOrGUID);
        String s2 = includeAnotherFile.getAttribute("x_extends");
        if (s2 == null || s2.trim().length() == 0) {
            return includeAnotherFile;
        }
        if (!ZomboidFileSystem.instance.isValidFilePathGuid(s2)) {
            s2 = ZomboidFileSystem.instance.resolveRelativePath(resolveFileOrGUID, s2);
        }
        return resolve(includeAnotherFile, parseXml(s2));
    }
    
    private static Element includeAnotherFile(final Element element, final String s) throws PZXmlParserException {
        String s2 = element.getAttribute("x_include");
        if (s2 == null || s2.trim().length() == 0) {
            return element;
        }
        if (!ZomboidFileSystem.instance.isValidFilePathGuid(s2)) {
            s2 = ZomboidFileSystem.instance.resolveRelativePath(s, s2);
        }
        final Element xml = parseXml(s2);
        if (!xml.getTagName().equals(element.getTagName())) {
            return element;
        }
        final Document newDocument = createNewDocument();
        final Node importNode = newDocument.importNode(element, true);
        final Node firstChild = importNode.getFirstChild();
        for (Node node = xml.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element) {
                importNode.insertBefore(newDocument.importNode(node, true), firstChild);
            }
        }
        importNode.normalize();
        return (Element)importNode;
    }
    
    private static Element resolve(final Element element, final Element element2) {
        final Document newDocument = createNewDocument();
        final Element resolve = resolve(element, element2, newDocument);
        newDocument.appendChild(resolve);
        if (PZXmlUtil.s_debugLogging) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, elementToPrettyStringSafe(element2), elementToPrettyStringSafe(element), elementToPrettyStringSafe(resolve)));
        }
        return resolve;
    }
    
    private static Element resolve(final Element element, final Element element2, final Document document) {
        if (isTextOnly(element)) {
            return (Element)document.importNode(element, true);
        }
        final Element element3 = document.createElement(element.getTagName());
        final ArrayList<Attr> list = new ArrayList<Attr>();
        final NamedNodeMap attributes = element2.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node item = attributes.item(i);
            if (!(item instanceof Attr)) {
                if (PZXmlUtil.s_debugLogging) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Node;)Ljava/lang/String;, item));
                }
            }
            else {
                list.add((Attr)document.importNode(item, true));
            }
        }
        final NamedNodeMap attributes2 = element.getAttributes();
        for (int j = 0; j < attributes2.getLength(); ++j) {
            final Node item2 = attributes2.item(j);
            if (!(item2 instanceof Attr)) {
                if (PZXmlUtil.s_debugLogging) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Node;)Ljava/lang/String;, item2));
                }
            }
            else {
                final Attr attr = (Attr)document.importNode(item2, true);
                final String name = attr.getName();
                boolean b = true;
                for (int k = 0; k < list.size(); ++k) {
                    if (list.get(k).getName().equals(name)) {
                        list.set(k, attr);
                        b = false;
                        break;
                    }
                }
                if (b) {
                    list.add(attr);
                }
            }
        }
        final Iterator<Attr> iterator = list.iterator();
        while (iterator.hasNext()) {
            element3.setAttributeNode(iterator.next());
        }
        final ArrayList<Element> list2 = new ArrayList<Element>();
        final HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (Node node = element2.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof Element)) {
                if (PZXmlUtil.s_debugLogging) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Node;)Ljava/lang/String;, node));
                }
            }
            else {
                final Element e = (Element)document.importNode(node, true);
                final String tagName = e.getTagName();
                hashMap.put(tagName, 1 + hashMap.getOrDefault(tagName, 0));
                list2.add(e);
            }
        }
        final HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
        for (Node node2 = element.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (!(node2 instanceof Element)) {
                if (PZXmlUtil.s_debugLogging) {
                    System.out.println(invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Node;)Ljava/lang/String;, node2));
                }
            }
            else {
                final Element e2 = (Element)document.importNode(node2, true);
                final String tagName2 = e2.getTagName();
                final int intValue = hashMap2.getOrDefault(tagName2, 0);
                final int l = 1 + intValue;
                hashMap2.put(tagName2, l);
                if (hashMap.getOrDefault(tagName2, 0) < l) {
                    list2.add(e2);
                }
                else {
                    int n = 0;
                    int n2 = 0;
                    while (n < list2.size()) {
                        final Element element4 = list2.get(n);
                        if (element4.getTagName().equals(tagName2)) {
                            if (n2 == intValue) {
                                list2.set(n, resolve(e2, element4, document));
                                break;
                            }
                            ++n2;
                        }
                        ++n;
                    }
                }
            }
        }
        final Iterator<Element> iterator2 = list2.iterator();
        while (iterator2.hasNext()) {
            element3.appendChild(iterator2.next());
        }
        return element3;
    }
    
    private static boolean isTextOnly(final Element element) {
        boolean b = false;
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            int n = 0;
            if (node instanceof Text) {
                n = (StringUtils.isNullOrWhitespace(node.getTextContent()) ? 0 : 1);
            }
            if (n == 0) {
                b = false;
                break;
            }
            b = true;
        }
        return b;
    }
    
    private static String elementToPrettyStringSafe(final Element element) {
        try {
            return elementToPrettyString(element);
        }
        catch (TransformerException ex) {
            return null;
        }
    }
    
    private static String elementToPrettyString(final Element n) throws TransformerException {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        final StreamResult streamResult = new StreamResult(new StringWriter());
        transformer.transform(new DOMSource(n), streamResult);
        return streamResult.getWriter().toString();
    }
    
    public static Document createNewDocument() {
        return PZXmlUtil.documentBuilders.get().newDocument();
    }
    
    private static Element parseXmlInternal(final String name) throws SAXException, IOException {
        try {
            final FileInputStream in = new FileInputStream(name);
            try {
                final BufferedInputStream is = new BufferedInputStream(in);
                try {
                    final Document parse = PZXmlUtil.documentBuilders.get().parse(is);
                    is.close();
                    final Element documentElement = parse.getDocumentElement();
                    documentElement.normalize();
                    final Element element = documentElement;
                    is.close();
                    in.close();
                    return element;
                }
                catch (Throwable t) {
                    try {
                        is.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (SAXException ex) {
            System.err.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
            throw ex;
        }
    }
    
    public static void forEachElement(final Element element, final Consumer<Element> consumer) {
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element) {
                consumer.accept((Element)node);
            }
        }
    }
    
    public static <T> T parse(final Class<T> clazz, final String s) throws PZXmlParserException {
        final Element xml = parseXml(s);
        try {
            return (T)UnmarshallerAllocator.get(clazz).unmarshal((Node)xml);
        }
        catch (JAXBException ex) {
            throw new PZXmlParserException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/String;, s, clazz), (Throwable)ex);
        }
    }
    
    public static <T> void write(final T t, final File file) throws TransformerException, IOException, JAXBException {
        final Document newDocument = createNewDocument();
        MarshallerAllocator.get(t).marshal((Object)t, (Node)newDocument);
        write(newDocument, file);
    }
    
    public static void write(final Document document, final File file) throws TransformerException, IOException {
        final String elementToPrettyString = elementToPrettyString(document.getDocumentElement());
        final FileOutputStream out = new FileOutputStream(file, false);
        final PrintWriter printWriter = new PrintWriter(out);
        printWriter.write(elementToPrettyString);
        printWriter.flush();
        out.flush();
        out.close();
    }
    
    public static <T> boolean tryWrite(final T t, final File file) {
        try {
            write(t, file);
            return true;
        }
        catch (TransformerException | IOException | JAXBException ex) {
            final Throwable t2;
            ExceptionLogger.logException(t2, invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/io/File;)Ljava/lang/String;, t, file));
            return false;
        }
    }
    
    public static boolean tryWrite(final Document document, final File file) {
        try {
            write(document, file);
            return true;
        }
        catch (TransformerException | IOException ex) {
            final Throwable t;
            ExceptionLogger.logException(t, invokedynamic(makeConcatWithConstants:(Lorg/w3c/dom/Document;Ljava/io/File;)Ljava/lang/String;, document, file));
            return false;
        }
    }
    
    static {
        PZXmlUtil.s_debugLogging = false;
        documentBuilders = ThreadLocal.withInitial(() -> {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder();
            }
            catch (ParserConfigurationException cause) {
                ExceptionLogger.logException(cause);
                throw new RuntimeException(cause);
            }
        });
    }
    
    private static final class UnmarshallerAllocator
    {
        private static final ThreadLocal<UnmarshallerAllocator> instance;
        private final Map<Class, Unmarshaller> m_map;
        
        private UnmarshallerAllocator() {
            this.m_map = new HashMap<Class, Unmarshaller>();
        }
        
        public static <T> Unmarshaller get(final Class<T> clazz) throws JAXBException {
            return UnmarshallerAllocator.instance.get().getOrCreate((Class<Object>)clazz);
        }
        
        private <T> Unmarshaller getOrCreate(final Class<T> clazz) throws JAXBException {
            Unmarshaller unmarshaller = this.m_map.get(clazz);
            if (unmarshaller == null) {
                unmarshaller = JAXBContext.newInstance(new Class[] { clazz }).createUnmarshaller();
                unmarshaller.setListener((Unmarshaller.Listener)new Unmarshaller.Listener() {
                    public void beforeUnmarshal(final Object o, final Object o2) {
                        super.beforeUnmarshal(o, o2);
                    }
                });
                this.m_map.put(clazz, unmarshaller);
            }
            return unmarshaller;
        }
        
        static {
            instance = ThreadLocal.withInitial((Supplier<? extends UnmarshallerAllocator>)UnmarshallerAllocator::new);
        }
    }
    
    private static final class MarshallerAllocator
    {
        private static final ThreadLocal<MarshallerAllocator> instance;
        private final Map<Class<?>, Marshaller> m_map;
        
        private MarshallerAllocator() {
            this.m_map = new HashMap<Class<?>, Marshaller>();
        }
        
        public static <T> Marshaller get(final T t) throws JAXBException {
            return get(t.getClass());
        }
        
        public static <T> Marshaller get(final Class<T> clazz) throws JAXBException {
            return MarshallerAllocator.instance.get().getOrCreate((Class<Object>)clazz);
        }
        
        private <T> Marshaller getOrCreate(final Class<T> clazz) throws JAXBException {
            Marshaller marshaller = this.m_map.get(clazz);
            if (marshaller == null) {
                marshaller = JAXBContext.newInstance(new Class[] { clazz }).createMarshaller();
                marshaller.setListener((Marshaller.Listener)new Marshaller.Listener() {
                    public void beforeMarshal(final Object o) {
                        super.beforeMarshal(o);
                    }
                });
                this.m_map.put(clazz, marshaller);
            }
            return marshaller;
        }
        
        static {
            instance = ThreadLocal.withInitial((Supplier<? extends MarshallerAllocator>)MarshallerAllocator::new);
        }
    }
}
