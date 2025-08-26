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
git push -u origin main # 提交到main分支上
```

（如果后面都是像同一个分支e.g.main提交，就可以省略后面的`-u origin main`了，直接`git push`）

## git status
```shell
git status # 查看状态
```

## 换行符
如果出现下面的有关`LF`和`CRLF`的问题
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
