# 部署文档（Deploy Guide)

本项目为 Spring Boot 后端 + Vue3 前端。当前仅提供“分类管理”模块；“文档信息”页面为占位（开发中）。

- 后端：Spring Boot 3 + Java 17 + MyBatis-Plus + MySQL 8
- 前端：Vue 3 + Vite + Element Plus

建议生产环境采用：后端独立 jar 运行，前端通过 Nginx 托管静态资源，并将 /api 反向代理至后端。

---

## 1. 环境要求

- 操作系统：Linux/macOS/Windows（生产推荐 Linux）
- Java：JDK 17+
- Maven：3.8+
- Node.js：18 LTS（用于构建前端）
- MySQL：8.x
- Nginx：1.18+（前端静态资源与反向代理）

---

## 2. 数据库初始化

1) 创建数据库（需先启动 MySQL 并确认账号密码）

```sql
CREATE DATABASE IF NOT EXISTS dms DEFAULT CHARSET utf8mb4;
```

2) 初始化表
- 自动建表：后端启动会执行 `schema.sql`
- 若自动失败，请手动执行：`server/src/main/resources/init.sql`

当前仅包含 `doc_category` 表（已移除文档相关表）。

---

## 3. 后端部署

### 3.1 配置

编辑 `server/src/main/resources/application.yml` 中的数据库连接：

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dms?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

如需修改端口，可在启动时指定：`--server.port=8080`。

### 3.2 构建

```bash
mvn -f server/pom.xml clean package -DskipTests
```

构建产物：`server/target/*.jar`

### 3.3 运行（基础方式）

```bash
java -jar server/target/*.jar --server.port=8080
```

按需配置 `JAVA_OPTS`（堆大小、GC 等）：

```bash
JAVA_OPTS="-Xms256m -Xmx512m" java $JAVA_OPTS -jar server/target/*.jar
```

### 3.4 运行（systemd 推荐）

创建服务文件 `/etc/systemd/system/dms.service`：

```
[Unit]
Description=DMS Backend Service
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/dms
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/dms/server.jar --server.port=8080
Restart=always
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

操作命令：

```bash
sudo systemctl daemon-reload
sudo systemctl enable dms
sudo systemctl start dms
sudo systemctl status dms
```

日志查看：

```bash
journalctl -u dms -f
```

---

## 4. 前端部署

### 4.1 构建

```bash
cd web
npm install
npm run build
```

构建产物：`web/dist` 目录。

### 4.2 使用 Nginx 托管静态资源并反代后端

Nginx 配置示例（`/etc/nginx/conf.d/dms.conf`）：

```
server {
  listen 80;
  server_name your.domain.com;  # 修改为实际域名或服务器IP

  # 前端静态资源目录
  root /var/www/dms-web;
  index index.html;

  # 前端路由 history 模式处理
  location / {
    try_files $uri $uri/ /index.html;
  }

  # 反向代理后端 API
  location /api/ {
    proxy_pass http://127.0.0.1:8080/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
```

将 `web/dist` 上传至 `/var/www/dms-web`：

```bash
sudo mkdir -p /var/www/dms-web
sudo cp -r web/dist/* /var/www/dms-web/

# 重载 Nginx
sudo nginx -t && sudo systemctl reload nginx
```

> 小贴士：如果前端和后端不在同域，也可以在后端开启 CORS；如果同域反代 `/api`，则无需 CORS。

---

## 5. 生产建议

- 进程守护：systemd/supervisor 保活，自动重启
- 日志：使用 `journalctl` 或在启动命令中指定 logback 文件输出
- 安全：后续如需登录/权限，建议加入鉴权（JWT/Session）
- 优化：
  - Nginx 开启 gzip 与缓存（对 `index.html` 禁缓存，对静态资源 hash 文件设置长缓存）
  - 使用 HTTPS（Let’s Encrypt）

Nginx 缓存与 gzip 示例（片段）：

```
http {
  gzip on;
  gzip_types text/plain text/css application/json application/javascript application/xml+rss image/svg+xml;
  gzip_min_length 1k;
  # ...
}

server {
  # 对静态资源设置较长缓存（示例：30天）
  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
  }
}
```

---

## 6. 常见问题（FAQ）

- Q：后端无法连接数据库？
  - A：检查 `application.yml` 的 JDBC 地址、用户名密码；确认数据库与表已创建；查看后端日志。

- Q：前端刷新后 404？
  - A：Nginx 需加入 `try_files $uri $uri/ /index.html;` 以支持前端路由。

- Q：跨域问题？
  - A：同域部署（前端反代到后端）可避免 CORS；否则需在后端开启 CORS。

- Q：页面样式不生效？
  - A：确保 `web/src/main.js` 已引入 `element-plus/dist/index.css` 与 `src/styles/theme.css`。

---

## 7. 快速验证清单（上线前）

- [ ] MySQL 连接正常，`doc_category` 表存在
- [ ] 后端 jar 正常运行，`/api/categories/tree` 可返回数据
- [ ] 前端 Nginx 服务可访问首页，路由跳转正常
- [ ] /api 反代通畅（或 CORS 生效）
- [ ] 日志与进程守护配置完成

---

## 8. 回滚策略

- 后端：保留上一版 jar，出现问题时回退
- 前端：保留上一版 dist 备份，快速覆盖回退

---

## 9. 目录结构（简要）

```
wdglxt/
├─ server/                  # 后端工程
│  ├─ src/main/java/
│  ├─ src/main/resources/
│  │  ├─ application.yml
│  │  ├─ schema.sql
│  │  └─ init.sql
│  └─ pom.xml
├─ web/                     # 前端工程
│  ├─ src/
│  │  ├─ views/
│  │  ├─ api/
│  │  └─ styles/theme.css
│  ├─ index.html
│  ├─ package.json
│  └─ vite.config.*
└─ deploy.md                # 本部署文档
```

---

## 10. 备忘

- 当前仅分类管理模块启用；文档相关后端代码与表已移除。
- 如需将来扩展文档模块，可重新引入相关接口与存储能力。
