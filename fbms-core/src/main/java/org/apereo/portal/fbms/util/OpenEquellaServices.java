package org.apereo.portal.fbms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Interfaces with an openEQUELLA institution
 */
@Service
public class OpenEquellaServices {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String accessToken = "NOT_SET";

    private String oeqUrl = "";

    private String oauthClientId = "";

    private String oauthClientSecret = "";

    private String targetCollectionUuid = "";


    public boolean gatherAccessToken() {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = String.format(
                    "%s/oauth/access_token?grant_type=client_credentials&client_id=%s&client_secret=%s&redirect_uri=default",
                    oeqUrl, oauthClientId, oauthClientSecret);

            HttpGet httpget = new HttpGet(url);

            logger.trace("Finding the openEQUELLA access token using [{}]", url);

            HttpResponse response;

            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();

            String respStr = EntityUtils.toString(entity);
            if (statusCode == HttpStatus.SC_OK) {
                JSONObject jresponse = new JSONObject(new JSONTokener(respStr));
                if (!jresponse.has("access_token")) {
                    logger.error("FAILURE:  Request for openEQUELLA access_token didn't return expected JSON key: {}",
                            respStr);
                    return false;
                } else {
                    accessToken = jresponse.getString("access_token");
                }
            } else {
                logger.error("FAILURE:  Request for openEQUELLA access_token failed with [{}]: {}", statusCode,
                        respStr);
                return false;
            }

        } catch (ParseException e) {
            logger.error("FAILURE:  Request for openEQUELLA access_token failed with ParseException: {}", e.getMessage());
            //logger.error(e);
            return false;
        } catch (IOException e) {
            logger.error("FAILURE:  Request for openEQUELLA access_token failed with IOException: {}", e.getMessage());
            //logger.error(e);
            return false;
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("FAILURE:  Request for openEQUELLA access_token failed.  Unable to close connection: {}", e.getMessage());
                return false;
            }
        }
        System.out.println("openEQUELLA access_token gathered: " + accessToken);
        return true;
    }
    
    public boolean save(String title) {
		try {

			JSONObject collJson = new JSONObject();
			collJson.put("uuid", targetCollectionUuid);

			JSONArray atts = new JSONArray();
			JSONObject attObj = new JSONObject();


			String itemUuid = UUID.randomUUID().toString();
			JSONObject itemObj = new JSONObject();
            itemObj.put("uuid", itemUuid);
            itemObj.put("version", 0);
            itemObj.put("name", title);
            String desc = "Test Item Desc " + System.currentTimeMillis();
            itemObj.put("description", desc);
			String metadata = "<xml><metadata><name>"+title+"</name><description>"+desc+"</description><tags><tag>demo</tag></tags></metadata></xml>";
            itemObj.put("metadata", metadata);
            itemObj.put("status", "live");
            itemObj.put("collection", collJson);
            itemObj.put("attachments", atts);

			// Upload the new version to Equella
			CloseableHttpClient httpclient = HttpClients.createDefault();
			String url = oeqUrl + "/api/item/?draft=false&waitforindex=true&keeplocked=false";
			HttpPost http = new HttpPost(url);

			logger.info("Updating the openEQUELLA resource with a new version via " + url + " with " + itemObj.toString());
			http.addHeader("X-Authorization", "access_token=" + accessToken);
			StringEntity input = new StringEntity(itemObj.toString());
			input.setContentType("application/json");
			http.setEntity(input);
			HttpResponse response = httpclient.execute(http);
			HttpEntity entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 201) {
				logger.info("Successfully created the openEQUELLA resource at: {}/{}", itemUuid, 1);
				entity.getContent().close();
				return true;
			} else {
				logger.warn("Unable to new version the openEQUELLA resource:  Status code on-save was: {}", statusCode);
				logger.warn("openEQUELLA response from trying to update the resource: {}", EntityUtils.toString(entity));
			}
		} catch (Exception e) {
			logger.warn("Unable to new version the openEQUELLA resource: {}", e.getMessage(), e);
		}
		return false;
	}
}
