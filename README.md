# nano-claude-code4j
## 项目介绍
仿claude code 的Java 版本

## 编译
java -Dapi.key=${api.key} 
其中-Dapi.key是llm的api key

注：使用idea编译时需要加上-parameters参数进行编译，可以使编译后的jar包能够通过反射获取方法的参数名称

# Todo
1.多轮对话持久化

2.多轮对话切换

3.读取claude.md

4.定时/延时任务交给其他线程做，而不是主线程做

5.异常恢复：
 - 达到max_token后截断，怎么把截断的两段对话合并发给用户
 - 其他异常，更明确的处理

6.插件机制

7.multi-agent

8.skill热更新

