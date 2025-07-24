package io.rong.imkit.manager;

import android.net.Uri;
import android.util.Log;

import com.custom.base.config.BaseConfig;

import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R;
import io.rong.imkit.feature.destruct.DestructManager;
import io.rong.imkit.http.HttpRequest;
import io.rong.imkit.picture.config.PictureMimeType;
import io.rong.imkit.picture.entity.LocalMedia;
import io.rong.imkit.picture.tools.FileUtils;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.ConversationIdentifier;
import io.rong.imlib.model.Message;
import io.rong.message.GIFMessage;
import io.rong.message.ImageMessage;
import io.rong.message.MediaMessageContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SendImageManager {
    private static final String TAG = "SendImageManager";

    private ExecutorService executorService;
    private UploadController uploadController;

    static class SingletonHolder {
        static SendImageManager sInstance = new SendImageManager();
    }

    public static SendImageManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private SendImageManager() {
        executorService = getExecutorService();
        uploadController = new UploadController();
    }

    public void sendImage(
            ConversationIdentifier conversationIdentifier, LocalMedia image, boolean isFull, boolean isPrivate) {
        if (image.getPath() == null) {
            return;
        }
        MediaMessageContent content;
        String mimeType = image.getMimeType();
        String path = image.getPath();
        if (FileUtils.isHttp(path)){
            Uri uri = Uri.parse(path);
            content = ImageMessage.obtain(uri, uri, isFull);
            content.setMediaUrl(Uri.parse(path));
        }else {
            if (!path.startsWith("content://") && !path.startsWith("file://")) {
                path = "file://" + path;
            }
            Uri uri = Uri.parse(path);
            if (PictureMimeType.isGif(mimeType)) {
                content = GIFMessage.obtain(uri);
                content.setMediaUrl(Uri.parse(path.replace("content://","")));
            } else {
                content = ImageMessage.obtain(uri, uri, isFull);
            }
            if (DestructManager.isActive()) {
                if (content != null) {
                    content.setDestruct(true);
                    content.setDestructTime(DestructManager.IMAGE_DESTRUCT_TIME);
                }
            }
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("isPrivate", String.valueOf(isPrivate));
        content.setExtra(hashMap.toString());

        IMCenter.getInstance()
                .insertOutgoingMessage(
                        conversationIdentifier,
                        Message.SentStatus.SENDING,
                        content,
                        System.currentTimeMillis(),
                        new RongIMClient.ResultCallback<Message>() {
                            @Override
                            public void onSuccess(Message message) {
                                Log.e(TAG, "onSuccess: "+isPrivate );
                                if (isPrivate){
                                    HashMap<String,String> expansionKey = new HashMap<>();
                                    expansionKey.put("isPrivate","true");
                                    message.setExpansion(expansionKey);
                                    message.setCanIncludeExpansion(true);
                                }else {
                                    if (HttpRequest.INSTANCE.getCanSendUnlockImageNumLiveData().getValue()!=null){
                                        HashMap<String,String> expansionKey = new HashMap<>();
                                        expansionKey.put("isPrivate",HttpRequest.INSTANCE.getCanSendUnlockImageNumLiveData().getValue()<=0?"true":"false");
                                        message.setExpansion(expansionKey);
                                        message.setCanIncludeExpansion(true);
                                    }
                                }
                                uploadController.execute(message);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.e(TAG, "onError: " + errorCode.getMessage() );
                                // do nothing
                            }
                        });
        // todo 旧版逻辑分 if else 要判断回调


    }

    public void cancelSendingImages(
            Conversation.ConversationType conversationType, String targetId) {
        RLog.d(TAG, "cancelSendingImages");
        if (conversationType != null && targetId != null && uploadController != null)
            uploadController.cancel(conversationType, targetId);
    }

    public void cancelSendingImage(
            Conversation.ConversationType conversationType, String targetId, int messageId) {
        RLog.d(TAG, "cancelSendingImages");
        if (conversationType != null
                && targetId != null
                && uploadController != null
                && messageId > 0) uploadController.cancel(conversationType, targetId, messageId);
    }

    private class UploadController implements Runnable {
        final List<Message> pendingMessages;
        Message executingMessage;

        public UploadController() {
            this.pendingMessages = new ArrayList<>();
        }

        public void execute(Message message) {
            synchronized (pendingMessages) {
                pendingMessages.add(message);
                if (executingMessage == null) {
                    executingMessage = pendingMessages.remove(0);
                    executorService.submit(this);
                }
            }
        }

        public void cancel(Conversation.ConversationType conversationType, String targetId) {
            synchronized (pendingMessages) {
                Iterator<Message> it = pendingMessages.iterator();
                while (it.hasNext()) {
                    Message msg = it.next();
                    if (msg.getConversationType().equals(conversationType)
                            && msg.getTargetId().equals(targetId)) {
                        it.remove();
                    }
                }

                if (pendingMessages.size() == 0) executingMessage = null;
            }
        }

        public void cancel(
                Conversation.ConversationType conversationType, String targetId, int messageId) {
            synchronized (pendingMessages) {
                int count = pendingMessages.size();
                for (int i = 0; i < count; i++) {
                    Message msg = pendingMessages.get(i);
                    if (msg.getConversationType().equals(conversationType)
                            && msg.getTargetId().equals(targetId)
                            && msg.getMessageId() == messageId) {
                        pendingMessages.remove(msg);
                        break;
                    }
                }
                if (pendingMessages.size() == 0) executingMessage = null;
            }
        }

        private void polling() {
            synchronized (pendingMessages) {
                RLog.d(TAG, "polling " + pendingMessages.size());
                if (pendingMessages.size() > 0) {
                    executingMessage = pendingMessages.remove(0);
                    executorService.submit(this);
                } else {
                    executingMessage = null;
                }
            }
        }

        @Override
        public void run() {
            boolean isDestruct = false;
            if (executingMessage.getContent() != null) {
                isDestruct = executingMessage.getContent().isDestruct();
            }

            Log.e(TAG, "run: "+((MediaMessageContent)executingMessage.getContent()).getLocalPath() );
            if (FileUtils.isHttp(((MediaMessageContent)executingMessage.getContent()).getLocalPath().toString())){
                IMCenter.getInstance().sendMessage(executingMessage, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        Log.e(TAG, "onAttached: " );
                    }

                    @Override
                    public void onSuccess(Message message) {
                        Log.e(TAG, "onSuccess: " );
                        //已发送次数
                        int sendMessagesNumber= BaseConfig.Companion.getGetInstance().getInt(executingMessage.getTargetId()+"_sendMessagesNumber",0)+1;
                        BaseConfig.Companion.getGetInstance().setInt(executingMessage.getTargetId()+"_sendMessagesNumber",sendMessagesNumber);
                        //剩余发送次数
                        int messagesNumber= BaseConfig.Companion.getGetInstance().getInt(executingMessage.getTargetId()+"_messagesNumber",0)-1;
                        BaseConfig.Companion.getGetInstance().setInt(executingMessage.getTargetId()+"_messagesNumber",messagesNumber);
                        if (HttpRequest.INSTANCE.getCanSendImageNumLiveData().getValue()!= null) {
                            HttpRequest.INSTANCE.getCanSendImageNumLiveData().postValue(HttpRequest.INSTANCE.getCanSendImageNumLiveData().getValue()-1);
                        }

                        Integer canSendUnlockImageNum = HttpRequest.INSTANCE.getCanSendUnlockImageNumLiveData().getValue();
                        if (canSendUnlockImageNum != null) {
                            if (canSendUnlockImageNum>0) {
                                HttpRequest.INSTANCE.getCanSendUnlockImageNumLiveData().postValue(canSendUnlockImageNum - 1);
                            }
                        }
                        polling();

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        polling();
                        Log.e(TAG, "onError: "+errorCode.getMessage() );
                    }
                });
            }else {
                IMCenter.getInstance().sendMediaMessage(executingMessage,
                        isDestruct ? IMCenter.getInstance().getContext().getString(R.string.rc_conversation_summary_content_burn) : null, null,
                        new IRongCallback.ISendMediaMessageCallback() {
                            @Override
                            public void onAttached(Message message) {
                                // default implementation ignored
                                Log.e(TAG, "onAttached: " );
                            }

                            @Override
                            public void onError(Message message, RongIMClient.ErrorCode code) {
                                polling();
                                Log.e(TAG, "onError: "+code.getMessage() );
                            }

                            @Override
                            public void onSuccess(Message message) {
                                Log.e(TAG, "onSuccess: " );
                                //已发送次数
                                int sendMessagesNumber= BaseConfig.Companion.getGetInstance().getInt(executingMessage.getTargetId()+"_sendMessagesNumber",0)+1;
                                BaseConfig.Companion.getGetInstance().setInt(executingMessage.getTargetId()+"_sendMessagesNumber",sendMessagesNumber);
                                //剩余发送次数
                                int messagesNumber= BaseConfig.Companion.getGetInstance().getInt(executingMessage.getTargetId()+"_messagesNumber",0)-1;
                                BaseConfig.Companion.getGetInstance().setInt(executingMessage.getTargetId()+"_messagesNumber",messagesNumber);

                                if (HttpRequest.INSTANCE.getCanSendImageNumLiveData().getValue()!= null) {
                                    HttpRequest.INSTANCE.getCanSendImageNumLiveData().postValue(HttpRequest.INSTANCE.getCanSendImageNumLiveData().getValue()-1);
                                }

                                Integer canSendUnlockImageNum = HttpRequest.INSTANCE.getCanSendUnlockImageNumLiveData().getValue();
                                if (canSendUnlockImageNum != null) {
                                    if (canSendUnlockImageNum>0) {
                                        HttpRequest.INSTANCE.getCanSendUnlockImageNumLiveData().postValue(canSendUnlockImageNum - 1);
                                    }
                                }
                                polling();

                            }

                            @Override
                            public void onProgress(Message message, int progress) {
                                // do nothing
                                Log.e(TAG, "onProgress: " );
                            }

                            @Override
                            public void onCanceled(Message message) {
                                // do nothing
                                Log.e(TAG, "onCanceled: " );

                            }
                        });
            }
        }
    }

    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService =
                    new ThreadPoolExecutor(
                            1,
                            Integer.MAX_VALUE,
                            60,
                            TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(),
                            threadFactory());
        }
        return executorService;
    }

    private ThreadFactory threadFactory() {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, "Rong SendMediaManager");
                result.setDaemon(false);
                return result;
            }
        };
    }
}
