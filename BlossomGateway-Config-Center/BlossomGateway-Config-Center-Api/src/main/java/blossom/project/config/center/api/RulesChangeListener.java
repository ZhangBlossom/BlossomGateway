package blossom.project.config.center.api;


import blossom.project.common.config.Rule;

import java.util.List;



/**
 * @author: ZhangBlossom
 * @date: 2023/11/1 19:22
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 规则变更监听器
 */

public interface RulesChangeListener {

    /**
     * 规则变更时调用此方法 对规则进行更新
     * @param rules 新规则
     */
    void onRulesChange(List<Rule> rules);
}
