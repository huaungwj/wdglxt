<template>
  <el-card shadow="never">
    <div style="text-align: center; padding: 60px 20px">
      <el-icon :size="80" color="#409eff"><FolderOpened /></el-icon>
      <h1 style="margin: 24px 0 12px; font-size: 28px; color: #303133">文档管理系统</h1>
      <p style="color: #909399; font-size: 14px">基于 Spring Boot + Vue 3 的文档管理系统</p>
      
      <el-divider />
      
      <div style="display: flex; gap: 24px; justify-content: center; margin-top: 40px">
        <el-card shadow="hover" style="width: 200px; cursor: pointer" @click="$router.push('/categories')">
          <div style="text-align: center">
            <el-icon :size="40" color="#67c23a"><Folder /></el-icon>
            <div style="margin-top: 12px; font-weight: 600">分类管理</div>
            <div style="margin-top: 8px; font-size: 12px; color: #909399">管理文档分类树</div>
          </div>
        </el-card>
        
        <el-card shadow="hover" style="width: 200px; cursor: pointer" @click="$router.push('/docs')">
          <div style="text-align: center">
            <el-icon :size="40" color="#409eff"><Document /></el-icon>
            <div style="margin-top: 12px; font-weight: 600">文档信息</div>
            <div style="margin-top: 8px; font-size: 12px; color: #909399">上传与管理文件</div>
          </div>
        </el-card>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { FolderOpened, Folder, Document } from '@element-plus/icons-vue'
</script>
