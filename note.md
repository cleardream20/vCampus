# git

## git clone
```shell
git clone https://github.com/cleardream20/vCampus.git
```

## git init
```shell
git init
```

## git push
### 首先查看一下branch（分支）

```shell
git branch
# or
git branch -a
```

看看有没有develop分支，这个就是开发主分支
然后切换分支到develop
```shell
git checkout develop # checkout切换分支
```

### 推送三部曲
1.add 2.commit 3.push

添加到缓冲区，写好相应描述，正式提交

（其实还有一个0.`git reset`，取消之前缓冲区暂存的文件，以免全都push上去了）

---
add
```shell
git add . # .所有文件
# or
git add ./xxx.xx # 指定某文件
```

---
commit
```shell
git commit -m "你的描述" # e.g."create ServerController"
```

---
push
```shell
git push -u origin develop # 提交到develop分支上
```

（如果后面都是像同一个分支e.g.develop提交，就可以省略后面的`-u origin develop`了，直接`git push`）

## git status
```shell
git status # 查看状态
```

## 换行符
如果出现类似下面的有关`LF`和`CRLF`的问题
```shell
PS E:\GithubProjs\vCampus> git add .
warning: in the working copy of '.gitignore', LF will be replaced by CRLF the next time Git touches it
warning: in the working copy of '.idea/misc.xml', LF will be replaced by CRLF the next time Git touches it
warning: in the working copy of 'pom.xml', LF will be replaced by CRLF the next time Git touches it
warning: in the working copy of 'src/main/java/org/example/Main.java', LF will be replaced by CRLF the next time Git touches it
```

这是关于不同系统的行尾符的，windows和linux不同，所以warning一下，不过完全没有关系

---
如果有强迫症不想看这些warning:
```shell
git config --global core.autocrlf true # windows推荐设置
```

## 团队协作 & 功能分支工作流
以develop为主分支，每个人搞自己的模块时在自己的分支模块上开发

例如我创建一个小分支`member/li`
```shell
# 先获取最新的develop分支内容
git checkout develop
git pull origin develop

# 从develop创建新分支
git checkout -b member/li

# 开发之后进行提交
git add .
git commit -m "develop login panel"
git push -u origin member/li # git push / git push origin member/li

# 如果想要merge到develop主分支上
git checkout develop
git pull origin develop # 如果有修改的话
git merge member/li # 尝试把自己的merge上去
git push origin develop # 推送更新

# 然后我在github上审核pull requests，如果没冲突都OK之后合并到develop分支上
# 大概是一两天合并一回，有需要随时跟我说~

# 如果分支用不到了还可以删掉（反正还能重建嘛）
git branch -d member/li
```

## 分支切换临时保存
一个分支的修改没有保存提交，就切换到另一个分支，会出问题

可以临时保存一下修改

e.g.从`member/li`切换到`develop`
```shell
# 保存当前未完成的更改
git stash

# 现在可以切换分支了
git checkout develop

# 回到 member/li 分支时，恢复保存的更改
git checkout member/li
git stash pop
```

## ??

## 数据库？
仔细想了一下问题应该不大，因为java应该支持sql语句，也就是可以用java语句操控数据库

比如在UserController里面实现一个UpdateUser()函数

那只要调用了这个函数就会更新数据库的User表，那有需要就调用一下呗

数据库应该有一个较完善的初始化，让大家尽量少一点问题

最终打包部署的数据库最后肯定要再统一整合一遍的，开发的时候就各操作各自的应该没问题

# maven
POM Project Object Model

核心: `pom.xml`

编译 测试 打包 部署 & **依赖管理**

[一小时Maven教程]https://www.bilibili.com/video/BV1uApMeWErY?p=5&vd_source=127961da3fc6c308c415223bd57e3f44

（这是分小结的，建议看安装、配置、命令使用、在IDEA中使用这几节）

## JDK 1.7+
```shell
java -version
```

## Maven下载
[Maven 官方下载页面](https://maven.apache.org/download.cgi)

Files [Binary zip archive, Link]

## 安装
### 解压
文件路径尽量没有空格和中文

### 环境变量
编辑系统环境变量

1.新建变量`MAVEN_HOME`，值为Maven的解压后所在的路径

2.编辑变量`Path`，添加一个`%MAVEN_HOME%\bin`

### 验证安装
```shell
mvn -c
```

## 配置本地仓库和镜像
1.找到Maven的配置文件：进入Maven安装目录的`conf`文件夹，找到`settings.xml`

2.复制文件：建议将`settings.xml`复制到你的本地仓库同级目录。Maven 会优先使用用户目录下的配置。

3.修改本地仓库路径（可选）：

编辑`settings.xml`，找到`<localRepository>`标签，修改为项目相应的路径

e.g.
```xml
<settings>
  <!-- 其他配置 -->
  <localRepository>D:/Projects</localRepository>
  <!-- 其他配置 -->
</settings>
```

4.配置国内镜像（加速下载）：

e.g.阿里云镜像
```xml
<mirror>
    <id>alimaven</id>
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/repository/public</url>
    <mirrorOf>*</mirrorOf>
</mirror>
```

5.profiles修改
```xml
<profile>
  <id>jdk-21</id>

  <activation>
    <activeByDefault>true</activeByDefault>
    <jdk>21</jdk>
  </activation>
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <maven.compiler.compilerVersion>21</maven.compiler.compilerVersion>
  </properties>
</profile>
```

21指定默认jdk版本号

## 常用命令
| 命令 | 说明                                 |
|--|------------------------------------|
| `mvn compile` | 编译源代码                              |
| `mvn test` | 运行测试                               |
| `mvn package` | 打包项目（生成JAR/WAR文件在`target/`目录下）     |
| `mvn clean` | 清理之前构建生成的文件                        |
| `mvn install` | 将项目安装到本地仓库                         |
| `mvn clean compile` | 组合命令，先清理再编译（有问题就`mvn clean compile`再重进编译器！） |
| `mvn clean package` | 组合命令                               |
| `mvn clean install` | 先清理，再编译、测试、打包，最后安装到本地仓库（最常用？！）     |
| `mvn dependency:tree` | 查看项目的依赖树 |

## pom.xml文件
**！！！**
~依赖版本管理器，不能随便乱改版本，如有需要一起商议~


# 项目架构（文件目录）
```bash
vCampus/ # 项目根目录
├── pom.xml # 父POM，管理所有子模块的公共依赖和配置
├── vCampus-common/ # 公共模块 (存放客户端和服务器端共用的类)
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── seu
│       │   │           └── vcampus
│       │   │               ├── model/ # 实体类 (必须实现Serializable)
│       │   │               │   ├── User.java
│       │   │               │   ├── Student.java
│       │   │               │   ├── Course.java
│       │   │               │   ├── Book.java
│       │   │               │   └── Product.java
│       │   │               ├── util/ # 工具类
│       │   │               │   ├── Message.java # 网络传输消息体
│       │   │               │   └── DBConnector.java # 数据库连接工具 (可选，也可分别放在C/S端)
│       │   │               └── exception/ # 自定义异常
│       │   │                   └── AuthenticationException.java
│       │   └── resources
│       └── test
│           └── java
├── vCampus-server/ # 服务器端模块
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── seu
│       │   │           └── vcampus
│       │   │               ├── ServerMain.java # 服务器主启动类
│       │   │               ├── controller/ # 控制器层 (处理业务逻辑，调用Service)
│       │   │               │   ├── UserController.java
│       │   │               │   ├── CourseController.java
│       │   │               │   ├── LibraryController.java
│       │   │               │   └── ShopController.java
│       │   │               ├── service/ # 服务层接口和实现
│       │   │               │   ├── IUserService.java
│       │   │               │   ├── UserServiceImpl.java
│       │   │               │   ├── ICourseService.java
│       │   │               │   └── CourseServiceImpl.java
│       │   │               ├── dao/ # 数据访问层
│       │   │               │   ├── IUserDao.java
│       │   │               │   ├── UserDaoImpl.java
│       │   │               │   ├── ICourseDao.java
│       │   │               │   └── CourseDaoImpl.java
│       │   │               └── socket/ # 网络通信层
│       │   │                   ├── ServerSocketThread.java # 服务端Socket线程
│       │   │                   └── ClientManager.java # 客户端连接管理池
│       │   └── resources
│       │       └── config.properties # 服务器配置文件 (如数据库URL、端口号)
│       └── test
│           └── java
├── vCampus-client/ # 客户端模块
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── seu
│       │   │           └── vcampus
│       │   │               ├── ClientMain.java # 客户端主启动类
│       │   │               ├── view/ # 视图层 (Swing界面)
│       │   │               │   ├── panel/
│       │   │               │   │   ├── LoginPanel.java
│       │   │               │   │   ├── MainPanel.java
│       │   │               │   │   ├── CourseSelectionPanel.java
│       │   │               │   │   ├── LibraryPanel.java
│       │   │               │   │   └── ShopPanel.java
│       │   │               │   └── frame/
│       │   │               │       └── MainFrame.java # 主窗口
│       │   │               ├── controller/ # 客户端控制器 (监听界面事件，发送请求)
│       │   │               │   ├── UserController.java
│       │   │               │   ├── CourseController.java
│       │   │               │   ├── LibraryController.java
│       │   │               │   └── ShopController.java
│       │   │               └── socket/ # 客户端网络通信
│       │   │                   └── ClientSocketHandler.java
│       │   └── resources
│       │       └── images/ # 存放图片资源
│       └── test
│           └── java
└── database/ # 数据库文件目录 (不纳入Maven模块，单独存放)
    ├── vCampus.accdb # 或 vCampus.mdb / vCampus.sqlite
    └── init.sql # 数据库初始化脚本 (创建表、插入测试数据)
```
