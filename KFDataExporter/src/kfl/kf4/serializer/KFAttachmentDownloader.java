package kfl.kf4.serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import kfl.kf4.connector.KFLoginModel;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class KFAttachmentDownloader {

	private CloseableHttpClient httpClient = HttpClients.createDefault();

	private KFLoginModel login;

	public void initialize(KFLoginModel login) throws Exception {
		this.login = login;
		HttpPost req = new HttpPost("http://" + login.getHost()
				+ "/authenticate");

		req.setEntity(new UrlEncodedFormEntity(Form.form()
				.add("DB", login.getDBName()).add("username", login.getUser())
				.add("password", login.getPassword()).build()));
		CloseableHttpResponse res = httpClient.execute(req);
		int status = res.getStatusLine().getStatusCode();
		System.out.println(status);
	}

	public void download(String id, File file) {
		try {
			String dbName = URLEncoder.encode(login.getDBName(), "UTF8");
			HttpGet req = new HttpGet("http://" + login.getHost()
					+ "/attachment?DB=" + dbName + "&AttachmentID=" + id);
			CloseableHttpResponse res = httpClient.execute(req);
			long len = res.getEntity().getContentLength();
			System.out.print("Download id:" + id + "(" + len + "bytes)");
			if (len <= 0) {
				System.out.println("canceled.");
				//return;
			}
			FileOutputStream out = new FileOutputStream(file);
			InputStream in = res.getEntity().getContent();
			byte[] buf = new byte[1024];
			long current = 0;
			while (current < len) {
				int nbyte = in.read(buf);
				out.write(buf, 0, nbyte);
				current += nbyte;
				System.out.print("=");
			}
			System.out.println();
			in.close();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
