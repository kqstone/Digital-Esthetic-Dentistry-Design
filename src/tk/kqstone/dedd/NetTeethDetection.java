package tk.kqstone.dedd;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NetTeethDetection implements ITeethDetection {

	private static final String NET_ADRESS = Configuration.SERVER_ADDR;
	public static final int PORT = Configuration.DEDDSERVER_PORT;

	private String netaddress;
	private int port;
	private String type;// 图片种类，口内照(intraoral)、面部照(face)

	private Preferences userPreferences = Preferences.userRoot().node("/config/settings");

	public NetTeethDetection() {
		String savedServerType = userPreferences.get("serverType", "default");
		if (!savedServerType.equals("default")) {
			netaddress = userPreferences.get("customServerAddress", NET_ADRESS);
		} else {
			netaddress = NET_ADRESS;
		}
		port = PORT;
	}

	public NetTeethDetection(String netaddress, int port) {
		this.netaddress = netaddress;
		this.port = port;
	}

	public NetTeethDetection(String netaddress, int port, String type) {
		this(netaddress, port);
		this.type = type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public List<Rectangle> detectTeeth(BufferedImage image) throws Exception {
		List<Rectangle> rects = null;
		File tmpFile = File.createTempFile("tmp_pic", ".jpg");
		ImageIO.write(image, "jpg", tmpFile);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		String apiUrl ="http://" + netaddress + ":" + port + "/detect_teeth/";
		HttpPost httpPost = new HttpPost(apiUrl);

		try {
			// 创建MultipartEntityBuilder来构建multipart/form-data实体
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			// 添加图片文件
			builder.addPart("file", new FileBody(tmpFile, ContentType.APPLICATION_OCTET_STREAM));

			// 添加model_type参数
			builder.addPart("detect_type", new StringBody(type, ContentType.TEXT_PLAIN));

			// 创建HttpEntity
			HttpEntity multipart = builder.build();
			httpPost.setEntity(multipart);

			// 发送请求并获取响应
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				System.out.println("Response status: " + response.getStatusLine().getStatusCode());
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					String result = EntityUtils.toString(responseEntity);
					StringBuilder sb = new StringBuilder(result);
					result = sb.substring(1, sb.length() - 1).replace("\\", "");
					System.out.println(result);
					Gson gson = new Gson();
					Type type = new TypeToken<ArrayList<Rectangle>>() {
					}.getType();
					rects = gson.fromJson(result, type);
				}
			} finally {
				response.close();
			}
		} finally {
			httpClient.close();
		}

		return rects;
	}

}
