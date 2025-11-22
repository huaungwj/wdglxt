package com.example.dms.controller;

import com.example.dms.application.CategoryApplicationService;
import com.example.dms.application.dto.CategoryCreateCommand;
import com.example.dms.application.dto.CategoryResponseDTO;
import com.example.dms.application.dto.CategoryUpdateCommand;
import com.example.dms.common.ApiResponse;
import com.example.dms.controller.dto.CategoryCreateResponseDTO;
import com.example.dms.controller.dto.CategoryRequestDTO;
import com.example.dms.controller.dto.CategoryTreeResponseDTO;
import com.example.dms.dto.CategoryTreeNode;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类控制器
 * 
 * Controller 层职责：
 * 1. 接收 HTTP 请求
 * 2. 参数验证（使用 @Valid 注解）
 * 3. 调用 Application Service 层处理业务逻辑
 * 4. 将 Application 层的响应转换为 Controller 层的响应 DTO
 * 5. 返回统一的 API 响应格式
 * 
 * 重要原则：
 * - 不直接使用 Mapper 或 Entity
 * - 不包含业务逻辑，只负责数据转换和调用
 * - 每层使用独立的数据类，保持层间解耦
 */
@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryApplicationService categoryApplicationService;

    public CategoryController(CategoryApplicationService categoryApplicationService) {
        this.categoryApplicationService = categoryApplicationService;
    }

    /**
     * 获取分类树（包含文件计数）
     * 
     * 流程：
     * 1. 调用 Application Service 获取树形数据
     * 2. 将 Application 层的 CategoryTreeNode 转换为 Controller 层的 CategoryTreeResponseDTO
     * 3. 返回响应
     */
    @GetMapping("/tree")
    public ApiResponse<List<CategoryTreeResponseDTO>> tree(){
        List<CategoryTreeNode> tree = categoryApplicationService.getTreeWithCounts();
        List<CategoryTreeResponseDTO> response = convertToTreeResponseDTO(tree);
        return ApiResponse.ok(response);
    }

    /**
     * 创建分类
     * 
     * 流程：
     * 1. 接收前端请求参数（CategoryRequestDTO）
     * 2. 转换为 Application 层的命令对象（CategoryCreateCommand）
     * 3. 调用 Application Service 创建分类
     * 4. 将 Application 层的响应转换为 Controller 层的响应 DTO
     * 5. 返回响应
     */
    @PostMapping
    public ApiResponse<CategoryCreateResponseDTO> create(@RequestBody @Valid CategoryRequestDTO request){
        // 将 Controller 层的请求 DTO 转换为 Application 层的命令对象
        CategoryCreateCommand command = new CategoryCreateCommand();
        command.setName(request.getName());
        command.setParentId(request.getParentId());
        
        // 调用 Application Service 处理业务逻辑
        CategoryResponseDTO appResponse = categoryApplicationService.create(command);
        
        // 将 Application 层的响应转换为 Controller 层的响应 DTO
        CategoryCreateResponseDTO response = convertToCreateResponseDTO(appResponse);
        
        return ApiResponse.ok(response);
    }

    /**
     * 更新分类
     * 
     * 流程：
     * 1. 接收路径参数 id 和请求体参数（CategoryRequestDTO）
     * 2. 转换为 Application 层的命令对象（CategoryUpdateCommand）
     * 3. 调用 Application Service 更新分类
     * 4. 返回成功响应
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody CategoryRequestDTO request){
        // 将 Controller 层的请求 DTO 转换为 Application 层的命令对象
        CategoryUpdateCommand command = new CategoryUpdateCommand();
        command.setName(request.getName());
        command.setParentId(request.getParentId());
        command.setSort(request.getSort());
        
        // 调用 Application Service 处理业务逻辑
        categoryApplicationService.update(id, command);
        
        return ApiResponse.ok();
    }

    /**
     * 删除分类
     * 
     * 流程：
     * 1. 接收路径参数 id
     * 2. 调用 Application Service 删除分类
     * 3. 返回成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        categoryApplicationService.delete(id);
        return ApiResponse.ok();
    }

    /**
     * 将 Application 层的响应转换为 Controller 层的响应 DTO
     * 
     * 作用：保持层间解耦，Controller 层不直接依赖 Application 层的数据结构
     * 如果将来需要修改响应格式，只需要修改这个方法即可
     */
    private CategoryCreateResponseDTO convertToCreateResponseDTO(CategoryResponseDTO appResponse) {
        CategoryCreateResponseDTO response = new CategoryCreateResponseDTO();
        response.setId(appResponse.getId());
        response.setName(appResponse.getName());
        response.setParentId(appResponse.getParentId());
        response.setSort(appResponse.getSort());
        response.setLevel(appResponse.getLevel());
        return response;
    }

    /**
     * 将树节点列表转换为 Controller 层的响应 DTO 列表
     * 
     * 作用：将 Application 层的 CategoryTreeNode 转换为 Controller 层的 CategoryTreeResponseDTO
     * 保持每层数据类的独立性
     */
    private List<CategoryTreeResponseDTO> convertToTreeResponseDTO(List<CategoryTreeNode> tree) {
        return tree.stream()
            .map(this::convertTreeNode)
            .collect(Collectors.toList());
    }

    /**
     * 递归转换单个树节点
     * 
     * 作用：将 CategoryTreeNode 转换为 CategoryTreeResponseDTO
     * 如果节点有子节点，递归转换所有子节点
     */
    private CategoryTreeResponseDTO convertTreeNode(CategoryTreeNode node) {
        CategoryTreeResponseDTO dto = new CategoryTreeResponseDTO();
        dto.setId(node.getId());
        dto.setName(node.getName());
        dto.setParentId(node.getParentId());
        dto.setSort(node.getSort());
        dto.setLevel(node.getLevel());
        dto.setFileCount(node.getFileCount());
        
        // 递归转换子节点
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            dto.setChildren(
                node.getChildren().stream()
                    .map(this::convertTreeNode)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }
}
