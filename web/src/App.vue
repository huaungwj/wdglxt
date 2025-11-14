<template>
  <el-container style="height: 100vh">
    <el-aside width="216px" class="app-aside">
      <div class="logo">文档管理</div>
      <el-menu :default-active="$route.path" router background-color="#ffffff" text-color="#303133" active-text-color="#409eff">
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/docs">
          <el-icon><Document /></el-icon>
          <span>文档信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="app-header" style="display: flex; align-items: center; justify-content: space-between; padding: 0 20px">
        <div class="title">{{ pageTitle }}</div>
        <div class="meta" style="display:flex; align-items:center; gap:10px">
          <el-avatar size="small">管</el-avatar>
          管理员
        </div>
      </el-header>
      <div style="background:#fff;border-bottom:1px solid var(--border);padding:8px 20px">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <el-main style="background: #f0f2f5; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { House, Folder, Document } from '@element-plus/icons-vue'

const route = useRoute()
const pageTitle = computed(() => {
  const map = { '/': '首页', '/categories': '分类管理', '/docs': '文档信息' }
  return map[route.path] || '文档管理系统'
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  color: #303133;
  border-bottom: 1px solid #dcdfe6;
}
.el-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
