# Digital Esthetic Dentistry Design

## 简介

Digital Esthetic Dentistry Design (DEDD) 是一个基于 Java 的图形用户界面（GUI）应用程序，专为数字化牙科美学设计而开发。它为牙医和设计师提供了工具，用于创建和操作牙科美学设计。

## 前提条件

要构建和运行此应用程序，您需要：

- Java Development Kit (JDK) 7 或更高版本
- Apache ANT

## 构建项目

1. 克隆仓库：

   ```bash
   git clone https://github.com/kqstone/Digital-Esthetic-Dentistry-Design.git
   ```

2. 进入项目目录：

   ```bash
   cd Digital-Esthetic-Dentistry-Design
   ```

3. 运行 ANT 构建项目：

   ```bash
   ant create_run_jar
   ```

   此命令将编译源代码并在项目目录中生成一个可运行的 JAR 文件 `dedd.jar`。

## 运行应用程序

运行以下命令启动应用程序：

```bash
java -jar dedd.jar
```

## 功能

- **编辑图片**：用于编辑牙科图像的工具。
- **统一规划**：支持牙科美学规划的功能。
- **标记唇部和牙齿**：在图像中标记和识别唇部和牙齿的工具。
- **调整牙齿**：调整牙齿位置和外观的功能。
- **导出设计**：将最终设计导出的选项。

## 作者

- **姓名**：kqstone
- **邮箱**：kqstone@163.com