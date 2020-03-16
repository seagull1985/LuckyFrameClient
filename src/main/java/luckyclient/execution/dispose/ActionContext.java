package luckyclient.execution.dispose;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;

import luckyclient.execution.dispose.actionkeyword.Action;
import luckyclient.execution.dispose.actionkeyword.ActionKeyWordParser;
import luckyclient.utils.LogUtil;

/**
 * 步骤动作容器，根据参数生成不同的动作关键字类型，执行相应的解析
 * @author: sunshaoyan@
 * @date: Created on 2019/4/13
 */
@SuppressWarnings("rawtypes")
public class ActionContext {

    private static Map<String, Class> allActions;

    static {
        Reflections reflections = new Reflections("luckyclient.execution.dispose.actionkeyword");
        Set<Class<?>> annotatedClasses =
                reflections.getTypesAnnotatedWith(Action.class);
        allActions = new ConcurrentHashMap<>();
        for (Class<?> classObject : annotatedClasses) {
            Action action = classObject
                    .getAnnotation(Action.class);
            allActions.put(action.name(), classObject);
        }
        allActions = Collections.unmodifiableMap(allActions);
    }

    private ActionKeyWordParser action;

    public ActionContext(String name){

        if (allActions.containsKey(name)) {
        	LogUtil.APP.info("Created Action name is {}", name);
            try {
                action = (ActionKeyWordParser) allActions.get(name).newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
            	LogUtil.APP.error("Instantiate Action failed", ex);
            }
        } else {
        	LogUtil.APP.warn("Specified Action name {} does not exist", name);
        }
    }

    public String parse(String actionParams,String testResult,String actionKeyWord) {
        if(null != action){
            testResult = action.parse(actionParams, testResult);
        }else {
            testResult="未检索到对应动作关键字，直接跳过此动作，请检查关键字："+actionKeyWord;
            LogUtil.APP.warn("未检索到对应动作关键字，直接跳过此动作，请检查关键字：{}",actionKeyWord);
        }
        return testResult;
    }

}
