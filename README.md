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
## 预览
<img width="1151" height="600" alt="2b1edbf3a3138ebe645ef724cad9330" src="https://github.com/user-attachments/assets/2d538d13-2b6d-4c53-936b-f4cd18287c82" />
<img width="1152" height="599" alt="21f019c8ee2ed30764bfef880c8dd28" src="https://github.com/user-attachments/assets/01abcf16-34a5-4eca-8321-0f384a499973" />
<img width="1152" height="597" alt="8cb547f984b4183402fe41b99790f49" src="https://github.com/user-attachments/assets/6da0593d-30b7-41af-9ae2-014b0b95da22" />
<img width="1151" height="585" alt="c2a6269b306957777f9f75ace31bdb4" src="https://github.com/user-attachments/assets/210d4338-834f-44e5-850c-ed2c63bfa8dc" />
<img width="1152" height="598" alt="10873f62de99565ae943efe26567017" src="https://github.com/user-attachments/assets/315bc609-6059-4be4-9ada-20171e3f267a" />
<img width="1152" height="599" alt="feab68c5d5da49f8a7d64ea25892268" src="https://github.com/user-attachments/assets/ac4a18fc-0cf0-492b-97d4-fef8d2ae712c" />
