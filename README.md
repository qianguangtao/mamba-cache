# 基于Spring Cache的多级缓存

## 前章

为Spring controller接口增加一个缓存层，可以提高接口的访问速度，降低数据库的IO。

通常缓存层的实现有两种方案，基于本地缓存（Caffeine, EHCache等）和Redis

- 本地缓存

  优点：读取速度快

  缺点：集群环境下，无法缓存同步

- Redis

  优点：解决了集群环境下，缓存同步

  缺点：增加了Redis服务器的压力

## cache-starter

cache-starter就是解决上述的问题，核心代码在**starter/cache-starter**（本仓库mamba-cache是为了演示的springboot工程）。基于Spring Cache，同时集成了Caffeine和Redis，使用Redisson的订阅/发布机制实现了本地缓存同步。并针对list或分页查询，解决查询的key定义问题（比如page查询通常带有查询条件），实现图如下：

涉及的技术点：

1. Spring 原生cache的使用
2. Spel表达式
3. Spring aop
4. 单例模式共享资源
5. Redisson的订阅/发布
6. Reflect反射

![spring cache 二级缓存](https://github.com/qianguangtao/mamba-cache/assets/6427290/7f892a01-d7d0-4e32-b159-7ec36e5f8c41)


## **UML类图**
![spring cache UML 二级缓存](https://github.com/qianguangtao/mamba-cache/assets/6427290/b8996720-a745-425b-81a5-a1151df06e6b)



## 使用

Springboot入口类添加@EnableCaching

```java
@EnableCaching
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 新增yml配置

```yaml
app:
  cache:
    caffeine-enabled: true # 是否开启caffeine缓存，false则只有redis一级缓存
    caffeine-timeout: 3000 # caffeine缓存过期时间，单位秒，非必填，默认180s
    redis-timeout: 6000 # redis缓存过期时间，单位秒，非必填，默认600s
    sync-enabled: true # 增删改是否需要同步集群中其他机器中的Caffeine
    cacheNamesPackage: com.app.demo.cache.enums # 添加了@CacheNames注解的枚举类package，可为空，空则扫描com.**
```

### **维护缓存前缀枚举**

   注意：

1. 枚举类要添加注解@CacheNames，cache-starter会获取所有带@CacheNames枚举的name()

2. 需要创建内部接口Names，在@CachePut, @Cacheable, @CacheEvict上使用

   注意：CacheEnum枚举的name()要和interface Names里属性的值保持一致。两个作用一样，都是缓存的前缀，只不过*注解里不能直接使用CacheEnum.User.name()

```java
@CacheNames
@AllArgsConstructor
public enum CacheEnum {
    /** 枚举name()用于spring cache的CacheManager管理所有缓存 */
    User(Names.User);

    /** code用于标记缓存的前缀，同枚举name()，只不过注解不能直接使用CacheEnum.Dict.name() */
    private final String code;

    public interface Names {
        String User = "User";
    }
}
```

### **springboot启动，添加redisson主题订阅**

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheApplicationRunner implements ApplicationRunner {

    private final CacheSubscriber cacheSubscriber;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cacheSubscriber.subscribeCacheTopic();
    }
}
```

### service方法上添加缓存注解

   以下是cache-starter注解使用举例。

   注意：

   缓存注解@CacheXXX只能用于被service直接调用的方法，不能使用在service.funcA()内部调用的funcB()中（AOP的特性，实际是代理类调用增强方法，而service.funcA()内部调用的funcB()是原始方法）

####  **新增or更新@CachePut**

   注意：

1. 新增or更新接口要返回插入的实体类，Spring Cache会以返回实体类的找key,value放入缓存。如下面的示例，如果返回的Student中id为空，则会报错

```java
@CachePut(value = CacheEnum.Names.User, key = "#result.id")
@Override
public User insert(User user) {
	user.setId("3");
	DB.put("3", user);
	return user;
}

@CachePut(value = CacheEnum.Names.User, key = "#user.id")
@Override
public User edit(User user) {
	User userInDB = DB.get(user.getId());
	userInDB.setUserName(user.getUserName());
	DB.put(user.getId(), userInDB);
	return userInDB;
}
```



#### **删除单条@CacheEvict**

```java
@CacheEvict(value = CacheEnum.Names.User, key = "#id")
@Override
public void delete(Long id) {
	DB.remove(id);
}
```

#### 批量删除@CollectionCacheEvict

   使用自定义的注解@CollectionCacheEvict，aop切面内部会获取idList，使用CacheManager删除

```java
@CollectionCacheEvict(value = CacheEnum.Names.User)
@Override
public void delete(List<String> idList) {
	idList.forEach(id -> DB.remove(id));
}
```

**单个查询@Cacheable**

   注意：

1. 要加@Cacheable要加unless和condition

```java
@Cacheable(value = CacheEnum.Names.User, key = "#id"
		, unless = "#result == null"
		, condition = "#id != null")
@Override
public User getById(Long id) {
	return DB.get(id);
}
```

#### list查询@CollectionCacheable

```java
@CollectionCacheable(value = CacheEnum.Names.User, timeout = 30, unit = TimeUnit.MINUTES)
@Override
public List<User> list(User user) {
	// userName不为空，根据userName模糊查询
	if (StrUtil.isNotBlank(user.getUserName())) {
		return DB.entrySet().stream().map(Map.Entry::getValue)
				.filter(u -> u.getUserName().indexOf(user.getUserName()) > -1).collect(Collectors.toList());
	}
	return DB.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
}
```

## 测试

### 修改yml中配置redis

### 启动DemoApplication.java

### 浏览器访问swagger

http://localhost:8080/doc.html

#### 查询用户列表（不带参数）

POST /users/list

第一次访问，debug进入了UserService.list(User user)方法

![image](https://github.com/qianguangtao/mamba-cache/assets/6427290/7788bffe-fbb4-45aa-90fd-40122573a06c)

##### Redis缓存

多了一条User:List:方法名的记录

![image](https://github.com/qianguangtao/mamba-cache/assets/6427290/102644da-8266-448f-9359-3973a54e2815)

##### Caffeine中缓存（name输入User）

GET /caffeines/{name}

![image](https://github.com/qianguangtao/mamba-cache/assets/6427290/4070ecab-ee80-44ab-aa5c-8f9555bca7e9)

第二次访问，debug不会进入了UserService.list(User user)方法

#### 查询用户列表（带参数）

POST /users/list

![image](https://github.com/qianguangtao/mamba-cache/assets/6427290/dd8704f8-813a-4597-b72c-22a3a0d783cc)

##### Redis缓存

多了一条User:List:MD5(入参)的记录

![image](https://github.com/qianguangtao/mamba-cache/assets/6427290/45dc0500-931d-4964-9ace-464975c13396)


##### Caffeine中缓存（name输入User）

![image](https://github.com/qianguangtao/mamba-cache/assets/6427290/0582f19b-db43-42b4-9cbf-0322af2af585)





