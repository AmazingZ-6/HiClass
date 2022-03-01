# HiClass

 基于 JetPack + MVVM 架构对HiClass代码进行重构
 
 #HiClass Refactor 2022/1/19
 
 弃用原版本数据库操作模块 启用room
  
 解决编辑数据更新UI时白屏延迟现象
 
 解决更新UI无反应现象
 
 #HiClass Refactor 2022/1/20
 
 通过阿里巴巴iconfont更新添加事项UI
 
 #HiClass Refactor 2022/1/21
 
 重构添加item模块代码
 
 重构返回主页面课表刷新模块代码
 
 启用MVVM机构重构ItemAddAdapter ItemAddViewModel SelectViewModel SelectAdapter
 
 #HiClass Refactor 2022/1/21
 
 Android 原生就是你吗一坨浓shit 开发就是在吃屎 去你妈的google 垃圾
 
 #HiClass Refactor 2022/1/22
 
 重构批量添加item模块
 
 一定程度解决更新UI卡顿现象
 
 #HiClass Refactor 2022/1/23
 
 重构批量删除模块
 
 美化整体UI
 
 #HiClass Refactor 2022/1/24
 
 重构TCP python服务端及Android客户端 弃用UDP传输
 
 UDP在相当一部分wifi情况下会出现全丢包现象 更换服务器端口后仍无法解决问题 猜测是路由器进行某种不为我所知的丢弃操作
 
 完成题库存储及通过python后端获取题库
 
 #HiClass Refactor 2022/1/25
 
 增加下载题库模块
 
 #HiClass Refactor 2022/1/25
 
 重构闹钟模块：完成前台service 数据库alarm装填 编辑闹钟布局
 
 #HiClass Refactor 2022/1/28
 
 解决通过thread数据库操作导致无法在onResume中正常实现数据刷新问题
 
 1.主线程强制等待\TimerTask -> 简单有效 但很蠢
 
 2.handle\AsyncTask -> 有效 但司马谷歌已弃用
 
 3.通过viewmodel livedata postValue方法在线程中设立完成标记，observe方法实现finish返回 -> 巧妙且有效 MVVM来作用了
 
 4.kotlin协程 通过suspend将函数挂起在协程中进行数据库工作 -> 未尝试 有理论可行性 用法相当高级 但需要对Dao模块进行大量再重构
 
 #HiClass Refactor 2022/1/29
 
 重构闹钟模块：..
 
 建立 activity + fragment + adapter + viewmodel + model 架构
 
 #HiClass Refactor 2022/1/30
 
 重构闹钟模块：.. 解决 data class 在paecelize序列化后仍无法在activity之间传递的问题：
 
 分解后传递
 
 #HiClass Refactor 2022/1/31
 
 重构闹钟模块：..
 
 #HiClass Refactor 2022/2/1
 
 重构闹钟模块：解决recyclerview中动态更改item样式出现复用问题：
 
 当进行item删除操作后需同时调用notifyItemRemoved. notifyItemRangeChanged 两接口 否则会出现position错乱等问题
 
 但 notifyItemRangeChanged 会回调 adapter 中的 onBindViewHolder 方法 导致界面会出现一些十分奇怪的现象（奇怪得跟谷歌死了吗一样）
 
 通过监听事件实现 viewmodel livedata observe 进行曲线更改 成功解决问题
 
 #HiClass Refactor 2022/2/4
 
 解决退出程序再次加载后视图重复加载现象
 
 慎用全局变量 //由于时间原因无法进行三次重构 只能在屎山里进行补救
 
 返回键退出程序后主activity销毁 但item\alarm数据保存在全局变量列表中 因进程没有杀死数据无法清空 导致重新加载程序后进行多次数据获取
 
 保持闹钟功能无法杀死进程 只能在退出程序时进行数据清除
 
 增加编辑头像等用户个性化资料功能
 
 #HiClass Refactor 2022/2/6
 
 重构添加闹钟后刷新模块
 
 解决pendingIntent传值重复问题：标志位设置 FLAG_UPdATE_CURRENT
 
 #HiClass Refactor 2022/2/12
 
 解决闹钟显示界面点击无反应现象：viewmodel lifeobserve 始终保持原有个数不改变 导致重复观察重复执行某段代码 通过date class 设置标志 治标不治本
 
 解决添加闹钟后页面刷新bug: 冒泡排序没写明白
 
 重构设置界面
 
 #HiClass/Refactor/2022/2/13
 
 解决编辑闹钟后页面刷新问题：注意越界情况下的判空安全措施
 
 重构AlarmService
 
 今日感触：截止目前 屎山重构成屎堆 留给中国队的时间已经不多了 无法二次重构...
 
 #HiClass Refactor 2022/2/15
 
 增加动态显示闹钟响铃时间模块
 
 存在问题：thread混乱导致程序崩溃
 
 #HiClass Refactor 2022/2/17
 
 重构动态显示闹钟响铃时间模块
 
 重构增加课表闹钟模块
 
 #HiClass Refacotr 2022/2/19
 
 开始重构申请权限模块
 
 #HiClass Refactor 2022/2/22
 
 解决课表模块scrollview 滚动内容显示不全问题：增加content高度
 
 增加自定义背景模块：增加沉浸式状态栏
 
 初步解决获取图库内容bitmap拉伸问题
 
 #HiClass Refactor 2022/2/28
 
 重构闹钟响铃界面及逻辑
 
 增加闹钟铃声选择
 
 修正闹钟部分bug
 
 修正10节以后事项无法正常显示bug
 
 
