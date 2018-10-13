package hp.redreader.com.app.utils.dialog

import android.app.Application
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.BaseTools
import hp.redreader.com.app.utils.ShareUtils
import hp.redreader.com.app.utils.data.TaskLocalDataSource
import hp.redreader.com.app.utils.listener.OnLoginListener
import hp.redreader.com.mvp.model.entity.data.User
import me.jessyan.mvparms.demo.app.handleLoginFailure

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/25/025 3:11
 * 修改人：  hp
 * 修改时间：2018/9/25/025 3:11
 * 修改备注：
 */
/**
 * 显示单行文字的AlertDialog
 */
fun show(v: View, title: String, clickListener: DialogInterface.OnClickListener) {
    val builder = AlertDialog.Builder(v.context)
    val view = View.inflate(v.context, R.layout.title_douban_top, null)
    val titleTop = view.findViewById<TextView>(R.id.title_top)
    titleTop.text = title
    builder.setView(view)
    builder.setPositiveButton("查看详情") { dialog, which -> clickListener.onClick(dialog, which) }
    builder.show()
}

/**
 * 显示选项的AlertDialog
 */
fun showItems(v: View, content: String?) {
    val items = arrayOf("复制", "分享")
    val builder = AlertDialog.Builder(v.context)
    builder.setItems(items) { dialog, which ->
        when (which) {
            0 -> {
                BaseTools.copy(v.context, content)
                ToastUtils.showShort("复制成功")
            }
            1 -> ShareUtils.share(v.context, content)
            else -> {
            }
        }
    }
    builder.show()
}


/**
 * 用于账号登录
 */
fun showItems(v: View, mApplication: Application, listener: OnLoginListener) {
    var list = TaskLocalDataSource.getInstance(mApplication).quaryAll(User::class.java)
    var items: Array<String>
    var isLogin: Boolean
    if (list == null || list.size == 0) {
        items = arrayOf("GitHub账号", "玩安卓账号")
        isLogin = false
    } else {
        var user = list[0]
        if (user == null) {
            items = arrayOf("GitHub账号", "玩安卓账号")
            isLogin = false
        } else {
            items = arrayOf("GitHub账号", "退出玩安卓（" + user.username + "）")
            isLogin = true
        }
    }
    showDialog(v, mApplication, items, listener, isLogin)

//            Injection.get().getSingleBean(object : UserDataCallback() {
//                fun onDataNotAvailable() {
//                    val items = arrayOf("GitHub账号", "玩安卓账号")
//                    showDialog(v, items, listener, false)
//                }
//
//                fun getData(bean: User) {
//                    val items = arrayOf("GitHub账号", "退出玩安卓（" + bean.getUsername() + "）")
//                    showDialog(v, items, listener, true)
//                }
//            })

}

private fun showDialog(v: View, mApplication: Application, items: Array<String>, listener: OnLoginListener, isLogin: Boolean) {
    val builder = AlertDialog.Builder(v.context)
    builder.setTitle("账号登录")
    builder.setItems(items) { dialog, which ->
        when (which) {
            0 -> listener.loginGitHub()
            1 -> if (isLogin) {
                TaskLocalDataSource.getInstance(mApplication).deleteAll(User::class.java)
                handleLoginFailure()
                ToastUtils.showLong("退出成功")
            } else {
                listener.loginWanAndroid()
            }
            else -> {
            }
        }
    }
    builder.show()
}