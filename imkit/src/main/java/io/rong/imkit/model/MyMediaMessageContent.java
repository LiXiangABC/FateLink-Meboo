package io.rong.imkit.model;

import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.message.MediaMessageContent;

@MessageTag(value = "app:myMediaContent", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class MyMediaMessageContent extends MediaMessageContent {
    /**
     * 读取接口，目的是要从 Parcel 中构造一个实现了 Parcelable 的类的实例处理。
     */
    public static final Creator<MyMediaMessageContent> CREATOR =
            new Creator<MyMediaMessageContent>() {

                @Override
                public MyMediaMessageContent createFromParcel(Parcel source) {
                    return new MyMediaMessageContent(source);
                }

                @Override
                public MyMediaMessageContent[] newArray(int size) {
                    return new MyMediaMessageContent[size];
                }
            };


    public MyMediaMessageContent(byte[] data) {
        String jsonStr = new String(data);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("localPath")) {
                setLocalPath(Uri.parse(jsonObj.optString("localPath")));
            }
            if (jsonObj.has("remoteUrl")) setMediaUrl(Uri.parse(jsonObj.optString("remoteUrl")));

            // 消息携带用户信息时, 自定义消息需添加下面代码
            if (jsonObj.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
            }
            // 将所有自定义变量从收到的 json 解析并赋值
//            if (jsonObj.has("content")) {
//                content = jsonObj.optString("content");
//            }

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }


    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public MyMediaMessageContent(Parcel in) {
        setLocalPath(ParcelUtils.readFromParcel(in, Uri.class));
    }


    public MyMediaMessageContent(Uri localUri,Uri mediaUri) {
        setLocalPath(localUri);
        setMediaUrl(mediaUri);
    }

    /**
     * 生成 MyMediaMessageContent 对象。
     *
     * @param localUri 媒体文件地址。
     * @return MyMediaMessageContent 对象实例。
     */
    public static MyMediaMessageContent obtain(Uri localUri,Uri mediaUri) {
        return new MyMediaMessageContent(localUri,mediaUri);
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {

            if (getLocalUri() != null) {
                /** 除非不使用 SDK 内置上传逻辑，否则 JSON 的 `localPath` 属性必须有值。 */
                jsonObj.put("localPath", getLocalUri().toString());
            }
            if (getMediaUrl() != null) {
                jsonObj.put("remoteUrl", getMediaUrl().toString());
            }
        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }
        return jsonObj.toString().getBytes();
    }

    /**
     * 获取本地图片地址（file:///）。
     *
     * @return 本地图片地址（file:///）。
     */
    public Uri getLocalUri() {
        return getLocalPath();
    }

    /**
     * 设置本地图片地址（file:///）。
     *
     * @param localUri 本地图片地址（file:///）.
     */
    public void setLocalUri(Uri localUri) {
        setLocalPath(localUri);
    }


    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志，可能是 0 或 PARCELABLE_WRITE_RETURN_VALUE。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, getLocalPath());
    }
}