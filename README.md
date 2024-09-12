# 基于SpringBoot与Vue的博客网站系统
## 项目描述：
采用SpringBoot3+Vue3编写的前后端分离项目，集成多种技术栈，使用JWT校验方案。实现了博客系统的用户管理文章管理等基础功能，用户可以登录网站编辑与发布个人博客文章，并对文章进行管理，也可以浏览其他用户发布的文章，并与博主进行交流。
## 后端涉及技术点：
- 采用SpringSecurity作为权限校验框架，并整合JWT校验方案
- 使用mybatis作为持久层框架，与mysql数据库进行交互
- 采用Redis缓存技术，实现如验证码过期控制、ip地址请求限流等功能
- 采用RabbitMQ消息队列，实现如积压邮件发送任务，由监听器统一管理的功能
- 使用Swagger自动生成接口文档
## 前端涉及技术点：
- 采用Vite前端构建工具搭建的Vue.js项目
- 采用Vue-Router作为前端web路由
- 采用VuexStore进行应用的状态管理，如用户的登录信息
- 采用TailwindCss作为css框架，进行自定义UI设计
- 采用Axios作为异步请求框架
- 采用Element-Plus作为Vue组件库
