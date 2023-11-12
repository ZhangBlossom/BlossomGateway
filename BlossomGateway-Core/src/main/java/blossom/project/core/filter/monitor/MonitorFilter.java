package blossom.project.core.filter.monitor;

import blossom.project.core.context.GatewayContext;
import blossom.project.core.filter.Filter;
import blossom.project.core.filter.FilterAspect;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

import static blossom.project.common.constant.FilterConst.*;
/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Prometheus入口过滤器
 *
 */

@Slf4j
@FilterAspect(id=MONITOR_FILTER_ID,
        name = MONITOR_FILTER_NAME,
        order = MONITOR_FILTER_ORDER)
public class MonitorFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        ctx.setTimerSample(Timer.start());
    }
}
