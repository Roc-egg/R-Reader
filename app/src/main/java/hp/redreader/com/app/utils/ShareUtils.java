package hp.redreader.com.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import hp.redreader.com.R;

/**
 * 类名：    RedReader
 * 类描述：  分享工具类
 * 创建人：  hp
 * 创建时间：2018/9/19/019 0:32
 * 修改人：  hp
 * 修改时间：2018/9/19/019 0:32
 * 修改备注：
 */
public class ShareUtils {
    public static void share(Context context, int stringRes) {
        share(context, context.getString(stringRes));
    }

    public static void shareImage(Context context, Uri uri, String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }


    public static void share(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.action_share));
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.action_share)));
    }
}
