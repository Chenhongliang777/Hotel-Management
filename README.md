# 悦居民宿业务管理系统

一体化民宿业务管理系统，包含管理后台（Web）和宾客端（微信小程序）。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0+
- **安全**: JWT 认证
- **构建**: Maven 3.6+

### 前端 - 管理后台
- **框架**: Vue 3.4
- **UI组件**: Element Plus 2.4
- **构建工具**: Vite 5.0
- **HTTP客户端**: Axios
- **图表**: ECharts 5.4
- **状态管理**: Pinia

### 前端 - 宾客端
- **平台**: 微信小程序

## 项目结构

```
homestay-business-management/
├── backend/                    # 后端项目
│   ├── src/main/java/com/homestay/
│   │   ├── common/            # 通用模块
│   │   ├── config/            # 配置类
│   │   ├── context/           # 上下文
│   │   ├── interceptor/       # 拦截器
│   │   └── module/            # 业务模块
│   │       ├── system/        # 系统管理(员工/宾客/日志/配置)
│   │       ├── room/          # 房间管理
│   │       ├── order/         # 订单管理
│   │       ├── payment/       # 支付管理
│   │       ├── inventory/     # 库存管理
│   │       ├── pos/           # POS消费
│   │       ├── cleaning/      # 清洁任务
│   │       └── statistics/    # 统计分析
│   ├── sql/                   # 数据库脚本
│   └── pom.xml
├── frontend/
│   ├── admin/                 # 管理后台
│   │   ├── src/
│   │   │   ├── api/          # API接口
│   │   │   ├── layout/       # 布局组件
│   │   │   ├── router/       # 路由配置
│   │   │   ├── stores/       # 状态管理
│   │   │   ├── utils/        # 工具函数
│   │   │   └── views/        # 页面组件
│   │   └── package.json
│   ├── guest-web/            # 宾客Web端
│   │   ├── src/
│   │   │   ├── api/          # API接口
│   │   │   ├── router/       # 路由配置
│   │   │   ├── stores/       # 状态管理
│   │   │   ├── utils/        # 工具函数
│   │   │   ├── views/        # 页面组件
│   │   │   └── styles/       # 样式文件
│   │   └── package.json
│   └── miniprogram/          # 微信小程序
│       ├── pages/            # 页面
│       ├── app.js
│       ├── app.json
│       └── app.wxss
└── README.md
```

## 功能模块

### 宾客端功能
- **房间查询**: 按日期、人数、偏好查询可用房间
- **房间详情**: 查看房型、价格、设施、照片等信息
- **在线预订**: 选择房型、填写信息、支付保证金
- **订单管理**: 查看订单状态、取消订单
- **个人中心**: 查看/修改个人信息、历史订单

#### 宾客端平台
- **Web端**: 官方网站，提供完整的自助服务功能
- **微信小程序**: 移动端便捷预订

### 管理端功能

#### 🔹 民宿经营者（最高权限）
- 员工管理：增删改员工账号、分配角色
- 房间与价格管理：设置房型、设施、动态调价
- 经营分析：查看营收、成本、利润报表
- 系统设置：配置保证金规则、取消政策
- 权限审计：查看操作日志

#### 🔹 前台员工（中等权限）
- 入住办理：为宾客办理入住、分配房间
- 结账退房：生成账单、押金抵扣、退款处理
- 订单管理：创建/修改/取消订单
- POS消费录入：记录宾客在店消费
- 房态查看：实时查看房间状态

#### 🔹 房务员工（基础权限）
- 房态更新：标记房间清洁状态
- 清洁任务：查看待清洁房间、完成任务
- 物资消耗：记录清洁用品消耗

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- 微信开发者工具（小程序开发）

### 数据库初始化

1. 创建数据库并执行SQL脚本：
```bash
mysql -u root -p < backend/sql/homestay_db.sql
```

2. 修改数据库连接配置 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/homestay_db
    username: root
    password: your_password
```

### 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问：
- API地址: http://localhost:8080/api
- Swagger文档: http://localhost:8080/api/swagger-ui.html

### 启动管理后台

```bash
cd frontend/admin
npm install
npm run dev
```

管理后台访问: http://localhost:3000

### 启动宾客Web端

```bash
cd frontend/guest-web
npm install
npm run dev
```

宾客Web端访问: http://localhost:3001

### 微信小程序

1. 使用微信开发者工具打开 `frontend/miniprogram` 目录
2. 修改 `app.js` 中的 `baseUrl` 为实际后端地址
3. 编译运行

## 测试账号

### 管理后台
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 经营者 | admin | 123456 |
| 经营者 | owner | 123456 |
| 前台 | reception1 | 123456 |
| 房务 | housekeeper1 | 123456 |

### 宾客端
| 用户名 | 密码 |
|--------|------|
| guest1 | 123456 |
| guest2 | 123456 |

## API接口说明

### 认证接口
- `POST /api/auth/employee/login` - 员工登录
- `POST /api/auth/guest/login` - 宾客登录
- `POST /api/auth/guest/register` - 宾客注册

### 房间管理
- `GET /api/roomType/available` - 获取可用房型
- `GET /api/roomType/search` - 搜索可用房型
- `GET /api/room/list` - 获取所有房间

### 订单管理
- `POST /api/order` - 创建订单
- `GET /api/order/my` - 获取当前宾客的订单
- `GET /api/order/{id}` - 获取订单详情
- `PUT /api/order/{id}/checkin` - 办理入住
- `PUT /api/order/{id}/checkout` - 办理退房
- `PUT /api/order/{id}/cancel` - 取消订单

### 文件上传
- `POST /api/file/upload` - 上传文件

## 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_employee | 员工表 |
| sys_guest | 宾客表 |
| sys_config | 系统配置表 |
| sys_operation_log | 操作日志表 |
| room_type | 房型表 |
| room | 房间表 |
| booking_order | 订单表 |
| payment_record | 支付记录表 |
| inventory_item | 库存物品表 |
| inventory_record | 库存变动记录表 |
| pos_record | POS消费记录表 |
| cleaning_task | 清洁任务表 |

## 注意事项

1. **密码存储**: 按要求使用明文存储，生产环境请改用加密存储
2. **文件上传**: 上传的文件存储在 `backend/uploads` 目录
3. **跨域配置**: 已配置允许所有来源，生产环境请限制
4. **JWT密钥**: 请修改 `application.yml` 中的 `jwt.secret`

## License

MIT License
