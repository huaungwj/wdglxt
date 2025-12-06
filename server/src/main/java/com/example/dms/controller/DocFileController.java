package com.example.dms.controller;

import com.example.dms.application.DocFileApplicationService;
import com.example.dms.application.dto.DocFileCreateCommand;
import com.example.dms.application.dto.DocFileSearchQuery;
import com.example.dms.common.ApiResponse;
import com.example.dms.common.util.JwtUtil;
import com.example.dms.controller.dto.DocFileRequestDTO;
import com.example.dms.controller.dto.DocFileResponseDTO;
import java.util.stream.Collectors;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档文件控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证
 * 3. 调用 Application Service 层处理业务逻辑
 * 4. 处理文件上传下载
 * 5. 返回统一的 API 响应格式
 * 
 * 重要原则：
 * - 不直接使用 Mapper 或 Entity
 * - 不包含业务逻辑，只负责数据转换和调用
 * - 每层使用独立的数据类，保持层间解耦
 */
@RestController
@RequestMapping("/api/docs")
public class DocFileController {

    private final DocFileApplicationService docFileApplicationService;
    private final JwtUtil jwtUtil;
    private final com.example.dms.application.service.PermissionService permissionService;
    private final String uploadDir = "uploads/";

    public DocFileController(
            DocFileApplicationService docFileApplicationService, 
            JwtUtil jwtUtil,
            com.example.dms.application.service.PermissionService permissionService) {
        this.docFileApplicationService = docFileApplicationService;
        this.jwtUtil = jwtUtil;
        this.permissionService = permissionService;
    }

    /**
     * 获取日期文件夹树形结构
     */
    @GetMapping("/date-folders")
    public ApiResponse<List<Map<String, Object>>> getDateFolders(
            @RequestHeader("Authorization") String token) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        Long currentUserId = getCurrentUserId(token);
        
        // 获取所有文档的创建日期（带权限过滤）
        DocFileSearchQuery query = new DocFileSearchQuery();
        query.setStatus(1); // 只查询正常状态的文档
        List<com.example.dms.application.dto.DocFileResponseDTO> allDocs = docFileApplicationService.search(
            query, currentUserId
        );
        
        // 提取所有唯一的日期（年/月/日）
        Set<String> dateSet = new HashSet<>();
        for (com.example.dms.application.dto.DocFileResponseDTO doc : allDocs) {
            if (doc.getCreatedAt() != null) {
                LocalDate date = doc.getCreatedAt().toLocalDate();
                String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                dateSet.add(dateStr);
            }
        }
        
        // 构建树形结构：年 -> 月 -> 日
        Map<String, Map<String, Set<String>>> yearMap = new HashMap<>();
        for (String dateStr : dateSet) {
            String[] parts = dateStr.split("/");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            
            yearMap.computeIfAbsent(year, k -> new HashMap<>())
                   .computeIfAbsent(month, k -> new HashSet<>())
                   .add(day);
        }
        
        // 转换为前端需要的格式
        for (Map.Entry<String, Map<String, Set<String>>> yearEntry : yearMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList())) {
            Map<String, Object> yearNode = new HashMap<>();
            yearNode.put("value", yearEntry.getKey());
            yearNode.put("label", yearEntry.getKey() + "年");
            yearNode.put("children", new ArrayList<>());
            
            List<Map<String, Object>> monthList = (List<Map<String, Object>>) yearNode.get("children");
            for (Map.Entry<String, Set<String>> monthEntry : yearEntry.getValue().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toList())) {
                Map<String, Object> monthNode = new HashMap<>();
                monthNode.put("value", yearEntry.getKey() + "/" + monthEntry.getKey());
                monthNode.put("label", monthEntry.getKey() + "月");
                monthNode.put("children", new ArrayList<>());
                
                List<Map<String, Object>> dayList = (List<Map<String, Object>>) monthNode.get("children");
                for (String day : monthEntry.getValue().stream().sorted().collect(Collectors.toList())) {
                    Map<String, Object> dayNode = new HashMap<>();
                    dayNode.put("value", yearEntry.getKey() + "/" + monthEntry.getKey() + "/" + day);
                    dayNode.put("label", day + "日");
                    dayNode.put("children", null);
                    dayList.add(dayNode);
                }
                
                monthList.add(monthNode);
            }
            
            tree.add(yearNode);
        }
        
        return ApiResponse.ok(tree);
    }

    /**
     * 搜索文档列表（带权限过滤）
     */
    @GetMapping
    public ApiResponse<List<DocFileResponseDTO>> search(
            @RequestParam(required = false) String fileNo,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) {
        
        Long currentUserId = getCurrentUserId(token);
        
        DocFileSearchQuery query = new DocFileSearchQuery();
        query.setFileNo(fileNo);
        query.setCreatorId(creatorId);
        query.setKeyword(keyword);
        query.setStatus(status);
        query.setCategoryId(categoryId);
        query.setPage(page);
        query.setPageSize(pageSize);
        
        List<com.example.dms.application.dto.DocFileResponseDTO> appResults = docFileApplicationService.search(query, currentUserId);
        List<DocFileResponseDTO> results = appResults.stream()
            .map(app -> convertToControllerDTO(app, request))
            .collect(Collectors.toList());
        return ApiResponse.ok(results);
    }

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public ApiResponse<DocFileResponseDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) throws IOException {
        
        Long currentUserId = getCurrentUserId(token);
        
        // 权限检查：是否有上传权限
        if (!permissionService.canUpload(currentUserId)) {
            return ApiResponse.fail("您没有上传文件的权限");
        }
        
        // 按日期创建文件夹（年/月/日）
        LocalDate now = LocalDate.now();
        String dateFolder = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dateDir = uploadDir + dateFolder + "/";
        
        // 创建日期目录
        File dir = new File(dateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(dateDir + filename);
        Files.write(filePath, file.getBytes());

        // 生成文件编号
        String fileNo = "F" + System.currentTimeMillis();

        // 创建命令对象
        DocFileCreateCommand command = new DocFileCreateCommand();
        command.setFileNo(fileNo);
        command.setName(originalFilename.substring(0, originalFilename.lastIndexOf(".")));
        command.setOriginalName(originalFilename);
        command.setPath(dateDir + filename);
        command.setSize(file.getSize());
        // 限制MIME类型长度，避免超过数据库字段长度
        String contentType = file.getContentType();
        if (contentType != null && contentType.length() > 255) {
            contentType = contentType.substring(0, 255);
        }
        command.setType(contentType);
        command.setCreatorId(currentUserId);
        command.setCategoryId(categoryId);
        command.setTags(tags);

        com.example.dms.application.dto.DocFileResponseDTO appResponse = docFileApplicationService.create(command);
        DocFileResponseDTO response = convertToControllerDTO(appResponse, request);
        return ApiResponse.ok(response);
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/batch-upload")
    public ApiResponse<List<DocFileResponseDTO>> batchUpload(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) throws IOException {
        
        Long currentUserId = getCurrentUserId(token);
        
        // 按日期创建文件夹（年/月/日）
        LocalDate now = LocalDate.now();
        String dateFolder = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String dateDir = uploadDir + dateFolder + "/";
        
        // 创建日期目录
        File dir = new File(dateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<DocFileResponseDTO> results = new java.util.ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                // 生成文件名
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = UUID.randomUUID().toString() + extension;

                // 保存文件
                Path filePath = Paths.get(dateDir + filename);
                Files.write(filePath, file.getBytes());

                // 生成文件编号
                String fileNo = "F" + System.currentTimeMillis();

                // 创建命令对象
                DocFileCreateCommand command = new DocFileCreateCommand();
                command.setFileNo(fileNo);
                command.setName(originalFilename.substring(0, originalFilename.lastIndexOf(".")));
                command.setOriginalName(originalFilename);
                command.setPath(dateDir + filename);
                command.setSize(file.getSize());
                // 限制MIME类型长度，避免超过数据库字段长度
        String contentType = file.getContentType();
        if (contentType != null && contentType.length() > 255) {
            contentType = contentType.substring(0, 255);
        }
        command.setType(contentType);
                command.setCreatorId(currentUserId);
                command.setCategoryId(categoryId);
                command.setTags(tags);

                com.example.dms.application.dto.DocFileResponseDTO appResponse = docFileApplicationService.create(command);
                DocFileResponseDTO response = convertToControllerDTO(appResponse, request);
                results.add(response);
            } catch (Exception e) {
                // 单个文件上传失败不影响其他文件
                e.printStackTrace();
            }
        }

        return ApiResponse.ok(results);
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) throws IOException {
        
        Long currentUserId = getCurrentUserId(token);
        
        com.example.dms.application.dto.DocFileResponseDTO appDocFile = docFileApplicationService.findById(id, currentUserId)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        // 权限检查：是否有下载权限
        if (!permissionService.canDownload(currentUserId, appDocFile.getCreatorId())) {
            return ResponseEntity.status(403).build();
        }
        
        DocFileResponseDTO docFile = convertToControllerDTO(appDocFile, null);

        File file = new File(docFile.getPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + docFile.getOriginalName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * 预览文件
     */
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> preview(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) throws IOException {
        
        Long currentUserId = getCurrentUserId(token);
        
        com.example.dms.application.dto.DocFileResponseDTO appDocFile = docFileApplicationService.findById(id, currentUserId)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        
        // 权限检查：是否有预览权限
        if (!permissionService.canPreview(currentUserId, appDocFile.getCreatorId())) {
            return ResponseEntity.status(403).build();
        }
        
        DocFileResponseDTO docFile = convertToControllerDTO(appDocFile, null);

        // 记录访问日志
        docFileApplicationService.incrementVisitCount(id, currentUserId);

        File file = new File(docFile.getPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        // 根据文件类型设置Content-Type
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (docFile.getType() != null) {
            mediaType = MediaType.parseMediaType(docFile.getType());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DocFileResponseDTO> getById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            HttpServletRequest request) {
        
        Long currentUserId = getCurrentUserId(token);
        
        com.example.dms.application.dto.DocFileResponseDTO appDocFile = docFileApplicationService.findById(id, currentUserId)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
        DocFileResponseDTO docFile = convertToControllerDTO(appDocFile, request);
        return ApiResponse.ok(docFile);
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        Long currentUserId = getCurrentUserId(token);
        docFileApplicationService.delete(id, currentUserId);
        return ApiResponse.ok();
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}/tags")
    public ApiResponse<Void> updateTags(
            @PathVariable Long id,
            @RequestBody DocFileRequestDTO request,
            @RequestHeader("Authorization") String token) {
        
        Long currentUserId = getCurrentUserId(token);
        docFileApplicationService.updateTags(id, request.getTags(), currentUserId);
        return ApiResponse.ok();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}/category")
    public ApiResponse<Void> updateCategory(
            @PathVariable Long id,
            @RequestBody DocFileRequestDTO request,
            @RequestHeader("Authorization") String token) {
        
        Long currentUserId = getCurrentUserId(token);
        docFileApplicationService.updateCategory(id, request.getCategoryId(), currentUserId);
        return ApiResponse.ok();
    }

    /**
     * 从Token中获取当前用户ID
     */
    private Long getCurrentUserId(String token) {
        String authToken = token.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(authToken);
    }

    /**
     * 将 Application 层的响应转换为 Controller 层的响应 DTO
     */
    private DocFileResponseDTO convertToControllerDTO(com.example.dms.application.dto.DocFileResponseDTO appResponse, HttpServletRequest request) {
        DocFileResponseDTO response = new DocFileResponseDTO();
        response.setId(appResponse.getId());
        response.setFileNo(appResponse.getFileNo());
        response.setName(appResponse.getName());
        response.setOriginalName(appResponse.getOriginalName());
        response.setPath(appResponse.getPath());
        response.setSize(appResponse.getSize());
        response.setType(appResponse.getType());
        response.setCreatorId(appResponse.getCreatorId());
        response.setCreatorName(appResponse.getCreatorName());
        response.setDeptId(appResponse.getDeptId());
        response.setDeptName(appResponse.getDeptName());
        response.setCategoryId(appResponse.getCategoryId());
        response.setCategoryName(appResponse.getCategoryName());
        response.setStatus(appResponse.getStatus());
        response.setVisitCount(appResponse.getVisitCount());
        response.setTags(appResponse.getTags());
        response.setCreatedAt(appResponse.getCreatedAt());
        response.setUpdatedAt(appResponse.getUpdatedAt());
        
        // 设置预览和下载的完整URL
        if (request != null && appResponse.getId() != null) {
            String baseUrl = getBaseUrl(request);
            response.setPreviewUrl(baseUrl + "/api/docs/preview/" + appResponse.getId());
            response.setDownloadUrl(baseUrl + "/api/docs/download/" + appResponse.getId());
        }
        
        return response;
    }
    
    /**
     * 获取请求的基础URL（协议+域名+端口）
     */
    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        
        // 如果是标准端口（HTTP 80, HTTPS 443），不显示端口号
        if ((scheme.equals("http") && serverPort != 80) || 
            (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        
        if (contextPath != null && !contextPath.isEmpty()) {
            url.append(contextPath);
        }
        
        return url.toString();
    }
}
