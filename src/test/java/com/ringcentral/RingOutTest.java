package com.ringcentral;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import com.ringcentral.definitions.MakeRingOutCallerInfoRequestFrom;
import com.ringcentral.definitions.MakeRingOutCallerInfoRequestTo;
import com.ringcentral.definitions.MakeRingOutRequest;


public class RingOutTest extends BaseTest {

    @Test
    public void testRingOut() throws IOException, RestException {

        MakeRingOutRequest requestBody = new MakeRingOutRequest()
            .from(new MakeRingOutCallerInfoRequestFrom().phoneNumber(config.get("username")))
            .to(new MakeRingOutCallerInfoRequestTo().phoneNumber(config.get("receiver")));
        ResponseBody response = restClient.post("/restapi/v1.0/account/~/extension/~/ring-out", requestBody);
        JsonElement jElement = new JsonParser().parse(response.string());
        JsonObject jObj = jElement.getAsJsonObject();
        String callId = jObj.get("id").getAsString();
        Assert.assertTrue("CallID is not retreived, call may not be successful", !callId.equals(""));

        //get call log information
        HttpClient.QueryParameter params = new HttpClient.QueryParameter("content-type", "application/json");
        String endpoint = "/restapi/v1.0/account/~/extension/~/call-log";
        response = restClient.get(endpoint, params);
        JsonElement callLogsJson = new JsonParser().parse(response.string());
        JsonObject callLogsJObj = callLogsJson.getAsJsonObject();
        JsonArray records = callLogsJObj.getAsJsonArray("records");
        JsonObject latestRecord = records.get(0).getAsJsonObject();
        String latestCallLogUrl = latestRecord.get("uri").getAsString();
        response = restClient.get(latestCallLogUrl, params);
        System.out.println(response.string());
    }

}
