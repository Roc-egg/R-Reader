package me.jessyan.mvparms.demo.app

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.format.Time
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.http.imageloader.ImageConfig
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.mvp.model.api.Constants
import hp.redreader.com.mvp.model.entity.data.User
import hp.redreader.com.mvp.ui.activity.menu.LoginActivity
import org.simple.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/5/23
 * Description :
 */
/**
 * 点击事件扩展方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method.invoke() }
    return this
}

/**
 * 点击事件扩展方法
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

/**
 * 设置View的可见
 */
fun View.visible(isVisible: Boolean): View {
    visibility = if (isVisible) VISIBLE else GONE
    return this
}

fun AppCompatActivity.showDialog(dialog: DialogFragment) {
    dialog.show(supportFragmentManager, "TAG")
}

fun Fragment.showDialog(dialog: DialogFragment) {
    dialog.show(fragmentManager, "TAG")
}

fun dismissDialog(dialog: DialogFragment) {
    dialog.dismiss()
}

/**
 *
 */
fun <T : ImageConfig> ImageView.loadImage(config: T) {
    ArmsUtils.obtainAppComponentFromContext(this.getContext())
            .imageLoader().loadImage(this.context, config)
}

//fun ImageView.loadImage(url: String) {
//    loadImage(url, 0)
//}
//
//fun ImageView.loadImage(url: String, placeholder: Int) {
//    ArmsUtils.obtainAppComponentFromContext(this.getContext())
//            .imageLoader().loadImage(this.context, YangYanImageConfig.Builder()
//                    .url(url)
//                    .placeholder(placeholder)
//                    .imageView(this)
//                    .build())
//}

fun Context.getTopActivity(): Activity {
    return ArmsUtils.obtainAppComponentFromContext(this).appManager().topActivity
}

/**
 * 通过uri  获取文件的路径
 */
fun Uri.getRealFilePath(context: Context): String? {
    val scheme = this.getScheme()
    var data: String? = null
    if (scheme == null)
        data = this.getPath()
    else if (ContentResolver.SCHEME_FILE == scheme) {
        data = this.getPath()
    } else if (ContentResolver.SCHEME_CONTENT == scheme) {
        val cursor = context.contentResolver.query(this, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (index > -1) {
                    data = cursor.getString(index)
                }
            }
            cursor.close()
        }
    }
    return data
}

/**
 * 登陆成功
 */
fun handleLoginSuccess() {
    SPUtils.getInstance().put(Constants.IS_LOGIN, true)
    EventBus.getDefault().post(User(), "LoginSuccess")
}

/**
 * 退出登录
 */
fun handleLoginFailure() {
    SPUtils.getInstance().put(Constants.IS_LOGIN, false)
    SPUtils.getInstance().remove("cookie")
    EventBus.getDefault().post(User(), "LoginSuccess")
}

/**
 * 是否登录，没有进入登录页面
 */
fun isLogin(): Boolean {
    val isLogin = SPUtils.getInstance().getBoolean(Constants.IS_LOGIN, false)
    return if (!isLogin) {
        ToastUtils.showShort("请先登录~")
        ArmsUtils.startActivity(LoginActivity::class.java)
        false
    } else {
        true
    }
}

/**
 * 判断网络是否可用
 * return true:不可用
 */
fun isNetWork(): Boolean {
    if (!NetworkUtils.isConnected()) {
        //判断网络是否连接
        return true
    } else if (!NetworkUtils.isConnected()) {
        //判断网络是否可用
        return true
    }
    return false
}

/**
 * 随机颜色
 */
fun randomColor(): Int {
    val random = Random()
    val red = random.nextInt(150) + 50//50-199
    val green = random.nextInt(150) + 50//50-199
    val blue = random.nextInt(150) + 50//50-199
    return Color.rgb(red, green, blue)
}

/**
 * 获取当前日期
 */
fun getData(): String {
    val sDateFormat = SimpleDateFormat("yyyy-MM-dd")
    return sDateFormat.format(Date())
}

/**
 * 获取当天日期
 */
fun getTodayTime(): ArrayList<String> {
    val data = getData()
    val split = data.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    val year = split[0]
    val month = split[1]
    val day = split[2]
    val list = ArrayList<String>()
    list.add(year)
    list.add(month)
    list.add(day)
    return list
}

/**
 * 通过比例设置图片的高度
 *
 * @param width 图片的宽
 * @param bili  图片比例
 * @param type  1:外层 LinearLayout 2：外层 RelativeLayout
 */
fun formartHight(imageView: ImageView, width: Int, bili: Float, type: Int) {
    val height = (width / bili).toInt()
    if (type == 1) {
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        imageView.layoutParams = lp
    } else {
        val lp = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        imageView.layoutParams = lp
    }
}

/**
 * 得到上一天的时间
 */
fun getLastTime(year: String, month: String, day: String): ArrayList<String> {
    val ca = Calendar.getInstance()//得到一个Calendar的实例
    ca.set(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day))//月份是从0开始的，所以11表示12月

    //使用roll方法进行向前回滚
    //cl.roll(Calendar.DATE, -1);
    //使用set方法直接进行设置
    val inDay = ca.get(Calendar.DATE)
    ca.set(Calendar.DATE, inDay - 1)

    val list = ArrayList<String>()
    list.add(ca.get(Calendar.YEAR).toString())
    list.add((ca.get(Calendar.MONTH) + 1).toString())
    list.add(ca.get(Calendar.DATE).toString())
    return list
}

/**
 * 获取当前时间是否大于12：30
 */
fun isRightTime(): Boolean {
    // or Time t=new Time("GMT+8"); 加上Time Zone资料。
    val t = Time()
    t.setToNow() // 取得系统时间。
    val hour = t.hour // 0-23
    val minute = t.minute
    return hour > 12 || (hour == 12 && minute >= 30)
}

