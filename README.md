# nano-claude-code4j
仿claude code 的Java 版本


注：使用idea编译时需要加上-parameters参数进行编译，可以是编译后的jar包能够通过反射获取方法的参数名称

# todo
1.多轮对话持久化
2.多轮对话切换
3.读取claude.md
4.定时/延时任务交给其他线程做，而不是子线程做
5.达到max_token后截断，怎么把截断的两段对话合并发给用户
6.插件机制
