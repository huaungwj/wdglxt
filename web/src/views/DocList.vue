<template>
  <div class="doc-list-container">
    <div class="doc-list-layout">
      <!-- 左侧日期树形选择器 -->
      <div class="date-tree-sidebar">
        <el-card shadow="never" style="height: 100%">
          <template #header>
            <div class="sidebar-header">按日期筛选</div>
          </template>
          <el-tree
            :data="dateTree"
            :props="{ children: 'children', label: 'label' }"
            node-key="value"
            :default-expand-all="false"
            @node-click="handleDateNodeClick"
            highlight-current
          >
            <template #default="{ node, data }">
              <span class="tree-node-label">{{ data.label }}</span>
            </template>
          </el-tree>
          <div style="margin-top: 10px">
            <el-button size="small" @click="clearDateFilter"
              >清除筛选</el-button
            >
          </div>
        </el-card>
      </div>

      <!-- 右侧文档列表 -->
      <div class="doc-list-content">
        <el-card shadow="never">
          <!-- 搜索筛选区域 -->
          <div class="search-section">
            <el-form :model="searchForm" inline>
              <el-form-item label="文件编号">
                <el-input
                  v-model="searchForm.fileNo"
                  placeholder="请输入文件编号"
                  clearable
                />
              </el-form-item>
              <el-form-item label="创建人">
                <el-select
                  v-model="searchForm.creatorId"
                  placeholder="请选择创建人"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="user in users"
                    :key="user.id"
                    :label="user.realName || user.username"
                    :value="user.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="关键字">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="请输入关键字"
                  clearable
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-select
                  v-model="searchForm.status"
                  placeholder="请选择状态"
                  clearable
                >
                  <el-option label="正常" :value="1" />
                  <el-option label="已删除" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item label="分类">
                <el-tree-select
                  v-model="searchForm.categoryId"
                  :data="categoryTree"
                  :render-after-expand="false"
                  placeholder="请选择分类"
                  clearable
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">清空</el-button>
                <el-button type="success" @click="goToUpload" v-permission="'file:upload'">上传</el-button>
              </el-form-item>
            </el-form>
            <div class="hint-text">可以点击分类名称重新选择分类</div>
          </div>

          <!-- 文档表格 -->
          <el-table
            :data="docList"
            stripe
            v-loading="loading"
            style="margin-top: 20px"
            @row-dblclick="handleRowDoubleClick"
          >
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="fileNo" label="文件编号" width="130" show-overflow-tooltip />
            <el-table-column
              prop="name"
              label="文件名"
              min-width="180"
              show-overflow-tooltip
            />
            <el-table-column label="文件类型" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="getFileTypeTagType(row)">
                  {{ getFileType(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="creatorName" label="创建人" width="100" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="创建时间" width="160" show-overflow-tooltip>
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="70">
              <template #default="{ row }">
                <el-tag
                  :type="row.status === 1 ? 'success' : 'danger'"
                  size="small"
                >
                  {{ row.status === 1 ? '正常' : '已删除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="categoryName" label="分类" width="100" show-overflow-tooltip>
              <template #default="{ row }">
                <span
                  @click="handleCategoryClick(row)"
                  style="cursor: pointer; color: #409eff"
                >
                  {{ row.categoryName || '未分类' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="visitCount" label="访问" width="70" />
            <el-table-column
              prop="tags"
              label="标签"
              width="100"
              show-overflow-tooltip
            />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="handlePreview(row)"
                  v-permission="'file:preview'"
                >
                  预览
                </el-button>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="handleDownload(row)"
                  v-permission="'file:download'"
                >
                  下载
                </el-button>
                <el-popconfirm
                  title="确定要删除该文件吗？"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  @confirm="handleDelete(row)"
                >
                  <template #reference>
                    <el-button link type="danger" size="small" v-permission="'file:delete'">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              :current-page="pagination.page"
              :page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </el-card>

        <!-- 分类选择对话框 -->
        <el-dialog
          v-model="categoryDialogVisible"
          title="选择分类"
          width="400px"
        >
          <el-tree-select
            v-model="selectedCategoryId"
            :data="categoryTree"
            :render-after-expand="false"
            placeholder="请选择分类"
            style="width: 100%"
          />
          <template #footer>
            <el-button @click="categoryDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleCategoryConfirm"
              >确认</el-button
            >
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { docAPI } from '../api/doc';
import { fetchCategoryTree } from '../api/category';
import { userAPI } from '../api/user';

const router = useRouter();

// 搜索表单
const searchForm = ref({
  fileNo: '',
  creatorId: null,
  keyword: '',
  status: 1,
  categoryId: null,
});

// 文档列表
const docList = ref([]);
const loading = ref(false);

// 分类列表（扁平化，用于其他用途）
const categories = ref([]);
// 分类树（用于TreeSelect）
const categoryTree = ref([]);

// 用户列表
const users = ref([]);

// 分页
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0,
});

// 分类选择对话框
const categoryDialogVisible = ref(false);
const selectedCategoryId = ref(null);
const currentRow = ref(null);

// 日期树形结构
const dateTree = ref([]);
const selectedDateNode = ref(null);

// 加载文档列表
const loadDocs = async () => {
  loading.value = true;
  try {
    const params = {
      ...searchForm.value,
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
    };
    const res = await docAPI.search(params);
    // 后端返回格式：{ code: 0, msg: 'ok', data: [...] }
    let docs = res.data || [];

    // 如果有日期筛选，进一步过滤
    if (searchForm.value.dateFilter) {
      const dateFilter = searchForm.value.dateFilter;
      docs = docs.filter((doc) => {
        if (!doc.createdAt) return false;
        const docDate = new Date(doc.createdAt);
        const year = docDate.getFullYear().toString();
        const month = String(docDate.getMonth() + 1).padStart(2, '0');
        const day = String(docDate.getDate()).padStart(2, '0');

        if (dateFilter.length === 4) {
          // 只选择了年
          return year === dateFilter;
        } else if (dateFilter.length === 7) {
          // 选择了年月
          return `${year}/${month}` === dateFilter;
        } else if (dateFilter.length === 10) {
          // 选择了年月日
          return `${year}/${month}/${day}` === dateFilter;
        }
        return false;
      });
    }

    docList.value = docs;
    // 如果没有分页信息，使用列表长度作为总数
    pagination.value.total = res.total || docList.value.length;
  } catch (error) {
    ElMessage.error('加载文档列表失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 加载分类树
const loadCategories = async () => {
  try {
    const res = await fetchCategoryTree();
    const treeData = res.data || [];

    // 转换为TreeSelect需要的格式（value, label, children）
    const convertToTreeSelectFormat = (nodes) => {
      return nodes.map((node) => ({
        value: node.id,
        label: node.name,
        children:
          node.children && node.children.length > 0
            ? convertToTreeSelectFormat(node.children)
            : undefined,
      }));
    };

    // 保存树形结构用于TreeSelect
    categoryTree.value = convertToTreeSelectFormat(treeData);

    // 扁平化分类树（用于其他用途，如显示）
    const flattenCategories = (nodes) => {
      let result = [];
      nodes.forEach((node) => {
        result.push({ id: node.id, name: node.name });
        if (node.children && node.children.length > 0) {
          result = result.concat(flattenCategories(node.children));
        }
      });
      return result;
    };
    categories.value = flattenCategories(treeData);
  } catch (error) {
    console.error('加载分类失败', error);
  }
};

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await userAPI.list();
    // 后端返回格式：{ code: 0, msg: 'ok', data: [...] }
    users.value = res.data || [];
  } catch (error) {
    console.error('加载用户列表失败', error);
  }
};

// 加载日期文件夹树
const loadDateFolders = async () => {
  try {
    const res = await docAPI.getDateFolders();
    dateTree.value = res.data || [];
  } catch (error) {
    console.error('加载日期文件夹失败', error);
  }
};

// 点击日期树节点
const handleDateNodeClick = (data) => {
  selectedDateNode.value = data;
  searchForm.value.dateFilter = data.value;
  pagination.value.page = 1;
  loadDocs();
};

// 清除日期筛选
const clearDateFilter = () => {
  selectedDateNode.value = null;
  searchForm.value.dateFilter = null;
  pagination.value.page = 1;
  loadDocs();
};

// 搜索
const handleSearch = () => {
  pagination.value.page = 1;
  loadDocs();
};

// 重置
const handleReset = () => {
  searchForm.value = {
    fileNo: '',
    creatorId: null,
    keyword: '',
    status: 1,
    categoryId: null,
    dateFilter: null,
  };
  selectedDateNode.value = null;
  handleSearch();
};

// 预览
const handlePreview = (row) => {
  router.push(`/docs/preview/${row.id}`);
};

// 下载
const handleDownload = async (row) => {
  try {
    // 如果后端返回了完整的下载URL，直接使用
    if (row.downloadUrl) {
      const token = localStorage.getItem('token');
      const response = await fetch(row.downloadUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = row.originalName || row.name;
      a.click();
      window.URL.revokeObjectURL(url);
      ElMessage.success('下载成功');
    } else {
      // 兼容旧方式：通过API下载
      const blob = await docAPI.download(row.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = row.originalName || row.name;
      a.click();
      window.URL.revokeObjectURL(url);
      ElMessage.success('下载成功');
    }
  } catch (error) {
    ElMessage.error('下载失败');
    console.error(error);
  }
};

// 删除
const handleDelete = async (row) => {
  try {
    await docAPI.delete(row.id);
    ElMessage.success('删除成功');
    loadDocs();
  } catch (error) {
    ElMessage.error('删除失败');
    console.error(error);
  }
};

// 双击行
const handleRowDoubleClick = (row) => {
  handlePreview(row);
};

// 点击分类名称
const handleCategoryClick = (row) => {
  currentRow.value = row;
  selectedCategoryId.value = row.categoryId;
  categoryDialogVisible.value = true;
};

// 确认分类选择
const handleCategoryConfirm = async () => {
  if (!currentRow.value) return;
  try {
    await docAPI.updateCategory(currentRow.value.id, selectedCategoryId.value);
    ElMessage.success('更新分类成功');
    categoryDialogVisible.value = false;
    loadDocs();
  } catch (error) {
    ElMessage.error('更新分类失败');
    console.error(error);
  }
};

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.page = 1;
  loadDocs();
};

// 页码改变
const handlePageChange = (page) => {
  pagination.value.page = page;
  loadDocs();
};

// 跳转到上传页面
const goToUpload = () => {
  router.push('/docs/upload');
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

// 获取文件类型（从文件名扩展名）
const getFileType = (row) => {
  const fileName = row.originalName || row.name || '';
  const lastDotIndex = fileName.lastIndexOf('.');
  if (lastDotIndex === -1) return '未知';
  const extension = fileName.substring(lastDotIndex + 1).toUpperCase();
  return extension || '未知';
};

// 获取文件类型标签的类型（用于不同颜色）
const getFileTypeTagType = (row) => {
  const fileType = getFileType(row).toLowerCase();
  // 根据文件类型返回不同的标签类型
  if (['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(fileType)) {
    return 'warning'; // Office文档用橙色
  } else if (['pdf'].includes(fileType)) {
    return 'danger'; // PDF用红色
  } else if (
    ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'].includes(fileType)
  ) {
    return 'success'; // 图片用绿色
  } else if (['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm'].includes(fileType)) {
    return 'info'; // 视频用蓝色
  } else if (['mp3', 'wav', 'ogg', 'm4a'].includes(fileType)) {
    return ''; // 音频用默认色
  } else if (['zip', 'rar', '7z', 'tar', 'gz'].includes(fileType)) {
    return 'warning'; // 压缩包用橙色
  } else {
    return ''; // 其他用默认色
  }
};

onMounted(() => {
  loadCategories();
  loadUsers();
  loadDateFolders();
  loadDocs();
});
</script>

<style scoped>
.doc-list-container {
  padding: 0;
  max-width: 100vw;
  overflow-x: hidden;
  box-sizing: border-box;
}

.doc-list-layout {
  display: flex;
  height: 100%;
  gap: 16px;
  max-width: 100%;
  box-sizing: border-box;
}

.date-tree-sidebar {
  height: auto;
  flex: 0 0 220px;
  min-width: 0;
  box-sizing: border-box;
}

.doc-list-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  box-sizing: border-box;
}

.search-section {
  margin-bottom: 16px;
}

.search-section :deep(.el-form) {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.search-section :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-section :deep(.el-form-item__label) {
  width: auto !important;
  padding-right: 8px;
}

.search-section :deep(.el-input),
.search-section :deep(.el-select),
.search-section :deep(.el-tree-select) {
  width: 180px;
}

.hint-text {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 表格容器优化 */
.doc-list-content :deep(.el-card__body) {
  padding: 16px;
  overflow-x: hidden;
  max-width: 100%;
  box-sizing: border-box;
}

.doc-list-content :deep(.el-card) {
  overflow: hidden;
}

.doc-list-content :deep(.el-table) {
  width: 100%;
  max-width: 100%;
  table-layout: fixed;
}

/* 确保表格列宽合理 */
.doc-list-content :deep(.el-table__header-wrapper),
.doc-list-content :deep(.el-table__body-wrapper) {
  overflow-x: hidden;
}

.doc-list-content :deep(.el-table__inner-wrapper) {
  overflow-x: hidden;
}

/* 响应式优化 */
@media (max-width: 1400px) {
  .date-tree-sidebar {
    flex: 0 0 200px;
  }
  
  .search-section :deep(.el-input),
  .search-section :deep(.el-select),
  .search-section :deep(.el-tree-select) {
    width: 160px;
  }
}

@media (max-width: 1200px) {
  .doc-list-layout {
    flex-direction: column;
  }
  
  .date-tree-sidebar {
    flex: 0 0 auto;
    width: 100%;
  }
}
</style>
