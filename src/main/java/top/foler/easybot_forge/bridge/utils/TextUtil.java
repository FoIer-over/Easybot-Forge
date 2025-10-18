package top.foler.easybot_forge.bridge.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class TextUtil {
    
    /**
     * 解析旧版颜色代码
     */
    public static String parseLegacyColor(String message) {
        if (message == null) {
            return "";
        }
        // 替换颜色代码
        message = message.replace("§0", "");
        message = message.replace("§1", "");
        message = message.replace("§2", "");
        message = message.replace("§3", "");
        message = message.replace("§4", "");
        message = message.replace("§5", "");
        message = message.replace("§6", "");
        message = message.replace("§7", "");
        message = message.replace("§8", "");
        message = message.replace("§9", "");
        message = message.replace("§a", "");
        message = message.replace("§b", "");
        message = message.replace("§c", "");
        message = message.replace("§d", "");
        message = message.replace("§e", "");
        message = message.replace("§f", "");
        message = message.replace("§k", "");
        message = message.replace("§l", "");
        message = message.replace("§m", "");
        message = message.replace("§n", "");
        message = message.replace("§o", "");
        message = message.replace("§r", "");
        return message;
    }
    
    /**
     * 将Component转换为Markdown格式
     */
    public static String toMarkdown(Component component) {
        StringBuilder sb = new StringBuilder();
        processComponent(component, sb);
        return sb.toString();
    }
    
    private static void processComponent(Component component, StringBuilder sb) {
        if (component == null) {
            return;
        }
        
        // 处理样式
        Style style = component.getStyle();
        boolean isBold = style.isBold();
        boolean isItalic = style.isItalic();
        boolean isUnderlined = style.isUnderlined();
        
        // 应用样式前缀
        if (isBold) {
            sb.append("**");
        }
        if (isItalic) {
            sb.append("*");
        }
        if (isUnderlined) {
            sb.append("__");
        }
        
        // 添加文本内容
        sb.append(component.getString());
        
        // 应用样式后缀（反向顺序）
        if (isUnderlined) {
            sb.append("__");
        }
        if (isItalic) {
            sb.append("*");
        }
        if (isBold) {
            sb.append("**");
        }
        
        // 处理悬停事件
        if (style.getHoverEvent() != null) {
            // 可以在这里添加悬停事件的处理
        }
        
        // 处理点击事件
        if (style.getClickEvent() != null) {
            String url = style.getClickEvent().getValue();
            if (url != null && !url.isEmpty()) {
                // 将文本转换为链接
                int start = sb.length() - component.getString().length();
                sb.insert(start, "[");
                sb.append("]()");
            }
        }
        
        // 处理子组件
        for (Component child : component.getSiblings()) {
            processComponent(child, sb);
        }
    }
    
    /**
     * 解析占位符
     */
    public static String parsePlaceholders(String text, String playerName) {
        // 基础占位符解析
        // 可以根据需要扩展支持更多的占位符
        if (text == null) {
            return "";
        }
        
        // 替换玩家名称占位符
        if (playerName != null) {
            text = text.replace("%player_name%", playerName);
        }
        
        // 替换颜色代码
        text = parseLegacyColor(text);
        
        return text;
    }
}