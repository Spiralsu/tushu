package com.shanzhu.book.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WechatPushUtils {

    // 【极其重要】：在这里填入你在 WxPusher 后台获取到的 appToken ！！
    // 例如：AT_yOxxxxxxxxx
    private static final String APP_TOKEN = "AT_ytET10XI7sqmlk96LFcvyIibVDTktvE0";

    /**
     * WxPusher 发送微信消息核心方法
     * @param uid      用户的 WxPusher UID (就是前端用户绑定的那个 UID_xxxxx)
     * @param title    消息标题
     * @param content  消息正文（支持 HTML 标签排版）
     */
    @SuppressWarnings("unchecked")
    public static void pushMessage(String uid, String title, String content) {
        if (uid == null || uid.trim().isEmpty() || !uid.startsWith("UID_")) {
            System.out.println("用户未绑定有效的 WxPusher UID，取消推送");
            return;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://wxpusher.zjiecode.com/api/send/message";

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建 WxPusher 要求的 JSON 数据体
            Map<String, Object> body = new HashMap<>();
            body.put("appToken", APP_TOKEN);
            body.put("content", "<h1>" + title + "</h1><br/><p style='color:#409EFF; font-size:16px;'>" + content + "</p>");
            body.put("summary", title); // 微信消息列表显示的摘要
            body.put("contentType", 2); // 1表示文字，2表示HTML（我们用2，这样发出去带颜色和换行，非常高级）

            // 存入接收者的 UID
            List<String> uids = new ArrayList<>();
            uids.add(uid);
            body.put("uids", uids);

            // 发送请求
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            System.out.println("WxPusher 推送结果: " + response);

        } catch (Exception e) {
            System.out.println("WxPusher 推送失败: " + e.getMessage());
        }
    }
}