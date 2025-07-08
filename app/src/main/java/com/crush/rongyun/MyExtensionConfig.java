package com.crush.rongyun;

import android.util.Log;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import io.rong.imkit.conversation.extension.DefaultExtensionConfig;
import io.rong.imkit.conversation.extension.component.emoticon.IEmoticonTab;
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule;
import io.rong.imkit.conversation.extension.component.plugin.ImagePlugin;
import io.rong.imlib.model.Conversation;

public class MyExtensionConfig extends DefaultExtensionConfig {
    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType, String targetId) {
        List<IPluginModule> pluginModules = super.getPluginModules(conversationType,targetId);
        ListIterator<IPluginModule> iterator = pluginModules.listIterator();

        // 删除扩展项
        while (iterator.hasNext()) {
            IPluginModule integer = iterator.next();
            // 以删除 FilePlugin 为例
//            if (integer instanceof FilePlugin) {
//                iterator.remove();
//            }
            if (!(integer instanceof ImagePlugin)) {
                iterator.remove();
            }
        }
//        pluginModules.add(new MyImagePlugin());
        return pluginModules;
    }
}