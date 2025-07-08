package io.rong.imkit.model;

import android.content.Context;
import android.net.Uri;

import io.rong.common.FileInfo;
import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.message.SightMessage;

public class MySightMessage extends SightMessage {


//    public static SightMessage obtain(Context context, Uri localUri, int duration) {
//        if (context != null && localUri != null) {
//            FileInfo fileInfo = FileUtils.getFileInfoByUri(context, localUri);
//            if (fileInfo != null) {
//                return new SightMessage(fileInfo, (Uri)null, localUri, duration);
//            } else {
//                RLog.e("SightMessage", "localUri is not file or content scheme");
//                return null;
//            }
//        } else {
//            RLog.e("SightMessage", "url or context is null");
//            return null;
//        }
//    }


}
