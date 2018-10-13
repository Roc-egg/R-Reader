package hp.redreader.com.app.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.transition.ArcMotion
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.RelativeLayout

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jess.arms.base.delegate.IActivity
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.lifecycle.ActivityLifecycleable
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.trello.rxlifecycle2.android.ActivityEvent

import java.lang.reflect.Method

import javax.inject.Inject

import hp.redreader.com.R
import hp.redreader.com.app.utils.*
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.statusbar.StatusBarUtil
import hp.redreader.com.mvp.ui.widegt.CustomChangeBounds
import hp.redreader.com.mvp.ui.widegt.MyNestedScrollView
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

import com.jess.arms.utils.ThirdViewUtil.convertAutoView

/**
 * 类名：    RedReader
 * 类描述：  电影、书籍等详情页
 * 创建人：  hp
 * 创建时间：2018/9/23/023 21:41
 * 修改人：  hp
 * 修改时间：2018/9/23/023 21:41
 * 修改备注：
 */
abstract class b<P : IPresenter> : AppCompatActivity(), IActivity, ActivityLifecycleable {

}
