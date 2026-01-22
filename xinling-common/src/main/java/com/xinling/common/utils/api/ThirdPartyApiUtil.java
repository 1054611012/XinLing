package com.xinling.common.utils.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import java.util.Map;

/**
 * 第三方API调用通用工具类
 * @author SuXia
 * @date 2025/12/4 09:23
 */
@Component
public class ThirdPartyApiUtil {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取实时金价数据
     * @return 金价数据Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getGoldPrice() {
        return restTemplate.getForObject(APIUrlEnum.GOLD_PRICE_URL.getPath(), Map.class);
    }

    /**
     * 通用的GET请求方法
     * @param url 请求URL
     * @return 响应数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String url) {
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * 通用的GET请求方法（支持自定义请求头）
     * @param url 请求URL
     * @param headers 请求头
     * @return 响应数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String url, HttpHeaders headers) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }

    /**
     * 通用的POST请求方法
     * @param url 请求URL
     * @param requestBody 请求体
     * @return 响应数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> post(String url, Object requestBody) {
        return restTemplate.postForObject(url, requestBody, Map.class);
    }

    /**
     * 通用的PUT请求方法
     * @param url 请求URL
     * @param requestBody 请求体
     * @return 响应数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> put(String url, Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
        return response.getBody();
    }

    /**
     * 通用的DELETE请求方法
     * @param url 请求URL
     * @return 响应数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> delete(String url) {
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
        return response.getBody();
    }

    /**
     * 通用的HTTP请求方法（支持泛型返回类型）
     * @param url 请求URL
     * @param responseType 返回类型
     * @return 响应数据
     */
    public <T> T get(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }
}
