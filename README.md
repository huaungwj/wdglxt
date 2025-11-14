# 文档管理系统 (DMS)

基于 **Spring Boot 3 + Vue 3** 的文档管理系统，支持分类树管理与文件上传/下载。

---

## 技术栈

### 后端
- Spring Boot 3.2.5 + Java 17
- MyBatis-Plus 3.5.6
- MySQL 8
- 本地磁盘文件存储

### 前端
- Vue 3 + JavaScript
- Element Plus
- Vite
- Pinia + Vue Router

---

## 快速启动

### 1. 数据库准备

在 MySQL 中执行以下 SQL 创建数据库：

```sql
CREATE DATABASE dms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**重要**：应用启动时会自动执行 `server/src/main/resources/schema.sql` 建表。如果自动建表失败，请手动执行以下 SQL：

```bash
# 方式一：命令行执行
mysql -uroot -p123456 dms < server/src/main/resources/init.sql

# 方式二：MySQL 客户端中执行
USE dms;
SOURCE /path/to/server/src/main/resources/init.sql;
```

或直接在 MySQL 客户端中执行 `server/src/main/resources/init.sql` 文件内容。

### 2. 后端启动

```bash
# 方式一：Maven 直接运行
cd server
mvn clean package
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package
java -jar target/dms-0.0.1-SNAPSHOT.jar
```

后端默认端口：`8080`  
Swagger 文档：http://localhost:8080/swagger-ui/index.html

### 3. 前端启动

```bash
cd web
npm install
npm run dev
```

前端默认端口：`5173`  
访问地址：http://localhost:5173

---

## 配置说明

### 数据库配置
文件：`server/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dms?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

### 文件存储配置
```yaml
file:
  storage-root: /data/files  # 本地存储根目录
  public-prefix: /files      # 静态资源访问前缀
```

应用启动时会自动创建 `/data/files` 目录（如不存在）。

---

## 功能模块

### 分类管理
- 树形结构展示
- 新增/编辑/删除分类
- 自动统计文件数（含子分类）
- 删除保护：有子分类或文件时禁止删除

### 文档管理
- 文件上传（支持大文件，最大 200MB）
- 分页列表查询
- 关键字搜索
- 文件下载
- 删除文件

---

## API 接口

### 分类接口
- `GET /api/categories/tree` - 获取分类树（含文件数）
- `POST /api/categories` - 新增分类
- `PUT /api/categories/{id}` - 编辑分类
- `DELETE /api/categories/{id}` - 删除分类

### 文档接口
- `GET /api/docs` - 分页查询文档列表
- `POST /api/docs/upload` - 上传文件
- `GET /api/docs/{id}/download` - 下载文件
- `DELETE /api/docs/{id}` - 删除文件

---

## 目录结构

```
wdglxt/
├── server/              # 后端项目
│   ├── src/main/java/com/example/dms/
│   │   ├── controller/  # 控制器
│   │   ├── service/     # 服务层
│   │   ├── mapper/      # MyBatis Mapper
│   │   ├── entity/      # 实体类
│   │   ├── config/      # 配置类
│   │   └── DmsApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── schema.sql   # 建表脚本
│   └── pom.xml
└── web/                 # 前端项目
    ├── src/
    │   ├── views/       # 页面组件
    │   ├── api/         # API 封装
    │   ├── router/      # 路由配置
    │   └── App.vue
    ├── index.html
    ├── vite.config.js
    └── package.json
```

---

## 常见问题

### 1. 后端启动失败：Unknown database 'dms'
**原因**：数据库未创建  
**解决**：在 MySQL 中执行 `CREATE DATABASE dms DEFAULT CHARACTER SET utf8mb4;`

### 2. Maven 依赖下载失败
**原因**：镜像仓库解析失败  
**解决**：
- 检查本机 `~/.m2/settings.xml` 的 mirror 配置
- 或执行 `mvn clean package -U` 强制更新

### 3. 前端跨域问题
**说明**：后端已配置 CORS 允许 `http://localhost:5173`  
**生产环境**：建议前后端同域部署或使用 Nginx 反向代理

### 4. 文件上传失败
**检查**：
- 后端 `/data/files` 目录是否有写权限
- `application.yml` 中 `spring.servlet.multipart.max-file-size` 配置

---

## 开发计划

- [x] 分类树管理
- [x] 文件上传/下载
- [x] 分页查询与搜索
- [ ] 用户登录与权限（JWT）
- [ ] 文件版本管理
- [ ] 文件预览（PDF/图片/Office）
- [ ] 操作日志

---

## License

MIT
