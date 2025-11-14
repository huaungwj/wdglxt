const path = require('path')

// JAR 路径策略：优先 JAR_PATH 环境变量；否则默认 /www/wwwroot/dms/dms-0.0.1-SNAPSHOT.jar
const jarPath = process.env.JAR_PATH || '/www/wwwroot/dms/dms-0.0.1-SNAPSHOT.jar'
const jarDir = path.dirname(jarPath)
// 配置路径策略：优先 CONFIG_PATH 环境变量；否则默认 /www/wwwroot/dms/application.yml
const configPath = process.env.CONFIG_PATH || path.join(jarDir, 'application.yml')
// Java 可执行文件：优先 JAVA_BIN；否则使用你的服务器路径
const javaBin = process.env.JAVA_BIN || '/www/server/java/jdk-17.0.8/bin/java'

module.exports = {
  apps: [
    {
      name: 'dms-backend',
      script: javaBin,
      interpreter: 'none',
      args: [
        '-server',
        ...(process.env.JAVA_OPTS ? process.env.JAVA_OPTS.split(' ') : []),
        '-jar',
        jarPath,
        `--spring.config.location=${configPath}`,
      ],
      cwd: process.cwd(),
      env: {
        NODE_ENV: 'production',
        // 可在启动时通过环境变量覆盖：
        // JAVA_BIN: '/usr/bin/java'
        // JAR_PATH: '/opt/dms/server.jar'
        // CONFIG_PATH: '/opt/dms/application.yml'
        // JAVA_OPTS: '-Xms256m -Xmx512m'
      },
      instances: 1,
      exec_mode: 'fork',
      autorestart: true,
      max_memory_restart: '512M',
      out_file: './logs/dms.out.log',
      error_file: './logs/dms.err.log',
      merge_logs: true,
      time: true,
      watch: false,
    },
  ],
}
