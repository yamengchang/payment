### 平台接入

注册eureka
--
##### application.properties 添加
	eureka.instance.hostname=localhost
	eureka.instance.port=8081
	eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
	eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${eureka.instance.port}/eureka/

##### 命名  spring.application.name= **  
	如：  spring.application.name=demoeSrvice 

##### 访问 网关地址加上服务的名称  
 	如：http://localhost:8765/demoervice/具体的方法 
 	** 注意spring.application.name=demoeSrvice 命名的是demoeSrvice（Service的s是大写，访问的时候一律小写）

至此接入平台接完成了

### 接入平台注意
 开发无需登录的访问，访问链接加上**/napi/** 即可
 不假**/napi/**的一律会先登录后跳转


### 单点登出
	http://localhost:8765/logout
	
##以下是与平台接入无关

	1：如需多版本共存：第三方平台的服务名称加上版本号，如  spring.application.name=demoeSrvice-v1
	网关访问对应的就是      http://localhost:8765/demoervice-v1/具体的方法
	2： a:网关配置黑名单，需要配合阿波罗，具体参考 阿波罗的文档配置实时生效无需重启 
	    b:在配置文件   black.ip= 192.168.1.123,  192.168.1.124，192.168.1.125配置不实时生效，需要重启p

	    
	