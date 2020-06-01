# easylimit文档

当前分支是`easylimit`框架的文档站点，由 [docsify](https://github.com/docsifyjs/docsify)负责页面展示。

修改文档可以直接修改md文件，提交后会自动部署到[Github Pages](https://easylimit.zifangsky.cn/)。

若有较复杂的改动，需要本地查看修改后的显示效果，则需要在安装完`Node.js`环境后安装`docsify`。详细安装方式如下：

#### （1）安装： ####

```bash
$ npm i docsify-cli -g
```

#### （2）初始化： ####

```bash
$ docsify init ./docs

Initialization succeeded! Please run docsify serve ./docs
```

初始化后系统帮我们生成了一个 ./docs 目录，目录中包含以下文件：

- index.html：入口文件
- README.md：将作为主页渲染
- .nojekyll：阻止 Github Pages 忽略以下划线开头的文件

#### （3）预览： ####

使用以下命令启动本地服务器：

```
$ docsify serve docs

Serving C:\Users\98383\Desktop\docs now.
Listening at http://localhost:3000
```

