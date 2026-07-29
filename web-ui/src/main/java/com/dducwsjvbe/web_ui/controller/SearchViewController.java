package com.dducwsjvbe.web_ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Controller cho trang tìm kiếm bài viết.
 *
 * - GET /search        : trả về giao diện Thymeleaf (search.html)
 * - GET /search/list    : proxy sang article-service (api 1 - search filter)
 * - PUT /search/approve/{id}  : proxy sang article-service (api 3 - update/duyệt)
 * - DELETE /search/delete/{id}: proxy sang article-service (api 4 - delete)
 * - GET /search/file-path     : proxy sang file-service (api 5 - view file path)
 *
 * JS ở search.html chỉ gọi về các endpoint /search/... của chính web-ui (same-origin),
 * không gọi thẳng ra Gateway (8081) nữa. Việc gọi Gateway do RestClient đảm nhiệm,
 * RestClient này đã có interceptor tự gắn Bearer access_token (lấy từ TokenContext,
 * được TokenRefreshFilter set từ cookie) nên không cần xử lý cookie/CORS phía trình duyệt.
 */
@Controller
@RequiredArgsConstructor
@Slf4j(topic = "Search-View-Controller")
public class SearchViewController {

    private final RestClient restClient;

    @Value("${GatewayPort.url}")
    private String gatewayUrl;
    @Value("${open.file-service}")
    private String fileServiceUrl;

    @GetMapping("/search")
    public String searchPage(Model model) {
        log.info("open search page");
        model.addAttribute("fileServiceUrl", fileServiceUrl);
        return "searchadmin"; // ứng với templates/search.html
    }

    // ---- proxy: tìm kiếm bài viết (api 1) ----
    @GetMapping(value = "/search/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchArticles(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "true") boolean isReady) {

        log.info("searchArticles name={}; message={}; pageNo={}; isReady={}", name, message, pageNo, isReady);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(gatewayUrl + "/api/v1/articles")
                .queryParam("pageNo", pageNo)
                .queryParam("isReady", isReady);

        if (name != null && !name.isBlank()) {
            builder.queryParam("article", "name:" + name);
        }
        if (message != null && !message.isBlank()) {
            builder.queryParam("article", "message:" + message);
        }

        return restClient.get()
                .uri(builder.build().toUri())
                .retrieve()
                .body(String.class);
    }

    // ---- proxy: duyệt bài viết (api 3) ----
    @PutMapping(value = "/search/approve/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String approveArticle(@PathVariable String articleId) {
        log.info("approveArticle id={}", articleId);
        return restClient.put()
                .uri(gatewayUrl + "/api/v1/articles/" + articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .retrieve()
                .body(String.class);
    }

    // ---- proxy: xóa bài viết (api 4) ----
    @DeleteMapping(value = "/search/delete/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteArticle(@PathVariable String articleId) {
        log.info("deleteArticle id={}", articleId);
        return restClient.delete()
                .uri(gatewayUrl + "/api/v1/articles/" + articleId)
                .retrieve()
                .body(String.class);
    }

    // ---- proxy: lấy đường dẫn file (api 5) ----
    @GetMapping(value = "/search/file-path", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getFilePath(@RequestParam String fileId, @RequestParam String articleId) {
        log.info("getFilePath fileId={}; articleId={}", fileId, articleId);

        String uri = UriComponentsBuilder
                .fromUriString(gatewayUrl + "/api/v1/files/path")
                .queryParam("fileId", fileId)
                .queryParam("articleId", articleId)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }
    //user
    @GetMapping("/search-user")
    public String searchUserPage(Model model) {
        log.info("open search user page");
        model.addAttribute("fileServiceUrl", fileServiceUrl);
        return "searchuser"; // ứng với templates/searchuser.html
    }

    // ---- proxy: tìm kiếm bài viết cho user (api 4) ----
    @GetMapping(value = "/search-user/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String searchArticlesForUser(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String message) {

        log.info("searchArticlesForUser name={}; message={}; pageNo={}", name, message, pageNo);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(gatewayUrl + "/api/v1/articles/true")
                .queryParam("pageNo", pageNo)
                .queryParam("isReady", true);

        if (name != null && !name.isBlank()) {
            builder.queryParam("article", "name:" + name);
        }
        if (message != null && !message.isBlank()) {
            builder.queryParam("article", "message:" + message);
        }

        return restClient.get()
                .uri(builder.build().toUri())
                .retrieve()
                .body(String.class);
    }

    @GetMapping("/report")
    public String reportPage() {
        log.info("open report page");
        return "report"; // ứng với templates/report.html
    }

    // ---- proxy: report bài viết (api 5) ----
    @PostMapping(value = "/search-user/report/{articleId}")
    @ResponseBody
    public ResponseEntity<Void> reportArticle(
            @PathVariable String articleId,
            @RequestParam String message) {

        log.info("reportArticle id={}; message={}", articleId, message);

        String uri = UriComponentsBuilder
                .fromUriString(gatewayUrl + "/api/v1/articles/" + articleId)
                .queryParam("message", message)
                .toUriString();

        restClient.post()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();

        return ResponseEntity.ok().build();
    }
}