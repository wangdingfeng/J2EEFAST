## ***Star上1000，修改协议成MIT, 欢迎 Star!!!***


# J2eeFAST

![输入图片说明](https://img.shields.io/badge/%E9%AB%98%E6%95%88-%E5%BF%AB%E9%80%9F-brightgreen "在这里输入图片标题")
![输入图片说明](https://img.shields.io/badge/%E5%AE%89%E5%85%A8-%E6%BA%90%E7%A0%81%E5%8F%AF%E6%8E%A7-blueviolet "在这里输入图片标题")
[![技术交流群](https://img.shields.io/badge/QQ%E6%8A%80%E6%9C%AF%E7%BE%A4-805468934-orange "J2eeFAST技术交流群") 
](https://jq.qq.com/?_wv=1027&k=5xTlnN6)

---

J2eeFAST 是一个 Java EE 企业级快速开发平台， **致力于打造中小企业最好用的开源免费的后台框架平台** 。 系统基于（Spring Boot、Spring MVC、Apache Shiro、MyBatis-Plus、Freemarker、Bootstrap、AdminLTE）经典技术开发， 系统内置核心模块包含众多常用基础功能(在线代码生成功能、组织机构、角色用户、菜单及按钮授权、数据权限、系统参数、license认证、BPM工作流等)，  **让你用最低的成本、最高的效率，开发项目，她也适合新手朋友练手** 。同时具备， **界面简洁美观、高效、安全、源码可控、版本迭代快、免费技术交流群** 等特点。她适用于所有Web应用，她会成为你快速开发项目的好帮手。


#### 软件架构 

1.  _核心框架：Spring Boot 2.2.5.RELEASE_ 
2.   _安全框架：Apache Shiro 1.4.2_ 
3.   _模板引擎：Freemarker_ 
4.   _前端：AdminLTE 2.3.8, Bootstrap 3.3.7, Bootstrap-Table 1.11.0, JQuery 3.3.1_ 
5.   _持久层框架：MyBatis-Plus 3.3.1_ 
6.   _定时任务: Quartz_ 
7.   _数据库连接池：Druid 1.10.1_ 
9.   _数据库: Mysql5.7_
10.  _分布式缓存数据库: Redis 4.0.9_
11.  _工具类：Hutool 4.5.8_ 
12.  _工作流引擎:flowable 6.4.2_

#### 演示地址

1.  演示地址： http://www.j2eefast.com <br>账号 ：admin 密码：admin
2.  功能还在陆续更新中......

#### 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
9.  登录日志：系统登录日志记录查询包含登录异常。
10. 在线用户：当前系统中活跃用户状态监控。
11. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
12. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
13. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
14. 在线构建器：拖动表单元素生成相应的HTML代码。
15. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。
16. 产品许可: 对项目进行许可证书控制，防止程序拷贝运行。
17. 公告通知:  针对项目升级重要情况出通知公告直接生成静态页面，可以实现延迟推送，设置时效性,实时预览功能

**common 模块中crypto包下有关于银行加密算法 DES 3DES 国密，怎么算MAC 银行密码加密、都是通过项目实践的。 对这块感兴趣的可以学习下** 
 
**产品许可生成数字证书 保护个人与企业的软件作品权益，降低盗版造成的损失**

 **项目配置文件yml对于敏感字段如数据库密码，证书密码，等重要敏感信息加密。防止敏感信息直接暴露！#注意 数据加密安全性其实是相对的,什么加密如果私钥或者key泄露都是无用,这个世界上没有一种加密方法是绝对安全的。这种加密方法只能做到：防君子不防小人！！**
```
写法需要加密的参数 ENC{} DES{} 包裹 或者 SM4{} 包裹 系统会自动识别，其他正常参数不加即可
例如:
spring:
    datasource:
        #默认(主)数据库配置
        default:
            driverClassName: com.mysql.cj.jdbc.Driver
            url: SM4(YPS0KwUcR6ZETrk1CkHPQydzrGO0WEGKb23G4SYdxRHfCqpDI+CrZfEhdxrjkLrH8TlmyqsC50mP/q4ZJzyJfauQIZ3AbsgQ3k/XzDwsrvSI+58c1UuMKaZW3zdIPj1wg+dUmfldaW4i3CQOfHGXwXL+hpVTIjpUBPZFewkPcnk=)
```
#### 安装教程

1.  需要准备环境 Mysql5.7以上、 JDK 1.8、 Maven 3.3 、Redis4.X 以上 、开发工具 eclipse 或者 IEDA
2.  下载源码 git clone https://gitee.com/zhouhuanOGP/J2EEFAST.git
3.  编译代码
    找到根目录下 pom.xml，执行 mvn clean install 命令编译一键打包。
    一般来说不会有什么问题，如果还是编译不成功，可以按照优先级逐个编译试一试。
4.  导入数据库
    db目录里initDb.sql 有建库建表语句按步骤执行即可
5.  将代码导入开发工具fast-admin启动模块- 执行 FastApplication 类即可
注意:
    fast-admin模块 资源目录 application-Test.yml 中修改连接数据库 链接地址 如果你是按照initDb.sql 建库，test.sql导入初始数据 则账号密码用户名都不需要修改

6. ****[搭建文档](https://www.yuque.com/u1174484/j2eefast/kt0nbi)**** 
#### 参与贡献

1.  本项目设计思路借鉴了当前Gitee中 开源项目中后台管理框架众多优秀项目的设计思路
2.  在此特别感谢 JeeSite、RuoYi、Mybatis-Plus 原作者的贡献付出！
3.  在此也感谢众多小伙伴提出项目问题及贡献的代码.

####  版本更新
* 本次更新
    * `2020-09-08 v2.0.9 ` 
    - 解决登录IP转换位置接口不够用问题
    - 修复前端表格插件显示跳转问题
    - 优化前端页面渲染
    - 优化shiro权限,优化session管理
    - 新增对最大在线人数控制，证书控制
    - 优化登录并发控制
    - 优化组件规范名称
    - 新增系统文件管理
    - 优化上传文件控制
    - 解决左侧菜单适配高度滑动问题,解决小窗口菜单点击出现重叠问题
    - 新增表格动态增减行数据
    - 新增大数据表格选择组件
    - 新增右侧滑动弹出窗口
    - 新增颜色选择器
    - 优化视频播放器
    - 升级shiro到最新版本1.6.0 权限绕过漏洞CVE-2020-13933
    - 升级fastjson到最新版1.2.73
    - 修复打包生成Windows命令文件
    - 修复代码生成管理人员字段、修复百度富文本路径问题
    - 优化表单验证只有id报错问题
    - 修改默认打包不打工作流模块
    - 去掉系统google字体
    - 新增系统部分引用功能标注，作者与申明
    - 修复前端请求获取异常问题
    - 新增请求控制实体类字段输出、敏感数据转换提供安全性
    - 修改弹出Tab框细节
    - 优化系统移动设备适配问题
    - 新增war打包
    - 其他细节优化
    
        
2. [更新日志](https://www.yuque.com/u1174484/j2eefast/yscyux) 

#### 关于系统
* J2eeFAST (快速开发开源系统)名字由来:包含作者对于软件开发的美好幻想，希望软件开发变的快速而简单，每个人都能分享自己的技术从而达到快速又强大的软件系统,让我们有更多的时间去陪伴家人!你可能在本系统中看到众多优秀开源项目的影子，因为她本身集成了众多优秀的开源项目精华功能，在这种环境中成长的，但是她目前还有很多缺点，希望大神们嘴下留情。如果你也喜欢开源、喜欢本项目,作者欢迎您的加入, J2eeFAST会因为您的加入而变的更加完善与丰富!
* 如果本项目对你有帮助,[请点击Star收藏](https://gitee.com/zhouhuanOGP/J2EEFAST),**本项目会长期维护**,若你在使用中有任何问题或建议,欢迎在[码云issue提交问题](https://gitee.com/zhouhuanOGP/J2EEFAST/issues)作者会第一时间处理，让我们一起完善J2eeFAST
* 官网: [http://www.j2eefast.com](http://www.j2eefast.com)
* 关于文档：[语雀 文档](https://www.yuque.com/u1174484/j2eefast/tv3kee)
* 关于更新：项目现在会放缓更新优化细节,演示网站会不定期暂停访问,带来不便尽情谅解!
* QQ群：[805468934](https://jq.qq.com/?_wv=1027&k=5xTlnN6)
#### 免责声明：
 1. **本项目代码全部开源，无需任何费用。如果有人向你贩卖本系统都是骗子!** 
1. 不得将 J2eeFAST于危害国家安全、荣誉和利益的行为，不能以任何形式用于非法为目的的行为,否则后果自负
1. J2eeFAST前身主要用于银行项目,本身很注重安全因素,可以从项目登陆可以看出。再者本身项目100%开源，但是您任需了解是软件皆有漏洞，任何人都无法保证软件100%没有漏洞。所以由本软件漏洞造成损失不予赔偿，同时也不承担任何因使用本软件而产生的相关法律责任。也请在软件上线前进行必要的安全监测，避免安全问题发生。

#### 参与开发
* 谢谢大家支持，如果你希望参与开发，欢迎fork本项目，并Pull Request您的commit。

>  **码云Gitee(主): [https://gitee.com/zhouhuanOGP/J2EEFAST](https://gitee.com/zhouhuanOGP/J2EEFAST)** 
> 
>  **Github(辅): [https://github.com/zhouhuan751312/J2EEFAST](https://github.com/zhouhuan751312/J2EEFAST)** 

#### 项目演示
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084001_75d40312_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084020_e10905d8_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084104_5497a596_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084221_70975dcf_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/142932_30f1f459_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/143020_d4583af2_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/143057_462c279f_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/143152_b0ff9fd0_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084132_00c4292f_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084145_791f453f_1816537.png "屏幕截图.png")