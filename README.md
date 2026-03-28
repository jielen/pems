# 物证管理系统 (PEMS)

公安机关物证全生命周期数字化管理系统，严格遵循《公安机关物证管理规定》"一物一码、全程留痕、责任到人、闭环管理"的管理要求。

## 项目结构

```
pems/
├── docs/                    # 项目文档
│   ├── requirements/        # 需求规格说明书
│   ├── design/              # 设计文档
│   │   ├── architecture/    # 架构设计
│   │   ├── database/        # 数据库设计
│   │   └── api/             # 接口设计
│   ├── ui/                  # UI设计
│   └── meeting/             # 会议纪要
├── pems_backend/            # 后端 (SpringBoot + MyBatis-Plus)
└── pems_frontend/           # 前端 (Vue3 + Element Plus)
```

## 技术栈

### 后端
- SpringBoot 4.0.3
- MyBatis-Plus 4.0.1
- Druid 1.2.28
- JWT 0.9.1
- Redis 6.0+
- MySQL 8.0+

### 前端
- Vue3 3.5.26
- Vite 6.4.1
- Element Plus 2.13.1
- Pinia 3.0.4

## 开发

详细开发文档请参考 [CLAUDE.md](CLAUDE.md)

## 文档

- [需求规格说明书](docs/requirements/物证管理系统需求规格说明书.md)
