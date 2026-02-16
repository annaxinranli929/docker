package command.news;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.io.InputStream;
import java.io.StringWriter;

public class XmlParser{

    private Document document;

    public XmlParser(String urlString) throws Exception{
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(urlString))
            .build();

        InputStream inputStream = client.send(req, HttpResponse.BodyHandlers.ofInputStream()).body();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = builder.parse(inputStream);
        document.getDocumentElement().normalize();
        inputStream.close();
    }

    public List<Map<String, String>> extractAllElements(String tagName){
        NodeList nodes = document.getElementsByTagName(tagName);
        if(nodes.getLength() == 0){
            throw new IllegalArgumentException("指定されたタグが見つかりません: " + tagName);
        }
        List<Map<String, String>> results = new ArrayList<>();

        for(int i = 0; i < nodes.getLength(); i++){
            Element root = (Element) nodes.item(i);
            Map<String, String> result = new LinkedHashMap<>();
            extractRecursively(root, result);
            results.add(result);
        }
        return results;
    }

    private void extractRecursively(Element element, Map<String, String> result){
        NodeList children = element.getChildNodes();

        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);

            if(child.getNodeType() == Node.ELEMENT_NODE){
                Element childElement = (Element) child;
                String tagName = childElement.getTagName();

               if(childElement.hasAttribute("url")){
                    // まずurl属性があれば取得
                    result.put(tagName, childElement.getAttribute("url"));
                } else {
                    // なければ中身を取得
                    String xmlContent = getInnerXml(childElement);
                    result.put(tagName, xmlContent);
                }

                extractRecursively(childElement, result);
            }
        }
    }

    private String getInnerXml(Element element){
        StringBuilder sb = new StringBuilder();
        NodeList children = element.getChildNodes();

        for(int i = 0; i < children.getLength(); i++){
            try{
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(children.item(i)), new StreamResult(writer));
                sb.append(writer.toString());
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return sb.toString().trim();
    }
}
