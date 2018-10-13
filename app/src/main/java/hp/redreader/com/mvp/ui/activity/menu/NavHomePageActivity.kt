package hp.redreader.com.mvp.ui.activity.menu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import hp.redreader.com.R
import hp.redreader.com.app.utils.ShareUtils.share
import kotlinx.android.synthetic.main.activity_nav_home_page.*
import me.jessyan.mvparms.demo.app.onClick

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/10/13/013 17:40
 * 修改人：  hp
 * 修改时间：2018/10/13/013 17:40
 * 修改备注：
 */
class NavHomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_home_page)
        // 解决7.0以上系统 滑动到顶部 标题裁减一半的问题
        setSupportActionBar(detail_toolbar)
        fab_share.onClick {
            share(this, R.string.string_share_text)
        }
    }
}
