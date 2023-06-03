# Regie-take-out
哔哩哔哩里的瑞吉外卖
新人后端项目,难度不高
非常适合我这种初学者
springBoot,Mybatis-plus，SpringCache,Redis,vue,js

基本框架是瑞吉外卖的
修改了，登录信息储存方式，把session换成了token，请求拦截器，响应拦截器等等
完善了地址系统，购物车的增删改查和几个零散的增删改查操作
轻微修改了几处前端页面

其实这个项目对我来说，感觉最麻烦的就是前端了，这个项目的前端虽然不难，但是东西有点多，初看的时候让人眼花缭乱
要不停的开项目调试debug，我记得最坑的时候是一个网络问题，porm加的依赖一直加载不出来，我还以为是java编译什么的出问题了，卡了好久。还比如redis要求java对象实现序列化接口
比如我想修改登录信息储存逻辑，写前端request.js的请求拦截器和响应拦截器，这个一开始就很头疼的，有一次js文件死活加载不进去，让我调试了快一天。



这个项目其实也不算很简单吧，要考虑的东西很多

比如不同类包之间的常用数据交互，这里建立了一个公共的类BaseContext来储存，这里用多线程的想法实现的，
这里也在里面用了session来实现数据交互

有静态资源映射器，来处理包扫描的问题

有过滤器统一处理请求

有全局异常处理器，来统一处理一些特殊的异常，比如SQLIntegrityConstraintViolationException，SQL异常，要将这些异常的原因反馈给用户

有对象映射器，将JSON数据统一转换java对象，或者将java对象统一转换为JSON

有元数据处理器，来实现公共字段的自动填充，比如类似修改时间，修改者，创建数据，创建者等数据。在每次修改或者创建的时候，自动填入这些信息

有一个统一的消息处理对象R，用来将要返回给页面的数据进行一些处理，附加一些特别的标记等

有一些要注入IOC的Bean，比如RedisTemplate等bean

当然，基本的增删改查也是少不了的，list，page，getById等等

再就是业务逻辑有时候也很头疼，要看数据库表配合看前端请求，前端对错误逻辑的处理，前端的请求拦截器等等

这些东西单个看上去好像没什么，但是当统合考虑的时候，一开始确实一点无从下手的感觉




其实我知道这个项目还有很多可以操作的地方，比如用户的个人页面可以进一步设计，添加一些有意思的资料比如优惠券，积分，会员等等。还可以对每一个菜品下加一个评论区来实现用户评论。
不过这些，emm,要是以后有时间的话，我会另开一个公共仓库来实现


写代码写项目真是有意思，真的有一分耕耘一分收获的感觉，实践无处不在，好似每一分钟都在实践。你的成果可以不停被检验，也可以不停修改，可以看到的价值在敲代码的手中产生

不像写论文，我的论文永远不知道什么时候会有价值，有的时候我也不知道我努力那么久，不停计算不停计算，写出的论文到底有没有意义？甚至也许早就可以断定它没有什么价值了吧？






