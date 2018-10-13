package hp.redreader.com.mvp.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import timber.log.Timber

/**
 * 类名：    R-Reader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/10/14/014 3:49
 * 修改人：  hp
 * 修改时间：2018/10/14/014 3:49
 * 修改备注：
 */
class TestActivity : BaseHeadeActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Timber.e("测试")
    }
}