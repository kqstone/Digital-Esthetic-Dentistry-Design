package tk.kqstone.dedd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomHelper {

	public static void addTextNode(Element parentElement, String nodeName, String textValue) {
		Document document = parentElement.getOwnerDocument();
		Element textNode = document.createElement(nodeName);
		textNode.setTextContent(textValue);
		parentElement.appendChild(textNode);
	}

	public static void addData(Element parentElement, Map<String, String> dataMap) {
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			addTextNode(parentElement, name, value);
		}
	}

	public static String getChildText(Node node, String childNodeName) {
		String text = null;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (nodeList.item(i).getNodeName().equals(childNodeName))
				text = nodeList.item(i).getTextContent();
		}
		return text;
	}

	public static String getAttributeValue(Node node, String attributeName) {
		String value = null;
		NamedNodeMap nodeMap = node.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			if (nodeMap.item(i).getNodeName().equals(attributeName))
				value = nodeMap.item(i).getNodeValue();
		}
		return value;
	}

	public static Map<String, String> getDataFromChildNode(Node node) {
		NodeList nodeList = node.getChildNodes();
		Map<String, String> data = new HashMap<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			String key = nodeList.item(i).getNodeName();
			String value = nodeList.item(i).getTextContent();
			data.put(key, value);
		}
		return data;
	}

	public static List<Node> getChildNode(Node node) {
		NodeList nodeList = node.getChildNodes();
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				nodes.add(nodeList.item(i));
			}
		}
		return nodes;
	}

}
