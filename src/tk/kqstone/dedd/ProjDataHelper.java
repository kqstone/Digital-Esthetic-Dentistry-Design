package tk.kqstone.dedd;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProjDataHelper {
	private static final String DEDD_FILE = "dedd.xml";
	private static final String BASE_IMG_FILE = "base.jpg";
	private static final String FRONT_IMG_FILE = "front.jpg";
	private static final String TAG_ROOT = "dedd_data";
	private static final String TAG_BASIC_INFO = "basic_info";
	private static final String TAG_LIP_POINTS = "lip_points";
	private static final String TAG_POINT = "point";
	private static final String TAG_POINT_ID = "id";
	private static final String TAG_X = "x";
	private static final String TAG_Y = "y";
	private static final String TAG_CLOSED = "closed";
	private static final String TAG_WORKPANEL = "workpanel";
	private static final String TAG_WORKPANEL_ID = "id";
	private static final String FRONT = "front";
	private static final String TAG_MARK_TEETH = "mark_teeth";
	private static final String TAG_ADJUST_TEETH = "adjust_teeth";
	private static final String TAG_BRIGHT = "bright";
	private static final String TAG_YELLOW = "yellow";
	private static final String BASE = "base";
	private static final String TAG_TOOTH = "tooth";

	public static boolean saveTofile(ProjData projData, File file) {
		List<File> files = new ArrayList<>();
		try {
			String dir = file.getParent() + File.separator;
			File tmpXmlFile = new File(dir + DEDD_FILE);
			File tmpBaseImage = new File(dir + BASE_IMG_FILE);
			File tmpFrontImage = new File(dir + FRONT_IMG_FILE);

			saveDataToXml(projData, tmpXmlFile);
			saveImage(projData.getBaseImage(), tmpBaseImage);
			saveImage(projData.getFrontImage(), tmpFrontImage);

			files.add(tmpXmlFile);
			files.add(tmpBaseImage);
			files.add(tmpFrontImage);
			Utils.zip(file, files);

			return true;

		} catch (IOException | ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			for (File tmp : files) {
				if (tmp.exists())
					tmp.delete();
			}
		}
		return false;
	}

	private static void saveImage(BufferedImage bufImage, File imageFile) throws IOException {
		if (bufImage != null)
			ImageIO.write(bufImage, "jpg", imageFile);
	}

	private static void saveDataToXml(ProjData projData, File xmlFile)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement(TAG_ROOT);
		doc.appendChild(root);

		BasicInfo basicInfo = projData.getBasicInfo();
		if (basicInfo != null) {
			Map<String, String> basicInfoMap = basicInfo.getHashMap();
			Element elmInfo = doc.createElement(TAG_BASIC_INFO);
			root.appendChild(elmInfo);

			DomHelper.addData(elmInfo, basicInfoMap);
		}

		List<Point2D.Float> lipPoints = projData.getLipPoints();
		if (lipPoints != null) {
			Element elmLipPoints = doc.createElement(TAG_LIP_POINTS);
			root.appendChild(elmLipPoints);
			for (int i = 0; i < lipPoints.size(); i++) {
				Element elmPoint = doc.createElement(TAG_POINT);
				elmPoint.setAttribute(TAG_POINT_ID, String.valueOf(i));
				elmLipPoints.appendChild(elmPoint);
				DomHelper.addTextNode(elmPoint, TAG_X, String.valueOf(lipPoints.get(i).x));
				DomHelper.addTextNode(elmPoint, TAG_Y, String.valueOf(lipPoints.get(i).y));
			}
			boolean closed = projData.getLipPathClosed();
			DomHelper.addTextNode(root, TAG_CLOSED, String.valueOf(closed));
		}

		Element elmFrontWorkPanel = doc.createElement(TAG_WORKPANEL);
		elmFrontWorkPanel.setAttribute(TAG_WORKPANEL_ID, FRONT);
		root.appendChild(elmFrontWorkPanel);
		List<ToothData> frontMarkedTeeth = projData.getFrontMarkedTeeth();
		addTeethData(elmFrontWorkPanel, frontMarkedTeeth, TAG_MARK_TEETH);

		List<ToothData> frontAdjustedTeeth = projData.getFrontAdjustedTeeth();
		addTeethData(elmFrontWorkPanel, frontAdjustedTeeth, TAG_ADJUST_TEETH);
		DomHelper.addTextNode(elmFrontWorkPanel, TAG_BRIGHT, String.valueOf(projData.getFrontAdjustedTeethBright()));
		DomHelper.addTextNode(elmFrontWorkPanel, TAG_YELLOW, String.valueOf(projData.getFrontAdjustedTeethYellow()));

		Element elmBaseWorkPanel = doc.createElement(TAG_WORKPANEL);
		elmBaseWorkPanel.setAttribute(TAG_WORKPANEL_ID, BASE);
		root.appendChild(elmBaseWorkPanel);
		List<ToothData> baseMarkedTeeth = projData.getBaseMarkedTeeth();
		addTeethData(elmBaseWorkPanel, baseMarkedTeeth, TAG_MARK_TEETH);

		List<ToothData> baseAdjustedTeeth = projData.getBaseAdjustedTeeth();
		addTeethData(elmBaseWorkPanel, baseAdjustedTeeth, TAG_ADJUST_TEETH);
		DomHelper.addTextNode(elmBaseWorkPanel, TAG_BRIGHT, String.valueOf(projData.getBaseAdjustedTeethBright()));
		DomHelper.addTextNode(elmBaseWorkPanel, TAG_YELLOW, String.valueOf(projData.getBaseAdjustedTeethYellow()));

		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer();
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.transform(new DOMSource(doc), new StreamResult(xmlFile));
	}

	private static void addToothData(Element parentElement, ToothData toothdata) {
		Document document = parentElement.getOwnerDocument();
		Element elmTooth = document.createElement(TAG_TOOTH);
		parentElement.appendChild(elmTooth);
		Map<String, String> dataMap = toothdata.getHashMap();
		DomHelper.addData(elmTooth, dataMap);
	}

	private static void addTeethData(Element parentElement, List<ToothData> teethdata, String teethtype) {
		if (teethdata != null) {
			Document document = parentElement.getOwnerDocument();
			Element teethType = document.createElement(teethtype);
			parentElement.appendChild(teethType);
			for (int i = 0; i < teethdata.size(); i++) {
				addToothData(teethType, teethdata.get(i));
			}
		}
	}

	public static ProjData readFromfile(File file)
			throws IOException, ParserConfigurationException, SAXException, DataCheckException {
		ProjData projData = new ProjData();
		List<File> files = new ArrayList<>();
		Utils.unzip(file, files);
		for (File tmpFile : files) {
			String name = tmpFile.getName();
			switch (name) {
			case DEDD_FILE:
				readDataFromXml(projData, tmpFile);
				break;
			case BASE_IMG_FILE:
				projData.setBaseImage(ImageIO.read(tmpFile));
				break;
			case FRONT_IMG_FILE:
				projData.setFrontImage(ImageIO.read(tmpFile));
				break;
			}
			tmpFile.delete();
		}
		return projData;
	}

	private static void readDataFromXml(ProjData projData, File xmlFile)
			throws ParserConfigurationException, SAXException, IOException, DataCheckException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xmlFile);
		String docFileName = doc.getDocumentElement().getNodeName();
		if (docFileName != TAG_ROOT)
			throw new DataCheckException();

		projData.setBasicInfo(getBasicInfoFromXML(doc));
		projData.setLipPoints(getLipPointsFromXML(doc));
		projData.setLipPathClosed(getLipPathClosed(doc));

		projData.setBaseMarkedTeeth(getTeethData(doc, BASE, TAG_MARK_TEETH));
		projData.setBaseAdjustedTeeth(getTeethData(doc, BASE, TAG_ADJUST_TEETH));
		projData.setFrontMarkedTeeth(getTeethData(doc, FRONT, TAG_MARK_TEETH));
		projData.setFrontAdjustedTeeth(getTeethData(doc, FRONT, TAG_ADJUST_TEETH));

		projData.setBaseAdjustedTeethBright(getBrightOrYellowValue(doc, BASE, TAG_BRIGHT));
		projData.setFrontAdjustedTeethBright(getBrightOrYellowValue(doc, FRONT, TAG_BRIGHT));
		projData.setBaseAdjustedTeethYellow(getBrightOrYellowValue(doc, BASE, TAG_YELLOW));
		projData.setFrontAdjustedTeethYellow(getBrightOrYellowValue(doc, FRONT, TAG_YELLOW));
	}

	private static int getBrightOrYellowValue(Document document, String workPanelId, String tag) {
		int value = 0;
		NodeList nodeList = document.getElementsByTagName(tag);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node workPanelNode = nodeList.item(i).getParentNode();
			if (DomHelper.getAttributeValue(workPanelNode, TAG_WORKPANEL_ID).equals(workPanelId)) {
				value = Integer.parseInt(nodeList.item(i).getTextContent());
				break;
			}
		}
		return value;
	}
	
	private static boolean getLipPathClosed(Document document) {
		NodeList nodelist = document.getChildNodes();
		String s = DomHelper.getChildText(nodelist.item(0), TAG_CLOSED);
		boolean closed = Boolean.valueOf(s);
		System.out.println("LipPathClosed:" + "[" + closed + "]" );
		return closed;
	}

	private static List<ToothData> getTeethData(Document document, String workPanelId, String teethType) {
		List<ToothData> teethData = null;
		NodeList elmWorkPanel = document.getElementsByTagName(TAG_WORKPANEL);
		for (int i = 0; i < elmWorkPanel.getLength(); i++) {
			if (DomHelper.getAttributeValue(elmWorkPanel.item(i), TAG_WORKPANEL_ID).equals(workPanelId)) {
				List<Node> workPanelChildNodes = DomHelper.getChildNode(elmWorkPanel.item(i));
				for (int k = 0; k < workPanelChildNodes.size(); k++) {
					if (workPanelChildNodes.get(k).getNodeName().equals(teethType)) {
						teethData = getTeethData(workPanelChildNodes.get(k));
						System.out.println("TeethData:" + "[" + workPanelId + "]" + "[" + teethType + "]" + "数量:"
								+ teethData.size());
						return teethData;
					}
				}
			}
		}
		return teethData;
	}

	private static BasicInfo getBasicInfoFromXML(Document document) {
		NodeList elmInfo = document.getElementsByTagName(TAG_BASIC_INFO).item(0).getChildNodes();
		Map<String, String> mapInfo = getDataFromNodeList(elmInfo);
		BasicInfo info = BasicInfo.getBasicInfo(mapInfo);
		return info;
	}

	private static List<Point2D.Float> getLipPointsFromXML(Document document) {
		NodeList nodelist = document.getElementsByTagName(TAG_LIP_POINTS);
		if (nodelist == null || nodelist.getLength() == 0)
			return null;
		List<Point2D.Float> points = new ArrayList<>();
		NodeList elmPoints = nodelist.item(0).getChildNodes();
		for (int i = 0; i < elmPoints.getLength(); i++) {
			if (elmPoints.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			int id = Integer.parseInt(DomHelper.getAttributeValue(elmPoints.item(i), TAG_POINT_ID));
			Map<String, String> pointMap = DomHelper.getDataFromChildNode(elmPoints.item(i));
			float x = Float.parseFloat(pointMap.get(TAG_X));
			float y = Float.parseFloat(pointMap.get(TAG_Y));
			Point2D.Float point = new Point2D.Float(x, y);
			points.add(id, point);
		}
		for (Point2D point : points) {
			System.out.println("lip_point:" + point.getX() + "," + point.getY());
		}

		return points;
	}

	private static ToothData getToothData(Node toothNode) {
		Map<String, String> toothDataMap = DomHelper.getDataFromChildNode(toothNode);
		if (toothDataMap.get("id") == null)
			return null;
		ToothData td = ToothData.getToothData(toothDataMap);
		return td;
	}

	private static List<ToothData> getTeethData(Node teethNode) {
		List<ToothData> teethData = new ArrayList<>();
		List<Node> toothNodes = DomHelper.getChildNode(teethNode);
		for (Node node : toothNodes) {
			if (!node.getNodeName().equals(TAG_TOOTH))
				continue;
			ToothData toothData = getToothData(node);
			teethData.add(toothData);
		}
		return teethData;
	}

	private static Map<String, String> getDataFromNodeList(NodeList nodeList) {
		Map<String, String> data = new HashMap<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			String key = nodeList.item(i).getNodeName();
			String value = nodeList.item(i).getTextContent();
			data.put(key, value);
		}
		return data;
	}

	static class DataCheckException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8483385783547870092L;

		public DataCheckException() {
			super("Data Type Check Exception: this is not a dedd data");

		}
	}

}
