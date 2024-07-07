package tk.kqstone.dedd;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DetectorAdapter implements IDetector {

	private static final String NET_ADRESS = Configuration.SERVER_ADDR;
	public static final int PORT = Configuration.DEDDSERVER_PORT;

	private String netaddress;
	private int port;
	private String urlSuffix;

	private Preferences userPreferences = Preferences.userRoot().node("/config/settings");

	public DetectorAdapter() {
		String savedServerType = userPreferences.get("serverType", "default");
		if (!savedServerType.equals("default")) {
			netaddress = userPreferences.get("customServerAddress", NET_ADRESS);
		} else {
			netaddress = NET_ADRESS;
		}
		port = PORT;
	}

	public DetectorAdapter(String urlSuffix) {
		this();
		this.urlSuffix = urlSuffix;
	}

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

	@Override
	public String detect(BufferedImage image) throws IOException {
		File tmpFile = File.createTempFile("tmp_pic", ".jpg");
		ImageIO.write(image, "jpg", tmpFile);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		String apiUrl = "http://" + netaddress + ":" + port + "/" + urlSuffix + "/";
		HttpPost httpPost = new HttpPost(apiUrl);

		try {
			// 创建MultipartEntityBuilder来构建multipart/form-data实体
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			// 添加图片文件
			builder.addPart("file", new FileBody(tmpFile, ContentType.APPLICATION_OCTET_STREAM));

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
					System.out.println(result);
					return result;
				}
			} finally {
				response.close();
			}
		} finally {
			httpClient.close();
		}

		return null;
	}

}
