# 校园旧书漂流共享系统 详细分析规格说明书

## 一、项目概述

本项目是一个前后端分离的校园图书漂流共享系统，采用 **Vue.js + SpringBoot** 技术栈开发。

### 1.1 技术栈说明

| 层级 | 技术 | 版本 | 作用 |
|------|------|------|------|
| 前端框架 | Vue.js | 2.x | 构建用户界面 |
| 前端UI库 | Element-UI | - | 提供美观的组件 |
| 前端路由 | Vue-Router | - | 页面路由管理 |
| 前端状态管理 | Vuex | - | 全局状态存储 |
| 后端框架 | SpringBoot | 2.5.6 | 提供RESTful API |
| 持久层框架 | MyBatis | 2.0.0 | 数据库操作 |
| 数据库 | MySQL | 8.0 | 存储业务数据 |
| 缓存 | Redis | - | 存储用户Token |
| 构建工具 | Maven | - | 项目依赖管理 |

---

## 二、文件夹与代码命名解析

### 2.1 后端项目结构 (book-backend)

```
book-backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── shanzhu/
│       │           └── book/          # 基础包名：shan(山) zhu(珠)，book(图书)
│       │               ├── config/    # 配置层：存放项目配置类
│       │               ├── exception/ # 异常层：自定义异常类
│       │               ├── interceptor/ # 拦截器层：请求拦截处理
│       │               ├── mapper/    # 持久层接口：定义数据库操作方法
│       │               ├── model/     # 实体层：数据库表对应的Java类
│       │               ├── service/   # 业务层接口：定义业务逻辑方法
│       │               ├── task/      # 定时任务层：定时执行的任务
│       │               ├── utils/     # 工具层：通用工具类
│       │               └── web/       # 控制层：接收前端请求的Controller
│       └── resources/
│           ├── mapper/                # MyBatis的XML映射文件
│           ├── static/                # 静态资源（图片等）
│           └── application.yml        # 项目配置文件
└── pom.xml                            # Maven依赖配置
```

#### 2.1.1 config（配置层）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `CorsConfig.java` | Cors(Cross-Origin Resource Sharing) + Config(配置) | 跨域配置类，允许前端跨域访问后端API |
| `WebMvcConfig.java` | WebMvc(Model-View-Controller) + Config | Web配置类，配置静态资源映射路径 |

#### 2.1.2 exception（异常层）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `BookNotEnoughException.java` | Book(图书) + NotEnough(不足) + Exception(异常) | 图书库存不足时抛出的异常 |
| `OperationFailureException.java` | Operation(操作) + Failure(失败) + Exception | 操作失败时抛出的异常 |
| `GlobalExceptionHandler.java` | Global(全局) + Exception(异常) + Handler(处理器) | 全局异常处理器，统一处理系统异常 |

#### 2.1.3 interceptor（拦截器层）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `UserInterceptor.java` | User(用户) + Interceptor(拦截器) | 用户登录拦截器，检查用户是否登录 |
| `ReaderInterceptor.java` | Reader(读者) + Interceptor(拦截器) | 读者权限拦截器，阻止普通用户访问管理页面 |

#### 2.1.4 mapper（持久层接口）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `UserMapper.java` | User(用户) + Mapper(映射器) | 用户表数据库操作接口 |
| `BookInfoMapper.java` | BookInfo(图书信息) + Mapper | 图书信息表数据库操作接口 |
| `BookTypeMapper.java` | BookType(图书类型) + Mapper | 图书类型表数据库操作接口 |
| `BorrowMapper.java` | Borrow(借阅) + Mapper | 借阅记录表数据库操作接口 |
| `MessageMapper.java` | Message(消息) + Mapper | 站内消息表数据库操作接口 |
| `BookWishMapper.java` | BookWish(图书心愿) + Mapper | 求书心愿表数据库操作接口 |

#### 2.1.5 model（实体层）详解

| 文件名 | 命名含义 | 对应数据库表 | 主要属性 |
|--------|----------|--------------|----------|
| `User.java` | User(用户) | user | userid(用户ID), username(姓名), studentid(学号), userpassword(密码), isadmin(是否管理员), status(状态), gender(性别), openId(微信OpenID), department(系部), creditScore(信用分), todayAddScore(今日已加分), scoreUpdateDate(加分日期) |
| `BookInfo.java` | BookInfo(图书信息) | book_info | bookid(图书ID), bookname(书名), bookauthor(作者), bookprice(价格), booktypeid(类型ID), bookdesc(描述), isborrowed(是否借出), bookimg(封面图片), bookcount(总数), inventory(库存), uploaderid(上传者ID), contactinfo(交接联系方式) |
| `BookType.java` | BookType(图书类型) | book_type | booktypeid(类型ID), booktypename(类型名称), booktypedesc(类型描述) |
| `Borrow.java` | Borrow(借阅) | borrow | borrowid(借阅ID), userid(用户ID), bookid(图书ID), borrowtime(借阅时间), returntime(归还时间), applytime(申请时间), borrowreason(申请理由), state(状态), returnmsg(归还寄语), secretCode(交接暗号), borrowDays(借阅天数) |
| `Message.java` | Message(消息) | message | messageid(消息ID), userid(接收用户ID), content(内容), isread(是否已读), createtime(创建时间) |
| `BookWish.java` | BookWish(图书心愿) | book_wish | wishId(心愿ID), userId(用户ID), userName(用户名), bookName(书名), wishDesc(心愿描述), state(状态), createTime(创建时间), fulfillUserId(满足者ID) |

#### 2.1.6 service（业务层接口）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `UserService.java` | User(用户) + Service(服务) | 用户相关业务：登录、注册、用户管理 |
| `BookInfoService.java` | BookInfo(图书信息) + Service | 图书相关业务：增删改查、库存管理 |
| `BookTypeService.java` | BookType(图书类型) + Service | 图书类型相关业务 |
| `BorrowService.java` | Borrow(借阅) + Service | 借阅相关业务：申请、审核、归还、暗号验证、信用分管理 |

#### 2.1.7 task（定时任务层）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `BorrowTask.java` | Borrow(借阅) + Task(任务) | 定时任务：每分钟扫描超时未处理的借阅申请自动取消；每日扣减逾期用户信用分 |

#### 2.1.8 utils（工具层）详解

| 文件名 | 命名含义 | 功能说明 |
|--------|----------|----------|
| `R.java` | R(Result的缩写) | 统一返回结果类，封装API响应数据 |
| `PageUtils.java` | Page(分页) + Utils(工具) | 分页参数解析工具 |
| `TokenProcessor.java` | Token(令牌) + Processor(处理器) | Token生成与管理工具 |
| `JsonUtil.java` | Json(JSON) + Util(工具) | JSON序列化/反序列化工具 |
| `PathUtils.java` | Path(路径) + Utils | 文件路径处理工具 |
| `WechatPushUtils.java` | Wechat(微信) + Push(推送) + Utils | 微信消息推送工具（WxPusher） |
| `BASE64Encoder.java` | BASE64(编码方式) + Encoder(编码器) | Base64编码工具 |

#### 2.1.9 web（控制层）详解

| 文件名 | 命名含义 | 请求路径前缀 | 功能说明 |
|--------|----------|--------------|----------|
| `UserController.java` | User(用户) + Controller(控制器) | /user | 用户登录、注册、信息管理 |
| `BookInfoController.java` | BookInfo(图书信息) + Controller | /bookInfo | 图书信息管理 |
| `BookTypeController.java` | BookType(图书类型) + Controller | /bookType | 图书类型管理 |
| `BorrowController.java` | Borrow(借阅) + Controller | /borrow | 借阅申请、审核、归还、暗号验证 |
| `MessageController.java` | Message(消息) + Controller | /message | 站内消息管理 |
| `BookWishController.java` | BookWish(图书心愿) + Controller | /wish | 求书心愿管理 |
| `uploadController.java` | upload(上传) + Controller | /upload | 图片文件上传 |

---

### 2.2 前端项目结构 (book-frontend)

```
book-frontend/
├── public/                  # 静态资源目录
│   ├── index.html          # 入口HTML文件
│   └── pic/                # 图片资源
├── src/
│   ├── api/                # API接口层：封装所有后端请求
│   ├── assets/             # 静态资源：图片、字体等
│   ├── components/         # 公共组件：可复用的Vue组件
│   ├── directive/          # 自定义指令：Vue指令
│   ├── layout/             # 布局组件：页面布局结构
│   ├── router/             # 路由配置：页面路由定义
│   ├── store/              # Vuex状态管理：全局数据存储
│   ├── styles/             # 样式文件：全局CSS/SCSS
│   ├── utils/              # 工具函数：通用工具方法
│   ├── vendor/             # 第三方库
│   └── views/              # 页面视图：各个功能页面
├── package.json            # npm依赖配置
└── vue.config.js           # Vue项目配置
```

#### 2.2.1 api（API接口层）详解

| 文件名 | 命名含义 | 对应后端Controller |
|--------|----------|-------------------|
| `user.js` | user(用户) | UserController |
| `bookinfo.js` | bookinfo(图书信息) | BookInfoController |
| `booktype.js` | booktype(图书类型) | BookTypeController |
| `borrow.js` | borrow(借阅) | BorrowController |
| `wish.js` | wish(心愿) | BookWishController |
| `message.js` | message(消息) | MessageController |
| `dashboard.js` | dashboard(仪表盘) | 多个Controller组合 |

#### 2.2.2 views（页面视图）详解

| 文件夹/文件 | 命名含义 | 页面功能 |
|-------------|----------|----------|
| `login/index.vue` | login(登录) | 用户登录页面 |
| `register/index.vue` | register(注册) | 用户注册页面 |
| `dashboard/index.vue` | dashboard(仪表盘) | 首页，展示统计数据 |
| `bookinfo/index.vue` | bookinfo(图书信息) | 图书列表、发布、申请漂流 |
| `booktype/index.vue` | booktype(图书类型) | 图书类型管理（管理员） |
| `borrow/index.vue` | borrow(借阅) | 借阅记录管理、审核、交接 |
| `user/index.vue` | user(用户) | 用户管理（管理员） |
| `wish/index.vue` | wish(心愿) | 求书心愿广场 |
| `password/index.vue` | password(密码) | 修改密码 |
| `404.vue` | 404(错误码) | 页面未找到 |

#### 2.2.3 store（Vuex状态管理）详解

| 文件 | 命名含义 | 功能说明 |
|------|----------|----------|
| `index.js` | 入口文件 | 创建Vuex Store实例 |
| `getters.js` | getters(获取器) | 定义全局状态获取方法 |
| `modules/user.js` | user(用户)模块 | 用户登录状态、Token、角色信息 |
| `modules/permission.js` | permission(权限)模块 | 动态路由权限管理 |
| `modules/app.js` | app(应用)模块 | 应用全局状态（侧边栏等） |
| `modules/settings.js` | settings(设置)模块 | 系统设置 |

#### 2.2.4 utils（工具函数）详解

| 文件 | 命名含义 | 功能说明 |
|------|----------|----------|
| `request.js` | request(请求) | Axios封装，统一处理请求/响应 |
| `auth.js` | auth(认证) | Token存取（使用Cookie） |
| `permission.js` | permission(权限) | 权限判断工具 |
| `validate.js` | validate(验证) | 表单验证工具 |
| `get-page-title.js` | get-page-title(获取页面标题) | 页面标题设置 |
| `index.js` | 通用工具 | 其他通用方法 |

---

## 三、数据库表结构详解

### 3.1 数据库概述

数据库名：`db_book`，字符集：`utf8mb3`

### 3.2 表结构详解

#### 3.2.1 user（用户表）

| 字段名 | 类型 | 含义 | 说明 |
|--------|------|------|------|
| userId | int | 用户ID | 主键，自增 |
| userName | varchar(20) | 用户姓名 | 用户的真实姓名 |
| studentId | varchar(20) | 学号 | 唯一登录凭证 |
| userPassword | varchar(50) | 密码 | 登录密码 |
| isAdmin | tinyint | 是否管理员 | 1:管理员, 0:普通用户 |
| status | tinyint | 状态 | 0:待审核, 1:正常, 2:禁用 |
| gender | tinyint | 性别 | 1:男, 0:女, 2:保密 |
| open_id | varchar(100) | 微信OpenID | 用于WxPusher微信推送 |
| department | varchar(50) | 所属系部 | 用户所在院系 |
| credit_score | int | 信用分 | 默认100，低于60禁止借阅 |
| today_add_score | int | 今日已加分 | 今日已恢复的信用分 |
| score_update_date | date | 加分日期 | 信用分最后增加日期 |

#### 3.2.2 book_info（图书信息表）

| 字段名 | 类型 | 含义 | 说明 |
|--------|------|------|------|
| bookId | int | 图书ID | 主键，自增 |
| bookName | varchar(50) | 书名 | 图书名称 |
| bookAuthor | varchar(50) | 作者 | 图书作者 |
| bookPrice | decimal(10,2) | 价格 | 图书原价 |
| bookTypeId | int | 类型ID | 所属院系分类 |
| bookDesc | varchar(1000) | 描述 | 图书简介/寄语 |
| isBorrow | tinyint | 借阅状态 | 0:在库, 1:漂流中, 2:申请中 |
| bookImage | varchar(255) | 封面图片 | 图片路径 |
| uploaderId | int | 上传者ID | 发布者用户ID |
| bookCount | int | 总数 | 图书总数量 |
| inventory | int | 库存 | 当前剩余数量 |
| contactInfo | varchar(255) | 交接联系方式 | 仅审核通过后可见 |

#### 3.2.3 book_type（图书类型表）

| 字段名 | 类型 | 含义 | 说明 |
|--------|------|------|------|
| bookTypeId | int | 类型ID | 主键，自增 |
| bookTypeName | varchar(50) | 类型名称 | 院系名称 |
| bookTypeDesc | varchar(255) | 类型描述 | 英文名或描述 |

**预设类型（11种）：**
1. 机械工程系
2. 电气与控制工程系
3. 大数据与智能工程系
4. 土木工程系
5. 地球科学与工程系
6. 矿业工程系
7. 管理科学与工程系
8. 经济与管理系
9. 艺术与设计科学系
10. 材料科学与工程系
11. 其他（未分类的漂流书籍）

#### 3.2.4 borrow（借阅记录表）

| 字段名 | 类型 | 含义 | 说明 |
|--------|------|------|------|
| borrowId | int | 借阅ID | 主键，自增 |
| userId | int | 用户ID | 申请人ID |
| bookId | int | 图书ID | 借阅的图书 |
| borrowTime | datetime | 借阅时间 | 实际交接时间 |
| returnTime | datetime | 归还时间 | 归还时间 |
| applyTime | datetime | 申请时间 | 提交申请时间 |
| borrowReason | varchar(255) | 申请理由 | 借阅理由 |
| state | tinyint | 状态 | 见下方状态定义 |
| returnMsg | varchar(255) | 归还寄语 | 归还时的感悟 |
| secretCode | varchar(10) | 交接暗号 | 6位数字验证码 |
| borrow_days | int | 借阅天数 | 预计借阅天数，默认30 |

**状态(state)定义：**
- 0: 审核中 - 等待发布者审核
- 1: 待交接 - 审核通过，等待线下交接
- 2: 漂流中 - 已完成交接，正在阅读
- 3: 已归还 - 已完成漂流
- 4: 已驳回 - 发布者拒绝借阅
- 5: 撤销 - 用户主动取消
- 6: 报损 - 书籍遗失/损坏
- 7: 系统取消 - 超时自动取消

#### 3.2.5 book_wish（求书心愿表）

| 字段名 | 类型 | 含义 | 说明 |
|--------|------|------|------|
| wish_id | int | 心愿ID | 主键，自增 |
| user_id | int | 用户ID | 发布心愿的用户 |
| user_name | varchar(255) | 用户名 | 发布者姓名 |
| book_name | varchar(255) | 书名 | 想要的书名 |
| wish_desc | varchar(500) | 心愿描述 | 求书说明 |
| state | int | 状态 | 0:求书中, 1:已满足 |
| create_time | datetime | 创建时间 | 发布时间 |
| fulfill_user_id | int | 满足者ID | 发布该书的人 |

#### 3.2.6 message（站内消息表）

| 字段名 | 类型 | 含义 | 说明 |
|--------|------|------|------|
| messageId | int | 消息ID | 主键，自增 |
| userId | int | 用户ID | 接收用户ID |
| content | varchar(500) | 内容 | 消息内容 |
| isRead | tinyint | 是否已读 | 0:未读, 1:已读 |
| createTime | datetime | 创建时间 | 消息发送时间 |

---

## 四、功能清单

### 4.1 功能模块总览

```
校园旧书漂流共享系统
├── 用户模块
│   ├── 用户登录
│   ├── 用户注册
│   ├── 退出登录
│   ├── 修改密码
│   ├── 个人信息查看
│   ├── 微信推送绑定
│   └── 信用分查看
├── 图书模块
│   ├── 图书列表查看
│   ├── 图书搜索筛选
│   ├── 发布旧书
│   ├── 编辑图书信息
│   ├── 删除/下架图书
│   ├── 查看我发布的
│   └── 查看漂流足迹
├── 借阅模块
│   ├── 申请漂流
│   ├── 审核申请（同意/驳回）
│   ├── 交接暗号验证
│   ├── 归还/传递图书
│   ├── 撤销申请
│   ├── 协商交接
│   ├── 报损登记
│   └── 一键催还
├── 心愿模块
│   ├── 发布求书心愿
│   ├── 查看心愿列表
│   ├── 满足他人心愿
│   └── 删除心愿
├── 用户管理模块（管理员）
│   ├── 用户列表
│   ├── 添加用户
│   ├── 编辑用户
│   ├── 删除用户
│   ├── 重置密码
│   └── 信用分管理
├── 图书类型模块（管理员）
│   ├── 类型列表
│   ├── 添加类型
│   ├── 编辑类型
│   └── 删除类型
├── 系统功能
│   ├── 首页数据统计
│   ├── 站内消息通知
│   ├── 定时任务（自动取消超时、扣信用分）
│   └── 微信消息推送
```

### 4.2 详细功能清单

#### 4.2.1 用户模块功能

| 功能编号 | 功能名称 | 功能描述 | 操作角色 |
|----------|----------|----------|----------|
| U001 | 用户登录 | 使用学号和密码登录系统 | 所有用户 |
| U002 | 用户注册 | 新用户注册账号，填写姓名、学号、密码、性别、系部 | 未注册用户 |
| U003 | 退出登录 | 清除登录状态，退出系统 | 已登录用户 |
| U004 | 修改密码 | 输入原密码和新密码修改登录密码 | 已登录用户 |
| U005 | 个人信息查看 | 查看个人基本信息、信用分 | 已登录用户 |
| U006 | 微信推送绑定 | 绑定WxPusher UID，接收微信通知 | 已登录用户 |
| U007 | 信用分查看 | 查看自己的信用分和风控状态 | 已登录用户 |

#### 4.2.2 图书模块功能

| 功能编号 | 功能名称 | 功能描述 | 操作角色 |
|----------|----------|----------|----------|
| B001 | 图书列表查看 | 分页查看所有图书信息，同性发布的优先显示 | 所有用户 |
| B002 | 图书搜索筛选 | 按书名、作者、类型搜索图书 | 所有用户 |
| B003 | 发布旧书 | 填写图书信息、上传封面、填写交接联系方式，发布到平台 | 已登录用户 |
| B004 | 编辑图书信息 | 修改图书名称、作者、描述、交接联系方式等信息 | 图书发布者/管理员 |
| B005 | 删除/下架图书 | 从平台移除图书 | 图书发布者/管理员 |
| B006 | 查看我发布的 | 筛选显示自己发布的图书 | 已登录用户 |
| B007 | 查看漂流足迹 | 查看图书的历史借阅记录 | 所有用户 |

#### 4.2.3 借阅模块功能

| 功能编号 | 功能名称 | 功能描述 | 操作角色 |
|----------|----------|----------|----------|
| R001 | 申请漂流 | 填写申请理由和借阅天数，申请借阅图书（信用分<60禁止） | 已登录用户 |
| R002 | 审核申请 | 同意或驳回借阅申请，可填写驳回理由，同意后生成6位暗号 | 图书发布者/管理员 |
| R003 | 交接暗号验证 | 发布者输入借阅者出示的6位暗号完成交接，双方获信用分奖励 | 图书发布者 |
| R004 | 归还/传递图书 | 填写漂流感悟和交接说明，归还图书，书籍所有权转移 | 借阅者 |
| R005 | 撤销申请 | 取消借阅申请（申请阶段或交接阶段） | 申请人 |
| R006 | 协商交接 | 发送消息请求协商新的交接地点 | 借阅者 |
| R007 | 报损登记 | 登记图书损坏或丢失 | 借阅者 |
| R008 | 一键催还 | 发送催还通知给借阅者 | 图书发布者 |

#### 4.2.4 心愿模块功能

| 功能编号 | 功能名称 | 功能描述 | 操作角色 |
|----------|----------|----------|----------|
| W001 | 发布求书心愿 | 填写想找的书名和心愿描述 | 已登录用户 |
| W002 | 查看心愿列表 | 查看所有求书心愿 | 所有用户 |
| W003 | 满足他人心愿 | 跳转到发布页面，为他人发布图书 | 已登录用户 |
| W004 | 删除心愿 | 删除自己发布的心愿 | 心愿发布者/管理员 |

#### 4.2.5 用户管理模块功能（管理员专属）

| 功能编号 | 功能名称 | 功能描述 | 操作角色 |
|----------|----------|----------|----------|
| UM001 | 用户列表 | 分页查看所有用户，支持按姓名/学号/系部筛选 | 管理员 |
| UM002 | 添加用户 | 录入新用户信息 | 管理员 |
| UM003 | 编辑用户 | 修改用户信息、角色、状态、信用分 | 管理员 |
| UM004 | 删除用户 | 删除用户账号 | 管理员 |
| UM005 | 重置密码 | 将用户密码重置为123456 | 管理员 |
| UM006 | 信用分管理 | 手动调整用户信用分 | 管理员 |

#### 4.2.6 图书类型模块功能（管理员专属）

| 功能编号 | 功能名称 | 功能描述 | 操作角色 |
|----------|----------|----------|----------|
| BT001 | 类型列表 | 查看所有图书类型（院系分类） | 管理员 |
| BT002 | 添加类型 | 新增图书分类 | 管理员 |
| BT003 | 编辑类型 | 修改类型名称和描述 | 管理员 |
| BT004 | 删除类型 | 删除图书分类 | 管理员 |

---

## 五、功能-代码对应关系

### 5.1 用户登录功能

#### 5.1.1 前端代码

**文件：** `src/views/login/index.vue`

**关键方法：** `handleLogin()`

```javascript
handleLogin() {
  this.$refs.loginForm.validate(valid => {
    if (valid) {
      this.loading = true
      // 调用Vuex的login action
      this.$store.dispatch('user/login', this.loginForm).then(() => {
        const targetPath = this.redirect || '/';
        if (targetPath.includes('/404')) {
          this.$router.push({ path: '/' });
        } else {
          this.$router.push({ path: targetPath });
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  })
}
```

**文件：** `src/store/modules/user.js`

**关键方法：** `login action`

```javascript
login({ commit }, userInfo) {
  const { username, password } = userInfo
  return new Promise((resolve, reject) => {
    const loginParams = {
      username: username.trim(),
      userpassword: password
    }
    login(loginParams).then(response => {
      const { code, msg, data } = response
      if (code !== 0 && code !== 200) {
        reject(msg || '登录失败，请检查账号密码')
        return
      }
      let token = null
      if (data && typeof data === 'object' && data.token) {
        token = data.token
      } else if (data && typeof data === 'string') {
        token = data
      }
      commit('SET_TOKEN', token)
      setToken(token)
      resolve()
    }).catch(error => {
      reject(error)
    })
  })
}
```

**文件：** `src/api/user.js`

**关键方法：** `login()`

```javascript
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}
```

#### 5.1.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/UserController.java`

**关键方法：** `login()`

```java
@PostMapping(value = "/login")
public R login(@RequestBody User user) {
    if(user.getStudentid() == null) user.setStudentid(user.getUsername());
    return userService.userLogin(user);
}
```

**文件：** `src/main/resources/mapper/UserMapper.xml`

**关键SQL：** `selectByUserName`

```xml
<select id="selectByUserName" resultMap="BaseResultMap">
    select userId, userName, studentId, userPassword, isAdmin, status, gender, open_id, department, credit_score, today_add_score, score_update_date
    from user 
    where studentId = #{username}
</select>
```

---

### 5.2 用户注册功能

#### 5.2.1 前端代码

**文件：** `src/views/register/index.vue`

**关键方法：** `handleRight()`

```javascript
handleRight() {
  this.$refs.loginForm.validate((valid) => {
    if (valid) {
      this.loading = true;
      register({
        username: this.loginForm.username,
        password: this.loginForm.password,
        gender: this.loginForm.gender
      }).then((res) => {
        this.loading = false;
        if (res === 0) {
          this.$message.error("注册失败，可能账号重复了");
        } else {
          this.$message.success("注册成功");
          setTimeout(() => {
            this.handleBack();
          }, 1500);
        }
      }).catch(() => {
        this.loading = false;
      });
    }
  });
}
```

**文件：** `src/api/user.js`

**关键方法：** `register()`

```javascript
export function register(params) {
  return request({
    url: '/user/register',
    method: 'post',
    params
  })
}
```

#### 5.2.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/UserController.java`

**关键方法：** `register()`

```java
@RequestMapping(value = "/register")
public Integer register(String username, String password) {
    return userService.register(username, password);
}
```

**文件：** `src/main/resources/mapper/UserMapper.xml`

**关键SQL：** `insert`

```xml
<insert id="insert" parameterType="com.shanzhu.book.model.User">
    insert into user (userName, studentId, userPassword, isAdmin, status, gender, department)
    values (#{username}, #{studentid}, #{userpassword}, #{isadmin}, #{status}, #{gender}, #{department})
</insert>
```

---

### 5.3 发布旧书功能

#### 5.3.1 前端代码

**文件：** `src/views/bookinfo/index.vue`

**关键方法：** `submitForm()`

```javascript
submitForm() {
  const action = this.formType === 0 ? addBookInfo : updateBookInfo;
  if (this.formType === 0) { 
    this.form.inventory = this.form.bookcount; 
    this.form.uploaderid = this.id; 
  }
  action(this.form).then((res) => { 
    if (res === 1) { 
      this.$message.success("操作成功"); 
      this.fetchData(); 
      this.dialogFormVisible = false; 
    } 
  });
}
```

**文件：** `src/api/bookinfo.js`

**关键方法：** `addBookInfo()`

```javascript
export function addBookInfo(data) {
    return request({
        url: '/bookInfo/addBookInfo',
        method: 'post',
        data
    })
}
```

#### 5.3.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/BookInfoController.java`

**关键方法：** `addBookInfo()`

```java
@PostMapping(value = "/addBookInfo")
public Integer addBookInfo(@RequestBody BookInfo bookInfo) {
    return bookInfoService.addBookInfo(bookInfo);
}
```

**文件：** `src/main/resources/mapper/BookInfoMapper.xml`

**关键SQL：** `insert`

```xml
<insert id="insert" parameterType="com.shanzhu.book.model.BookInfo">
    insert into book_info (bookName, bookAuthor, bookPrice, bookTypeId, bookDesc, isBorrow, bookImage, bookCount, inventory, uploaderId, contactInfo)
    values (#{bookname}, #{bookauthor}, #{bookprice}, #{booktypeid}, #{bookdesc},
            #{isborrowed}, #{bookimg}, #{bookcount}, #{inventory}, #{uploaderid}, #{contactinfo})
</insert>
```

---

### 5.4 申请漂流功能

#### 5.4.1 前端代码

**文件：** `src/views/bookinfo/index.vue`

**关键方法：** `submitBorrow()`

```javascript
submitBorrow() {
  if (!this.borrowForm.borrowreason || this.borrowForm.borrowreason.trim() === '') { 
    return this.$message.warning("申请理由不能为空哦！"); 
  }
  this.borrowLoading = true;
  request({
    url: '/borrow/borrowBook', 
    method: 'post',
    data: { 
      userid: this.id, 
      bookid: this.borrowForm.bookid, 
      borrowreason: this.borrowForm.borrowreason, 
      borrowDays: this.borrowForm.borrowDays 
    }
  }).then(res => {
    this.borrowLoading = false;
    if (res.code === 0 || res === 1 || res.code === 200) {
      this.$message.success("🎉 申请成功！系统已通知发布者。");
      this.borrowDialogVisible = false;
      this.fetchData();
    } else {
      this.$message.error(res.msg || res.message || "操作失败，请检查或重试！");
    }
  }).catch(() => { this.borrowLoading = false; });
}
```

#### 5.4.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/BorrowController.java`

**关键方法：** `borrowBook()`

```java
@RequestMapping("/borrowBook")
public R borrowBook(@RequestParam(required = false) Integer userid,
                    @RequestParam(required = false) Integer bookid,
                    @RequestBody(required = false) Map<String, Object> body) {
    if (userid == null && body != null) {
        if (body.get("userid") != null) userid = Integer.parseInt(body.get("userid").toString());
        if (body.get("bookid") != null) bookid = Integer.parseInt(body.get("bookid").toString());
    }
    if (userid == null || bookid == null) return R.error("缺少用户或书籍ID");

    Borrow borrow = new Borrow();
    borrow.setUserid(userid);
    borrow.setBookid(bookid);

    if (body != null) {
        if (body.get("borrowreason") != null) {
            borrow.setBorrowreason(body.get("borrowreason").toString());
        }
        if (body.get("borrowDays") != null) {
            borrow.setBorrowDays(Integer.parseInt(body.get("borrowDays").toString()));
        }
    }
    return borrowService.addBorrow(borrow);
}
```

**文件：** `src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java`

**关键方法：** `addBorrow()`

```java
@Override
@Transactional
public R addBorrow(Borrow borrow) {
    User currentUser = userMapper.selectByPrimaryKey(borrow.getUserid());

    // 管理员不可借书
    if (currentUser != null && currentUser.getIsadmin() != null && currentUser.getIsadmin() == 1) {
        return R.error("管理员账号为后台管理专用，不可参与前端借阅流转！");
    }

    // 信用分检查
    if (currentUser != null && currentUser.getCreditScore() != null && currentUser.getCreditScore() < 60) {
        return R.error("您的信用分低于 60 分，已被限制借阅！如有特殊情况请线下联系管理员。");
    }

    BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
    if (book == null) return R.error("图书不存在");

    // 禁止自己借阅自己的书籍
    if (book.getUploaderid() != null && book.getUploaderid().equals(borrow.getUserid())) {
        return R.error("不能申请借阅自己发布（或当前持有）的书籍哦！");
    }

    if (book.getInventory() <= 0) {
        return R.error("当前图书库存不足，正在漂流中");
    }

    borrow.setState(0); // 0-待审核
    borrow.setApplytime(new Date());
    if (borrow.getBorrowreason() == null) borrow.setBorrowreason("用户申请漂流");

    int row = borrowMapper.insert(borrow);

    // 通知发布者
    if (row > 0 && book.getUploaderid() != null) {
        String msgContent = "【系统通知】有同学申请借阅了您发布的书籍《" + book.getBookname() + "》！\n" +
                "TA 的申请理由是：[" + borrow.getBorrowreason() + "]\n" +
                "目前正在等待管理员审核，请留意后续通知。";
        messageMapper.insert(new Message(book.getUploaderid(), msgContent));

        // 微信推送
        User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
        if (uploader != null && uploader.getOpenId() != null) {
            WechatPushUtils.pushMessage(
                    uploader.getOpenId(),
                    "📚 新的漂流申请",
                    "您好！您发布的《" + book.getBookname() + "》有人申请啦！<br/>留言：" + borrow.getBorrowreason() + "<br/>请尽快登录系统进行审核！"
            );
        }
    }

    return row > 0 ? R.ok("漂流申请已提交，请等待发布者审核") : R.error("申请失败");
}
```

**文件：** `src/main/resources/mapper/BorrowMapper.xml`

**关键SQL：** `insert`

```xml
<insert id="insert" parameterType="com.shanzhu.book.model.Borrow">
    insert into borrow (userId, bookId, applyTime, borrowReason, state, borrowTime, borrow_days)
    values (#{userid}, #{bookid}, NOW(), #{borrowreason}, #{state}, #{borrowtime}, #{borrowDays})
</insert>
```

---

### 5.5 审核申请功能

#### 5.5.1 前端代码

**文件：** `src/views/borrow/index.vue`

**关键方法：** `handleAudit()`

```javascript
handleAudit(row, targetState) {
  if (targetState === 3) {
    this.$prompt('请输入委婉拒绝的理由：', '驳回申请', { 
      confirmButtonText: '驳回', 
      cancelButtonText: '取消', 
      type: 'warning' 
    }).then(({ value }) => {
      request({ 
        url: '/borrow/audit', 
        method: 'post', 
        params: { borrowId: row.borrowid, state: targetState, feedback: value || '发布者暂时无法出借' } 
      }).then(res => {
        if (res.code === 0 || res === 1) { 
          this.$message.success('已驳回。'); 
          this.getList(); 
        } else { 
          this.$message.error(res.msg); 
        }
      });
    });
  } else {
    this.$confirm(`确认同意借出该请求吗？`, '提示', { type: 'success' }).then(() => {
      request({ 
        url: '/borrow/audit', 
        method: 'post', 
        params: { borrowId: row.borrowid, state: targetState } 
      }).then(res => {
        if (res.code === 0 || res === 1) { 
          this.$message.success('审批通过！已成功通知对方。'); 
          this.getList(); 
        } else { 
          this.$message.error(res.msg); 
        }
      });
    });
  }
}
```

#### 5.5.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/BorrowController.java`

**关键方法：** `auditBorrow()`

```java
@PostMapping("/audit")
public R auditBorrow(@RequestParam Integer borrowId,
                     @RequestParam Integer state,
                     @RequestParam(required = false) String feedback) {
    return borrowService.auditBorrow(borrowId, state, feedback);
}
```

**文件：** `src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java`

**关键方法：** `auditBorrow()`

```java
@Override
@Transactional
public R auditBorrow(Integer borrowId, Integer state, String feedback) {
    Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
    if (borrow == null) return R.error("记录不存在");
    BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());

    if (state == 1) { // 审核通过
        if (book.getInventory() <= 0) return R.error("审核失败：库存不足");

        book.setInventory(book.getInventory() - 1);
        if (book.getInventory() == 0) book.setIsborrowed(1);
        bookInfoMapper.updateByPrimaryKeySelective(book);

        // 生成6位随机暗号
        String secretCode = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        borrow.setState(4); // 4-待交接

        // 同意的瞬间，将applytime刷新为现在！让3天交接倒计时重新开始算
        borrow.setApplytime(new Date());

        borrow.setSecretCode(secretCode);
        borrowMapper.updateByPrimaryKeySelective(borrow);

        String contact = (book.getContactinfo() != null && !book.getContactinfo().isEmpty()) ? book.getContactinfo() : "无特殊说明，请当面沟通。";
        String msgToApplicant = "【系统通知】你的漂流申请《" + book.getBookname() + "》已通过！\n" +
                "对方交接说明：【" + contact + "】\n" +
                "你的专属提货暗号为：【" + secretCode + "】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。";
        messageMapper.insert(new Message(borrow.getUserid(), msgToApplicant));

        User applyUser = userMapper.selectByPrimaryKey(borrow.getUserid());
        if (applyUser != null && applyUser.getOpenId() != null) {
            WechatPushUtils.pushMessage(applyUser.getOpenId(), "✅ 审核通过通知", "您对《" + book.getBookname() + "》的借阅申请已通过！请准备线下交接。");
        }

        if (book.getUploaderid() != null) {
            String msgToUploader = "【系统通知】您发布的书籍《" + book.getBookname() + "》借阅申请已通过审核！请等待交接。";
            messageMapper.insert(new Message(book.getUploaderid(), msgToUploader));
        }
        return R.ok("审核通过，已下发交接暗号，倒计时重置！");
    } else {
        borrow.setState(3); // 3-驳回
        String reason = feedback != null ? feedback : "无";
        borrow.setReturnmsg("【发布者驳回】" + reason);
        borrowMapper.updateByPrimaryKeySelective(borrow);

        Message msg = new Message(borrow.getUserid(), "【系统通知】您的申请《" + book.getBookname() + "》被驳回。原因：" + reason);
        messageMapper.insert(msg);
        User applyUser = userMapper.selectByPrimaryKey(borrow.getUserid());
        if (applyUser != null && applyUser.getOpenId() != null) {
            WechatPushUtils.pushMessage(applyUser.getOpenId(), "❌ 审核驳回通知", "对《" + book.getBookname() + "》的申请被拒绝。<br/>原因：" + reason);
        }
        return R.ok("已驳回申请");
    }
}
```

---

### 5.6 交接暗号验证功能

#### 5.6.1 前端代码

**文件：** `src/views/borrow/index.vue`

**关键方法：** `submitHandover()`

```javascript
submitHandover() {
  if (this.inputSecretCode.length !== 6) return this.$message.warning("请输入完整的 6 位交接暗号！");
  this.submitLoading = true;
  request({ 
    url: '/borrow/verifyCode', 
    method: 'post', 
    params: { borrowId: this.currentRowId, secretCode: this.inputSecretCode } 
  }).then(res => {
    this.submitLoading = false;
    if (res.code === 0 || res === 1) { 
      this.$message.success("🎉 暗号核销正确！交接成功！您的信用分已获奖励！"); 
      this.dialogVisible = false; 
      this.getList(); 
    }
    else { 
      this.$message.error(res.msg || "❌ 拦截失败！" + res.msg); 
    }
  }).catch(() => { this.submitLoading = false; });
}
```

#### 5.6.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/BorrowController.java`

**关键方法：** `verifyCode()`

```java
@PostMapping("/verifyCode")
public R verifyCode(@RequestParam Integer borrowId, @RequestParam String secretCode) {
    return borrowService.verifyCode(borrowId, secretCode);
}
```

**文件：** `src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java`

**关键方法：** `verifyCode()`

```java
@Override
@Transactional
public R verifyCode(Integer borrowId, String secretCode) {
    Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
    if (borrow == null) return R.error("记录不存在");
    if (borrow.getState() != 4) return R.error("当前状态不支持验证暗号");
    if (borrow.getSecretCode() == null || !borrow.getSecretCode().equals(secretCode)) {
        return R.error("暗号错误，请核对后再试！");
    }

    // 漏洞防范：核销时如果变成老赖，直接阻断！
    User borrower = userMapper.selectByPrimaryKey(borrow.getUserid());
    if (borrower != null && borrower.getCreditScore() != null && borrower.getCreditScore() < 60) {
        return R.error("阻断交易！该借阅者近期违规信用已跌破60，系统强制终止交接！");
    }

    borrow.setState(1);
    borrow.setBorrowtime(new Date());
    borrowMapper.updateByPrimaryKeySelective(borrow);

    // 成功交接回血策略
    BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
    if (book != null && book.getUploaderid() != null) {
        User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
        if (uploader != null && (uploader.getCreditScore() == null || uploader.getCreditScore() < 100)) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String todayStr = sdf.format(new java.util.Date());
            String updateStr = uploader.getScoreUpdateDate() == null ? "" : sdf.format(uploader.getScoreUpdateDate());

            int todayAdded = uploader.getTodayAddScore() == null ? 0 : uploader.getTodayAddScore();
            if (!todayStr.equals(updateStr)) {
                todayAdded = 0;
                uploader.setScoreUpdateDate(new java.util.Date());
            }

            int currentScore = uploader.getCreditScore() == null ? 100 : uploader.getCreditScore();
            if (todayAdded < 5 && currentScore < 100) {
                // 交接成功理论加3分
                int canAdd = Math.min(3, 5 - todayAdded);
                int finalScore = Math.min(100, currentScore + canAdd);
                int actualAdded = finalScore - currentScore;

                if (actualAdded > 0) {
                    uploader.setCreditScore(finalScore);
                    uploader.setTodayAddScore(todayAdded + actualAdded);
                    userMapper.updateByPrimaryKeySelective(uploader);
                    messageMapper.insert(new Message(uploader.getUserid(), "【信用奖励】成功交接给下一位书友！本次恢复 " + actualAdded + " 分 (今日已累计恢复 " + (todayAdded + actualAdded) + "/5 分)，当前：" + finalScore + " 分。"));
                }
            }
        }
    }
    return R.ok("暗号正确！交接成功，书籍正式进入漂流中。");
}
```

---

### 5.7 归还/传递图书功能

#### 5.7.1 前端代码

**文件：** `src/views/borrow/index.vue`

**关键方法：** `submitReturn()`

```javascript
submitReturn() {
  if(!this.returnForm.returnMsg) return this.$message.warning("写点感悟吧！");
  if(!this.returnForm.contactInfo) return this.$message.warning("请留下交接说明！");
  request({ 
    url: '/borrow/returnBook', 
    method: 'post', 
    params: { 
      borrowId: this.returnForm.borrowId, 
      bookId: this.returnForm.bookId, 
      returnMsg: this.returnForm.returnMsg, 
      contactInfo: this.returnForm.contactInfo 
    } 
  }).then(res => {
    if(res.code === 0 || res === 1) { 
      this.$message.success("传递成功！"); 
      this.returnDialogVisible = false; 
      this.getList(); 
    } else { 
      this.$message.error(res.msg); 
    }
  });
}
```

#### 5.7.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/BorrowController.java`

**关键方法：** `returnBook()`

```java
@PostMapping("/returnBook")
public R returnBook(@RequestParam Integer borrowId, @RequestParam Integer bookId, 
                    @RequestParam String returnMsg, @RequestParam(defaultValue = "") String contactInfo) {
    int result = borrowService.returnBook(borrowId, bookId, returnMsg, contactInfo);
    if (result > 0) {
        return R.ok();
    }
    return R.error("操作失败");
}
```

**文件：** `src/main/java/com/shanzhu/book/service/impl/BorrowServiceImpl.java`

**关键方法：** `returnBook()`

```java
@Override
@Transactional
public Integer returnBook(Integer borrowId, Integer bookId, String returnMsg, String contactInfo) {
    Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
    if (borrow == null || borrow.getState() != 1) return 0;

    borrow.setState(2);
    borrow.setReturntime(new Date());
    borrow.setReturnmsg(returnMsg);
    borrowMapper.updateByPrimaryKeySelective(borrow);

    BookInfo book = bookInfoMapper.selectByPrimaryKey(bookId);
    if (book != null) {
        Integer oldUploaderId = book.getUploaderid();
        book.setInventory(book.getInventory() + 1);
        if (book.getInventory() > 0) book.setIsborrowed(0);

        // 书籍所有权转移！
        book.setUploaderid(borrow.getUserid());

        if (contactInfo != null && !contactInfo.trim().isEmpty()) {
            book.setContactinfo(contactInfo);
        } else {
            book.setContactinfo("新主人暂未留下具体地址，请申请后通过系统留言沟通。");
        }
        bookInfoMapper.updateByPrimaryKeySelective(book);

        String newOwnerMsg = "【漂流接力】恭喜！您已读完《" + book.getBookname() + "》。\n" +
                "由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n" +
                "等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！";
        messageMapper.insert(new Message(borrow.getUserid(), newOwnerMsg));

        if (oldUploaderId != null && !oldUploaderId.equals(borrow.getUserid())) {
            messageMapper.insert(new Message(oldUploaderId, "【系统通知】您最初发布的《" + book.getBookname() + "》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！"));
        }
    }
    return 1;
}
```

---

### 5.8 用户管理功能（管理员）

#### 5.8.1 前端代码

**文件：** `src/views/user/index.vue`

**关键方法：** `getList()`, `createData()`, `updateData()`, `handleDelete()`

```javascript
getList() {
  this.listLoading = true
  queryUsersByPage(this.listQuery).then(res => {
    if(res.data) { this.list = res.data; this.total = res.count || res.total || 0 }
    else if(res.rows) { this.list = res.rows; this.total = res.total }
    else { this.list = res; this.total = res.length }
    this.listLoading = false
  })
}

createData() {
  this.$refs['dataForm'].validate((valid) => {
    if (valid) {
      this.temp.userpassword = '123456'
      addUser(this.temp).then(() => {
        this.list.unshift(this.temp)
        this.dialogFormVisible = false
        this.$message.success('添加成功，初始密码 123456')
        this.getList()
      })
    }
  })
}
```

#### 5.8.2 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/UserController.java`

**关键方法：** `queryUsersByPage()`, `addUser()`, `updateUser()`, `deleteUser()`

```java
@GetMapping(value = "/queryUsersByPage")
public Map<String, Object> queryUsersByPage(@RequestParam Map<String, Object> params) {
    PageUtils.parsePageParams(params);
    int count = userService.getSearchCount(params);
    List<User> list = userService.searchUsersByPage(params);
    return R.getListResultMap(0, "success", count, list);
}

@PostMapping(value = "/addUser")
public Integer addUser(@RequestBody User user) {
    if(user.getStatus() == null) user.setStatus(1);
    if(user.getIsadmin() == null) user.setIsadmin(0);
    return userService.addUser(user);
}
```

---

### 5.9 心愿模块功能

#### 5.9.1 后端代码

**文件：** `src/main/java/com/shanzhu/book/web/BookWishController.java`

```java
@RestController
@RequestMapping("/wish")
public class BookWishController {

    @Resource
    private BookWishMapper bookWishMapper;

    @PostMapping("/add")
    public Integer addWish(@RequestBody BookWish wish) {
        return bookWishMapper.insertWish(wish);
    }

    @GetMapping("/list")
    public List<BookWish> getWishList() {
        return bookWishMapper.selectAllWishes();
    }

    @PostMapping("/fulfill")
    public Integer fulfillWish(Integer wishId, Integer fulfillUserId) {
        return bookWishMapper.fulfillWish(wishId, fulfillUserId);
    }

    @PostMapping("/delete")
    public Integer deleteWish(Integer wishId) {
        return bookWishMapper.deleteWish(wishId);
    }
}
```

---

## 六、代码联动逻辑详解

### 6.1 用户登录完整联动流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           用户登录完整流程                                    │
└─────────────────────────────────────────────────────────────────────────────┘

【第一步：用户操作】
用户在登录页面输入学号和密码，点击"安全登录"按钮

【第二步：前端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/views/login/index.vue                                              │
│ 方法：handleLogin()                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ handleLogin() {                                                              │
│   this.$refs.loginForm.validate(valid => {                                   │
│     if (valid) {                                                             │
│       this.loading = true                                                    │
│       this.$store.dispatch('user/login', this.loginForm)  ← 调用Vuex        │
│     }                                                                        │
│   })                                                                         │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/store/modules/user.js                                              │
│ 方法：login action                                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ login({ commit }, userInfo) {                                                │
│   const { username, password } = userInfo                                    │
│   const loginParams = {                                                      │
│     username: username.trim(),                                               │
│     userpassword: password                                                   │
│   }                                                                          │
│   login(loginParams).then(response => {  ← 调用API                           │
│     const { code, data } = response                                          │
│     if (code === 0) {                                                        │
│       commit('SET_TOKEN', token)  ← 保存Token到Vuex                          │
│       setToken(token)  ← 保存Token到Cookie                                   │
│     }                                                                        │
│   })                                                                         │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/api/user.js                                                        │
│ 方法：login()                                                                │
├─────────────────────────────────────────────────────────────────────────────┤
│ export function login(data) {                                                │
│   return request({                                                           │
│     url: '/user/login',                                                      │
│     method: 'post',                                                          │
│     data   ← 发送 { username: "学号", userpassword: "密码" }                  │
│   })                                                                         │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/utils/request.js                                                   │
│ 说明：Axios请求拦截器，自动添加baseURL                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ const service = axios.create({                                               │
│   baseURL: 'http://localhost:9111/BookManager/',                             │
│   timeout: 50000                                                             │
│ })                                                                           │
│ 实际请求：POST http://localhost:9111/BookManager/user/login                  │
└─────────────────────────────────────────────────────────────────────────────┘

【第三步：后端处理】
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：UserController.java                                                    │
│ 方法：login()                                                                │
├─────────────────────────────────────────────────────────────────────────────┤
│ @PostMapping(value = "/login")                                               │
│ public R login(@RequestBody User user) {                                     │
│     if(user.getStudentid() == null) user.setStudentid(user.getUsername());   │
│     return userService.userLogin(user);  ← 调用Service                       │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：UserServiceImpl.java                                                   │
│ 方法：userLogin()                                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│ public R userLogin(User user) {                                              │
│     // 1. 调用Mapper查询用户                                                 │
│     User dbUser = userMapper.selectByUserName(user.getStudentid());          │
│                                                                              │
│     // 2. 验证密码                                                           │
│     if(dbUser == null || !dbUser.getUserpassword().equals(...)) {            │
│         return R.error("账号或密码错误");                                     │
│     }                                                                        │
│                                                                              │
│     // 3. 检查状态                                                           │
│     if(dbUser.getStatus() != 1) {                                            │
│         return R.error("账号已被禁用");                                       │
│     }                                                                        │
│                                                                              │
│     // 4. 生成Token                                                          │
│     String token = TokenProcessor.getInstance().generateToken();             │
│                                                                              │
│     // 5. 存储到内存Map                                                      │
│     TokenProcessor.getInstance().saveUser(token, dbUser);                    │
│                                                                              │
│     // 6. 返回结果                                                           │
│     return R.ok().put("data", token);                                        │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：UserMapper.xml                                                         │
│ SQL：selectByUserName                                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ <select id="selectByUserName" resultMap="BaseResultMap">                     │
│     select userId, userName, studentId, userPassword, isAdmin, status,       │
│            gender, open_id, department, credit_score,                        │
│            today_add_score, score_update_date                                │
│     from user                                                                │
│     where studentId = #{username}                                            │
│ </select>                                                                    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
                              MySQL数据库
                    执行SQL查询，返回用户记录

【第四步：响应返回】
                                    ↓
后端返回JSON：{ "code": 0, "msg": "success", "data": "生成的Token字符串" }
                                    ↓
前端接收响应，保存Token到Cookie和Vuex
                                    ↓
前端跳转到首页
```

### 6.2 申请漂流完整联动流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          申请漂流完整流程                                     │
└─────────────────────────────────────────────────────────────────────────────┘

【第一步：用户操作】
用户在图书列表页面点击"申请漂流"按钮，填写申请理由和借阅天数，点击"确认申请"

【第二步：前端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/views/bookinfo/index.vue                                           │
│ 方法：submitBorrow()                                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ submitBorrow() {                                                             │
│   if (!this.borrowForm.borrowreason.trim()) {                                │
│     return this.$message.warning("申请理由不能为空哦！");                     │
│   }                                                                          │
│   this.borrowLoading = true;                                                 │
│   request({                                                                  │
│     url: '/borrow/borrowBook',                                               │
│     method: 'post',                                                          │
│     data: {                                                                  │
│       userid: this.id,                                                       │
│       bookid: this.borrowForm.bookid,                                        │
│       borrowreason: this.borrowForm.borrowreason,                            │
│       borrowDays: this.borrowForm.borrowDays                                 │
│     }                                                                        │
│   }).then(res => {                                                           │
│     if (res.code === 0 || res === 1) {                                       │
│       this.$message.success("🎉 申请成功！");                                 │
│     }                                                                        │
│   })                                                                         │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第三步：后端处理】
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowController.java                                                  │
│ 方法：borrowBook()                                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ @RequestMapping("/borrowBook")                                               │
│ public R borrowBook(@RequestParam Integer userid,                            │
│                     @RequestParam Integer bookid,                            │
│                     @RequestBody Map<String, Object> body) {                 │
│     Borrow borrow = new Borrow();                                            │
│     borrow.setUserid(userid);                                                │
│     borrow.setBookid(bookid);                                                │
│     borrow.setBorrowreason(body.get("borrowreason").toString());             │
│     borrow.setBorrowDays(Integer.parseInt(body.get("borrowDays").toString()));│
│     return borrowService.addBorrow(borrow);                                  │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowServiceImpl.java                                                 │
│ 方法：addBorrow()                                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│ public R addBorrow(Borrow borrow) {                                          │
│     // 1. 检查用户信用分（<60禁止借阅）                                       │
│     User currentUser = userMapper.selectByPrimaryKey(borrow.getUserid());    │
│     if(currentUser.getCreditScore() < 60) {                                  │
│         return R.error("您的信用分过低，已被限制借阅！");                      │
│     }                                                                        │
│                                                                              │
│     // 2. 检查图书库存                                                       │
│     BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());   │
│     if(book.getInventory() <= 0) {                                           │
│         return R.error("该图书已无库存！");                                   │
│     }                                                                        │
│                                                                              │
│     // 3. 创建借阅记录（状态：待审核）                                        │
│     borrow.setState(0);                                                      │
│     borrowMapper.insert(borrow);                                             │
│                                                                              │
│     // 4. 发送站内消息通知发布者                                              │
│     Message msg = new Message(book.getUploaderid(),                          │
│         "有人申请借阅您的《" + book.getBookname() + "》，请及时审核。");       │
│     messageMapper.insert(msg);                                               │
│                                                                              │
│     // 5. 微信推送通知发布者                                                  │
│     User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());     │
│     WechatPushUtils.pushMessage(uploader.getOpenId(), "借阅申请通知",         │
│         "有人申请借阅您的《" + book.getBookname() + "》");                     │
│                                                                              │
│     return R.ok("申请成功，请等待审核");                                       │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowMapper.xml                                                       │
│ SQL：insert                                                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│ <insert id="insert" parameterType="com.shanzhu.book.model.Borrow">           │
│     insert into borrow (userId, bookId, applyTime, borrowReason, state,      │
│                         borrow_days)                                         │
│     values (#{userid}, #{bookid}, NOW(), #{borrowreason}, #{state},          │
│             #{borrowDays})                                                   │
│ </insert>                                                                    │
└─────────────────────────────────────────────────────────────────────────────┘

【第四步：数据库变化】
- borrow表新增一条记录，state=0（待审核）
- message表新增一条通知消息

【第五步：响应返回】
后端返回：{ "code": 0, "msg": "申请成功，请等待审核" }
前端显示成功提示，刷新图书列表
```

### 6.3 审核申请并生成暗号完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      审核申请并生成暗号完整流程                                │
└─────────────────────────────────────────────────────────────────────────────┘

【第一步：发布者操作】
发布者在"我借出的"标签页看到申请，点击"同意借出"或"委婉拒绝"

【第二步：前端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/views/borrow/index.vue                                             │
│ 方法：handleAudit()                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ handleAudit(row, targetState) {                                              │
│   if (targetState === 3) {                                                   │
│     // 驳回：弹出输入框填写驳回理由                                           │
│     this.$prompt('请输入委婉拒绝的理由：', '驳回申请', {...}).then(({value})=>{│
│       request({                                                              │
│         url: '/borrow/audit',                                                │
│         method: 'post',                                                      │
│         params: { borrowId: row.borrowid, state: 3, feedback: value }        │
│       })                                                                     │
│     })                                                                       │
│   } else {                                                                   │
│     // 同意：弹出确认框                                                       │
│     this.$confirm('确认同意借出该请求吗？', '提示', {...}).then(() => {        │
│       request({                                                              │
│         url: '/borrow/audit',                                                │
│         method: 'post',                                                      │
│         params: { borrowId: row.borrowid, state: 1 }                         │
│       })                                                                     │
│     })                                                                       │
│   }                                                                          │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第三步：后端处理】
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowController.java                                                  │
│ 方法：auditBorrow()                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ @PostMapping("/audit")                                                       │
│ public R auditBorrow(@RequestParam Integer borrowId,                         │
│                      @RequestParam Integer state,                            │
│                      @RequestParam(required = false) String feedback) {      │
│     return borrowService.auditBorrow(borrowId, state, feedback);             │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowServiceImpl.java                                                 │
│ 方法：auditBorrow()                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ public R auditBorrow(Integer borrowId, Integer state, String feedback) {     │
│     Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);               │
│                                                                              │
│     if(state == 1) {                                                         │
│         // 同意借出：生成6位随机暗号                                          │
│         String secretCode = String.valueOf((int)((Math.random() * 9 + 1)     │
│             * 100000));                                                      │
│         borrow.setSecretCode(secretCode);                                    │
│         borrow.setState(4);  // 状态改为"待交接"                              │
│         borrow.setApplytime(new Date()); // 重置倒计时                        │
│                                                                              │
│         // 发送站内消息通知申请人                                             │
│         Message msg = new Message(borrow.getUserid(),                        │
│             "您的漂流申请《" + bookName + "》已通过审核，" +                   │
│             "暗号：" + secretCode + "，请联系发布者交接。");                   │
│         messageMapper.insert(msg);                                           │
│                                                                              │
│         // 微信推送                                                           │
│         WechatPushUtils.pushMessage(openId, "审核通过通知",                   │
│             "暗号：" + secretCode);                                           │
│     } else {                                                                 │
│         // 驳回：退回库存                                                     │
│         borrow.setState(3);                                                  │
│         borrow.setReturnmsg("【发布者驳回】" + feedback);                     │
│         BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());│
│         book.setInventory(book.getInventory() + 1);                          │
│         bookInfoMapper.updateByPrimaryKeySelective(book);                    │
│     }                                                                        │
│                                                                              │
│     borrowMapper.updateByPrimaryKeySelective(borrow);                        │
│     return R.ok();                                                           │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第四步：数据库变化】
同意时：
- borrow表的state改为4（待交接），secretCode填入6位暗号
- message表新增通知消息

驳回时：
- borrow表的state改为3（已驳回），returnmsg填入驳回理由
- book_info表的inventory加1（退回库存）

【第五步：借阅者收到通知】
借阅者在"我借入的"页面看到状态变为"待交接"，显示6位暗号
```

### 6.4 交接暗号验证完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        交接暗号验证完整流程                                    │
└─────────────────────────────────────────────────────────────────────────────┘

【第一步：线下交接】
借阅者找到发布者，出示6位暗号

【第二步：发布者输入暗号】
发布者在"核销提书暗号"弹窗中输入借阅者出示的6位数字

【第三步：前端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/views/borrow/index.vue                                             │
│ 方法：submitHandover()                                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│ submitHandover() {                                                           │
│   if (this.inputSecretCode.length !== 6) {                                   │
│     return this.$message.warning("请输入完整的 6 位交接暗号！");               │
│   }                                                                          │
│   request({                                                                  │
│     url: '/borrow/verifyCode',                                               │
│     method: 'post',                                                          │
│     params: { borrowId: this.currentRowId, secretCode: this.inputSecretCode }│
│   }).then(res => {                                                           │
│     if (res.code === 0) {                                                    │
│       this.$message.success("🎉 暗号核销正确！交接成功！");                    │
│     } else {                                                                 │
│       this.$message.error(res.msg);                                          │
│     }                                                                        │
│   })                                                                         │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第四步：后端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowServiceImpl.java                                                 │
│ 方法：verifyCode()                                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ public R verifyCode(Integer borrowId, String secretCode) {                   │
│     Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);               │
│                                                                              │
│     // 1. 验证暗号是否正确                                                    │
│     if(!secretCode.equals(borrow.getSecretCode())) {                         │
│         return R.error("暗号错误！请核对后重试。");                            │
│     }                                                                        │
│                                                                              │
│     // 2. 检查借阅者信用分（<60阻断交易）                                      │
│     User borrower = userMapper.selectByPrimaryKey(borrow.getUserid());       │
│     if(borrower.getCreditScore() < 60) {                                     │
│         return R.error("阻断交易！该借阅者信用已跌破60！");                    │
│     }                                                                        │
│                                                                              │
│     // 3. 更新状态为"漂流中"                                                  │
│     borrow.setState(1);                                                      │
│     borrow.setBorrowtime(new Date());  // 记录实际交接时间                    │
│     borrowMapper.updateByPrimaryKeySelective(borrow);                        │
│                                                                              │
│     // 4. 给发布者增加信用分（+3分，每日上限5分）                              │
│     BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());   │
│     User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());     │
│     // ... 信用分恢复逻辑 ...                                                 │
│                                                                              │
│     return R.ok("交接成功！");                                                 │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第五步：数据库变化】
- borrow表的state改为1（漂流中），borrowtime填入当前时间
- user表的creditScore增加3分（发布者奖励，每日上限5分）

【第六步：结果展示】
发布者看到"交接成功"提示
借阅者看到状态变为"正在阅读中"
```

### 6.5 定时任务自动处理流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        定时任务自动处理流程                                    │
└─────────────────────────────────────────────────────────────────────────────┘

【触发条件】
系统每1分钟自动执行一次

【代码实现】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowTask.java                                                        │
│ 方法：systemPatrolTask()                                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ @Scheduled(cron = "0 */1 * * * ?")  // 每1分钟执行                           │
│ public void systemPatrolTask() {                                             │
│     System.out.println("【系统巡查卫士】开始执行自动化风控巡查...");           │
│                                                                              │
│     // 获取所有借阅记录                                                       │
│     List<Borrow> allBorrows = borrowMapper.selectAllByLimit(allParams);      │
│                                                                              │
│     for (Borrow borrow : allBorrows) {                                       │
│         // 1. 处理超时未审核(0)和超时未交接(4)的记录                           │
│         if ((borrow.getState() == 0 || borrow.getState() == 4)               │
│             && borrow.getApplytime() != null) {                              │
│                                                                              │
│             long timeout = 3L * 24 * 60 * 60 * 1000;  // 3天                 │
│             // 测试模式：申请理由包含"测试"则3分钟超时                          │
│             if (borrow.getBorrowreason() != null                             │
│                 && borrow.getBorrowreason().contains("测试")) {               │
│                 timeout = 3L * 60 * 1000;  // 3分钟                          │
│             }                                                                │
│                                                                              │
│             if (now - borrow.getApplytime().getTime() > timeout) {           │
│                 // 自动取消                                                   │
│                 borrow.setState(7);  // 已失效                               │
│                 borrow.setReturnmsg("【系统强制取消】超出处理期限...");         │
│                 borrowMapper.updateByPrimaryKeySelective(borrow);            │
│                                                                              │
│                 // 如果是待交接超时，退回库存                                  │
│                 if (oldState == 4) {                                         │
│                     book.setInventory(book.getInventory() + 1);              │
│                     bookInfoMapper.updateByPrimaryKeySelective(book);        │
│                 }                                                            │
│                                                                              │
│                 // 发送站内消息通知                                           │
│                 messageMapper.insert(new Message(userId, "系统通知..."));     │
│             }                                                                │
│         }                                                                    │
│                                                                              │
│         // 2. 处理逾期未还(1)的记录，每日扣分                                  │
│         if (borrow.getState() == 1 && borrow.getBorrowtime() != null) {      │
│             int allowDays = borrow.getBorrowDays() != null                   │
│                 ? borrow.getBorrowDays() : 30;                               │
│             long borrowedMillis = now - borrow.getBorrowtime().getTime();    │
│             long allowMillis = allowDays * 24 * 60 * 60 * 1000;              │
│                                                                              │
│             if (borrowedMillis > allowMillis) {                              │
│                 // 扣除2分信用分                                              │
│                 User borrower = userMapper.selectByPrimaryKey(userId);       │
│                 int newScore = Math.max(0, borrower.getCreditScore() - 2);   │
│                 borrower.setCreditScore(newScore);                           │
│                 userMapper.updateByPrimaryKeySelective(borrower);            │
│                                                                              │
│                 // 发送扣分通知                                               │
│                 messageMapper.insert(new Message(userId,                     │
│                     "【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。"));    │
│             }                                                                │
│         }                                                                    │
│     }                                                                        │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【处理结果】
1. 超时未审核/未交接：自动取消，状态改为7（已失效）
2. 逾期未还：每日扣2分信用分，发送通知
```

### 6.6 归还/传递图书完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        归还/传递图书完整流程                                    │
└─────────────────────────────────────────────────────────────────────────────┘

【第一步：借阅者操作】
借阅者读完书后，在"我借入的"页面点击"归还/传递"按钮

【第二步：前端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：src/views/borrow/index.vue                                             │
│ 方法：submitReturn()                                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ submitReturn() {                                                             │
│   if(!this.returnForm.returnMsg) {                                           │
│     return this.$message.warning("写点感悟吧！");                             │
│   }                                                                          │
│   if(!this.returnForm.contactInfo) {                                         │
│     return this.$message.warning("请留下交接说明！");                         │
│   }                                                                          │
│   request({                                                                  │
│     url: '/borrow/returnBook',                                               │
│     method: 'post',                                                          │
│     params: {                                                                │
│       borrowId: this.returnForm.borrowId,                                    │
│       bookId: this.returnForm.bookId,                                        │
│       returnMsg: this.returnForm.returnMsg,                                  │
│       contactInfo: this.returnForm.contactInfo                               │
│     }                                                                        │
│   }).then(res => {                                                           │
│     if(res.code === 0 || res === 1) {                                        │
│       this.$message.success("传递成功！");                                    │
│     }                                                                        │
│   })                                                                         │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第三步：后端处理】
┌─────────────────────────────────────────────────────────────────────────────┐
│ 文件：BorrowServiceImpl.java                                                 │
│ 方法：returnBook()                                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ public Integer returnBook(Integer borrowId, Integer bookId,                  │
│                          String returnMsg, String contactInfo) {             │
│     Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);               │
│                                                                              │
│     // 1. 更新借阅记录状态                                                    │
│     borrow.setState(2);  // 已归还                                           │
│     borrow.setReturntime(new Date());                                        │
│     borrow.setReturnmsg(returnMsg);                                          │
│     borrowMapper.updateByPrimaryKeySelective(borrow);                        │
│                                                                              │
│     // 2. 更新图书信息（核心：所有权转移！）                                   │
│     BookInfo book = bookInfoMapper.selectByPrimaryKey(bookId);               │
│     book.setInventory(book.getInventory() + 1);  // 恢复库存                 │
│     book.setIsborrowed(0);  // 标记为有货                                     │
│     book.setUploaderid(borrow.getUserid());  // 所有权转移给借阅者！          │
│     book.setContactinfo(contactInfo);  // 更新交接联系方式                    │
│     bookInfoMapper.updateByPrimaryKeySelective(book);                        │
│                                                                              │
│     // 3. 发送通知                                                           │
│     String msg = "【漂流接力】恭喜！您已读完《" + book.getBookname() +        │
│         "》。书本现在由您保管，等待下一位有缘人申请！";                         │
│     messageMapper.insert(new Message(borrow.getUserid(), msg));              │
│                                                                              │
│     return 1;                                                                │
│ }                                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

【第四步：数据库变化】
- borrow表的state改为2（已归还），returntime和returnmsg更新
- book_info表的uploaderid改为借阅者ID（所有权转移！）
- book_info表的contactinfo更新为新的交接方式

【第五步：结果展示】
借阅者收到"漂流接力"通知，该书出现在TA的"我借出的"列表中
原发布者收到"接力成功"通知
```

---

## 七、信用分系统详解

### 7.1 信用分机制说明

| 场景 | 信用分变化 | 说明 |
|------|-----------|------|
| 初始状态 | 100分 | 新用户默认100分 |
| 逾期未还 | -2分/天 | 每日自动扣除 |
| 交接成功 | +3分 | 发布者/持有者获得奖励 |
| 每日恢复上限 | 5分 | 同一天最多恢复5分 |
| 信用分<60 | 禁止借阅 | 被系统限制 |

### 7.2 信用分相关代码

**文件：** `User.java`（实体类）

```java
@Data
public class User implements Serializable {
    private Integer userid;
    private String username;
    private String studentid;
    private String userpassword;
    private Integer isadmin;
    private Integer status;
    private Integer gender;
    private String openId;
    private String department;
    private Integer creditScore;        // 信用分
    private Integer todayAddScore;      // 今日已加分
    private java.util.Date scoreUpdateDate;  // 加分日期
}
```

**文件：** `UserMapper.xml`（数据库映射）

```xml
<result column="credit_score" jdbcType="INTEGER" property="creditScore" />
<result column="today_add_score" jdbcType="INTEGER" property="todayAddScore" />
<result column="score_update_date" jdbcType="DATE" property="scoreUpdateDate" />
```

---

## 八、微信推送功能详解

### 8.1 微信推送场景

| 场景 | 推送对象 | 推送内容 |
|------|----------|----------|
| 有人申请借书 | 发布者 | 书名、申请理由 |
| 审核通过 | 申请人 | 书名、交接暗号 |
| 审核驳回 | 申请人 | 书名、驳回原因 |
| 交接协商 | 发布者 | 协商留言 |
| 书籍报损 | 发布者 | 书名、原因 |
| 一键催还 | 借阅者 | 书名、逾期提醒 |

### 8.2 微信推送代码

**文件：** `WechatPushUtils.java`

```java
public class WechatPushUtils {
    private static final String APP_TOKEN = "你的WxPusher Token";
    
    public static void pushMessage(String openId, String title, String content) {
        // 调用WxPusher API发送微信消息
        // ...
    }
}
```

---

## 九、项目配置说明

### 9.1 后端配置 (application.yml)

```yaml
server:
  port: 9111                    # 后端服务端口
  servlet:
    context-path: /BookManager  # 项目访问路径前缀

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/db_book
    username: root
    password: 123456
  redis:
    host: localhost
    port: 6379

mybatis:
  mapper-locations: classpath:mapper/*.xml  # Mapper XML文件位置
```

### 9.2 前端配置

**API基础路径：** `src/utils/request.js`
```javascript
baseURL: 'http://localhost:9111/BookManager/'
```

**路由配置：** `src/router/index.js`
- 公共路由：登录、注册、首页
- 动态路由：根据角色动态加载（管理员/普通用户）

---

## 十、总结

本项目是一个功能完善的校园旧书漂流共享系统，实现了：

1. **用户管理**：登录、注册、权限控制、信用分管理、微信绑定
2. **图书管理**：发布、搜索、编辑、删除、库存管理、交接联系方式
3. **借阅流程**：申请→审核→交接（暗号验证）→归还→传递（所有权转移）
4. **心愿系统**：发布心愿、满足心愿
5. **消息通知**：站内消息、微信推送
6. **定时任务**：自动取消超时、逾期扣分
7. **信用分系统**：风控机制、每日恢复上限

系统采用前后端分离架构，代码结构清晰，功能模块划分合理，适合作为毕业设计或课程设计参考。
