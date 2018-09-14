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
    public void testRingOutAndCallLog() throws IOException, RestException {

        MakeRingOutRequest callRequestBody = new MakeRingOutRequest()
            .from(new MakeRingOutCallerInfoRequestFrom().phoneNumber(config.get("username")))
            .to(new MakeRingOutCallerInfoRequestTo().phoneNumber(config.get("receiver")));
        ResponseBody callReqAck = restClient.post("/restapi/v1.0/account/~/extension/~/ring-out", callRequestBody);
        JsonElement jsonCallReqAck = new JsonParser().parse(callReqAck.string());
        JsonObject jObjCallReqAck = jsonCallReqAck.getAsJsonObject();
        String callId = jObjCallReqAck.get("id").getAsString();
        Assert.assertTrue("CallID is not retreived, call may not be successful", !callId.equals(""));

        //get call log information
        HttpClient.QueryParameter params = new HttpClient.QueryParameter("content-type", "application/json");
        String endpoint = "/restapi/v1.0/account/~/extension/~/call-log";
        ResponseBody callsLog = restClient.get(endpoint, params);
        JsonElement callsLogJson = new JsonParser().parse(callsLog.string());
        JsonObject callLogsJObj = callsLogJson.getAsJsonObject();
        JsonArray callRecords = callLogsJObj.getAsJsonArray("records");
        JsonObject recentCallRecord = callRecords.get(0).getAsJsonObject();
        String latestCallLogUrl = recentCallRecord.get("uri").getAsString();
        ResponseBody callLog = restClient.get(latestCallLogUrl, params);
        System.out.println(callLog.string());
    }

}
