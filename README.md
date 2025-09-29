# Fun Campus 校园管理系统

<p align="center">
  <img src="https://img.shields.io/badge/SmartAdmin-Framework-blue" alt="SmartAdmin Framework">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen" alt="Spring Boot 3.5.4">
  <img src="https://img.shields.io/badge/Vue-3.4.27-brightgreen" alt="Vue 3.4.27">
</p>

## 简介

Fun Campus 是一个现代化的校园管理系统，仿照 [PU口袋校园](https://www.pocketuni.net/) 设计，基于 [SmartAdmin](https://gitee.com/lab1024/smart-admin) 框架进行深度定制开发。系统提供完整的校园信息化解决方案，涵盖学生管理、活动组织、社团运营等核心功能。

## 核心特性

✨ **全平台覆盖** - 一套代码同时支持Web管理后台、移动端APP及各类小程序  
⚡ **高性能架构** - 基于Spring Boot 3.5.4 + Vue 3.4.27构建，响应迅速  
📱 **移动优先** - 完美适配手机端体验，操作流畅自然  
🔐 **安全可靠** - 集成Sa-Token权限体系，保障数据安全  
🎨 **现代化UI** - 采用Ant Design Vue组件库，界面美观大方  

## 技术架构

### 后端技术栈
- 核心框架：Spring Boot 3.5.4 (Java 17)
- 数据库框架：MyBatis Plus 3.5.12
- 安全框架：Sa-Token 1.44.0
- 数据库连接池：Druid 1.2.25
- 缓存中间件：Redis (通过Redisson实现)
- API文档：Knife4j + OpenAPI 3

### 前端技术栈

#### 管理后台 (smart-admin-web-javascript)
- 核心框架：Vue 3.4.27
- UI组件库：Ant Design Vue 4.2.5
- 构建工具：Vite 5.2.12
- 状态管理：Pinia 2.1.7
- 路由管理：Vue Router 4.3.2
- 图表库：ECharts 5.4.3

#### 移动端应用 (smart-app)
- 核心框架：UniApp 3.0 (Vue 3.2.47)
- UI组件库：Uni UI 1.5.0
- 支持编译到多个平台：
  - iOS/Android APP
  - 微信小程序
  - 支付宝小程序
  - 百度小程序
  - 字节跳动小程序
  - QQ小程序
  - 快手小程序
  - 小红书小程序

## 项目结构

```
fun-campus/
├── sa-admin/                    # 后台管理服务（Spring Boot）
├── sa-base/                     # 后台基础模块
├── smart-admin-web-javascript/  # 管理后台前端（Vue3 + Ant Design Vue）
├── smart-app/                   # 移动端应用（UniApp）
└── sql_script/                  # 数据库脚本
```

## SmartAdmin框架

本项目基于 [SmartAdmin](https://gitee.com/lab1024/smart-admin) 开发，SmartAdmin是一套企业级后台管理系统快速开发框架，提供了丰富的基础功能和组件，包括用户管理、角色权限、菜单管理、操作日志等，能够帮助开发者快速构建企业级应用系统。

## 适用场景

- 高校学生管理系统
- 校园活动组织平台
- 社团运营管理
- 学分/积分管理
- 校园信息发布

## 许可证

本项目采用 MIT License 开源协议，详情请查看 [LICENSE](LICENSE) 文件。

---