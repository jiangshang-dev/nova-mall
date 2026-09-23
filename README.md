# Nova Mall

> 单商家 B2C 商城 · Spring Boot 3 + Vue 3 全栈开源实践

<p align="center">
  <a href="https://github.com/jiangshang-dev/nova-mall/stargazers"><img src="https://img.shields.io/github/stars/jiangshang-dev/nova-mall?style=for-the-badge&logo=github" alt="Stars"/></a>
  <a href="https://github.com/jiangshang-dev/nova-mall/network/members"><img src="https://img.shields.io/github/forks/jiangshang-dev/nova-mall?style=for-the-badge" alt="Forks"/></a>
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License"/>
</p>

<p align="center">
  <b>后端 API</b> ·
  <a href="https://github.com/jiangshang-dev/nova-mall-views">商城前台</a> ·
  <a href="https://github.com/jiangshang-dev/nova-mall-back">管理后台</a>
</p>

---

## ⭐ 开源约定（请先读）

本仓库遵循开源精神，代码可自由学习与二次开发。

同时请遵守一条**君子协议**：

> **请先给本仓库点一个 Star，再执行 Clone。**  
> 未 Star 禁止 Clone（开源江湖规矩，不做技术校验，全靠人品）。

点 Star 是对作者最大的鼓励，也方便你之后找回项目。  
三个仓库都 Star 一下更佳：

| 仓库 | 说明 | Star |
|------|------|------|
| [nova-mall](https://github.com/jiangshang-dev/nova-mall) | 后端（本仓库） | [![Star](https://img.shields.io/github/stars/jiangshang-dev/nova-mall?style=social)](https://github.com/jiangshang-dev/nova-mall) |
| [nova-mall-views](https://github.com/jiangshang-dev/nova-mall-views) | C 端商城前台 | [![Star](https://img.shields.io/github/stars/jiangshang-dev/nova-mall-views?style=social)](https://github.com/jiangshang-dev/nova-mall-views) |
| [nova-mall-back](https://github.com/jiangshang-dev/nova-mall-back) | 运营管理后台 | [![Star](https://img.shields.io/github/stars/jiangshang-dev/nova-mall-back?style=social)](https://github.com/jiangshang-dev/nova-mall-back) |

```bash
# 正确姿势：浏览器点亮 Star → 再拉代码
git clone https://github.com/jiangshang-dev/nova-mall.git
git clone https://github.com/jiangshang-dev/nova-mall-views.git
git clone https://github.com/jiangshang-dev/nova-mall-back.git
```

---

## 项目简介

Nova Mall 是一套**单商家自营 B2C** 电商示例工程，覆盖「前台下单 → 支付 → 履约 → 售后评价 → 人工客服」主链路，适合学习 / 二次开发 / 毕业设计参考。

### 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 21 · Spring Boot 3.5 · Spring Security · JWT · MyBatis-Plus · Redis · WebSocket |
| 前台 | Vue 3 · Vite · Ant Design Vue · Vue Router |
| 后台 | Vue 3 · Vite · Ant Design Vue · Pinia · Vditor（Markdown） |
| 支付 | 微信支付 APIv3（Native 扫码）· 策略模式预留支付宝 / Apple Pay |
| 数据 | MySQL 8 |

### 功能概览

- **商品**：无限级分类、列表搜索、详情富文本、划线价、加购 / 立即购买  
- **交易**：购物车、收货地址（省市区含港澳台）、配送方式与运费、货到付款核销  
- **支付**：微信扫码（Native）、演示支付兜底、支付回调入账  
- **订单**：待支付 / 已支付 / 发货 / 完成 / 取消  
- **评价**：购买后方可评价、星级与内容  
- **会员中心**：昵称、头像、地址簿、我的订单  
- **客服**：WebSocket 人工客服、后台工作台、展示会员 IP  
- **权限**：RBAC 动态菜单 / 按钮权限、操作日志  

---

## 仓库结构

```
nova-mall/                 # 后端（本仓库）
├── nova-common/           # 公共模块（security / redis / websocket / oss …）
├── nova-modules/          # 业务服务
├── nova-web/              # 启动模块（8082）
└── sql/                   # 建库与补丁脚本

nova-mall-views/           # C 端前台（5173）
nova-mall-back/            # 管理后台（3100）
```

---

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis（可选，按配置）

### 1. 数据库

```bash
# 导入主库（按仓库内 sql 目录实际脚本执行）
mysql -uroot -p < sql/nova_mall_v1.sql
# 如有补丁，按顺序执行 sql/*_patch.sql
```

修改 `nova-web/src/main/resources/application-dev.yml` 中的数据源、Redis、支付等配置。

### 2. 启动后端

```bash
cd nova-mall
mvn -pl nova-web -am clean install -DskipTests
cd nova-web && mvn spring-boot:run
```

默认端口：`http://localhost:8082`  
接口文档：`http://localhost:8082/doc.html`（若已启用）

### 3. 启动前台 / 后台

```bash
# 商城前台
cd nova-mall-views && npm i && npm run dev
# → http://localhost:5173

# 管理后台
cd nova-mall-back && npm i && npm run dev
# → http://localhost:3100
```

前端默认将 `/api` 代理到 `http://localhost:8082`。

### 演示账号

| 端 | 账号 | 密码 |
|----|------|------|
| 管理后台 | `admin` / `admin@nova.com` | `admin123` |
| 商城前台 | `user` / `user@nova.com` | `admin123` |

> 具体以库内初始化用户为准；也可前台自助注册。

---

## 微信支付（可选）

配置见 `application-dev.yml` → `nova-mall.pay.wxpay`：

- 商户号、证书序列号、AppID、APIv3 密钥  
- `apiclient_key.pem` 放在 `nova-web/src/main/resources/cert/`（**勿提交真实私钥到公开仓库**）  
- `notify-url` 需公网 HTTPS（可用 natapp / cpolar）  
- `enabled: true` 后重启；未配齐时自动走演示扫码  

证书与密钥请从 [微信商户平台](https://pay.weixin.qq.com) 自行申请，**不要使用他人培训材料中的证书**。

---

## 文档与贡献

- 业务说明：`docs/说明.md`  
- Issue / PR 欢迎，请尽量附复现步骤与环境信息  
- 提交前请勿携带 `.env`、私钥、真实商户密钥  

---

## License

[MIT](./LICENSE) — 可商用、可修改，请保留版权声明。  
若本项目对你有帮助，请别忘了 **Star** 三连，谢谢！

<p align="center">
  <sub>Made with ☕ by <a href="https://github.com/jiangshang-dev">jiangshang-dev</a></sub>
</p>
