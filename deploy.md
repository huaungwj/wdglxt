# 文档管理系统部署文档（Deploy Guide）

本项目是一个基于 **Spring Boot 3 + Vue 3** 的完整文档管理系统，包含完整的权限管理、用户管理、文档管理等功能模块。

## 技术栈

### 后端
- Spring Boot 3.2.5 + Java 17
- MyBatis-Plus 3.5.6
- MySQL 8.x
- JWT 认证
- BCrypt 密码加密
- 本地磁盘文件存储

### 前端
- Vue 3 (Composition API)
- Element Plus
- Vite
- Pinia + Vue Router
- Axios

---

## 1. 环境要求

- **操作系统**：Linux/macOS/Windows（生产环境推荐 Linux）
- **Java**：JDK 17+
- **Maven**：3.8+
- **Node.js**：18 LTS（用于构建前端，使用 PM2 管理后端时也需要）
- **PM2**：可选，用于管理后端进程（推荐，需要 Node.js）
- **MySQL**：8.x（或 5.7+，需注意排序规则兼容性）
- **Nginx**：1.18+（生产环境推荐，用于前端静态资源托管和 API 反向代理）

---

## 2. 数据库初始化

### 2.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS dms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2.2 初始化表结构和数据

**方式一：自动初始化（推荐）**

后端启动时会自动执行 `server/src/main/resources/schema.sql` 创建表结构。

**方式二：手动初始化**

如果自动初始化失败，可以手动执行：

```bash
mysql -uroot -p123456 dms < server/src/main/resources/init.sql
```

或者在 MySQL 客户端中执行：

```sql
USE dms;
SOURCE /path/to/server/src/main/resources/init.sql;
```

### 2.3 数据库表说明

系统包含以下核心表：

**权限管理相关：**
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_dept` - 部门表
- `sys_menu` - 菜单表
- `sys_permission` - 权限表
- `sys_post` - 岗位表
- `sys_user_role` - 用户角色关联表
- `sys_role_menu` - 角色菜单关联表
- `sys_role_permission` - 角色权限关联表
- `sys_role_dept` - 角色部门关联表（用于自定义数据权限）
- `sys_dict` - 字典表

**文档管理相关：**
- `doc_category` - 文档分类表
- `doc_file` - 文档文件表
- `doc_visit_log` - 文档访问日志表

---

## 3. 后端部署

### 3.1 配置文件

**方式一：使用外部配置文件（推荐 - 生产环境）**

在 jar 包同级目录创建 `application.yml`，Spring Boot 会自动加载外部配置文件（优先级高于 jar 包内的配置）：

```bash
# 在部署目录创建配置文件
sudo mkdir -p /opt/dms
sudo nano /opt/dms/application.yml
```

配置文件内容：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dms?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 修改为实际数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: always  # 自动执行 schema.sql
      continue-on-error: false
      schema-locations: classpath:schema.sql
  servlet:
    multipart:
      max-file-size: 200MB
      max-request-size: 200MB

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: AUTO

file:
  storage-root: /data/dms/uploads  # 文件存储根目录（使用绝对路径）
  public-prefix: /files             # 文件访问前缀

logging:
  level:
    root: info
```

**方式二：修改 jar 包内的配置文件（开发环境）**

编辑 `server/src/main/resources/application.yml`，然后重新打包。

**配置文件优先级：**
1. 命令行参数（`--spring.config.location`）
2. jar 包同级目录的 `application.yml`（外部配置）
3. jar 包内的 `application.yml`（内置配置）

**重要配置说明：**
- `spring.datasource.*` - 数据库连接配置
- `file.storage-root` - 文件上传存储目录（生产环境建议使用绝对路径，如 `/data/dms/uploads`）
- `file.public-prefix` - 文件访问 URL 前缀
- **使用外部配置文件的好处**：修改配置后只需重启服务，无需重新打包 jar

### 3.2 构建

```bash
cd server
mvn clean package -DskipTests
```

构建产物：`server/target/dms-0.0.1-SNAPSHOT.jar`

### 3.3 运行

**开发环境：**

```bash
cd server
mvn spring-boot:run
```

**生产环境（基础方式）：**

```bash
# 方式1：使用外部配置文件（推荐）
# 将 application.yml 放在 jar 包同级目录，Spring Boot 会自动加载
java -jar /opt/dms/dms-0.0.1-SNAPSHOT.jar

# 方式2：通过参数指定配置文件路径
java -jar /opt/dms/dms-0.0.1-SNAPSHOT.jar --spring.config.location=file:/opt/dms/application.yml

# 方式3：通过命令行参数覆盖配置
java -jar /opt/dms/dms-0.0.1-SNAPSHOT.jar --server.port=8080 --spring.datasource.password=your_password
```

**生产环境（方式一 - PM2，推荐）：**

使用 PM2 管理 Java 应用进程：

```bash
# 1. 安装 PM2（如果未安装）
npm install -g pm2

# 2. 创建部署目录
mkdir -p /www/wwwroot/dms-wdglxt
mkdir -p /data/dms/uploads

# 3. 复制 jar 文件和配置文件
cp server/target/dms-0.0.1-SNAPSHOT.jar /www/wwwroot/dms-wdglxt/
cp application.yml /www/wwwroot/dms-wdglxt/application.yml

# 4. 修改配置文件（根据实际环境调整）
vim /www/wwwroot/dms-wdglxt/application.yml

# 5. 使用 PM2 启动（在项目根目录执行）
pm2 start pm2-ecosystem.config.cjs

# 6. 查看状态
pm2 status
pm2 logs dms-backend

# 7. 设置开机自启
pm2 save
pm2 startup
```

**PM2 常用命令：**

```bash
# 启动应用
pm2 start pm2-ecosystem.config.cjs

# 停止应用
pm2 stop dms-backend

# 重启应用
pm2 restart dms-backend

# 查看日志
pm2 logs dms-backend
pm2 logs dms-backend --lines 100  # 查看最近 100 行

# 查看状态
pm2 status
pm2 info dms-backend

# 监控
pm2 monit

# 删除应用
pm2 delete dms-backend

# 保存当前进程列表（用于开机自启）
pm2 save

# 设置开机自启
pm2 startup
```

**PM2 配置文件说明：**

配置文件 `pm2-ecosystem.config.cjs` 支持通过环境变量自定义路径：

```bash
# 方式1：使用默认路径（/www/wwwroot/dms-wdglxt）
pm2 start pm2-ecosystem.config.cjs

# 方式2：通过环境变量自定义路径
JAR_PATH=/opt/dms/server.jar \
CONFIG_PATH=/opt/dms/application.yml \
JAVA_BIN=/usr/bin/java \
JAVA_OPTS="-Xms512m -Xmx1024m" \
pm2 start pm2-ecosystem.config.cjs
```

**生产环境（方式二 - systemd）：**

创建服务文件 `/etc/systemd/system/dms.service`：

```ini
[Unit]
Description=DMS Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/dms
# 使用外部配置文件（推荐）
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/dms/dms-0.0.1-SNAPSHOT.jar --spring.config.location=file:/opt/dms/application.yml
Restart=always
RestartSec=5
SuccessExitStatus=143

# 日志输出
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

操作命令：

```bash
# 创建部署目录
sudo mkdir -p /opt/dms
sudo mkdir -p /data/dms/uploads

# 复制 jar 文件和配置文件到目标目录
sudo cp server/target/dms-0.0.1-SNAPSHOT.jar /opt/dms/
sudo cp application.yml /opt/dms/application.yml

# 修改配置文件（根据实际环境调整数据库密码、文件存储路径等）
sudo vim /opt/dms/application.yml

# 设置权限
sudo chown -R www-data:www-data /opt/dms
sudo chown -R www-data:www-data /data/dms/uploads

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable dms
sudo systemctl start dms
sudo systemctl status dms
```

**注意**：如果使用外部配置文件，修改配置后只需重启服务即可生效，无需重新打包：

```bash
# 修改配置文件
sudo vim /opt/dms/application.yml

# 重启服务使配置生效
sudo systemctl restart dms
```

**PM2 查看日志：**

```bash
# 实时查看日志
pm2 logs dms-backend -f

# 查看最近 100 行日志
pm2 logs dms-backend --lines 100

# 查看错误日志
pm2 logs dms-backend --err

# 清空日志
pm2 flush dms-backend
```

**systemd 查看日志：**

```bash
# 查看服务日志
journalctl -u dms -f

# 查看最近 100 行日志
journalctl -u dms -n 100
```

---

## 4. 前端部署

### 4.1 开发环境运行

```bash
cd web
npm install
npm run dev
```

前端开发服务器：http://localhost:5173

### 4.2 生产环境构建

```bash
cd web
npm install
npm run build
```

构建产物：`web/dist` 目录

### 4.3 使用 Nginx 部署

**Nginx 配置示例**（`/etc/nginx/conf.d/dms.conf`）：

```nginx
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

    # 文件访问（直接访问上传的文件）
    location /files/ {
        alias /data/dms/uploads/;
        expires 7d;
        add_header Cache-Control "public";
    }

    # 反向代理后端 API
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持（如需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

**部署步骤：**

```bash
# 创建前端目录
sudo mkdir -p /var/www/dms-web

# 复制构建产物
sudo cp -r web/dist/* /var/www/dms-web/

# 设置权限
sudo chown -R www-data:www-data /var/www/dms-web

# 测试配置
sudo nginx -t

# 重载 Nginx
sudo systemctl reload nginx
```

**Nginx 性能优化配置**（在 `http` 块中添加）：

```nginx
http {
    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1k;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/json application/javascript application/xml+rss image/svg+xml;

    # 静态资源缓存
    map $sent_http_content_type $expires {
        default                    off;
        text/html                 epoch;
        text/css                  max;
        application/javascript     max;
        ~image/                   30d;
        ~font/                    30d;
    }
    expires $expires;
    add_header Cache-Control "public, immutable";
}
```

---

## 5. 功能模块说明

### 5.1 权限管理

系统采用 **RBAC（基于角色的访问控制）** 权限模型，支持：

- **角色管理**：创建角色，设置角色状态、数据权限范围
- **菜单管理**：树形菜单结构，支持目录、菜单、按钮三种类型
- **权限管理**：细粒度权限控制（如 `file:upload`、`file:delete` 等）
- **数据权限**：支持 5 种数据权限范围
  - 全部数据权限
  - 自定义数据权限（指定部门）
  - 本部门数据权限
  - 本部门及以下数据权限
  - 仅本人数据权限

### 5.2 用户管理

- 用户 CRUD 操作
- 用户角色分配
- 用户状态管理（正常/停用）
- 用户信息完善（手机、邮箱、性别、岗位等）
- 系统管理员保护（admin 用户不允许修改、删除、禁用）

### 5.3 部门管理

- 树形部门结构
- 部门信息管理（负责人、联系电话、邮箱等）
- 部门状态管理

### 5.4 岗位管理

- 岗位信息管理
- 岗位状态管理

### 5.5 文档管理

- 文档分类管理（树形结构）
- 文档上传/下载/预览/删除
- 文档访问权限控制
- 文档访问日志

### 5.6 字典管理

- 系统字典维护
- 字典项权限控制

---

## 6. 默认账号信息

系统初始化后，默认包含以下数据：

**默认用户：**
- 用户名：`admin`
- 密码：`123456`（建议首次登录后立即修改）
- 角色：超级管理员（拥有所有权限）

**默认角色：**
- 超级管理员（`admin`）- 全部数据权限
- 普通角色（`common`）- 本部门数据权限
- 普通用户（`USER`）- 全部数据权限

**默认权限：**
- 文件操作：`file:upload`、`file:download`、`file:preview`、`file:delete`
- 菜单权限：`menu:system`、`menu:user`、`menu:dept`、`menu:role`、`menu:menu`、`menu:dict`、`menu:permission`、`menu:post`、`menu:category`、`menu:document`
- 功能权限：`user:add`、`user:edit`、`user:delete`、`user:query` 等

---

## 7. API 接口说明

### 7.1 认证接口

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/current` - 获取当前用户信息

### 7.2 用户管理

- `GET /api/users` - 用户列表（支持分页、搜索）
- `GET /api/users/{id}` - 用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/users/{id}/roles` - 获取用户角色
- `POST /api/users/{id}/roles` - 分配用户角色

### 7.3 角色管理

- `GET /api/roles` - 角色列表
- `GET /api/roles/{id}/menus` - 获取角色菜单
- `POST /api/roles/{id}/menus` - 分配角色菜单
- `GET /api/roles/{id}/depts` - 获取角色部门（自定义数据权限）
- `POST /api/roles/{id}/depts` - 分配角色部门

### 7.4 权限管理

- `GET /api/permissions/current` - 获取当前用户权限列表
- `GET /api/permissions/check?permissionCode=xxx` - 检查权限

### 7.5 文档管理

- `GET /api/docs` - 文档列表（支持分页、搜索）
- `POST /api/docs/upload` - 上传文档
- `GET /api/docs/{id}/download` - 下载文档
- `GET /api/docs/{id}/preview` - 预览文档
- `DELETE /api/docs/{id}` - 删除文档

---

## 8. 生产环境建议

### 8.1 安全建议

1. **修改默认密码**：首次登录后立即修改 admin 用户密码
2. **数据库安全**：
   - 使用强密码
   - 限制数据库访问 IP
   - 定期备份数据库
3. **HTTPS**：生产环境必须使用 HTTPS（可使用 Let's Encrypt 免费证书）
4. **文件存储**：
   - 使用独立存储目录，设置适当权限
   - 定期清理无用文件
   - 考虑使用对象存储（OSS）替代本地存储

### 8.2 性能优化

1. **数据库优化**：
   - 为常用查询字段添加索引
   - 定期分析慢查询日志
   - 考虑读写分离（如需要）
2. **文件上传优化**：
   - 大文件上传考虑分片上传
   - 文件存储考虑 CDN 加速
3. **缓存策略**：
   - 菜单、权限等数据可考虑 Redis 缓存
   - 静态资源使用 Nginx 缓存

### 8.3 监控与日志

1. **应用监控**：
   - 使用 Spring Boot Actuator 监控应用健康状态
   - 集成 Prometheus + Grafana（可选）
2. **日志管理**：
   - 配置日志轮转（logback）
   - 集中日志收集（ELK 或类似方案）
3. **错误追踪**：
   - 集成 Sentry 或其他错误追踪服务（可选）

### 8.4 备份策略

1. **数据库备份**：
   ```bash
   # 每日备份脚本示例
   mysqldump -uroot -p123456 dms > /backup/dms_$(date +%Y%m%d).sql
   ```
2. **文件备份**：定期备份 `uploads` 目录
3. **配置备份**：备份 `application.yml` 等配置文件

---

## 9. 常见问题（FAQ）

### Q1：后端无法连接数据库？

**A：** 检查以下几点：
- `application.yml` 中的数据库连接配置是否正确
- MySQL 服务是否启动
- 数据库 `dms` 是否已创建
- 数据库用户是否有足够权限
- 防火墙是否开放 3306 端口

### Q2：前端刷新后 404？

**A：** Nginx 配置中必须包含：
```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

### Q3：跨域问题？

**A：** 
- 如果前端和后端通过 Nginx 反向代理在同一域名下，无需配置 CORS
- 如果前后端分离部署，需要在后端配置 CORS（已在 `CorsConfig` 中配置）

### Q4：文件上传失败？

**A：** 检查：
- `file.storage-root` 目录是否存在且有写权限
- 文件大小是否超过 `max-file-size` 限制（默认 200MB）
- 磁盘空间是否充足

### Q5：权限不生效？

**A：** 检查：
- 用户是否已分配角色
- 角色是否已分配菜单和权限
- 前端是否正确加载权限（查看浏览器控制台）
- 用户是否重新登录以刷新权限

### Q6：系统管理员无法修改？

**A：** 这是系统保护机制，`admin` 用户（ID=1 或 username='admin'）不允许：
- 修改用户信息
- 删除用户
- 禁用用户

如需修改，请直接在数据库中操作。

### Q7：如何修改配置而不重新打包？

**A：** 使用外部配置文件（推荐）：
1. 在 jar 包同级目录创建 `application.yml`（如 `/www/wwwroot/dms-wdglxt/application.yml`）
2. Spring Boot 会自动加载外部配置文件（优先级高于 jar 包内配置）
3. 修改配置后重启服务即可

**PM2 方式：**
```bash
# 修改配置文件
vim /www/wwwroot/dms-wdglxt/application.yml

# 重启服务使配置生效
pm2 restart dms-backend
```

**systemd 方式：**
```bash
# 修改配置文件
sudo vim /opt/dms/application.yml

# 重启服务使配置生效
sudo systemctl restart dms
```

**配置文件优先级：**
- 命令行参数（`--spring.config.location=file:/path/to/config.yml`）
- jar 包同级目录的 `application.yml`
- jar 包内的 `application.yml`

### Q8：PM2 和 systemd 有什么区别？

**A：** 
- **PM2**：Node.js 生态的进程管理器，适合熟悉 Node.js 环境的开发者，提供丰富的监控和管理功能
- **systemd**：Linux 系统原生的服务管理器，更底层，适合系统管理员使用

**选择建议：**
- 如果服务器已安装 Node.js 和 PM2，推荐使用 PM2（更易用）
- 如果希望使用系统原生服务管理，推荐使用 systemd（更稳定）

---

## 10. 快速验证清单（上线前）

- [ ] MySQL 数据库已创建，所有表结构正确
- [ ] 数据库初始化数据已导入（用户、角色、权限等）
- [ ] 后端 jar 包构建成功
- [ ] 外部配置文件已创建（`/www/wwwroot/dms-wdglxt/application.yml` 或 `/opt/dms/application.yml`），数据库密码等配置已修改
- [ ] 后端服务正常启动，无错误日志
- [ ] 文件存储目录已创建且有写权限
- [ ] 前端构建成功，`dist` 目录完整
- [ ] Nginx 配置正确，静态资源可访问
- [ ] API 反向代理正常，接口可调用
- [ ] 默认账号可正常登录
- [ ] 权限系统正常工作，菜单和按钮权限正确显示
- [ ] 文件上传/下载功能正常
- [ ] 日志输出正常
- [ ] 进程守护配置完成（PM2 或 systemd）
  - [ ] PM2：`pm2 status` 显示应用运行中，`pm2 save` 已执行
  - [ ] systemd：`systemctl status dms` 显示 active (running)

---

## 11. 目录结构

```
wdglxt/
├── application.yml                   # 外部配置文件模板（生产环境使用）
├── pm2-ecosystem.config.cjs         # PM2 进程管理配置文件
├── server/                          # 后端工程
│   ├── src/main/java/
│   │   └── com/example/dms/
│   │       ├── controller/          # REST 控制器
│   │       ├── service/             # 业务服务层
│   │       ├── repository/          # 数据访问层
│   │       │   ├── entity/          # 实体类
│   │       │   └── mapper/          # MyBatis Mapper
│   │       ├── config/              # 配置类
│   │       ├── common/               # 公共类
│   │       └── exception/           # 异常处理
│   ├── src/main/resources/
│   │   ├── application.yml          # 应用配置（开发环境）
│   │   ├── schema.sql               # 表结构（自动执行）
│   │   └── init.sql                 # 完整初始化脚本（含数据）
│   └── pom.xml                      # Maven 配置
├── web/                              # 前端工程
│   ├── src/
│   │   ├── views/                   # 页面组件
│   │   ├── api/                     # API 封装
│   │   ├── router/                   # 路由配置
│   │   ├── composables/             # Composition API
│   │   ├── directives/              # 自定义指令
│   │   └── styles/                   # 样式文件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── deploy.md                         # 本部署文档
```

---

## 12. 版本更新

### 更新后端

**PM2 方式：**

```bash
# 1. 停止服务
pm2 stop dms-backend

# 2. 备份当前版本和配置文件
mkdir -p /www/wwwroot/dms-wdglxt/backup
cp /www/wwwroot/dms-wdglxt/dms-0.0.1-SNAPSHOT.jar /www/wwwroot/dms-wdglxt/backup/dms-$(date +%Y%m%d).jar
cp /www/wwwroot/dms-wdglxt/application.yml /www/wwwroot/dms-wdglxt/backup/application-$(date +%Y%m%d).yml

# 3. 复制新版本 jar 包（配置文件保持不变）
cp server/target/dms-0.0.1-SNAPSHOT.jar /www/wwwroot/dms-wdglxt/

# 4. 启动服务（使用外部配置文件）
pm2 start pm2-ecosystem.config.cjs

# 5. 检查服务状态
pm2 status
pm2 logs dms-backend --lines 50
```

**systemd 方式：**

```bash
# 1. 停止服务
sudo systemctl stop dms

# 2. 备份当前版本和配置文件
sudo mkdir -p /opt/dms/backup
sudo cp /opt/dms/dms-0.0.1-SNAPSHOT.jar /opt/dms/backup/dms-$(date +%Y%m%d).jar
sudo cp /opt/dms/application.yml /opt/dms/backup/application.yml-$(date +%Y%m%d)

# 3. 复制新版本 jar 包（配置文件保持不变）
sudo cp server/target/dms-0.0.1-SNAPSHOT.jar /opt/dms/

# 4. 启动服务（使用外部配置文件）
sudo systemctl start dms

# 5. 检查服务状态
sudo systemctl status dms
```

**注意**：使用外部配置文件时，更新 jar 包不会影响配置文件，配置修改和版本更新互不干扰。

### 更新前端

```bash
# 1. 构建新版本
cd web
npm run build

# 2. 备份当前版本
sudo cp -r /var/www/dms-web /var/www/dms-web-backup-$(date +%Y%m%d)

# 3. 部署新版本
sudo cp -r dist/* /var/www/dms-web/

# 4. 重载 Nginx
sudo systemctl reload nginx
```

---

## 13. 技术支持

如遇到问题，请检查：
1. 后端日志：`journalctl -u dms -f`
2. Nginx 日志：`/var/log/nginx/error.log`
3. 浏览器控制台：F12 开发者工具
4. 数据库连接：`mysql -uroot -p123456 dms -e "SHOW TABLES;"`

---

**最后更新：** 2025-12-03


## 12. 版本更新

### 更新后端

```bash
# 1. 停止服务
sudo systemctl stop dms

# 2. 备份当前版本和配置
sudo mkdir -p /opt/dms/backup
sudo cp /opt/dms/dms-0.0.1-SNAPSHOT.jar /opt/dms/backup/dms-$(date +%Y%m%d).jar
# 如果使用外部配置文件，也备份一下
sudo cp /opt/dms/application.yml /opt/dms/backup/application-$(date +%Y%m%d).yml

# 3. 复制新版本 jar 包（注意：不会覆盖外部配置文件）
sudo cp server/target/dms-0.0.1-SNAPSHOT.jar /opt/dms/

# 4. 检查外部配置文件是否存在（如果不存在，需要创建）
if [ ! -f /opt/dms/application.yml ]; then
    echo "警告：外部配置文件不存在，将使用 jar 包内默认配置"
fi

# 5. 启动服务
sudo systemctl start dms

# 6. 检查服务状态
sudo systemctl status dms
```

**注意：** 使用外部配置文件时，更新 jar 包不会影响配置文件，可以安全地更新应用而不丢失配置。

### 更新前端

```bash
# 1. 构建新版本
cd web
npm run build

# 2. 备份当前版本
sudo cp -r /var/www/dms-web /var/www/dms-web-backup-$(date +%Y%m%d)

# 3. 部署新版本
sudo cp -r dist/* /var/www/dms-web/

# 4. 重载 Nginx
sudo systemctl reload nginx
```

---

## 13. 技术支持

如遇到问题，请检查：
1. 后端日志：`journalctl -u dms -f`
2. Nginx 日志：`/var/log/nginx/error.log`
3. 浏览器控制台：F12 开发者工具
4. 数据库连接：`mysql -uroot -p123456 dms -e "SHOW TABLES;"`

---

**最后更新：** 2025-12-03

sudo systemctl reload nginx
```

---

## 13. 技术支持

如遇到问题，请检查：
1. 后端日志：`journalctl -u dms -f`
2. Nginx 日志：`/var/log/nginx/error.log`
3. 浏览器控制台：F12 开发者工具
4. 数据库连接：`mysql -uroot -p123456 dms -e "SHOW TABLES;"`

---

**最后更新：** 2025-12-03


## 12. 版本更新

### 更新后端

```bash
# 1. 停止服务
sudo systemctl stop dms

# 2. 备份当前版本和配置
sudo mkdir -p /opt/dms/backup
sudo cp /opt/dms/dms-0.0.1-SNAPSHOT.jar /opt/dms/backup/dms-$(date +%Y%m%d).jar
# 如果使用外部配置文件，也备份一下
sudo cp /opt/dms/application.yml /opt/dms/backup/application-$(date +%Y%m%d).yml

# 3. 复制新版本 jar 包（注意：不会覆盖外部配置文件）
sudo cp server/target/dms-0.0.1-SNAPSHOT.jar /opt/dms/

# 4. 检查外部配置文件是否存在（如果不存在，需要创建）
if [ ! -f /opt/dms/application.yml ]; then
    echo "警告：外部配置文件不存在，将使用 jar 包内默认配置"
fi

# 5. 启动服务
sudo systemctl start dms

# 6. 检查服务状态
sudo systemctl status dms
```

**注意：** 使用外部配置文件时，更新 jar 包不会影响配置文件，可以安全地更新应用而不丢失配置。

### 更新前端

```bash
# 1. 构建新版本
cd web
npm run build

# 2. 备份当前版本
sudo cp -r /var/www/dms-web /var/www/dms-web-backup-$(date +%Y%m%d)

# 3. 部署新版本
sudo cp -r dist/* /var/www/dms-web/

# 4. 重载 Nginx
sudo systemctl reload nginx
```

---

## 13. 技术支持

如遇到问题，请检查：
1. 后端日志：`journalctl -u dms -f`
2. Nginx 日志：`/var/log/nginx/error.log`
3. 浏览器控制台：F12 开发者工具
4. 数据库连接：`mysql -uroot -p123456 dms -e "SHOW TABLES;"`

---

**最后更新：** 2025-12-03
